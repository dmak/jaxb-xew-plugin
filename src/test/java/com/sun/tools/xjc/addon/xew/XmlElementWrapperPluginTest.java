/*
 * XmlElementWrapperPluginTest.java
 *
 * Copyright (C) 2022, Tobias Warneke
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.assertj3.XmlAssert;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Testcases for the XEW Plugin.
 *
 * @author Tobias Warneke
 */
public class XmlElementWrapperPluginTest {

	private static final String	PREGENERATED_SOURCES_PREFIX	= "src/test/generated_resources/";
	private static final String	GENERATED_SOURCES_PREFIX	= "target/test/generated_xsd_classes/";

	private static final Log	logger						= LogFactory.getLog(XmlElementWrapperPluginTest.class);

	@Test
	public void testUsage() {
		assertThat(new XmlElementWrapperPlugin().getUsage()).isNotNull();
	}

	@Test
	public void testUnknownOption() {
		assertThatThrownBy(() -> runTest("different-namespaces", singletonList("-Xxew:unknown"), false, emptyList()))
		            .isInstanceOf(BadCommandLineException.class);
	}

	@Test
	public void testInvalidInstantiationMode() {
		assertThatThrownBy(() -> runTest("element-list-extended", singletonList("-Xxew:instantiate invalid"), false,
		            emptyList())).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testInvalidControlFile() {
		assertThatThrownBy(
		            () -> runTest("element-list-extended", singletonList("-Xxew:control invalid"), false, emptyList()))
		                        .isInstanceOf(BadCommandLineException.class);
	}

	@Test
	public void testInvalidCollectionClass() {
		assertThatThrownBy(() -> runTest("element-list-extended", singletonList("-Xxew:collection badvalue"), false,
		            emptyList())).isInstanceOf(BadCommandLineException.class);
	}

	@Test
	public void testInvalidCustomization() {
		assertThatThrownBy(() -> runTest("element-with-invalid-customization", emptyList(), false, emptyList()))
		            .isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Disabled("This test works reliably on Java7 but produces different results from run to run on Java8, see https://github.com/eclipse-ee4j/jaxb-ri/issues/1682")
	public void testDifferentNamespacesForWrapperAndElement() throws Exception {
		// Plural form in this case will have no impact as all properties are already in plural:
		List<String> extraXewOptions = asList("-debug", "-Xxew:collection", "java.util.LinkedList", "-Xxew:instantiate",
		            "lazy", "-Xxew:plural");
		List<String> classesToCheck = asList("BaseContainer", "Container", "Entry", "package-info");
		runTest("different-namespaces", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testInnerElement() throws Exception {
		List<String> extraXewOptions = asList("-verbose", "-Xxew:instantiate none",
		            "-Xxew:control " + getClass().getResource("inner-element-control.txt").getFile());
		List<String> classesToCheck = asList("Filesystem", "Volumes");
		runTest("inner-element", extraXewOptions, true, classesToCheck);
	}

	@Test
	public void testInnerScopedElement() throws Exception {
		List<String> extraXewOptions = asList("-verbose", "-Xxew:instantiate early");
		List<String> classesToCheck = asList("Catalogue");
		runTest("inner-scoped-element", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testInnerElementWithValueObjects() throws Exception {
		List<String> classesToCheck = asList("Article", "Articles", "ArticlesCollections", "Filesystem", "Publisher",
		            "Volume", "impl.ArticleImpl", "impl.ArticlesImpl", "impl.ArticlesCollectionsImpl",
		            "impl.FilesystemImpl", "impl.PublisherImpl", "impl.VolumeImpl", "impl.ObjectFactory",
		            "impl.JAXBContextFactory");
		runTest("inner-element-value-objects", singletonList("-debug"), false, classesToCheck);
	}

	@Test
	public void testAnnotationReference() throws Exception {
		// "Markup.java" cannot be verified for content because the content is changing from
		// one compilation to other as order of @XmlElementRef/@XmlElement annotations is not pre-defined
		// (set is used as their container).
		List<String> extraXewOptions = asList("-verbose", "-debug");
		List<String> classesToCheck = asList("ClassCommon", "ClassesEu", "ClassesUs", "ClassExt", "Markup", "Para",
		            "SearchEu", "SearchMulti");
		runTest("annotation-reference", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementAsParametrisationPublisher() throws Exception {
		final List<String> extraXewOptions = asList("-debug", "-Xxew:control "
		            + getClass().getResource("element-as-parametrisation-publisher-control.txt").getFile());
		List<String> classesToCheck = asList("Article", "Articles", "ArticlesCollections", "Publisher");
		runTest("element-as-parametrisation-publisher", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementAsParametrisationFamily() throws Exception {
		List<String> extraXewOptions = asList("-debug",
		            "-Xxew:control "
		                        + getClass().getResource("element-as-parametrisation-family-control.txt").getFile(),
		            "-Xxew:summary " + GENERATED_SOURCES_PREFIX + "summary.txt");
		List<String> classesToCheck = asList("Family", "FamilyMember");
		runTest("element-as-parametrisation-family", extraXewOptions, false, classesToCheck);

		assertThat(Paths.get(GENERATED_SOURCES_PREFIX, "summary.txt")).content()
		            .contains("1 candidate(s) being considered").contains("0 modification(s) to original code")
		            .contains("0 deletion(s) from original code");
	}

	@Test
	public void testElementWithParent() throws Exception {
		List<String> extraXewOptions = singletonList("-debug");
		List<String> classesToCheck = asList("Alliance", "Group", "Organization");
		runTest("element-with-parent", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementAny() throws Exception {
		List<String> extraXewOptions = asList("-quiet", "-Xxew:plural");
		List<String> classesToCheck = singletonList("Message");
		runTest("element-any", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementAnyType() throws Exception {
		List<String> extraXewOptions = singletonList("-Xxew:plural");
		List<String> classesToCheck = asList("Conversion", "Entry");
		runTest("element-any-type", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementMixed() throws Exception {
		// Most classes cannot be tested for content
		List<String> extraXewOptions = singletonList("-debug");
		List<String> classesToCheck = asList("B", "Br", "I", "AnyText", "package-info");
		runTest("element-mixed", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementListExtended() throws Exception {
		// This run is configured from XSD (<xew:xew ... >):
		List<String> classesToCheck = singletonList("Foo");
		runTest("element-list-extended", emptyList(), false, classesToCheck);
	}

	@Test
	public void testElementNameCollision() throws Exception {
		// Most classes cannot be tested for content
		List<String> extraXewOptions = asList("-debug", "-Xxew:instantiate", "lazy");
		List<String> classesToCheck = asList("Root", "package-info");
		runTest("element-name-collision", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementScoped() throws Exception {
		// Most classes cannot be tested for content
		List<String> extraXewOptions = singletonList("-debug");
		List<String> classesToCheck = asList("Return", "SearchParameters", "package-info");
		runTest("element-scoped", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementWithAdapter() throws Exception {
		// Plural form in this case will have no impact as there is property customization:
		List<String> extraXewOptions = asList("-Xxew:plural", "-Xxew:collectionInterface java.util.Collection");
		List<String> classesToCheck = asList("Calendar", "Adapter1");
		runTest("element-with-adapter", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementWithCustomization() throws Exception {
		// This run is additionally configured from XSD (<xew:xew ... >):
		List<String> extraXewOptions = asList("-debug", "-Xxew:plural");
		List<String> classesToCheck = asList("PostOffice", "Args");
		runTest("element-with-customization", extraXewOptions, false, classesToCheck);
	}

	@Test
	public void testElementReservedWord() throws Exception {
		List<String> classesToCheck = asList("Class", "Method");
		runTest("element-reserved-word", emptyList(), false, classesToCheck);
	}

	@Test
	public void testSubstitutionGroups() throws Exception {
		List<String> classesToCheck = asList("Address", "ContactInfo", "Customer", "PhoneNumber");
		runTest("substitution-groups", emptyList(), false, classesToCheck);
	}

	@Test
	public void testUnqualifiedSchema() throws Exception {
		List<String> classesToCheck = asList("RootElement", "package-info");
		runTest("unqualified", emptyList(), false, classesToCheck);
	}

	/**
	 * Standard test for XSD examples.
	 *
	 * @param testName
	 *            the prototype of XSD file name / package name
	 * @param extraXewOptions
	 *            to be passed to plugin
	 * @param generateEpisode
	 *            generate episode file and check the list of classes included into it
	 * @param classesToCheck
	 *            expected classes/files in target directory; these files content is checked if it is present in
	 *            resources directory; {@code ObjectFactory.java} is automatically included
	 */
	static void runTest(String testName, List<String> extraXewOptions, boolean generateEpisode,
	            List<String> classesToCheck) throws Exception {
		String resourceXsd = testName + ".xsd";
		String packageName = testName.replace('-', '_');

		// Force plugin to reinitialize the logger:
		System.clearProperty(XmlElementWrapperPlugin.COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY);

		URL xsdUrl = XmlElementWrapperPluginTest.class.getResource(resourceXsd);

		Path baseDir = Paths.get(GENERATED_SOURCES_PREFIX);
		Files.createDirectories(baseDir);

		PrintStream loggingPrintStream = new PrintStream(
		            new LoggingOutputStream(logger, LoggingOutputStream.LogLevel.INFO, "[XJC] "));

		List<String> opts = new ArrayList<>(extraXewOptions);
		opts.addAll(asList("-no-header", "-extension", "-Xxew", "-d", baseDir.toString(), xsdUrl.getFile()));

		String episodeFile = baseDir.resolve("episode.xml").toString();

		// Episode plugin should be triggered after Xew, see https://github.com/dmak/jaxb-xew-plugin/issues/6
		if (generateEpisode) {
			opts.addAll(asList("-episode", episodeFile));
		}

		assertThat(Driver.run(opts.toArray(new String[0]), loggingPrintStream, loggingPrintStream))
		            .as("XJC compilation failed. Checked console for more info.").isZero();

		if (generateEpisode) {
			// FIXME: Episode file actually contains only value objects
			Set<String> classReferences = getClassReferencesFromEpisodeFile(episodeFile);

			if (classesToCheck.contains("package-info")) {
				classReferences.add(packageName + ".package-info");
			}

			assertThat(classReferences).as("Wrong number of classes in episode file").hasSize(classReferences.size())
			            .as("Missing classes in episode file").containsExactlyInAnyOrderElementsOf(classesToCheck
			                        .stream().map(clz -> packageName + "." + clz).collect(Collectors.toList()));
		}

		Path targetDir = baseDir.resolve(packageName);

		Collection<String> generatedJavaSources;

		// *.properties files are ignored:
		try (Stream<Path> stream = Files.walk(targetDir)) {
			generatedJavaSources = stream
			            .filter(file -> Files.isRegularFile(file) && file.getFileName().toString().endsWith(".java"))
			            .map(f -> f.toString().substring(targetDir.toString().length() + 1).replace('\\', '/'))
			            .collect(Collectors.toSet());
		}

		// This class is added and checked by default:
		classesToCheck = new ArrayList<>(classesToCheck);
		classesToCheck.add("ObjectFactory");

		assertThat(generatedJavaSources).as("Missing classes in target directory").containsExactlyInAnyOrderElementsOf(
		            classesToCheck.stream().map(clz -> clz.replace('.', '/') + ".java").collect(Collectors.toList()));

		// Check the contents for those files which exist in resources:
		SoftAssertions softly = new SoftAssertions();
		for (String className : classesToCheck) {
			className = className.replace('.', '/');

			Path sourceFile = Paths.get(PREGENERATED_SOURCES_PREFIX + packageName, className + "" + ".java");

			String targetClassName = className + ".java";
			softly.assertThat(targetDir.resolve(targetClassName))
			            .as("For " + targetClassName + " in " + PREGENERATED_SOURCES_PREFIX + packageName)
			            .hasSameTextualContentAs(sourceFile, StandardCharsets.UTF_8);
		}
		softly.assertAll();

		JAXBContext jaxbContext = compileAndLoad(packageName, targetDir, generatedJavaSources);

		URL xmlTestFile = XmlElementWrapperPluginTest.class.getResource(testName + ".xml");

		if (xmlTestFile != null) {
			StringWriter writer = new StringWriter();

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(schemaFactory.newSchema(xsdUrl));

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			Object bean = unmarshaller.unmarshal(xmlTestFile);
			marshaller.marshal(bean, writer);

			XmlAssert.assertThat(xmlTestFile).and(writer.toString()).ignoreComments().ignoreWhitespace().areSimilar();
		}
	}

	/**
	 * The method performs:
	 * <ul>
	 * <li>Compilation of given set of Java source files
	 * <li>Construction of custom class loader
	 * <li>Creation of JAXB context
	 * </ul>
	 *
	 * @param packageName
	 *            package name to which java classes belong to
	 * @param targetDir
	 *            the target directory
	 * @param generatedJavaSources
	 *            list of Java source files which should become a part of JAXB context
	 */
	private static JAXBContext compileAndLoad(String packageName, Path targetDir,
	            Collection<String> generatedJavaSources) throws MalformedURLException, JAXBException {
		String[] javaSources = new String[generatedJavaSources.size()];

		int i = 0;
		for (String javaSource : generatedJavaSources) {
			javaSources[i++] = (targetDir.resolve(javaSource)).toString();
		}

		StringWriter writer = new StringWriter();

		assertThat(com.sun.tools.javac.Main.compile(javaSources, new PrintWriter(writer)))
		            .as(() -> "javac failed with message: " + writer).isZero();

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		URLClassLoader newClassLoader = new URLClassLoader(
		            new URL[] { new File(GENERATED_SOURCES_PREFIX).toURI().toURL() }, currentClassLoader);

		return JAXBContext.newInstance(packageName, newClassLoader);
	}

	/**
	 * Return values of all {@code <jaxb:class ref="..." />} attributes.
	 */
	private static Set<String> getClassReferencesFromEpisodeFile(String episodeFile) throws SAXException {
		DOMForest forest = new DOMForest(new XMLSchemaInternalizationLogic(), new Options());

		Document episodeDocument = forest.parse(new InputSource(episodeFile), true);

		NodeList nodeList = episodeDocument.getElementsByTagNameNS(Const.JAXB_NSURI, "class");
		Set<String> classReferences = new HashSet<>();

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			classReferences.add(((Element) nodeList.item(i)).getAttribute("ref"));
		}

		return classReferences;
	}
}
