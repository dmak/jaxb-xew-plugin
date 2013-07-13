/*
 * XmlElementWrapperPlugin.java
 * 
 * Copyright (C) 2009, Bjarne Hansen, http://www.conspicio.dk.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.sun.tools.xjc.addon.xew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSDeclaration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;

/**
 * The XML Element Wrapper plugin is a JAXB plugin for the XJC compiler enabling generation of "natural" Java classes
 * for handling collection types. The code generated will be annotated with {@link XmlElementWrapper} and
 * {@link XmlElement} annotations and will have no extra inner classes representing the immediate collection type.
 * 
 * @see <a href="http://www.conspicio.dk/blog/bjarne/jaxb-xmlelementwrapper-plugin">plugin site</a>
 * @see <a href="http://www.conspicio.dk/projects/overview">source code and binary packages</a>
 * 
 * @author Bjarne Hansen
 * @author Dmitry Katsubo
 */
public class XmlElementWrapperPlugin extends Plugin {

	private static final String PLUGIN_NAME                            = "Xxew";
	private static final String OPTION_NAME_DELETE                     = "-" + PLUGIN_NAME + ":delete";
	private static final String OPTION_NAME_INCLUDE                    = "-" + PLUGIN_NAME + ":includeFile";
	private static final String OPTION_NAME_EXCLUDE                    = "-" + PLUGIN_NAME + ":excludeFile";
	private static final String OPTION_NAME_SUMMARY                    = "-" + PLUGIN_NAME + ":summaryFile";
	private static final String OPTION_NAME_COLLECTION                 = "-" + PLUGIN_NAME + ":collection";
	private static final String OPTION_NAME_INSTANTIATE                = "-" + PLUGIN_NAME + ":instantiate";

	private File                includeFile                            = null;
	/**
	 * List of classes for inclusion
	 */
	private Set<String>         include                                = null;

	private File                excludeFile                            = null;
	/**
	 * List of classes for exclusion
	 */
	private Set<String>         exclude                                = null;

	private File                summaryFile                            = null;
	private PrintWriter         summary                                = null;
	private Class<?>            collectionInterfaceClass               = java.util.List.class;
	private Class<?>            collectionImplClass                    = java.util.ArrayList.class;
	private Instantiation       instantiation;
	private boolean             deleteCandidates                       = false;

	// This is currently an experimental and not properly working feature, so keep this field set to false.
	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean				applyPluralForm				= Ring.get(BIGlobalBinding.class).isSimpleMode();
	private static boolean      applyPluralForm                        = false;

	private static final String FACTORY_CLASS_NAME                     = "ObjectFactory";

	private Log                 logger;

	static final String         COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY = "org.apache.commons.logging.simplelog.defaultlog";

	@Override
	public String getOptionName() {
		return PLUGIN_NAME;
	}

	@Override
	public String getUsage() {
		return "  -"
		            + PLUGIN_NAME
		            + ": Replace collection types with fields having the @XmlElementWrapper and @XmlElement annotations.";
	}

	void initLoggerIfNecessary(Options opts) {
		if (logger != null) {
			return;
		}

		// Allow the caller to control the log level by explicitly setting this system variable:
		if (System.getProperty(COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY) == null) {
			String logLevel = "WARN";

			if (opts.quiet) {
				logLevel = "FATAL";
			}
			else if (opts.debugMode) {
				logLevel = "DEBUG";
			}
			else if (opts.verbose) {
				logLevel = "INFO";
			}

			System.setProperty(COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY, logLevel);
		}

		logger = LogFactory.getLog(getClass());
	}

	@Override
	public void onActivated(Options opts) {
		initLoggerIfNecessary(opts);

		logger.debug("JAXB Compilation started (XmlElementWrapperPlugin.onActivated):");
		logger.debug("  buildId         : " + Options.getBuildID());
		logger.debug("  targetDir       : " + opts.targetDir);
		logger.debug("  defaultPackage  : " + opts.defaultPackage);
		logger.debug("  defaultPackage2 : " + opts.defaultPackage2);
		logger.debug("  debug           : " + opts.debugMode);
		logger.debug("  verbose         : " + opts.verbose);
		logger.debug("  quiet           : " + opts.quiet);
		logger.debug("  grammars        : " + opts.getGrammars().length);

		for (int i = 0; i < opts.getGrammars().length; i++) {
			logger.debug("\t  [" + i + "]: " + opts.getGrammars()[i].getSystemId());
		}
	}

	@Override
	public int parseArgument(Options opts, String[] args, int i) throws BadCommandLineException, IOException {
		initLoggerIfNecessary(opts);

		int recognized = 0;
		String filename;

		String arg = args[i];
		logger.debug("Argument[" + i + "] = " + arg);

		if (arg.startsWith(OPTION_NAME_DELETE)) {
			recognized++;
			deleteCandidates = true;
		}
		else if (arg.startsWith(OPTION_NAME_INCLUDE)) {
			recognized++;
			include = new HashSet<String>();

			if (arg.length() > OPTION_NAME_INCLUDE.length()) {
				filename = arg.substring(OPTION_NAME_INCLUDE.length()).trim();
			}
			else {
				filename = args[i + 1];
				recognized++;
			}

			includeFile = new File(filename);
			readCandidates(includeFile, include);
		}
		else if (arg.startsWith(OPTION_NAME_EXCLUDE)) {
			recognized++;
			exclude = new HashSet<String>();

			if (arg.length() > OPTION_NAME_EXCLUDE.length()) {
				filename = arg.substring(OPTION_NAME_EXCLUDE.length()).trim();
			}
			else {
				filename = args[i + 1];
				recognized++;
			}

			excludeFile = new File(filename);
			readCandidates(excludeFile, exclude);
		}
		else if (arg.startsWith(OPTION_NAME_SUMMARY)) {
			recognized++;
			if (arg.length() > OPTION_NAME_SUMMARY.length()) {
				filename = arg.substring(OPTION_NAME_SUMMARY.length()).trim();
			}
			else {
				filename = args[i + 1];
				recognized++;
			}

			summaryFile = new File(filename);
			summary = new PrintWriter(new FileOutputStream(summaryFile));
		}
		else if (arg.startsWith(OPTION_NAME_COLLECTION)) {
			String ccn;

			recognized++;
			if (arg.length() > OPTION_NAME_COLLECTION.length()) {
				ccn = arg.substring(OPTION_NAME_COLLECTION.length()).trim();
			}
			else {
				ccn = args[i + 1];
				recognized++;
			}
			try {
				collectionImplClass = Class.forName(ccn);
			}
			catch (ClassNotFoundException e) {
				throw new BadCommandLineException(OPTION_NAME_COLLECTION + " " + ccn + ": Class not found.");
			}
		}
		else if (arg.startsWith(OPTION_NAME_INSTANTIATE)) {
			String instantiate;
			recognized++;

			if (arg.length() > OPTION_NAME_INSTANTIATE.length()) {
				instantiate = arg.substring(OPTION_NAME_INSTANTIATE.length()).trim().toUpperCase();
			}
			else {
				instantiate = args[i + 1].trim().toUpperCase();
				recognized++;
			}
			instantiation = Instantiation.valueOf(instantiate);
		}
		else if (arg.startsWith("-" + PLUGIN_NAME + ":")) {
			throw new BadCommandLineException("Invalid argument " + arg);
		}

		return recognized;
	}

	@Override
	public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
		logger.debug("JAXB Process Model (run)...");

		// Write summary information on the option for this compilation.
		writeSummary("Compilation:");
		writeSummary("  JAXB version         : " + Options.getBuildID());
		writeSummary("  Include file         : " + (includeFile == null ? "<none>" : includeFile));
		writeSummary("  Exclude file         : " + (excludeFile == null ? "<none>" : excludeFile));
		writeSummary("  Summary file         : " + (summaryFile == null ? "<none>" : summaryFile));
		writeSummary("  Instantiation mode   : " + instantiation);
		writeSummary("  Collection impl      : " + collectionImplClass);
		writeSummary("  Collection interface : " + collectionInterfaceClass);
		writeSummary("  Delete candidates    : " + deleteCandidates);
		writeSummary("");

		// Visit all classes generated by JAXB and find candidate classes for transformation.
		Map<String, Candidate> candidatesMap = new HashMap<String, Candidate>();

		// Write information on candidate classes to summary file.
		writeSummary("Candidates:");

		for (Iterator<Candidate> iter = findCandidateClasses(outline).iterator(); iter.hasNext();) {
			Candidate candidate = iter.next();

			if (isIncluded(candidate)) {
				candidatesMap.put(candidate.getClassName(), candidate);

				writeSummary("\t[" + (candidate.isMarkedForRemoval() ? "!" : "+") + "]: " + getIncludeOrExcludeReason()
				            + ":\t" + candidate.getClassName());
			}
			else {
				writeSummary("\t[-]: " + getIncludeOrExcludeReason() + ":\t" + candidate.getClassName());
				iter.remove();
			}
		}

		writeSummary("\t" + candidatesMap.size() + " candidate(s) being considered.");
		writeSummary("");

		writeSummary("Modifications:");

		int modificationCount = 0;

		// Visit all classes again to check if the candidate is not eligible for removal:
		// * If there are classes that extend the candidate
		// * If there are class fields, that refer the candidate by e.g. @XmlElementRef annotation
		for (ClassOutline outlineClass : outline.getClasses()) {
			// Get the implementation class for the current class.
			JDefinedClass implClass = outlineClass.implClass;

			// We cannot remove candidates that have parent classes, but we can still substitute them:
			Candidate parentCandidate = candidatesMap.get(implClass._extends().fullName());

			if (parentCandidate != null) {
				logger.debug("Candidate " + parentCandidate.getClassName() + " is a parent of " + implClass.name()
				            + " and hence won't be removed.");
				parentCandidate.unmarkForRemoval();
			}

			// Visit all fields in this class.
			for (FieldOutline field : outlineClass.getDeclaredFields()) {
				// Only non-primitive fields are interesting.
				if (!(field.getRawType() instanceof JClass)) {
					continue;
				}

				JClass fieldType = (JClass) field.getRawType();

				String fieldName = field.getPropertyInfo().getName(false);

				Candidate candidate = null;

				for (Candidate c : candidatesMap.values()) {
					// Skip fields with basic types as for example any class can be casted to Object.
					if (fieldType.isAssignableFrom(c.getCandidateClass()) && !isTopClass(fieldType)) {
						// If the given field has type T, it cannot be also in the list of parametrisations (e.g. T<T>).
						candidate = c;
						break;
					}
					// If the candidate T is referred from list of parametrisations (e.g. List<T>), it cannot be removed.
					// However field substitutions will take place.
					else if (isListedAsParametrisation(c.getCandidateClass(), fieldType)) {
						logger.debug("Candidate " + c.getClassName() + " is listed as parametrisation of "
						            + implClass.fullName() + "#" + fieldName + " and hence won't be removed.");
						c.unmarkForRemoval();
					}
				}

				if (candidate == null) {
					checkAnnotationReference(candidatesMap, implClass.fields().get(fieldName));

					continue;
				}

				// We have a candidate field to be replaced with a wrapped version. Report finding to summary file.
				writeSummary("\tReplacing field " + implClass.fullName() + "#" + fieldName + " with type "
				            + fieldType.name());
				modificationCount++;

				// The container class has to be deleted. Check that inner class has to be moved to it's parent.
				if (moveInnerClassToParentIfNecessary(outline, candidate)) {
					modificationCount++;
				}

				List<JClass> fieldTypeParametrisations = candidate.getFieldClass().getTypeParameters();

				// Create the new interface and collection classes using the specified interface and
				// collection classes (configuration) with an element type corresponding to
				// the element type from the collection present in the candidate class (narrowing).
				JClass collectionInterfaceClass = outline.getCodeModel().ref(this.collectionInterfaceClass)
				            .narrow(fieldTypeParametrisations);
				JClass collectionImplClass = outline.getCodeModel().ref(this.collectionImplClass)
				            .narrow(fieldTypeParametrisations);

				// Remove original field which refers to the inner class.
				JFieldVar originalImplField = implClass.fields().get(fieldName);
				implClass.removeField(originalImplField);

				if (isPluralFormApplicable(field)) {
					String oldFieldName = fieldName;

					// Taken from com.sun.tools.xjc.reader.xmlschema.ParticleBinder#makeJavaName():
					fieldName = JJavaName.getPluralForm(fieldName);
					field.getPropertyInfo().setName(false, fieldName);

					// Correct the @XmlType class-level annotation:
					for (JAnnotationUse annotation : implClass.annotations()) {
						String annotationClassName = annotation.getAnnotationClass().name();

						if (!annotationClassName.equals("XmlType")) {
							continue;
						}

						// FIXME: Pending for https://java.net/jira/browse/JAXB-884
						for (JAnnotationValue ann : ((JAnnotationArrayMember) annotation.getAnnotationMembers().get(
						            "propOrder")).annotations()) {
							// FIXME: There is no way to set the correct property name back to annotation.
							//if (oldFieldName.equals(getAnnotationStringValue(ann))) {
							//	break;
							//}
						}

						break;
					}
				}

				// Add new wrapped version of the field using the original field name.
				// GENERATED CODE: protected I<T> fieldName;
				JFieldVar implField = implClass.field(JMod.PROTECTED, collectionInterfaceClass, fieldName);

				// If instantiation is specified to be "early", add code for creating new instance of the collection class.
				if (instantiation == Instantiation.EARLY) {
					logger.debug("Applying EARLY instantiation...");
					// GENERATED CODE: ... fieldName = new C<T>();
					implField.init(JExpr._new(collectionImplClass));
				}

				// Annotate the new field with the @XmlElementWrapper annotation using the original field name.
				JAnnotationUse xmlElementWrapperAnnotation = implField.annotate(XmlElementWrapper.class);

				JExpression wrapperXmlName = getXmlElementMemberExpression(originalImplField, "name");
				if (wrapperXmlName != null) {
					xmlElementWrapperAnnotation.param("name", wrapperXmlName);
				}
				else {
					xmlElementWrapperAnnotation.param("name", originalImplField.name());
				}

				JExpression warpperXmlRequired = getXmlElementMemberExpression(originalImplField, "required");
				if (warpperXmlRequired != null) {
					xmlElementWrapperAnnotation.param("required", warpperXmlRequired);
				}

				// Namespace of the wrapper element
				JExpression wrapperXmlNamespace = getXmlElementMemberExpression(originalImplField, "namespace");
				if (wrapperXmlNamespace != null) {
					xmlElementWrapperAnnotation.param("namespace", wrapperXmlNamespace);
				}

				// Annotate the new field with the @XmlElement annotation using the field name from the wrapped type as name.
				JAnnotationUse xmlElementAnnotation = implField.annotate(XmlElement.class);

				JExpression xmlName = getXmlElementMemberExpression(candidate.getField(), "name");
				if (xmlName != null) {
					xmlElementAnnotation.param("name", xmlName);
				}
				else {
					xmlElementAnnotation.param("name", candidate.getFieldName());
				}

				// Namespace of the element itself
				JExpression xmlNamespace = getXmlElementMemberExpression(candidate.getField(), "namespace");
				if (xmlNamespace != null) {
					xmlElementAnnotation.param("namespace", xmlNamespace);
				}
				else if (wrapperXmlNamespace != null) {
					// FIXME: Only theoretical case, as it cannot happen that element does not have a namespace, but wrapper element has:
					xmlElementAnnotation.param("namespace", wrapperXmlNamespace);
				}

				if (candidate.isValueObjectDisabled() && candidate.getFieldParametrisationImpl() != null) {
					xmlElementAnnotation.param("type", candidate.getFieldParametrisationImpl());
				}

				// Custom java adapter
				JExpression xmlJavaTypeAdapter = getXmlJavaTypeAdapterMemberExpression(candidate.getField());

				if (xmlJavaTypeAdapter != null) {
					implField.annotate(XmlJavaTypeAdapter.class).param("value", xmlJavaTypeAdapter);
				}

				String fieldPublicName = field.getPropertyInfo().getName(true);

				JDefinedClass implementationInterface = null;

				for (Iterator<JClass> iter = implClass._implements(); iter.hasNext();) {
					JClass interfaceClass = iter.next();

					// If value class implements some JVM interface it is not considered as such interface cannot be modified:
					if (interfaceClass instanceof JDefinedClass
					            && deleteSettersGetters((JDefinedClass) interfaceClass, fieldPublicName)) {
						implementationInterface = (JDefinedClass) interfaceClass;
						break;
					}
				}

				// Find original getter and setter methods to remove.
				deleteSettersGetters(implClass, fieldPublicName);

				if (isPluralFormApplicable(field)) {
					fieldPublicName = JJavaName.getPluralForm(fieldPublicName);
					field.getPropertyInfo().setName(true, fieldPublicName);
				}

				// Add a new getter method returning the (wrapped) field added.
				// GENERATED CODE: public I<T> getFieldName() { ... return fieldName; }
				JMethod getterMethod = implClass.method(JMod.PUBLIC, collectionInterfaceClass, "get" + fieldPublicName);

				if (instantiation == Instantiation.LAZY) {
					logger.debug("Applying LAZY instantiation...");
					// GENERATED CODE: if (fieldName == null) fieldName = new C<T>();
					getterMethod.body()._if(JExpr.ref(fieldName).eq(JExpr._null()))._then()
					            .assign(JExpr.ref(fieldName), JExpr._new(collectionImplClass));
				}

				// GENERATED CODE: return "fieldName";
				getterMethod.body()._return(JExpr.ref(fieldName));

				// Add a new setter method:
				// GENERATED CODE: public void setFieldName(I<T> fieldName) { this.fieldName = fieldName; }
				JMethod setterMethod = implClass.method(JMod.PUBLIC, outline.getCodeModel().VOID, "set"
				            + fieldPublicName);

				setterMethod.body().assign(JExpr._this().ref(fieldName),
				            setterMethod.param(collectionInterfaceClass, fieldName));

				// Modify interface as well:
				if (implementationInterface != null) {
					writeSummary("\tCorrecting interface " + implementationInterface.fullName());

					implementationInterface.method(JMod.PUBLIC, collectionInterfaceClass, "get" + fieldPublicName);
					setterMethod = implementationInterface.method(JMod.PUBLIC, outline.getCodeModel().VOID, "set"
					            + fieldPublicName);
					setterMethod.param(collectionInterfaceClass, fieldName);
				}
			}
		}

		writeSummary("\t" + modificationCount + " modification(s) to original code.");
		writeSummary("");

		int deletionCount = 0;

		if (deleteCandidates) {
			deletionCount = deleteCandidates(outline, candidatesMap.values());
		}

		writeSummary("\t" + deletionCount + " deletion(s) from original code.");
		writeSummary("");

		logger.debug("Closing summary...");
		closeSummary();

		logger.debug("Done");

		return true;
	}

	/**
	 * If candidate class contains the inner class, which will be referred from collection, then this inner class has to
	 * be moved to top class. For example from<br>
	 * {@code TopClass -> ContainerClass (marked for removal) -> ElementClass}<br>
	 * we need to get<br>
	 * {@code TopClass (will have a collection) -> ElementClass}.<br>
	 * Also this move should be reflected on factory method names.
	 * 
	 * @param context
	 *            TODO
	 */
	private boolean moveInnerClassToParentIfNecessary(Outline outline, Candidate candidate) {
		// Skip basic parametrisations like "List<String>":
		if (candidate.getFieldParametrisationClass() == null) {
			return false;
		}

		JDefinedClass fieldParametrisationImpl = candidate.getFieldParametrisationImpl();

		if (candidate.getCandidateClass() != fieldParametrisationImpl.parentContainer()) {
			return false;
		}

		JDefinedClass fieldParametrisationClass = candidate.getFieldParametrisationClass();

		String oldFactoryMethodName = fieldParametrisationClass.outer().name() + fieldParametrisationClass.name();

		renameFactoryMethod(fieldParametrisationImpl._package()._getClass(FACTORY_CLASS_NAME), oldFactoryMethodName,
		            fieldParametrisationClass.name());

		moveClassLevelUp(outline, fieldParametrisationImpl);

		if (candidate.isValueObjectDisabled()) {
			renameFactoryMethod(fieldParametrisationClass._package()._getClass(FACTORY_CLASS_NAME),
			            oldFactoryMethodName, fieldParametrisationClass.name());

			moveClassLevelUp(outline, fieldParametrisationClass);
		}

		return true;
	}

	/**
	 * Locate the candidates classes for substitution/removal.
	 * 
	 * @return a map className -> Candidate
	 */
	private Collection<Candidate> findCandidateClasses(Outline outline) {
		Map<String, ClassOutline> interfaceImplementations = new HashMap<String, ClassOutline>();

		// Visit all classes to create a map "interfaceName -> ClassOutline".
		// This map is later used to resolve implementations from interfaces.
		for (ClassOutline classOutline : outline.getClasses()) {
			for (Iterator<JClass> iter = classOutline.implClass._implements(); iter.hasNext();) {
				JClass interfaceClass = iter.next();

				if (interfaceClass instanceof JDefinedClass) {
					// Don't care if interfaces collide: value classes have exactly one implementation.
					interfaceImplementations.put(interfaceClass.fullName(), classOutline);

					// FIXME: Generated model has one value class for each interface:
					//assert oldClass == null;
				}
			}
		}

		Collection<Candidate> candidates = new ArrayList<Candidate>();

		JClass collectionClass = outline.getCodeModel().ref(Collection.class);

		// Visit all classes created by JAXB processing to collect all potential wrapper classes to be removed:
		for (ClassOutline classOutline : outline.getClasses()) {
			JDefinedClass candidateClass = classOutline.implClass;

			// * The candidate class should not extend any other model class (as the total number of properties in this case will be more than 1)
			if (!isHiddenClass(candidateClass._extends())) {
				continue;
			}

			JFieldVar field = null;

			// * The candidate class should have exactly one property
			for (JFieldVar f : candidateClass.fields().values()) {
				if ((f.mods().getValue() & JMod.STATIC) == JMod.STATIC) {
					continue;
				}

				// If there are at least two non-static fields, we discard this candidate:
				if (field != null) {
					field = null;
					break;
				}

				field = f;
			}

			// "field" is null if there are no fields (or all fields are static) or there are more then two fields.
			// The only property should be a collection, hence it should be class:
			if (field == null || !(field.type() instanceof JClass)) {
				continue;
			}

			JClass fieldType = (JClass) field.type();

			// * The property should be a collection
			if (!collectionClass.isAssignableFrom(fieldType)) {
				continue;
			}

			List<JClass> fieldParametrisations = fieldType.getTypeParameters();

			// FIXME: All known collections have exactly one parametrisation type.
			assert fieldParametrisations.size() == 1;

			// * The only one parametrisation type should not be java.lang.Object (the case for <xs:any>)
			//   or java.io.Serializable / javax.xml.bind.JAXBElement (the case for <xs:complexType ... mixed="true">)
			if (!fieldParametrisations.get(0).erasure().fullName().equals(Object.class.getName()) && isTopClass(fieldParametrisations.get(0))) {
				continue;
			}

			JDefinedClass fieldParametrisationClass = null;
			JDefinedClass fieldParametrisationImpl = null;

			// Parametrisations like "List<String>" or "List<Serialazable>" are not considered.
			// They are substituted as is and do not require moving of classes.
			if (fieldParametrisations.get(0) instanceof JDefinedClass) {
				fieldParametrisationClass = (JDefinedClass) fieldParametrisations.get(0);

				ClassOutline fieldParametrisationClassOutline = interfaceImplementations.get(fieldParametrisationClass
				            .fullName());

				if (fieldParametrisationClassOutline != null) {
					assert fieldParametrisationClassOutline.ref == fieldParametrisationClass;

					fieldParametrisationImpl = fieldParametrisationClassOutline.implClass;
				}
				else {
					fieldParametrisationImpl = fieldParametrisationClass;
				}
			}

			// We have a candidate class
			Candidate candidate = new Candidate(candidateClass, field, fieldParametrisationClass,
			            fieldParametrisationImpl);
			candidates.add(candidate);

			logger.debug("Candidate found: " + candidate.getClassName() + " [private "
			            + candidate.getFieldClass().name() + " " + candidate.getFieldName() + "]");
		}

		return candidates;
	}

	/**
	 * Delete all candidate classes together with setter/getter methods and helper methods from
	 * <code>ObjectFactory</code>.
	 * 
	 * @return the number of deletions performed
	 */
	private int deleteCandidates(Outline outline, Collection<Candidate> candidates) {
		int deletionCount = 0;

		writeSummary("Deletions:");

		// Visit all candidate classes.
		for (Candidate candidate : candidates) {
			// Only consider candidates that are actually included...
			if (!isIncluded(candidate) || !candidate.isMarkedForRemoval()) {
				continue;
			}

			// Get the defined class for candidate class.
			JDefinedClass candidateClass = candidate.getCandidateClass();

			deleteFactoryMethod(candidate.getValueObjectFactoryClass(), candidateClass);
			deletionCount++;

			deleteClass(outline, candidateClass);
			deletionCount++;

			// Redo the same for interface:
			if (candidate.isValueObjectDisabled()) {
				deleteFactoryMethod(candidate.getObjectFactoryClass(), candidateClass);
				deletionCount++;

				for (Iterator<JClass> iter = candidateClass._implements(); iter.hasNext();) {
					JClass interfaceClass = iter.next();

					if (!isHiddenClass(interfaceClass)) {
						deleteClass(outline, (JDefinedClass) interfaceClass);
						deletionCount++;
					}
				}
			}
		}

		return deletionCount;
	}

	//
	// Model factory manipulation helpers.
	//

	/**
	 * Rename methods in factory class: {@code createABC() -> createAC()}.
	 */
	private void renameFactoryMethod(JDefinedClass factoryClass, String oldMethodName, String newMethodName) {
		for (JMethod method : factoryClass.methods()) {
			String methodName = method.name();

			if (!methodName.contains(oldMethodName)) {
				continue;
			}

			method.name(methodName.replace(oldMethodName, newMethodName));

			writeSummary("Renamed " + methodName + " -> " + method.name() + " in " + factoryClass.fullName());
		}
	}

	/**
	 * Remove method {@code ObjectFactory} that creates an object of a given {@code clazz}.
	 * 
	 * @return {@code true} is such method was successfully located and removed
	 */
	private boolean deleteFactoryMethod(JDefinedClass factoryClass, JDefinedClass clazz) {
		for (Iterator<JMethod> iter = factoryClass.methods().iterator(); iter.hasNext();) {
			JMethod method = iter.next();

			// Remove the methods:
			// * public T createT() { return new T(); }
			// * public JAXBElement<T> createT(T value) { return new JAXBElement<T>(QNAME, T.class, null, value); }
			// FIXME: The last case is mostly theoretical and live example is not known.
			if ((method.type() instanceof JDefinedClass && ((JDefinedClass) method.type()).isAssignableFrom(clazz))
			            || isListedAsParametrisation(clazz, method.type())) {
				writeSummary("\tRemoving method " + method.type().fullName() + " " + method.name() + "() from "
				            + factoryClass.fullName());
				iter.remove();

				return true;
			}
		}

		return false;
	}

	//
	// Model manipulation helpers.
	//

	/**
	 * Returns {@code true} if setter/getter with given public name was successfully removed from given class/interface.
	 */
	private boolean deleteSettersGetters(JDefinedClass clazz, String fieldPublicName) {
		boolean result = false;

		for (Iterator<JMethod> iter = clazz.methods().iterator(); iter.hasNext();) {
			JMethod m = iter.next();

			if (m.name().equals("set" + fieldPublicName) || m.name().equals("get" + fieldPublicName)) {
				iter.remove();
				result = true;
			}
		}

		return result;
	}

	/**
	 * Move the given class to his grandparent (either class or package).
	 */
	private void moveClassLevelUp(Outline outline, JDefinedClass clazz) {
		// Container can be a class or package:
		JClassContainer container = clazz.parentContainer().parentContainer();
		setPrivateField(clazz, "outer", container);

		// FIXME: Pending https://java.net/jira/browse/JAXB-957
		if (container.isClass()) {
			// Element class should be added as its container child:
			JDefinedClass parentClass = (JDefinedClass) container;

			writeSummary("\tMoving inner class " + clazz.fullName() + " to class " + parentClass.fullName());

			((Map<String, JDefinedClass>) getPrivateField(parentClass, "classes")).put(clazz.name(), clazz);
		}
		else {
			JPackage parentPackage = (JPackage) container;

			writeSummary("\tMoving inner class " + clazz.fullName() + " to package " + parentPackage.name());

			((Map<String, JDefinedClass>) getPrivateField(parentPackage, "classes")).put(clazz.name(), clazz);

			// In this scenario class should have "static" modifier reset:
			setPrivateField(clazz.mods(), "mods", clazz.mods().getValue() & ~JMod.STATIC);

			for (ClassOutline classOutline : outline.getClasses()) {
				if (classOutline.implClass == clazz) {
					XSComponent sc = classOutline.target.getSchemaComponent();

					// FIXME: Inner class is always a local declaration.
					assert (sc instanceof XSDeclaration && ((XSDeclaration) sc).isLocal());

					setPrivateField(sc, "anonymous", Boolean.FALSE);
				}
			}
		}
	}

	/**
	 * Remove the given class from it's parent class or package it is defined in.
	 */
	private void deleteClass(Outline outline, JDefinedClass clazz) {
		if (clazz.parentContainer().isClass()) {
			// The candidate class is an inner class. Remove the class from its parent class.
			JDefinedClass parentClass = (JDefinedClass) clazz.parentContainer();

			writeSummary("\tRemoving class " + clazz.fullName() + " from class " + parentClass.fullName());

			for (Iterator<JDefinedClass> iter = parentClass.classes(); iter.hasNext();) {
				if (iter.next().equals(clazz)) {
					iter.remove();
					break;
				}
			}
		}
		else {
			// The candidate class is in a package. Remove the class from the package.
			JPackage parentPackage = (JPackage) clazz.parentContainer();

			writeSummary("\tRemoving class " + clazz.fullName() + " from package " + parentPackage.name());

			parentPackage.remove(clazz);

			// And also remove the class from model.
			for (Iterator<? extends ClassOutline> iter = outline.getClasses().iterator(); iter.hasNext();) {
				ClassOutline classOutline = iter.next();
				if (classOutline.implClass == clazz) {
					outline.getModel().beans().remove(classOutline.target);
					iter.remove();
				}
			}
		}
	}

	/**
	 * Returns {@code true} if given class is a top of the class hierarchy like {@link Object} or {@link Serializable}
	 * or {@link JAXBElement}, or it is a customized super-class for all model beans. Basic JVM classes like
	 * {@link String} or {@link Integer} are not treated as top-classes: for them this method returns {@code false}.
	 */
	private static boolean isTopClass(JClass clazz) {
		// We need a basis for narrowed class:
		String className = clazz.erasure().fullName();

		if ((className.equals(Object.class.getName()) || className.equals(Serializable.class.getName()) || className
		            .equals(JAXBElement.class.getName()))) {
			return true;
		}

		// See also https://java.net/jira/browse/JAXB-958
		return clazz instanceof JDefinedClass && ((JDefinedClass) clazz).isHidden();
	}

	/**
	 * Returns {@code true} if given class is hidden, that is not generated & saved by XJC. These are for example
	 * instances of {@link JCodeModel.JReferencedClass} (JVM-wide classes) or instances of {@link JDefinedClass} with
	 * hidden flag (customized super-class or super-interface).
	 */
	static boolean isHiddenClass(JClass clazz) {
		// See also https://java.net/jira/browse/JAXB-958
		return !(clazz instanceof JDefinedClass) || ((JDefinedClass) clazz).isHidden();
	}

	/**
	 * Returns <code>true</code> of the given <code>type</code> is {@link JClass} and contains <code>classToCheck</code>
	 * in the list of parametrisations.
	 */
	private static boolean isListedAsParametrisation(JClass classToCheck, JType type) {
		return type instanceof JClass && ((JClass) type).getTypeParameters().contains(classToCheck);
	}

	/**
	 * Apply the plural form if there are no customizations. Assuming that customization is correct as may define the
	 * plural form in more correct way, e.g. "fieldsOfScience" instead of "fieldOfSciences".
	 */
	private static boolean isPluralFormApplicable(FieldOutline field) {
		// FIXME: It looks like customizations are always empty:
		return applyPluralForm && field.getPropertyInfo().getCustomizations().isEmpty();
	}

	/**
	 * Returns the string value of passed argument.
	 */
	private static final String toString(JGenerable generable) {
		// There is hardly any clean universal way to get the value from e.g. JExpression except of serializing it.
		// Compare JStringLiteral and JExp#dotclass().
		Writer w = new StringWriter();

		generable.generate(new JFormatter(w));

		// FIXME: Hopefully nobody will put quotes into annotation member value.
		return w.toString().replace("\"", "");
	}

	//
	// Annotation helpers.
	//

	/**
	 * For the given annotatable check that all annotations (and all annotations within annotations recursively) do not
	 * refer any candidate for removal.
	 */
	private void checkAnnotationReference(Map<String, Candidate> candidatesMap, JAnnotatable annotatable) {
		for (JAnnotationUse annotation : annotatable.annotations()) {
			String annotationClassName = annotation.getAnnotationClass().name();

			if (annotationClassName.equals("XmlElementRefs") || annotationClassName.equals("XmlElements")) {
				checkAnnotationReference(candidatesMap,
				            (JAnnotationArrayMember) annotation.getAnnotationMembers().get("value"));

				continue;
			}

			// FIXME: Dirty workaround for http://java.net/jira/browse/JAXB-784:
			if (!(annotationClassName.equals("XmlElementRef") || annotationClassName.equals("XmlElement"))) {
				continue;
			}

			JExpression type = getAnnotationMemberExpression(annotation, "type");

			if (type == null) {
				// Can be the case for @XmlElement(name = "publication-reference", namespace = "http://mycompany.org/exchange")
				// or any other annotation without "type" 
				continue;
			}

			String typeClassName = toString(type).replace(".class", "");

			Candidate candidate = candidatesMap.get(typeClassName);

			if (candidate != null) {
				logger.debug("Candidate " + candidate.getClassName()
				            + " is used in XmlElements/XmlElementRef and hence won't be removed.");
				candidate.unmarkForRemoval();
			}
		}
	}

	/**
	 * Returns the value of the given annotation member of {@link XmlElement} annotation for the given field.
	 */
	private static JExpression getXmlElementMemberExpression(JVar field, String annotationMember) {
		return getAnnotationMemberExpression(field, "XmlElement", annotationMember);
	}

	/**
	 * Returns the value of the "value" member of {@link XmlJavaTypeAdapter} annotation for the given field.
	 */
	private static JExpression getXmlJavaTypeAdapterMemberExpression(JVar field) {
		return getAnnotationMemberExpression(field, "XmlJavaTypeAdapter", "value");
	}

	/**
	 * Returns the value of the given annotation member of given annotation for the given field.
	 */
	private static JExpression getAnnotationMemberExpression(JVar field, String annotationClassName,
	            String annotationMember) {
		for (JAnnotationUse annotation : field.annotations()) {
			if (annotation.getAnnotationClass().name().equals(annotationClassName)) {
				return getAnnotationMemberExpression(annotation, annotationMember);
			}
		}

		return null;
	}

	/**
	 * Returns the string value of annotation element. For example, for annotation
	 * <code>@XmlElementRef(name = "last-name", namespace = "http://mycompany.org/exchange", type = JAXBElement.class)</code>
	 * for member <code>name</code> the value <code>last-name</code> will be returned.
	 */
	private static final JExpression getAnnotationMemberExpression(JAnnotationUse annotation, String annotationMember) {
		JAnnotationValue annotationValue = annotation.getAnnotationMembers().get(annotationMember);

		if (annotationValue == null) {
			return null;
		}

		// FIXME: Pending for https://java.net/jira/browse/JAXB-878
		return (JExpression) getPrivateField(annotationValue, "value");
	}

	//
	// Reflection helpers.
	//

	private static void setPrivateField(Object obj, String fieldName, Object newValue) {
		setPrivateField(obj, obj.getClass(), fieldName, newValue);
	}

	/**
	 * Set the {@code newValue} to private field {@code fieldName} of given object {@code obj}.
	 */
	private static void setPrivateField(Object obj, Class<?> clazz, String fieldName, Object newValue) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, newValue);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == Object.class) {
				// Field is really not found:
				throw new RuntimeException(e);
			}

			// Try super class:
			setPrivateField(obj, clazz.getSuperclass(), fieldName, newValue);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of private field {@code fieldName} of given object {@code obj}.
	 */
	private static Object getPrivateField(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	//
	// Includes / excludes support.
	//

	private boolean hasIncludes() {
		return include != null;
	}

	private boolean hasExcludes() {
		return exclude != null;
	}

	private boolean isIncluded(Candidate candidate) {
		//
		// A candidate is included if, ...
		// 1. No includes and no excludes have been specified
		// 2. Includes have been specified and candidate is included, and no excludes have been specified.
		// 3. No includes have been specified and excludes have been specified and candidate is not in excludes.
		// 4. Both includes and excludes have been specified and candidate is in includes and not in excludes.
		//
		if (!hasIncludes() && !hasExcludes()) {
			return true; // [+] (default)
		}
		else if (hasIncludes() && !hasExcludes()) {
			return include.contains(candidate.getClassName()); // [+/-] (included)
		}
		else if (!hasIncludes() && hasExcludes()) {
			return !exclude.contains(candidate.getClassName()); // [+/-] (excluded)
		}
		else {
			return include.contains(candidate.getClassName()) && !exclude.contains(candidate.getClassName()); // [+/-] (override)
		}
	}

	private String getIncludeOrExcludeReason() {
		if (!hasIncludes() && !hasExcludes()) {
			return "(default)"; // [+] (default)
		}
		else if (hasIncludes() && !hasExcludes()) {
			return "(included)";
		}
		else if (!hasIncludes() && hasExcludes()) {
			return "(excluded)";
		}
		else {
			return "(override)";
		}
	}

	/**
	 * Read all candidates from a given file into the given set.
	 */
	private static void readCandidates(File file, Set<String> candidates) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line;

		while ((line = input.readLine()) != null) {
			line = line.trim();

			// Lines starting with # are considered comments.
			if (!line.startsWith("#")) {
				candidates.add(line);
			}
		}

		input.close();
	}

	//
	// Logging helpers
	//

	private void writeSummary(String s) {
		if (summary != null) {
			summary.println(s);
		}

		logger.info(s);
	}

	private void closeSummary() {
		if (summary != null) {
			summary.close();
		}
	}

	/**
	 * Types of collection instantiation modes.
	 */
	private enum Instantiation {
		EARLY, LAZY
	}

	/**
	 * Describes the collection container class -- a candidate for removal.
	 */
	private static class Candidate {
		private JDefinedClass       objectFactoryClass;

		private final JDefinedClass valueObjectFactoryClass;

		private final JDefinedClass candidateClass;

		private final JFieldVar     field;

		private final JDefinedClass fieldParametrisationClass;

		private final JDefinedClass fieldParametrisationImpl;

		private boolean             markedForRemoval = true;

		Candidate(JDefinedClass candidateClass, JFieldVar field, JDefinedClass fieldParametrizationClass,
		            JDefinedClass fieldParametrisationImpl) {
			// If class has a non-hidden interface, then there is object factory in another package.
			for (Iterator<JClass> iter = candidateClass._implements(); iter.hasNext();) {
				JClass interfaceClass = iter.next();

				if (!isHiddenClass(interfaceClass)) {
					objectFactoryClass = interfaceClass._package()._getClass(FACTORY_CLASS_NAME);

					if (objectFactoryClass != null) {
						break;
					}
				}
			}

			this.valueObjectFactoryClass = candidateClass._package()._getClass(FACTORY_CLASS_NAME);
			this.candidateClass = candidateClass;
			this.field = field;
			this.fieldParametrisationClass = fieldParametrizationClass;
			this.fieldParametrisationImpl = fieldParametrisationImpl;

			assert objectFactoryClass != valueObjectFactoryClass;
		}

		/**
		 * Object Factory class for interface classes. It's usually located in {@code impl.} subpackage relative to
		 * {@code valueObjectFactoryClass} package. May be {@code null}.
		 */

		public JDefinedClass getObjectFactoryClass() {
			return objectFactoryClass;
		}

		/**
		 * Object Factory class for value (implementation) classes. Is not {@code null}.
		 */
		public JDefinedClass getValueObjectFactoryClass() {
			return valueObjectFactoryClass;
		}

		/**
		 * Container class
		 */
		public JDefinedClass getCandidateClass() {
			return candidateClass;
		}

		/**
		 * Container class name
		 */
		public String getClassName() {
			return candidateClass.fullName();
		}

		/**
		 * The only field in container class.
		 */
		public JFieldVar getField() {
			return field;
		}

		/**
		 * The name of the only field in container class.
		 */
		public String getFieldName() {
			return field.name();
		}

		/**
		 * The class of the only field in container class.
		 */
		public JClass getFieldClass() {
			return (JClass) field.type();
		}

		/**
		 * The only parametrisation class of the field. In case of basic parametrisation like {@link List<String>} this
		 * property is {@code null}.
		 */
		public JDefinedClass getFieldParametrisationClass() {
			return fieldParametrisationClass;
		}

		/**
		 * If {@link #getFieldParametrisationClass()} is an interface, then this holds that same value. Otherwise it
		 * holds the implementation (value object) of {@link #getFieldParametrisationClass()}. In case of basic
		 * parametrisation like {@code List<String>} this property is {@code null}.
		 */
		public JDefinedClass getFieldParametrisationImpl() {
			return fieldParametrisationImpl;
		}

		/**
		 * Returns {@code true} if the setting {@code <jaxb:globalBindings generateValueClass="false">} is active.
		 */
		public boolean isValueObjectDisabled() {
			return objectFactoryClass != null;
		}

		/**
		 * This flag controls if given candidate has green light to be removed.
		 */
		public boolean isMarkedForRemoval() {
			return markedForRemoval;
		}

		/**
		 * Signal that this candidate should not be removed from model on some reason.
		 */
		public void unmarkForRemoval() {
			this.markedForRemoval = false;
		}

		@Override
		public String toString() {
			return "Candidate[" + getClassName() + "]";
		}
	}
}
