/*
 * XmlElementWrapperPluginTest.java
 * 
 * Copyright (C) 2009, Tobias Warneke
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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Testcases for the XEW Plugin.
 * 
 * @author Tobias Warneke
 */
public class XmlElementWrapperPluginTest {

	private static final String PREGENERATED_SOURCES_PREFIX = "src/test/generated_resources/";
	private static final String GENERATED_SOURCES_PREFIX    = "target/test/generated_xsd_classes/";

	private static final Log    logger                      = LogFactory.getLog(XmlElementWrapperPluginTest.class);

	@Test
	public void testUsage() throws Exception {
		assertNotNull(new XmlElementWrapperPlugin().getUsage());
	}

	@Test(expected = BadCommandLineException.class)
	public void testUnknownOption() throws Exception {
		assertXsd("different-namespaces", new String[] { "-Xxew:unknown" }, false);
	}

	@Test
	public void testDifferentNamespacesForWrapperAndElement() throws Exception {
		// Plural form in this case will have no impact as all properties are already in plural:
		assertXsd("different-namespaces", new String[] { "-Xxew:delete", "-Xxew:collection", "java.util.LinkedList",
		        "-Xxew:instantiate", "lazy", "-Xxew:plural" }, false, "Container", "Entry", "package-info");
	}

	@Test
	public void testInnerElement() throws Exception {
		assertXsd("inner-element", new String[] { "-verbose", "-Xxew:delete", "-Xxew:instantiate none",
		        "-Xxew:include " + getClass().getResource("inner-element-includes.txt").getFile(),
		        "-Xxew:exclude " + getClass().getResource("inner-element-excludes.txt").getFile(), }, true,
		            "Filesystem", "Volumes");
	}

	@Test
	public void testInnerElementWithValueObjects() throws Exception {
		assertXsd("inner-element-value-objects", new String[] { "-debug", "-Xxew:delete" }, false, "Article",
		            "Articles", "ArticlesCollections", "Filesystem", "Publisher", "Volume", "impl.ArticleImpl",
		            "impl.ArticlesImpl", "impl.ArticlesCollectionsImpl", "impl.FilesystemImpl", "impl.PublisherImpl",
		            "impl.VolumeImpl", "impl.ObjectFactory", "impl.JAXBContextFactory");
	}

	@Test
	public void testAnnotationReference() throws Exception {
		// "Markup.java" cannot be verified for content because the content is changing from
		// one compilation to other as order of @XmlElementRef/@XmlElement annotations is not pre-defined
		// (set is used as their container).
		assertXsd("annotation-reference", new String[] { "-verbose", "-debug" }, false, "ClassCommon", "ClassesEu",
		            "ClassesUs", "ClassExt", "Markup", "Para", "SearchEu", "SearchMulti");
	}

	@Test
	public void testElementAsParametrisation1() throws Exception {
		assertXsd("element-as-parametrisation-1",
		            new String[] { "-Xxew:exclude "
		                        + getClass().getResource("element-as-parametrisation-1-excludes.txt").getFile() },
		            false, "Article", "Articles", "ArticlesCollections", "Publisher");
	}

	@Test
	public void testElementAsParametrisation2() throws Exception {
		assertXsd("element-as-parametrisation-2",
		            new String[] {
		                    "-Xxew:include "
		                                + getClass().getResource("element-as-parametrisation-2-includes.txt").getFile(),
		                    "-Xxew:summary " + GENERATED_SOURCES_PREFIX + "summary.txt" }, false, "Family",
		            "FamilyMember");

		String summaryFile = FileUtils.readFileToString(new File(GENERATED_SOURCES_PREFIX + "summary.txt"));

		assertTrue(summaryFile.contains("1 candidate(s) being considered"));
		assertTrue(summaryFile.contains("0 modification(s) to original code"));
		assertTrue(summaryFile.contains("0 deletion(s) from original code"));
	}

	@Test
	public void testElementWithParent() throws Exception {
		assertXsd("element-with-parent", new String[] { "-debug", "-Xxew:delete" }, false, "Alliance", "Group",
		            "Organization");
	}

	@Test
	public void testElementAny() throws Exception {
		assertXsd("element-any", new String[] { "-quiet", "-Xxew:delete", "-Xxew:plural" }, false, "Message");
	}

	@Test
	public void testElementAnyType() throws Exception {
		assertXsd("element-any-type", new String[] { "-Xxew:delete", "-Xxew:plural" }, false, "Conversion", "Entry");
	}

	@Test
	public void testElementMixed() throws Exception {
		// Most classes cannot be tested for content
		assertXsd("element-mixed", new String[] { "-debug", "-Xxew:delete" }, false, "B", "Br", "I", "AnyText",
		            "package-info");
	}

	@Test
	public void testElementListExtended1() throws Exception {
		// This run is configured from XSD (<xew:xew ... >):
		assertXsd("element-list-extended-1", null, false, "Foo");
	}

	@Test(expected = NullPointerException.class)
	public void testElementListExtended2() throws Exception {
		// This run is configured from XSD (<xew:xew ... >):
		assertXsd("element-list-extended-2", null, false, "CouponBookType", "CouponType", "CurrencyAmountType",
		            "MetaObjectType", "package-info");
	}

	@Test
	public void testElementScoped() throws Exception {
		// Most classes cannot be tested for content
		assertXsd("element-scoped", new String[] { "-debug", "-Xxew:delete" }, false, "Return", "package-info");
	}

	@Test
	public void testElementWithAdapter() throws Exception {
		// Plural form in this case will have no impact as there is property customization:
		assertXsd("element-with-adapter", new String[] { "-Xxew:delete", "-Xxew:plural" }, false, "Calendar",
		            "Adapter1");
	}

	@Test
	public void testElementWithCustomization() throws Exception {
		assertXsd("element-with-customization", new String[] { "-debug", "-Xxew:delete", "-Xxew:plural" }, false,
		            "PostOffice");
	}

	@Test
	public void testElementReservedWord() throws Exception {
		assertXsd("element-reserved-word", new String[] { "-Xxew:delete" }, false, "Class", "Method");
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
	private void assertXsd(String testName, String[] extraXewOptions, boolean generateEpisode, String... classesToCheck)
	            throws Exception {
		String resourceXsd = testName + ".xsd";
		String packageName = testName.replace('-', '_');

		// Force plugin to reinitialize the logger:
		System.clearProperty(XmlElementWrapperPlugin.COMMONS_LOGGING_LOG_LEVEL_PROPERTY_KEY);

		URL xsdUrl = getClass().getResource(resourceXsd);

		File targetDir = new File(GENERATED_SOURCES_PREFIX);

		targetDir.mkdirs();

		PrintStream loggingPrintStream = new PrintStream(new LoggingOutputStream(logger,
		            LoggingOutputStream.LogLevel.INFO, "[XJC] "));

		String[] opts = ArrayUtils.addAll(extraXewOptions, "-no-header", "-extension", "-Xxew", "-d",
		            targetDir.getPath(), xsdUrl.getFile());

		String episodeFile = new File(targetDir, "episode.xml").getPath();

		// Episode plugin should be triggered after Xew, see https://github.com/dmak/jaxb-xew-plugin/issues/6
		if (generateEpisode) {
			opts = ArrayUtils.addAll(opts, "-episode", episodeFile);
		}

		assertTrue("XJC compilation failed. Checked console for more info.",
		            Driver.run(opts, loggingPrintStream, loggingPrintStream) == 0);

		if (generateEpisode) {
			// FIXME: Episode file actually contains only value objects
			Set<String> classReferences = getClassReferencesFromEpisodeFile(episodeFile);

			assertEquals("Wrong number of classes in episode file", classesToCheck.length, classReferences.size());

			for (String className : classesToCheck) {
				assertTrue(className + " class is missing in episode file;",
				            classReferences.contains(packageName + "." + className));
			}
		}

		targetDir = new File(targetDir, packageName);

		Collection<String> generatedJavaSources = new HashSet<String>();

		// *.properties files are ignored:
		for (File targetFile : FileUtils.listFiles(targetDir, new String[] { "java" }, true)) {
			// This is effectively the path of targetFile relative to targetDir:
			generatedJavaSources.add(targetFile.getPath().substring(targetDir.getPath().length() + 1)
			            .replace('\\', '/'));
		}

		// This class is added and checked by default:
		classesToCheck = ArrayUtils.add(classesToCheck, "ObjectFactory");

		assertEquals("Wrong number of generated classes;", classesToCheck.length, generatedJavaSources.size());

		for (String className : classesToCheck) {
			className = className.replace('.', '/') + ".java";

			assertTrue(className + " is missing in target directory", generatedJavaSources.contains(className));
		}

		// Check the contents for those files which exist in resources:
		for (String className : classesToCheck) {
			className = className.replace('.', '/') + ".java";

			File sourceFile = new File(PREGENERATED_SOURCES_PREFIX + packageName, className);

			if (sourceFile.exists()) {
				// To avoid CR/LF conflicts:
				assertEquals("For " + className, FileUtils.readFileToString(sourceFile).replace("\r", ""), FileUtils
				            .readFileToString(new File(targetDir, className)).replace("\r", ""));
			}
		}

		JAXBContext jaxbContext = compileAndLoad(packageName, targetDir, generatedJavaSources);

		URL xmlTestFile = getClass().getResource(testName + ".xml");

		if (xmlTestFile != null) {
			StringWriter writer = new StringWriter();

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			unmarshaller.setSchema(schemaFactory.newSchema(xsdUrl));

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(unmarshaller.unmarshal(xmlTestFile), writer);

			Diff xmlDiff = new Diff(IOUtils.toString(xmlTestFile), writer.toString());

			// This listener ignores text nodes that differ only by leading/trailing whitespace:
			xmlDiff.overrideDifferenceListener(new DifferenceListener() {

				public int differenceFound(Difference difference) {
					if (difference.getId() == DifferenceConstants.TEXT_VALUE_ID
					            && difference.getControlNodeDetail().getValue().trim()
					                        .equals(difference.getTestNodeDetail().getValue().trim())) {
						return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
					}

					return RETURN_ACCEPT_DIFFERENCE;
				}

				public void skippedComparison(Node control, Node test) {
				}
			});

			assertXMLEqual("Generated XML is wrong: " + writer.toString(), xmlDiff, true);
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
	private JAXBContext compileAndLoad(String packageName, File targetDir, Collection<String> generatedJavaSources)
	            throws MalformedURLException, JAXBException {
		String[] javaSources = new String[generatedJavaSources.size()];

		int i = 0;
		for (String javaSource : generatedJavaSources) {
			javaSources[i++] = (new File(targetDir, javaSource)).toString();
		}

		StringWriter writer = new StringWriter();

		if (com.sun.tools.javac.Main.compile(javaSources, new PrintWriter(writer)) != 0) {
			fail("javac failed with message: " + writer.toString());
		}

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		URLClassLoader newClassLoader = new URLClassLoader(new URL[] { new File(GENERATED_SOURCES_PREFIX).toURI()
		            .toURL() }, currentClassLoader);

		return JAXBContext.newInstance(packageName, newClassLoader);
	}

	/**
	 * Return values of all {@code <jaxb:class ref="..." />} attributes.
	 */
	private Set<String> getClassReferencesFromEpisodeFile(String episodeFile) throws SAXException {
		DOMForest forest = new DOMForest(new XMLSchemaInternalizationLogic());

		Document episodeDocument = forest.parse(new InputSource(episodeFile), true);

		NodeList nodeList = episodeDocument.getElementsByTagNameNS(Const.JAXB_NSURI, "class");
		Set<String> classReferences = new HashSet<String>();

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			classReferences.add(((Element) nodeList.item(i)).getAttribute("ref"));
		}

		return classReferences;
	}
}
