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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
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
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import org.xml.sax.ErrorHandler;

/**
 * The XML Element Wrapper plugin is a JAXB plugin for the xjc compiler enabling generation of "natural" Java classes
 * for handling collection types. The code generated will be annotated with {@link XmlElementWrapper} and
 * {@link XmlElement} annotations and will have no extra inner classes representing the immediate collection type.
 * 
 * @see <a href="http://www.conspicio.dk/blog/bjarne/jaxb-xmlelementwrapper-plugin">plugin site</a>
 * @see <a href="http://www.conspicio.dk/projects/overview">source code and binary packages</a>
 */
public class XmlElementWrapperPlugin extends Plugin {
	private static final String PLUGIN_NAME              = "Xxew";
	private static final String OPTION_NAME_DELETE       = "-" + PLUGIN_NAME + ":delete";
	private static final String OPTION_NAME_INCLUDE      = "-" + PLUGIN_NAME + ":includeFile";
	private static final String OPTION_NAME_EXCLUDE      = "-" + PLUGIN_NAME + ":excludeFile";
	private static final String OPTION_NAME_SUMMARY      = "-" + PLUGIN_NAME + ":summaryFile";
	private static final String OPTION_NAME_COLLECTION   = "-" + PLUGIN_NAME + ":collection";
	private static final String OPTION_NAME_INSTANTIATE  = "-" + PLUGIN_NAME + ":instantiate";

	private static final String FACTORY_CLASS_NAME       = "ObjectFactory";

	private File                includeFile              = null;
	private Set<String>         include                  = null;                              // list of classes for inclusion
	private File                excludeFile              = null;
	private Set<String>         exclude                  = null;                              // list of classes for exclusion
	private File                summaryFile              = null;
	private PrintWriter         summary                  = null;
	private boolean             debugMode                = false;
	private boolean             verbose                  = false;
	private Class<?>            collectionInterfaceClass = java.util.List.class;
	private Class<?>            collectionImplClass      = java.util.ArrayList.class;
	private Instantiation       instantiation            = Instantiation.EARLY;
	private boolean             deleteCandidates         = false;

	// This is currently an experimental and not properly working feature, so keep this field set to false.
	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean				applyPluralForm				= Ring.get(BIGlobalBinding.class).isSimpleMode();
	private static boolean      applyPluralForm          = false;

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

	@Override
	public void onActivated(Options opts) {
		debugMode = opts.debugMode;
		verbose = opts.verbose;

		// If we are in debug mode, report...
		writeDebug("JAXB Compilation started (XmlElementWrapperPlugin.onActivated):");
		writeDebug("\tbuildId        :\t" + Options.getBuildID());
		writeDebug("\tdefaultPackage :\t" + opts.defaultPackage);
		writeDebug("\tdefaultPackage2:\t" + opts.defaultPackage2);
		writeDebug("\tquiet          :\t" + opts.quiet);
		writeDebug("\tdebug          :\t" + opts.debugMode);
		writeDebug("\ttargetDir      :\t" + opts.targetDir);
		writeDebug("\tverbose        :\t" + opts.verbose);
		writeDebug("\tGrammars       :\t" + opts.getGrammars().length);

		for (int i = 0; i < opts.getGrammars().length; i++) {
			writeDebug("\t  [" + i + "]: " + opts.getGrammars()[i].getSystemId());
		}
	}

	@Override
	public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
		int recognized = 0;
		String filename;

		String arg = args[i];
		writeDebug("Argument[" + i + "] = " + arg);

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
		writeDebug("JAXB Process Model (run)...");

		// Write summary information on the option for this compilation.
		writeSummary(" ");
		writeSummary("Compilation:");
		writeSummary("\tDate         :\t-");
		writeSummary("\tVersion      :\t-");
		writeSummary("\tJAXB version :\t" + Options.getBuildID());
		writeSummary("\tInclude file :\t" + (includeFile == null ? "<none>" : includeFile));
		writeSummary("\tExclude file :\t" + (excludeFile == null ? "<none>" : excludeFile));
		writeSummary("\tSummary file :\t" + (summaryFile == null ? "<none>" : summaryFile));
		writeSummary("\tInstantiate  :\t" + instantiation);
		writeSummary("\tCollection   :\t" + collectionImplClass);
		writeSummary("\tInterface    :\t" + collectionInterfaceClass);
		writeSummary("\tDelete       :\t" + deleteCandidates);
		writeSummary(" ");

		// Find candidate classes for transformation.
		// Candidates are classes having exactly one field which is a collection.
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
		writeSummary(" ");

		// Visit all classes generated by JAXB.
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
					// We also stop the cycle leaving candidate variable NULL:
					else if (isListedAsParametrisation(c.getDefinedClass(), field.getRawType())) {
						writeDebug("Candidate " + c.getClassName()
						            + " is listed as parametrisation and hence won't be removed.");
						c.setMarkedForRemoval(false);
						break;
					}
				}

				if (candidate == null) {
					continue;
				}

				// We have a candidate field to be replaced with a wrapped version. Report finding to summary file.
				writeSummary("\t" + outlineClass.target.getName() + "#" + fieldName + "\t" + typeName);
				modificationCount++;

				List<JClass> itemNarrowing = (candidate.getFieldType()).getTypeParameters();

				// Create the new interface and collection classes using the specified interface and
				// collection classes (configuration) with an element type corresponding to
				// the element type from the collection present in the candidate class (narrowing).
				JClass collectionInterfaceClass = implementationClass.owner().ref(this.collectionInterfaceClass)
				            .narrow(itemNarrowing);
				JClass collectionImplClass = implementationClass.owner().ref(this.collectionImplClass)
				            .narrow(itemNarrowing);

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

						for (JAnnotationValue ann : ((JAnnotationArrayMember) annotation.getAnnotationMembers().get(
						            "propOrder")).annotations()) {
							if (oldFieldName.equals(getAnnotationStringValue(ann))) {
								// FIXME: There is no way to set the correct property name back to annotation.
								break;
							}
						}

						break;
					}
				}

				// Add new wrapped version of the field using the original field name.
				// GENERATED CODE: protected I<T> fieldName;
				JFieldVar implField = implementationClass.field(JMod.PROTECTED, collectionInterfaceClass, fieldName);

				// If instantiation is specified to be "early", add code for creating new instance of the collection class.
				if (instantiation == Instantiation.EARLY) {
					writeDebug("Applying EARLY instantiation...");
					// GENERATED CODE: ... fieldName = new C<T>();
					implField.init(JExpr._new(collectionImplClass));
				}

				// Annotate the new field with the @XmlElementWrapper annotation using the original field name as name.
				JAnnotationUse xmlElementWrapperAnnotation = implField.annotate(XmlElementWrapper.class);

				String xmlElementName = extractXmlElementName(field.getPropertyInfo());
				xmlElementWrapperAnnotation.param("name", xmlElementName);

				if (isXmlRequired(originalImplField)) {
					xmlElementWrapperAnnotation.param("required", true);
				}

				String namespace = getXmlNamespace(originalImplField);
				if (namespace != null) {
					xmlElementWrapperAnnotation.param("namespace", namespace);
				}

				// Annotate the new field with the @XmlElement annotation using the field name from the wrapped type as name.
				JAnnotationUse xmlElementAnnotation = implField.annotate(XmlElement.class);
				xmlElementAnnotation.param("name", candidate.getXmlElementName());
				// TODO: The below assumption that the element belongs to the same namespace as the wrapper element,
				// is not generally true for all cases (but I not know any real example proving that).
				// If you know the better way to detect it, put it here:

				// The preferred namespace name comes from the wrapped
				String wrapperNamespace = null;
				if (field.getRawType() instanceof JDefinedClass)
				{
					JDefinedClass jdc = (JDefinedClass) field.getRawType();
					Map<String, JFieldVar> jdcFields = jdc.fields();
					if (jdcFields.size() == 1) // we only expect one field in wrapper classes (or we could pull out by expected name?)
					{
						JFieldVar jfieldVar = jdcFields.values().iterator().next();
						wrapperNamespace = getXmlNamespace(jfieldVar);
					}
				}

				if (wrapperNamespace != null) {
					xmlElementAnnotation.param("namespace", wrapperNamespace);
				}
				else if (namespace != null) {
					xmlElementAnnotation.param("namespace", namespace);
				}

				// -- debug info
				StringWriter w = new StringWriter();
				JFormatter f = new JFormatter(w);
				xmlElementWrapperAnnotation.generate(f);
				w.write(" ");
				xmlElementAnnotation.generate(f);

				writeDebug(w.toString());
				// -- end of debug info

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
					writeDebug("Applying LAZY instantiation...");
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
		writeSummary(" ");

		int deletionCount = 0;

		if (deleteCandidates) {
			deletionCount = deleteCandidates(candidates);
		}

		writeSummary("\t" + deletionCount + " deletion(s) from original code.");
		writeSummary(" ");

		writeDebug("Closing summary...");
		closeSummary();

		writeDebug("Done");

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
		JClass parentClass = implementationClass._extends();

		// We cannot remove candidates that have parent classes, but we can still substitute them:
		if (parentClass != null) {
			Candidate candidate = candidates.get(parentClass.fullName());

			if (candidate != null) {
				writeDebug("Candidate " + candidate.getClassName() + " is a parent of " + implementationClass.name()
				            + " and hence won't be removed.");
				candidate.setMarkedForRemoval(false);
			}
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

			String typeClassName = getAnnotationMemberStringValue(annotation, "type");

			if (typeClassName == null) {
				// Can be the case for @XmlElement(name = "publication-reference", namespace = "http://mycompany.org/exchange")
				// or any other annotation without 
				continue;
			}

			typeClassName = typeClassName.replace(".class", "");

			Candidate candidate = candidates.get(typeClassName);

			if (candidate != null) {
				writeDebug("Candidate " + candidate.getClassName()
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
	private int deleteCandidates(Map<String, Candidate> candidates) {
		int deletionCount = 0;

		writeSummary("Deletions:");

		// Visit all candidate classes.
		for (Candidate c : candidates.values()) {
			// Only consider candidates that are actually included...
			if (!isIncluded(c) || !c.isMarkedForRemoval()) {
				continue;
			}

			// Get the defined class for candidate class.
			JDefinedClass candidateClass = c.getDefinedClass();

			// Locate the ObjectFactory inside the package of the candidate class.
			JPackage pkg = candidateClass._package();
			JDefinedClass factoryClass = pkg._getClass(FACTORY_CLASS_NAME);

			// Remove methods referencing the candidate class from the ObjectFactory.
			for (Iterator<JMethod> iter = factoryClass.methods().iterator(); iter.hasNext();) {
				JMethod m = iter.next();

				// Remove the methods:
				// * public T createT() { return new T(); }
				// * public JAXBElement<T> createT(T value) { return new JAXBElement<T>(QNAME, T.class, null, value); }
				if (m.type().compareTo(candidateClass) == 0 || isListedAsParametrisation(candidateClass, m.type())) {
					writeSummary("\tRemoving method " + m.type().fullName() + " " + m.name() + " from "
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
				Iterator<JDefinedClass> itor = parent.classes();
				while (itor.hasNext())
					if (itor.next().equals(candidateClass)) {
						itor.remove();
						break;
					}
				deletionCount++;
			}
			else {
				// The candidate class in in a package. Remove the class from the package.
				writeSummary("\tRemoving class " + candidateClass.fullName() + " from package " + pkg.name());
				pkg.remove(candidateClass);
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
		LineNumberReader input;
		String line;

		input = new LineNumberReader(new FileReader(file));
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

		// Visit all classes created by JAXB processing to collect all potential classes to be removed:
		for (CClassInfo classInfo : outline.getModel().beans().values()) {
			String className = classInfo.fullName();

			// * The candidate class must have exactly one property
			// * The candidate class should not extend any other class (as the total number of properties will be more than 1)
			if (classInfo.getProperties().size() == 1 && classInfo.getBaseClass() == null) {
				CPropertyInfo property = classInfo.getProperties().get(0);

				// The property must be a collection
				if (property.isCollection()) {
					if (property.ref().size() == 1) {
						// We have a candidate class.
						Candidate candidate = new Candidate(outline.getClazz(classInfo).implClass, property);
						candidates.put(className, candidate);
						writeDebug("Candidate found: " + candidate.getClassName() + " [private "
						            + candidate.getFieldType().name() + " " + candidate.getFieldName() + "]");
					}
				}
			}
		}

		return candidates;
	}

	/**
	 * Returns <code>true</code> of the given variable is annotated as <code>@XmlElement(required = true)</code>.
	 */
	private static boolean isXmlRequired(JVar var) {
		for (JAnnotationUse annotation : var.annotations()) {
			if (annotation.getAnnotationClass().name().equals("XmlElement")) {
				return annotation.getAnnotationMembers().containsKey("required");
			}
		}

		return false;
	}

	/**
	 * Returns the XML namespace for the given variable, if is present in annotations.
	 */
	private static String getXmlNamespace(JVar var) {
		for (JAnnotationUse annotation : var.annotations()) {
			if (annotation.getAnnotationClass().name().equals("XmlElement")) {
				return getAnnotationMemberStringValue(annotation, "namespace");
			}
		}

		return null;
	}

	/**
	 * Returns the string value of annotation element. For example, for annotation
	 * <code>@XmlElementRef(name = "last-name", namespace = "http://mycompany.org/exchange", type = JAXBElement.class)</code>
	 * for member <code>name</code> the value <code>last-name</code> will be returned.
	 */
	private static final String getAnnotationMemberStringValue(JAnnotationUse annotation, String annotationMember) {
		JAnnotationValue annotationValue = annotation.getAnnotationMembers().get(annotationMember);

		if (annotationValue != null) {
			return getAnnotationStringValue(annotationValue);
		}

		return null;
	}

	/**
	 * Returns the string value of annotation.
	 */
	private static final String getAnnotationStringValue(JAnnotationValue annotationValue) {
		// FIXME: Waiting for improvement in http://java.net/jira/browse/JAXB-878 and http://java.net/jira/browse/JAXB-879:
		Writer w = new StringWriter();

		annotationValue.generate(new JFormatter(w));

		return w.toString().replace("\"", "");
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

	private void writeSummary(String s) {
		if (summary != null) {
			summary.println(s);
		}

		if (verbose) {
			System.out.println(s);
		}
	}

	private void closeSummary() {
		if (summary != null) {
			summary.close();
		}
	}

	private void writeDebug(String s) {
		if (debugMode) {
			System.out.println("DEBUG:" + s);
		}
	}

	/**
	 * For the given class property returns the XML tag name associated with it.
	 */
	static String extractXmlElementName(CPropertyInfo property) {
		if (!(property instanceof CElementPropertyInfo)) {
			return null;
		}

		CElementPropertyInfo propertyInfo = (CElementPropertyInfo) property;

		if (propertyInfo.getTypes().isEmpty()) {
			return null;
		}

		return propertyInfo.getTypes().get(0).getTagName().getLocalPart();
	}

	private enum Instantiation {
		EARLY, LAZY
	}

	/**
	 * @author bjh
	 */
	private static class Candidate {
		private JDefinedClass clazz;
		private String        fieldName;
		private JClass        fieldType;
		private String        xmlElementName   = null;
		private boolean       markedForRemoval = true;

		public Candidate(JDefinedClass clazz, CPropertyInfo property) {
			this.clazz = clazz;
			this.fieldName = property.getName(false);
			// A given property is a collection, hence it is a class:
			this.fieldType = (JClass) clazz.fields().get(this.fieldName).type();
			this.xmlElementName = extractXmlElementName(property);

			if (this.xmlElementName == null) {
				this.xmlElementName = this.fieldName;
			}
		}

		public JDefinedClass getDefinedClass() {
			return clazz;
		}

		public String getClassName() {
			return clazz.fullName();
		}

		public String getFieldName() {
			return fieldName;
		}

		public JClass getFieldType() {
			return fieldType;
		}

		public String getXmlElementName() {
			return xmlElementName;
		}

		public boolean isMarkedForRemoval() {
			return markedForRemoval;
		}

		public void setMarkedForRemoval(boolean markedForRemoval) {
			this.markedForRemoval = markedForRemoval;
		}
	}
}
