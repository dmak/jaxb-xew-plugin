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

import static com.sun.tools.xjc.addon.xew.CommonUtils.addAnnotation;
import static com.sun.tools.xjc.addon.xew.CommonUtils.generableToString;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getAnnotation;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getAnnotationMemberExpression;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getPrivateField;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getXsdDeclaration;
import static com.sun.tools.xjc.addon.xew.CommonUtils.isHiddenClass;
import static com.sun.tools.xjc.addon.xew.CommonUtils.isListedAsParametrisation;
import static com.sun.tools.xjc.addon.xew.CommonUtils.removeAnnotation;
import static com.sun.tools.xjc.addon.xew.CommonUtils.setPrivateField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

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
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.xml.bind.v2.model.core.PropertyKind;
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
	private static final String OPTION_NAME_INCLUDE                    = "-" + PLUGIN_NAME + ":include";
	private static final String OPTION_NAME_EXCLUDE                    = "-" + PLUGIN_NAME + ":exclude";
	private static final String OPTION_NAME_SUMMARY                    = "-" + PLUGIN_NAME + ":summary";
	private static final String OPTION_NAME_COLLECTION                 = "-" + PLUGIN_NAME + ":collection";
	private static final String OPTION_NAME_INSTANTIATE                = "-" + PLUGIN_NAME + ":instantiate";
	private static final String OPTION_NAME_APPLY_PLURAL_FORM          = "-" + PLUGIN_NAME + ":plural";

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
	private Instantiation       instantiation                          = Instantiation.EARLY;
	private boolean             deleteCandidates                       = false;

	private JClass              xmlElementDeclModelClass;

	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean             applyPluralForm                        = Ring.get(BIGlobalBinding.class).isSimpleMode();
	// This is currently an experimental and not properly working feature, so keep this field set to false.
	private boolean             applyPluralForm                        = false;

	private static final String FACTORY_CLASS_NAME                     = "ObjectFactory";

	static final String         COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY = "org.apache.commons.logging.simplelog.defaultlog";

	private Log                 logger;

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

	/**
	 * Parse argument at a given index. Option value may go within the same argument, or as following argument.
	 * 
	 * @param args
	 *            list of arguments
	 * @param index
	 *            current index
	 * @param optionName
	 *            the option to parse
	 * @param value
	 *            parser option value
	 * @return number of arguments processed
	 */
	private int parseArgument(String[] args, int index, String optionName, StringBuilder value) {
		int recognized = 0;
		String arg = args[index];

		if (arg.startsWith(optionName)) {
			recognized++;

			if (arg.length() > optionName.length()) {
				value.append(arg.substring(optionName.length()).trim());
			}
			else {
				value.append(args[index + 1]);
				recognized++;
			}
		}

		return recognized;
	}

	@Override
	public int parseArgument(Options opts, String[] args, int i) throws BadCommandLineException, IOException {
		initLoggerIfNecessary(opts);

		int recognized = 0;

		String arg = args[i];
		logger.debug("Argument[" + i + "] = " + arg);
		StringBuilder value = new StringBuilder();

		if (arg.startsWith(OPTION_NAME_DELETE)) {
			recognized++;
			deleteCandidates = true;
		}
		else if ((recognized = parseArgument(args, i, OPTION_NAME_INCLUDE, value)) > 0) {
			include = new HashSet<String>();
			includeFile = new File(value.toString());

			readCandidates(includeFile, include);
		}
		else if ((recognized = parseArgument(args, i, OPTION_NAME_EXCLUDE, value)) > 0) {
			exclude = new HashSet<String>();
			excludeFile = new File(value.toString());

			readCandidates(excludeFile, exclude);
		}
		else if ((recognized = parseArgument(args, i, OPTION_NAME_SUMMARY, value)) > 0) {
			summaryFile = new File(value.toString());
			summary = new PrintWriter(new FileOutputStream(summaryFile));
		}
		else if ((recognized = parseArgument(args, i, OPTION_NAME_COLLECTION, value)) > 0) {
			String ccn = value.toString();

			try {
				collectionImplClass = Class.forName(ccn);
			}
			catch (ClassNotFoundException e) {
				throw new BadCommandLineException(OPTION_NAME_COLLECTION + " " + ccn + ": Class not found.", e);
			}
		}
		else if ((recognized = parseArgument(args, i, OPTION_NAME_INSTANTIATE, value)) > 0) {
			instantiation = Instantiation.valueOf(value.toString().toUpperCase());
		}
		else if (arg.equals(OPTION_NAME_APPLY_PLURAL_FORM)) {
			recognized++;
			applyPluralForm = true;
		}
		else if (arg.startsWith("-" + PLUGIN_NAME + ":")) {
			throw new BadCommandLineException("Invalid argument " + arg);
		}

		return recognized;
	}

	@Override
	public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
		JCodeModel codeModel = outline.getCodeModel();
		JClass xmlElementWrapperModelClass = codeModel.ref(XmlElementWrapper.class);
		JClass xmlElementModelClass = codeModel.ref(XmlElement.class);
		JClass xmlAnyElementModelClass = codeModel.ref(XmlAnyElement.class);
		JClass xmlMixedModelClass = codeModel.ref(XmlMixed.class);
		JClass xmlElementRefsModelClass = codeModel.ref(XmlElementRefs.class);
		JClass xmlElementsModelClass = codeModel.ref(XmlElements.class);
		JClass xmlJavaTypeAdapterModelClass = codeModel.ref(XmlJavaTypeAdapter.class);
		JClass xmlTypeModelClass = codeModel.ref(XmlType.class);
		xmlElementDeclModelClass = codeModel.ref(XmlElementDecl.class);

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
		writeSummary("  Plural form          : " + applyPluralForm);
		writeSummary("");

		// Visit all classes generated by JAXB and find candidate classes for transformation.
		Map<String, Candidate> candidatesMap = new HashMap<String, Candidate>();

		// Write information on candidate classes to summary file.
		writeSummary("Candidates:");

		for (Iterator<Candidate> iter = findCandidateClasses(outline).iterator(); iter.hasNext();) {
			Candidate candidate = iter.next();

			if (isIncluded(candidate)) {
				writeSummary("\t[+]: " + getIncludeOrExcludeReason() + ":\t" + candidate.getClassName());
				candidatesMap.put(candidate.getClassName(), candidate);
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
			JDefinedClass targetClass = outlineClass.implClass;

			// We cannot remove candidates that have parent classes, but we can still substitute them:
			Candidate parentCandidate = candidatesMap.get(targetClass._extends().fullName());

			if (parentCandidate != null) {
				logger.debug("Candidate " + parentCandidate.getClassName() + " is a parent of " + targetClass.name()
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
				CPropertyInfo fieldPropertyInfo = field.getPropertyInfo();

				// For example, XSD attributes (PropertyKind.ATTRIBUTE) are always simple types:
				if (!(fieldPropertyInfo.kind() == PropertyKind.ELEMENT || fieldPropertyInfo.kind() == PropertyKind.REFERENCE)) {
					continue;
				}

				String fieldName = fieldPropertyInfo.getName(false);

				Candidate candidate = null;

				for (Candidate c : candidatesMap.values()) {
					// Skip fields with basic types as for example any class can be casted to Object.
					if (fieldType.isAssignableFrom(c.getCandidateClass()) && !isHiddenClass(fieldType)) {
						// If the given field has type T, it cannot be also in the list of parametrisations (e.g. T<T>).
						candidate = c;
						break;
					}
					// If the candidate T is referred from list of parametrisations (e.g. List<T>), it cannot be removed.
					// However field substitutions will take place.
					else if (isListedAsParametrisation(c.getCandidateClass(), fieldType)) {
						logger.debug("Candidate " + c.getClassName() + " is listed as parametrisation of "
						            + targetClass.fullName() + "#" + fieldName + " and hence won't be removed.");
						c.unmarkForRemoval();
					}
				}

				JFieldVar originalImplField = targetClass.fields().get(fieldName);

				if (candidate == null) {
					checkAnnotationReference(candidatesMap, originalImplField);

					continue;
				}

				// We have a candidate field to be replaced with a wrapped version. Report finding to summary file.
				writeSummary("\tReplacing field [" + fieldType.name() + " " + targetClass.fullName() + "#" + fieldName
				            + "]");
				candidate.incrementSubstitutions();
				modificationCount++;

				// The container class has to be deleted. Check that inner class has to be moved to it's parent.
				if (moveInnerClassToParent(outline, candidate)) {
					modificationCount++;
				}

				List<JClass> fieldTypeParametrisations = candidate.getFieldClass().getTypeParameters();

				// Create the new interface and collection classes using the specified interface and
				// collection classes (configuration) with an element type corresponding to
				// the element type from the collection present in the candidate class (narrowing).
				JClass collectionInterfaceClass = codeModel.ref(this.collectionInterfaceClass).narrow(
				            fieldTypeParametrisations);
				JClass collectionImplClass = codeModel.ref(this.collectionImplClass).narrow(fieldTypeParametrisations);

				String schemaElementName = getXsdDeclaration(fieldPropertyInfo).getName();

				boolean pluralFormWasApplied = false;

				// Apply the plural form if there are no customizations. Assuming that customization is correct as may define the
				// plural form in more correct way, e.g. "field[s]OfScience" instead of "fieldOfScience[s]".
				if (applyPluralForm && hasNoPropertyNameCustomization(fieldName, schemaElementName)) {
					String oldFieldName = fieldName;

					// Taken from com.sun.tools.xjc.reader.xmlschema.ParticleBinder#makeJavaName():
					fieldName = JJavaName.getPluralForm(fieldName);

					// The field e.g. "return" was escaped as "_return", but after conversion to plural
					// it became valid Java identifier, so we remove the leading "_":
					if (fieldName.startsWith("_") && JJavaName.isJavaIdentifier(fieldName.substring(1))) {
						fieldName = fieldName.substring(1);
					}

					if (!fieldName.equals(oldFieldName)) {
						pluralFormWasApplied = true;

						originalImplField.name(fieldName);
						fieldPropertyInfo.setName(false, fieldName);

						// Correct the @XmlType class-level annotation:
						JAnnotationValue propOrderValue = getAnnotation(targetClass, xmlTypeModelClass)
						            .getAnnotationMembers().get("propOrder");

						if (propOrderValue != null) {
							for (JAnnotationValue annotationValue : (List<JAnnotationValue>) getPrivateField(
							            propOrderValue, "values")) {
								if (oldFieldName.equals(generableToString(annotationValue))) {
									setPrivateField(annotationValue, "value", JExpr.lit(fieldName));
									break;
								}
							}
						}
					}
				}

				// Transform the field accordingly.
				originalImplField.type(collectionInterfaceClass);

				// If instantiation is specified to be "early", add code for creating new instance of the collection class.
				if (instantiation == Instantiation.EARLY) {
					logger.debug("Applying EARLY instantiation...");
					// GENERATED CODE: ... fieldName = new C<T>();
					originalImplField.init(JExpr._new(collectionImplClass));
				}

				// Annotate the field with the @XmlElementWrapper annotation using the original field name.
				JAnnotationUse xmlElementWrapperAnnotation = originalImplField.annotate(xmlElementWrapperModelClass);
				JAnnotationUse xmlElementOriginalAnnotation = getAnnotation(originalImplField, xmlElementModelClass);
				JExpression wrapperXmlNamespace = null;

				if (xmlElementOriginalAnnotation != null) {
					JExpression wrapperXmlName = getAnnotationMemberExpression(xmlElementOriginalAnnotation, "name");
					if (wrapperXmlName != null) {
						xmlElementWrapperAnnotation.param("name", wrapperXmlName);
					}
					else if (pluralFormWasApplied) {
						xmlElementWrapperAnnotation.param("name", schemaElementName);
					}

					JExpression wrapperXmlRequired = getAnnotationMemberExpression(xmlElementOriginalAnnotation,
					            "required");
					if (wrapperXmlRequired != null) {
						xmlElementWrapperAnnotation.param("required", wrapperXmlRequired);
					}

					JExpression wrapperXmlNillable = getAnnotationMemberExpression(xmlElementOriginalAnnotation,
					            "nillable");
					if (wrapperXmlNillable != null) {
						xmlElementWrapperAnnotation.param("nillable", wrapperXmlNillable);
					}

					// Namespace of the wrapper element
					wrapperXmlNamespace = getAnnotationMemberExpression(xmlElementOriginalAnnotation, "namespace");
					if (wrapperXmlNamespace != null) {
						xmlElementWrapperAnnotation.param("namespace", wrapperXmlNamespace);
					}

					removeAnnotation(originalImplField, xmlElementOriginalAnnotation);
				}
				else {
					if (pluralFormWasApplied) {
						xmlElementWrapperAnnotation.param("name", schemaElementName);
					}
				}

				boolean xmlElementInfoWasTransferred = false;

				// Transfer @XmlAnyElement, @XmlElementRefs, @XmlElements:
				for (JClass annotationModelClass : new JClass[] { xmlAnyElementModelClass, xmlMixedModelClass,
				        xmlElementRefsModelClass, xmlElementsModelClass }) {
					JAnnotationUse annotation = getAnnotation(candidate.getField(), annotationModelClass);

					if (annotation != null) {
						xmlElementInfoWasTransferred = true;

						addAnnotation(originalImplField, annotation);
					}
				}

				if (!xmlElementInfoWasTransferred) {
					// Annotate the field with the @XmlElement annotation using the field name from the wrapped type as name.
					// We cannot just re-use the same annotation object instance, as for example, we need to set XML name and this
					// will impact the candidate field annotation in case candidate is unmarked from removal.
					JAnnotationUse xmlElementAnnotation = originalImplField.annotate(xmlElementModelClass);
					xmlElementOriginalAnnotation = getAnnotation(candidate.getField(), xmlElementModelClass);

					if (xmlElementOriginalAnnotation != null) {
						JExpression xmlName = getAnnotationMemberExpression(xmlElementOriginalAnnotation, "name");
						if (xmlName != null) {
							xmlElementAnnotation.param("name", xmlName);
						}
						else {
							xmlElementAnnotation.param("name", candidate.getFieldName());
						}

						JExpression xmlNamespace = getAnnotationMemberExpression(xmlElementOriginalAnnotation, "namespace");
						if (xmlNamespace != null) {
							xmlElementAnnotation.param("namespace", xmlNamespace);
						}
						else if (candidate.getFieldTargetNamespace() != null) {
							xmlElementAnnotation.param("namespace", candidate.getFieldTargetNamespace());
						}

						JExpression type = getAnnotationMemberExpression(xmlElementOriginalAnnotation, "type");
						if (type != null) {
							xmlElementAnnotation.param("type", type);
						}
					}
					else {
						xmlElementAnnotation.param("name", candidate.getFieldName());
						if (candidate.getFieldTargetNamespace() != null) {
							xmlElementAnnotation.param("namespace", candidate.getFieldTargetNamespace());
						}
					}
				}

				JAnnotationUse adapterAnnotation = getAnnotation(candidate.getField(), xmlJavaTypeAdapterModelClass);

				if (adapterAnnotation != null) {
					addAnnotation(originalImplField, adapterAnnotation);
				}

				// Same as fieldName, but used as getter/setter method name:
				String propertyName = fieldPropertyInfo.getName(true);

				JDefinedClass implementationInterface = null;

				for (Iterator<JClass> iter = targetClass._implements(); iter.hasNext();) {
					JClass interfaceClass = iter.next();

					// If value class implements some JVM interface it is not considered as such interface cannot be modified:
					if (interfaceClass instanceof JDefinedClass
					            && deleteSettersGetters((JDefinedClass) interfaceClass, propertyName)) {
						implementationInterface = (JDefinedClass) interfaceClass;
						break;
					}
				}

				// Find original getter and setter methods to remove.
				deleteSettersGetters(targetClass, propertyName);

				if (pluralFormWasApplied) {
					propertyName = JJavaName.getPluralForm(propertyName);
					fieldPropertyInfo.setName(true, propertyName);
				}

				// Add a new getter method returning the (wrapped) field added.
				// GENERATED CODE: public I<T> getFieldName() { ... return fieldName; }
				JMethod getterMethod = targetClass.method(JMod.PUBLIC, collectionInterfaceClass, "get" + propertyName);

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
				JMethod setterMethod = targetClass.method(JMod.PUBLIC, codeModel.VOID, "set" + propertyName);

				setterMethod.body().assign(JExpr._this().ref(fieldName),
				            setterMethod.param(collectionInterfaceClass, fieldName));

				// Modify interface as well:
				if (implementationInterface != null) {
					writeSummary("\tCorrecting interface " + implementationInterface.fullName());

					implementationInterface.method(JMod.PUBLIC, collectionInterfaceClass, "get" + propertyName);
					setterMethod = implementationInterface.method(JMod.PUBLIC, codeModel.VOID, "set" + propertyName);
					setterMethod.param(collectionInterfaceClass, fieldName);
				}

				modificationCount += createScopedFactoryMethods(codeModel, candidate.getValueObjectFactoryClass(),
				            candidate.getScopedElementInfos().values(), targetClass);

				if (candidate.isValueObjectDisabled()) {
					modificationCount += createScopedFactoryMethods(codeModel, candidate.getObjectFactoryClass(),
					            candidate.getScopedElementInfos().values(), targetClass);
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
	private boolean moveInnerClassToParent(Outline outline, Candidate candidate) {
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

		moveClassLevelUp(outline, fieldParametrisationImpl);

		renameFactoryMethod(fieldParametrisationImpl._package()._getClass(FACTORY_CLASS_NAME), oldFactoryMethodName,
		            fieldParametrisationClass.name());

		if (candidate.isValueObjectDisabled()) {
			moveClassLevelUp(outline, fieldParametrisationClass);

			renameFactoryMethod(fieldParametrisationClass._package()._getClass(FACTORY_CLASS_NAME),
			            oldFactoryMethodName, fieldParametrisationClass.name());
		}

		return true;
	}

	/**
	 * Create factory methods with a new scope for elements that should be scoped.
	 * 
	 * @param targetClass
	 *            the class that is applied the transformation of properties
	 * @return number of created methods
	 * @see com.sun.tools.xjc.generator.bean.ObjectFactoryGenerator
	 */
	private int createScopedFactoryMethods(JCodeModel codeModel, JDefinedClass factoryClass,
	            Collection<ScopedElementInfo> scopedElementInfos, JDefinedClass targetClass) {
		int createdMethods = 0;

		NEXT: for (ScopedElementInfo info : scopedElementInfos) {
			String dotClazz = targetClass.fullName() + ".class";

			// First check that such factory method has not yet been created. It can be the case if target class
			// is substituted with e.g. two candidates, each candidate having a field with the same name.
			// FIXME: Could it be the case that these two fields have different namespaces?
			for (JMethod method : factoryClass.methods()) {
				JAnnotationUse xmlElementDeclAnnotation = getAnnotation(method, xmlElementDeclModelClass);

				if (xmlElementDeclAnnotation == null) {
					continue;
				}

				JExpression scope = getAnnotationMemberExpression(xmlElementDeclAnnotation, "scope");
				JExpression name = getAnnotationMemberExpression(xmlElementDeclAnnotation, "name");

				if (scope != null && dotClazz.equals(generableToString(scope))
				            && generableToString(info.name).equals(generableToString(name))) {
					continue NEXT;
				}
			}

			// Generate the scoped factory method:
			//   @XmlElementDecl(..., scope = T.class)
			//   public JAXBElement<X> createT...(X value) { return new JAXBElement<...>(QNAME, X.class, T.class, value); }
			StringBuilder methodName = new StringBuilder();

			JDefinedClass container = targetClass;

			while (true) {
				methodName.insert(0, container.name());

				if (container.parentContainer().isClass()) {
					container = (JDefinedClass) container.parentContainer();
				}
				else {
					break;
				}
			}

			methodName.insert(0, "create").append(NameConverter.standard.toPropertyName(generableToString(info.name)));

			JClass wrapperType = codeModel.ref(JAXBElement.class).narrow(info.type);

			JMethod method = factoryClass.method(JMod.PUBLIC, wrapperType, methodName.toString());

			method.annotate(xmlElementDeclModelClass).param("namespace", info.namespace).param("name", info.name)
			            .param("scope", targetClass);

			// FIXME: Make a try to load constants and (a) rename it appropriately (b) use it?
			JInvocation qname = JExpr._new(codeModel.ref(QName.class)).arg(info.namespace).arg(info.name);

			method.body()._return(
			            JExpr._new(wrapperType).arg(qname).arg(info.type.boxify().dotclass())
			                        .arg(targetClass.dotclass()).arg(method.param(info.type, "value")));

			createdMethods++;
		}

		return createdMethods;
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
					// Don't care if some interfaces collide: value classes have exactly one implementation
					interfaceImplementations.put(interfaceClass.fullName(), classOutline);
				}
			}
		}

		Collection<Candidate> candidates = new ArrayList<Candidate>();

		JClass collectionModelClass = outline.getCodeModel().ref(Collection.class);

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
			if (!collectionModelClass.isAssignableFrom(fieldType)) {
				continue;
			}

			List<JClass> fieldParametrisations = fieldType.getTypeParameters();

			// FIXME: All known collections have exactly one parametrisation type.
			assert fieldParametrisations.size() == 1;

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

			String fieldTargetNamespace = null;

			XSDeclaration xsdDeclaration = getXsdDeclaration(classOutline.target.getProperty(field.name()));

			if (xsdDeclaration != null && !xsdDeclaration.getTargetNamespace().isEmpty()) {
				fieldTargetNamespace = xsdDeclaration.getTargetNamespace();
			}

			// We have a candidate class:
			Candidate candidate = new Candidate(candidateClass, field, fieldTargetNamespace, fieldParametrisationClass,
			            fieldParametrisationImpl, xmlElementDeclModelClass);
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
			if (!isIncluded(candidate) || !candidate.canBeRemoved()) {
				continue;
			}

			// Get the defined class for candidate class.
			JDefinedClass candidateClass = candidate.getCandidateClass();

			deletionCount += deleteFactoryMethod(candidate.getValueObjectFactoryClass(), candidate);

			deleteClass(outline, candidateClass);
			deletionCount++;

			// Redo the same for interface:
			if (candidate.isValueObjectDisabled()) {
				deletionCount += deleteFactoryMethod(candidate.getObjectFactoryClass(), candidate);

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

			writeSummary("\tRenamed " + methodName + " -> " + method.name() + " in " + factoryClass.fullName());
		}
	}

	/**
	 * Remove method {@code ObjectFactory} that creates an object of a given {@code clazz}.
	 * 
	 * @return {@code 1} if such method was successfully located and removed
	 */
	private int deleteFactoryMethod(JDefinedClass factoryClass, Candidate candidate) {
		int deletedMethods = 0;

		for (Iterator<JMethod> iter = factoryClass.methods().iterator(); iter.hasNext();) {
			JMethod method = iter.next();

			// Remove the methods:
			// * public T createT() { return new T(); }
			// * public JAXBElement<T> createT(T value) { return new JAXBElement<T>(QNAME, T.class, null, value); }
			// * @XmlElementDecl(..., scope = X.class)
			//   public JAXBElement<T> createT...(T value) { return new JAXBElement<...>(QNAME, T.class, X.class, value); }
			if ((method.type() instanceof JDefinedClass && ((JDefinedClass) method.type()).isAssignableFrom(candidate
			            .getCandidateClass()))
			            || isListedAsParametrisation(candidate.getCandidateClass(), method.type())
			            || candidate.getScopedElementInfos().containsKey(method.name())) {
				writeSummary("\tRemoving factory method [" + method.type().fullName() + "#" + method.name()
				            + "()] from " + factoryClass.fullName());
				iter.remove();

				deletedMethods++;
			}
		}

		return deletedMethods;
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
	@SuppressWarnings("unchecked")
	private void moveClassLevelUp(Outline outline, JDefinedClass clazz) {
		// Modify the container so it now refers the class. Container can be a class or package.
		JClassContainer container = clazz.parentContainer().parentContainer();

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
			setPrivateField(clazz.mods(), "mods", Integer.valueOf(clazz.mods().getValue() & ~JMod.STATIC));

			for (ClassOutline classOutline : outline.getClasses()) {
				if (classOutline.implClass == clazz) {
					XSComponent sc = classOutline.target.getSchemaComponent();

					// FIXME: Inner class is always a local declaration.
					assert (sc instanceof XSDeclaration && ((XSDeclaration) sc).isLocal());

					setPrivateField(sc, "anonymous", Boolean.FALSE);
				}
			}
		}

		// Finally modify the class so that it refers back the container:
		setPrivateField(clazz, "outer", container);
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
					break;
				}
			}
		}
	}

	/**
	 * Check that given field property has no name customizations.
	 * 
	 * @see com.sun.xml.bind.api.impl.NameUtil
	 * @see com.sun.codemodel.JJavaName
	 * @see com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty#getCustomization(XSComponent)
	 */
	private boolean hasNoPropertyNameCustomization(String fieldName, String schemaElementName) {
		// Customizations are not available after model is created as Ring is released. Correct code would look like this:
		// return BIProperty.getCustomization(((XSParticle) field.getPropertyInfo().getSchemaComponent()).getTerm()).getName() != null;
		String originalFieldName = NameConverter.standard.toVariableName(NameConverter.standard
		            .toPropertyName(schemaElementName));

		// The names should match either exactly, or Java name may be prefixed with "_":
		return fieldName.equals(originalFieldName)
		            || (fieldName.length() - originalFieldName.length() == 1 && fieldName.endsWith(originalFieldName));
	}

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

			Candidate candidate = candidatesMap.get(generableToString(type).replace(".class", ""));

			if (candidate != null) {
				logger.debug("Candidate " + candidate.getClassName()
				            + " is used in XmlElements/XmlElementRef and hence won't be removed.");
				candidate.unmarkForRemoval();
			}
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
		else if (!hasIncludes()) {
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
		else if (!hasIncludes()) {
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
		/**
		 * Collection is initialized as property initializer (created when class is constructed).
		 */
		EARLY,

		/**
		 * Collection is initialized in getter (created when property is accessed the first time).
		 */
		LAZY,

		/**
		 * Collection is never initialized. It's consumers responsibility to set the property to some collection
		 * instance.
		 */
		NONE
	}

	/**
	 * Container for information relative to scoped elements.
	 */
	private static class ScopedElementInfo {
		/**
		 * Element name ("post-office").
		 */
		public JExpression name;

		/**
		 * Element namespace ("http://foo.bar").
		 */
		public JExpression namespace;

		/**
		 * Element type ({@link String}).
		 */
		public JType       type;

		public ScopedElementInfo(JExpression name, JExpression namespace, JType type) {
			this.name = name;
			this.namespace = namespace;
			this.type = type;
		}
	}

	/**
	 * Describes the collection container class -- a candidate for removal.
	 */
	private static class Candidate {
		private final JDefinedClass                  candidateClass;

		private final JFieldVar                      field;

		private final String                         fieldTargetNamespace;

		private final JDefinedClass                  fieldParametrisationClass;

		private final JDefinedClass                  fieldParametrisationImpl;

		private JDefinedClass                        objectFactoryClass;

		private final JDefinedClass                  valueObjectFactoryClass;

		private final Map<String, ScopedElementInfo> scopedElementInfos = new HashMap<String, ScopedElementInfo>();

		/**
		 * By default the candidate is marked for removal unless something prevents it from being removed.
		 */
		private boolean                              markedForRemoval   = true;

		/**
		 * Number of times this candidate has been substituted in the model.
		 */
		private int                                  substitutionsCount;

		Candidate(JDefinedClass candidateClass, JFieldVar field, String fieldTargetNamespace,
		            JDefinedClass fieldParametrizationClass, JDefinedClass fieldParametrisationImpl,
		            JClass xmlElementDeclModelClass) {
			this.candidateClass = candidateClass;
			this.field = field;
			this.fieldTargetNamespace = fieldTargetNamespace;
			this.fieldParametrisationClass = fieldParametrizationClass;
			this.fieldParametrisationImpl = fieldParametrisationImpl;

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

			assert objectFactoryClass != valueObjectFactoryClass;

			String dotClazz = candidateClass.fullName() + ".class";

			for (JMethod method : valueObjectFactoryClass.methods()) {
				JAnnotationUse xmlElementDeclAnnotation = getAnnotation(method, xmlElementDeclModelClass);

				if (xmlElementDeclAnnotation == null) {
					continue;
				}

				JExpression scope = getAnnotationMemberExpression(xmlElementDeclAnnotation, "scope");

				if (scope == null || !dotClazz.equals(generableToString(scope))) {
					continue;
				}

				scopedElementInfos.put(method.name(),
				            new ScopedElementInfo(getAnnotationMemberExpression(xmlElementDeclAnnotation, "name"),
				                        getAnnotationMemberExpression(xmlElementDeclAnnotation, "namespace"), method
				                                    .params().get(0).type()));
			}
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
		 * The XSD namespace of the property associated with a field.
		 */
		public String getFieldTargetNamespace() {
			return fieldTargetNamespace;
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
		 * Return information about scoped elements, that have this candidate as a scope.
		 * 
		 * @return object factory method name -to- element info map
		 */
		public Map<String, ScopedElementInfo> getScopedElementInfos() {
			return scopedElementInfos;
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
		 * Returns {@code true} if the setting {@code <jaxb:globalBindings generateValueClass="false">} is active.
		 */
		public boolean isValueObjectDisabled() {
			return objectFactoryClass != null;
		}

		/**
		 * Has given candidate green light to be removed?
		 */
		public boolean canBeRemoved() {
			return markedForRemoval && substitutionsCount > 0;
		}

		/**
		 * Increments number of substitutions for this candidate.
		 */
		public void incrementSubstitutions() {
			substitutionsCount++;
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
