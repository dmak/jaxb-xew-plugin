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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
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
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
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
	private Set<String>         include                                = null;                                             // list of classes for inclusion
	private File                excludeFile                            = null;
	private Set<String>         exclude                                = null;                                             // list of classes for exclusion
	private File                summaryFile                            = null;
	private PrintWriter         summary                                = null;
	private Class<?>            collectionInterfaceClass               = java.util.List.class;
	private Class<?>            collectionImplClass                    = java.util.ArrayList.class;
	private Instantiation       instantiation                          = Instantiation.EARLY;
	private boolean             deleteCandidates                       = false;

	// This is currently an experimental and not properly working feature, so keep this field set to false.
	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean				applyPluralForm				= Ring.get(BIGlobalBinding.class).isSimpleMode();
	private static boolean      applyPluralForm                        = false;

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
		writeSummary("  JAXB version : " + Options.getBuildID());
		writeSummary("  Include file : " + (includeFile == null ? "<none>" : includeFile));
		writeSummary("  Exclude file : " + (excludeFile == null ? "<none>" : excludeFile));
		writeSummary("  Summary file : " + (summaryFile == null ? "<none>" : summaryFile));
		writeSummary("  Instantiate  : " + instantiation);
		writeSummary("  Collection   : " + collectionImplClass);
		writeSummary("  Interface    : " + collectionInterfaceClass);
		writeSummary("  Delete       : " + deleteCandidates);
		writeSummary("");

		// Visit all classes generated by JAXB and find candidate classes for transformation.
		Map<String, Candidate> candidates = findCandidateClasses(outline);

		// Write information on candidate classes to summary file.
		writeSummary("Candidates:");

		for (Iterator<Map.Entry<String, Candidate>> iter = candidates.entrySet().iterator(); iter.hasNext();) {
			Candidate candidate = iter.next().getValue();

			if (isIncluded(candidate)) {
				writeSummary("\t[" + (candidate.isMarkedForRemoval() ? "!" : "+") + "]: " + getIncludeOrExcludeReason()
				            + ":\t" + candidate.getClassName());
			}
			else {
				writeSummary("\t[-]: " + getIncludeOrExcludeReason() + ":\t" + candidate.getClassName());
				iter.remove();
			}
		}

		writeSummary("\t" + candidates.size() + " candidate(s) being considered.");
		writeSummary("");

		writeSummary("Modifications:");

		int modificationCount = 0;

		// Visit all classes again to check if the candidate is not eligible for removal:
		// * If there are classes that extend the candidate
		// * If there are class fields, that refer the candidate by e.g. @XmlElementRef annotation
		for (ClassOutline outlineClass : outline.getClasses()) {
			// Get the implementation class for the current class.
			JDefinedClass implementationClass = outlineClass.implClass;

			cancelRemovalIfNecessary(candidates, implementationClass);

			// Visit all fields in this class.
			for (FieldOutline field : outlineClass.getDeclaredFields()) {
				// Extract the field name and type of the current field.
				String fieldName = field.getPropertyInfo().getName(false);
				String typeName = field.getRawType().fullName();

				Candidate candidate = null;

				for (Candidate c : candidates.values()) {
					// If the given field has type T of the candidate, it cannot be also in the list of parametrisations (e.g. T<T>),
					// so we stop the cycle:
					if (c.getClassName().equals(typeName)) {
						candidate = c;
						break;
					}
					// If the candidate T is referred from list of parametrisations (e.g. List<T>), it cannot be removed.
					// However field substitutions will take place.
					else if (isListedAsParametrisation(c.getModelClass(), field.getRawType())) {
						logger.debug("Candidate " + c.getClassName() + " is listed as parametrisation of "
						            + implementationClass.fullName() + "#" + fieldName + " and hence won't be removed.");
						c.setMarkedForRemoval(false);
					}
				}

				if (candidate == null) {
					continue;
				}

				// We have a candidate field to be replaced with a wrapped version. Report finding to summary file.
				writeSummary("\tReplacing field " + outlineClass.target.getName() + "#" + fieldName + " with type "
				            + typeName);
				modificationCount++;

				// The container class has to be deleted. Check that inner class has to be moved to it's parent.
				if (moveInnerClassToParentIfNecessary(outline, candidate)) {
					modificationCount++;
				}

				List<JClass> itemNarrowing = candidate.getFieldClass().getTypeParameters();

				// Create the new interface and collection classes using the specified interface and
				// collection classes (configuration) with an element type corresponding to
				// the element type from the collection present in the candidate class (narrowing).
				JClass collectionInterfaceClass = outline.getCodeModel().ref(this.collectionInterfaceClass)
				            .narrow(itemNarrowing);
				JClass collectionImplClass = outline.getCodeModel().ref(this.collectionImplClass).narrow(itemNarrowing);

				// Remove original field which refers to the inner class.
				JFieldVar originalImplField = implementationClass.fields().get(fieldName);
				implementationClass.removeField(originalImplField);

				if (isPluralFormApplicable(field)) {
					String oldFieldName = fieldName;

					// Taken from com.sun.tools.xjc.reader.xmlschema.ParticleBinder#makeJavaName():
					fieldName = JJavaName.getPluralForm(fieldName);
					field.getPropertyInfo().setName(false, fieldName);

					// Correct the @XmlType class-level annotation:
					for (JAnnotationUse annotation : implementationClass.annotations()) {
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
				JFieldVar implField = implementationClass.field(JMod.PROTECTED, collectionInterfaceClass, fieldName);

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

				String fieldPublicName = field.getPropertyInfo().getName(true);

				// Find original getter and setter methods to remove.
				for (Iterator<JMethod> iter = implementationClass.methods().iterator(); iter.hasNext();) {
					JMethod m = iter.next();

					if (m.name().equals("set" + fieldPublicName) || m.name().equals("get" + fieldPublicName)) {
						iter.remove();
					}
				}

				if (isPluralFormApplicable(field)) {
					fieldPublicName = JJavaName.getPluralForm(fieldPublicName);
					field.getPropertyInfo().setName(true, fieldPublicName);
				}

				// Add a new getter method returning the (wrapped) field added.
				// GENERATED CODE: public I<T> getFieldName() { ... return fieldName; }
				JMethod getterMethod = implementationClass.method(JMod.PUBLIC, collectionInterfaceClass, "get"
				            + fieldPublicName);

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
				JMethod setterMethod = implementationClass.method(JMod.PUBLIC, outline.getCodeModel().VOID, "set"
				            + fieldPublicName);

				setterMethod.body().assign(JExpr._this().ref(fieldName),
				            setterMethod.param(collectionInterfaceClass, fieldName));
			}
		}

		writeSummary("\t" + modificationCount + " modification(s) to original code.");
		writeSummary("");

		int deletionCount = 0;

		if (deleteCandidates) {
			deletionCount = deleteCandidates(outline, candidates);
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
	 */
	private boolean moveInnerClassToParentIfNecessary(Outline outline, Candidate candidate) {
		// Skip basic parametrisations like "List<String>":
		if (!(candidate.getFieldParametrisationClass() instanceof JDefinedClass)) {
			return false;
		}

		JDefinedClass fieldParametrizationClass = (JDefinedClass) candidate.getFieldParametrisationClass();

		if (!candidate.getModelClass().equals(fieldParametrizationClass.outer())) {
			return false;
		}

		String oldFactoryMethodName = fieldParametrizationClass.outer().name() + fieldParametrizationClass.name();
		JDefinedClass factoryClass = candidate.getFactoryClass();

		// Rename methods in factory class: createABC() -> createAC()
		for (JMethod method : factoryClass.methods()) {
			String methodName = method.name();

			if (!methodName.contains(oldFactoryMethodName)) {
				continue;
			}

			method.name(methodName.replace(oldFactoryMethodName, fieldParametrizationClass.name()));
		}

		// Container can be a class or package:
		JClassContainer container = candidate.getModelClass().parentContainer();
		setPrivateField(fieldParametrizationClass, "outer", container);

		// FIXME: Pending https://java.net/jira/browse/JAXB-957
		// Element class should be added as its container child:
		if (container instanceof JDefinedClass) {
			JDefinedClass parentClass = (JDefinedClass) container;

			writeSummary("\tMoving inner class " + fieldParametrizationClass.fullName() + " to class "
			            + parentClass.fullName());

			((Map<String, JDefinedClass>) getPrivateField(parentClass, "classes")).put(
			            fieldParametrizationClass.name(), fieldParametrizationClass);
		}
		else {
			JPackage parentPackage = (JPackage) container;

			writeSummary("\tMoving inner class " + fieldParametrizationClass.fullName() + " to package "
			            + parentPackage.name());

			((Map<String, JDefinedClass>) getPrivateField(parentPackage, "classes")).put(
			            fieldParametrizationClass.name(), fieldParametrizationClass);

			// In this scenario class should have "static" modifier reset:
			setPrivateField(fieldParametrizationClass.mods(), "mods", fieldParametrizationClass.mods().getValue()
			            & ~JMod.STATIC);

			for (ClassOutline classOutline : outline.getClasses()) {
				if (classOutline.implClass == fieldParametrizationClass) {
					XSComponent sc = classOutline.target.getSchemaComponent();

					// FIXME: Inner class is always a local declaration.
					assert (sc instanceof XSDeclaration && ((XSDeclaration) sc).isLocal());

					setPrivateField(sc, "anonymous", Boolean.FALSE);
				}
			}
		}

		return true;
	}

	/**
	 * This method checks if the removal of the candidate is actual eligible. In particular:
	 * <ol>
	 * <li>The candidate class should not the the parent of the given class.
	 * <li>The candidate class should not be referred in any annotation for any field of the given class.
	 * </ol>
	 */
	private void cancelRemovalIfNecessary(Map<String, Candidate> candidates, JDefinedClass implementationClass) {
		// We cannot remove candidates that have parent classes, but we can still substitute them:
		Candidate candidate = candidates.get(implementationClass._extends().fullName());

		if (candidate != null) {
			logger.debug("Candidate " + candidate.getClassName() + " is a parent of " + implementationClass.name()
			            + " and hence won't be removed.");
			candidate.setMarkedForRemoval(false);
		}

		for (JFieldVar field : implementationClass.fields().values()) {
			checkAnnotationReference(candidates, field);
		}
	}

	/**
	 * For the given annotatable check that all annotations (and all annotations within annotations recursively) do not
	 * refer any candidate for removal.
	 */
	private void checkAnnotationReference(Map<String, Candidate> candidates, JAnnotatable annotatable) {
		for (JAnnotationUse annotation : annotatable.annotations()) {
			String annotationClassName = annotation.getAnnotationClass().name();

			if (annotationClassName.equals("XmlElementRefs") || annotationClassName.equals("XmlElements")) {
				checkAnnotationReference(candidates,
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

			Candidate candidate = candidates.get(typeClassName);

			if (candidate != null) {
				logger.debug("Candidate " + candidate.getClassName()
				            + " is used in XmlElements/XmlElementRef and hence won't be removed.");
				candidate.setMarkedForRemoval(false);
			}
		}
	}

	/**
	 * Delete all candidate classes together with setter/getter methods and helper methods from
	 * <code>ObjectFactory</code>.
	 * 
	 * @return the number of deletions performed
	 */
	private int deleteCandidates(Outline outline, Map<String, Candidate> candidates) {
		int deletionCount = 0;

		writeSummary("Deletions:");

		// Visit all candidate classes.
		for (Candidate candidate : candidates.values()) {
			// Only consider candidates that are actually included...
			if (!isIncluded(candidate) || !candidate.isMarkedForRemoval()) {
				continue;
			}

			// Get the defined class for candidate class.
			JDefinedClass candidateClass = candidate.getModelClass();
			JDefinedClass factoryClass = candidate.getFactoryClass();

			// Remove methods referencing the candidate class from the ObjectFactory.
			for (Iterator<JMethod> iter = factoryClass.methods().iterator(); iter.hasNext();) {
				JMethod method = iter.next();

				// Remove the methods:
				// * public T createT() { return new T(); }
				// * public JAXBElement<T> createT(T value) { return new JAXBElement<T>(QNAME, T.class, null, value); }
				// FIXME: The last case is mostly theoretical and live example is not known.
				if (method.type() == candidateClass || isListedAsParametrisation(candidateClass, method.type())) {
					writeSummary("\tRemoving method " + method.type().fullName() + " " + method.name() + "() from "
					            + factoryClass.fullName());
					iter.remove();
					deletionCount++;
				}
			}

			// Remove the candidate from the class or package it is defined in.
			if (candidateClass.parentContainer().isClass()) {
				// The candidate class is an inner class. Remove the class from its parent class.
				JDefinedClass parent = (JDefinedClass) candidateClass.parentContainer();

				writeSummary("\tRemoving class " + candidateClass.fullName() + " from class " + parent.fullName());

				for (Iterator<JDefinedClass> iter = parent.classes(); iter.hasNext();) {
					if (iter.next().equals(candidateClass)) {
						iter.remove();
						break;
					}
				}

				deletionCount++;
			}
			else {
				// The candidate class is in a package. Remove the class from the package.
				writeSummary("\tRemoving class " + candidateClass.fullName() + " from package "
				            + candidateClass._package().name());
				candidateClass._package().remove(candidateClass);

				// And also remove the class from model.
				for (Iterator<? extends ClassOutline> iter = outline.getClasses().iterator(); iter.hasNext();) {
					ClassOutline classOutline = iter.next();
					if (classOutline.implClass == candidateClass) {
						outline.getModel().beans().remove(classOutline.target);
						iter.remove();
					}
				}

				deletionCount++;
			}
		}

		return deletionCount;
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

	private boolean hasIncludes() {
		return include != null;
	}

	private boolean hasExcludes() {
		return exclude != null;
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

	/**
	 * Locate the candidates classes for substitution/removal.
	 * 
	 * @return a map className -> Candidate
	 */
	private Map<String, Candidate> findCandidateClasses(Outline outline) {
		Map<String, Candidate> candidates = new HashMap<String, Candidate>();

		// Visit all classes created by JAXB processing to collect all potential wrapper classes to be removed:
		for (CClassInfo classInfo : outline.getModel().beans().values()) {
			String className = classInfo.fullName();

			// * The candidate class must have exactly one property
			// * The candidate class should not extend any other class (as the total number of properties in this case will be more than 1)
			if (classInfo.getProperties().size() != 1 || classInfo.getBaseClass() != null) {
				continue;
			}

			CPropertyInfo property = classInfo.getProperties().get(0);

			// * The property must be a collection
			if (!property.isCollection()) {
				continue;
			}

			JDefinedClass modelClass = outline.getClazz(classInfo).implClass;

			JFieldVar field = modelClass.fields().get(property.getName(false));

			// A given property is a collection, hence it is a class:
			List<JClass> fieldParametrisations = ((JClass) field.type()).getTypeParameters();

			// FIXME: All known collections have exactly one parametrisation type.
			assert fieldParametrisations.size() == 1;

			// * The only one parametrisation type should not be java.lang.Object (the case for <xs:any>)
			//   or java.io.Serializable / javax.xml.bind.JAXBElement (the case for <xs:complexType ... mixed="true">)
			JClass fieldParametrisationClass = fieldParametrisations.get(0);
			// We need a basis for narrowed class:
			String fieldParametrisationClassName = fieldParametrisationClass.erasure().fullName();

			if (fieldParametrisationClassName.equals(Object.class.getName())
			            || fieldParametrisationClassName.equals(Serializable.class.getName())
			            || fieldParametrisationClassName.equals(JAXBElement.class.getName())) {
				continue;
			}

			// We have a candidate class
			Candidate candidate = new Candidate(modelClass, field, fieldParametrisationClass);
			candidates.put(className, candidate);

			logger.debug("Candidate found: " + candidate.getClassName() + " [private "
			            + candidate.getFieldClass().name() + " " + candidate.getFieldName() + "]");
		}

		return candidates;
	}

	/**
	 * Returns the value of the given annotation member of {@link XmlElement} annotation for the given field.
	 */
	private static JExpression getXmlElementMemberExpression(JVar field, String annotationMember) {
		for (JAnnotationUse annotation : field.annotations()) {
			if (annotation.getAnnotationClass().name().equals("XmlElement")) {
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

	/**
	 * Returns <code>true</code> of the given <code>type</code> is {@link JClass} and contains <code>classToCheck</code>
	 * in the list of parametrisations.
	 */
	private boolean isListedAsParametrisation(JClass classToCheck, JType type) {
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
		private JDefinedClass       modelClass;
		private JFieldVar           field;
		private JClass              fieldParametrisationClass;
		private boolean             markedForRemoval   = true;

		private static final String FACTORY_CLASS_NAME = "ObjectFactory";

		public Candidate(JDefinedClass modelClass, JFieldVar field, JClass fieldParametrizationClass) {
			this.modelClass = modelClass;
			this.field = field;
			this.fieldParametrisationClass = fieldParametrizationClass;
		}

		/**
		 * Container class
		 */
		public JDefinedClass getModelClass() {
			return modelClass;
		}

		/**
		 * Container class name
		 */
		public String getClassName() {
			return modelClass.fullName();
		}

		/**
		 * Return the class which corresponds to "ObjectFactory" class in the model.
		 */
		public JDefinedClass getFactoryClass() {
			return modelClass._package()._getClass(FACTORY_CLASS_NAME);
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
		 * The only parametrisation class of the field.
		 */
		public JClass getFieldParametrisationClass() {
			return fieldParametrisationClass;
		}

		/**
		 * This flag controls if given candidate has green light to be removed.
		 */
		public boolean isMarkedForRemoval() {
			return markedForRemoval;
		}

		public void setMarkedForRemoval(boolean markedForRemoval) {
			this.markedForRemoval = markedForRemoval;
		}
	}
}
