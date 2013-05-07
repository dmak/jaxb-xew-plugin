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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Testcases for the XEW Plugin
 * 
 * @author Tobias Warneke
 */
public class XmlElementWrapperPluginTest {

	private static final String PREGENERATED_SOURCES_PREFIX = "src/test/jaxb_resources/";
	private static final String GENERATED_SOURCES_PREFIX    = "target/test/generated-xsd-classes/";

	private static final Log    logger                      = LogFactory.getLog(XmlElementWrapperPluginTest.class);

	@Test
	public void testDifferentNamespacesForWrapperAndElement() throws Throwable {
		assertXsd("different-namespaces-for-wrapper-and-element.xsd", "different_namespaces", new String[] {
		        "-Xxew:collection java.util.LinkedList", "-Xxew:instantiate lazy" }, false, 4, "Composed");
	}

	@Test
	public void testInnerElementClass() throws Throwable {
		assertXsd("inner-element-class.xsd", "inner_element_class", new String[] { "-debug",
		        "-Xxew:includeFile " + getClass().getResource("inner-element-class-includes.txt").getFile() }, true, 3,
		            "Filesystem", "Volume");
	}

	@Test
	public void testAnnotationReferenceInChoice() throws Throwable {
		// "Markup.java" cannot be tested for content because it contents is changing from one compilation to other
		// as order of @XmlElementRef annotations is not pre-defined (set is used as container).
		assertXsd("annotation-reference-in-choice.xsd", "annotation_reference", new String[] { "-debug", "-verbose" },
		            false, 3, "Markup", "Sub");
	}

	@Test
	public void testElementWithParent() throws Throwable {
		assertXsd("element-with-parent.xsd", "element_with_parent", null, false, 3, "Group", "Organization");
	}

	@Test
	public void testElementReferencedTwice() throws Throwable {
		assertXsd("element-referenced-twice.xsd", "element_referenced_twice", new String[] { "-Xxew:summaryFile "
		            + GENERATED_SOURCES_PREFIX + "summary.txt" }, false, 3, "Family", "FamilyMember");
	}

	@Test
	public void testElementAny() throws Throwable {
		assertXsd("element-any.xsd", "element_any", null, false, 2, "Data");
	}

	/**
	 * Standard test for XSD examples.
	 * 
	 * @param resourceXsd
	 *            XSD file name
	 * @param packageName
	 *            target package name
	 * @param extraXewOptions
	 *            to be passed to plugin
	 * @param generateEpisode
	 *            generate episode file and check the list of classes included into it
	 * @param totalNumberOfFiles
	 *            total number of generated files, including {@code ObjectFactory.java} and {@code package-info.java}.
	 * @param classesToCheck
	 *            expected classes/files in target directory; these files content is checked if it is present in
	 *            resources directory; {@code ObjectFactory.java} is automatically included
	 */
	private void assertXsd(String resourceXsd, String packageName, String[] extraXewOptions, boolean generateEpisode,
	            int totalNumberOfFiles, String... classesToCheck) throws Exception {
		String xsdUrl = getClass().getResource(resourceXsd).getFile();

		File targetDir = new File(GENERATED_SOURCES_PREFIX);

		targetDir.mkdirs();

		LoggingPrintStream loggingPrintStream = new LoggingPrintStream();

		String[] opts = ArrayUtils.addAll(extraXewOptions, "-no-header", "-Xxew", "-Xxew:delete", "-d",
		            targetDir.getPath(), xsdUrl);

		String episodeFile = new File(targetDir, "episode.xml").getPath();

		// Episode plugin should be triggered after Xew, see https://github.com/dmak/jaxb-xew-plugin/issues/6
		if (generateEpisode) {
			opts = ArrayUtils.addAll(opts, "-episode", episodeFile);
		}

		assertTrue("XJC compilation failed. Checked console for more info.",
		            Driver.run(opts, loggingPrintStream, loggingPrintStream) == 0);

		if (generateEpisode) {
			Set<String> classReferences = getClassReferencesFromEpisodeFile(episodeFile);

			assertEquals(classesToCheck.length, classReferences.size());

			for (String className : classesToCheck) {
				assertTrue(className + " class is missing in episode file",
				            classReferences.contains(packageName + "." + className));
			}
		}

		targetDir = new File(targetDir, packageName);

		Set<String> generatedFileList = new HashSet<String>();

		Collections.addAll(generatedFileList, targetDir.list());

		assertEquals(totalNumberOfFiles, generatedFileList.size());

		// This class is added and checked by default:
		classesToCheck = ArrayUtils.add(classesToCheck, "ObjectFactory");

		for (String className : classesToCheck) {
			className += ".java";

			assertTrue(className + " class is missing in target directory", generatedFileList.contains(className));
		}

		String preGeneratedDir = PREGENERATED_SOURCES_PREFIX + getClass().getPackage().getName().replace('.', '/')
		            + "/" + packageName;

		// Check the contents for those files which exist in resources:
		for (String className : classesToCheck) {
			className += ".java";

			File sourceFile = new File(preGeneratedDir, className);

			if (sourceFile.exists()) {
				// To avoid CR/LF conflicts:
				assertEquals("For " + className, FileUtils.readFileToString(sourceFile).replace("\r", ""), FileUtils
				            .readFileToString(new File(targetDir, className)).replace("\r", ""));
			}
		}
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

	/**
	 * Class will redirect everything printed to this {@link PrintStream} to logger.
	 */
	private static class LoggingPrintStream extends PrintStream {

		private final StringBuilder sb = new StringBuilder();

		public LoggingPrintStream() {
			super(new NullOutputStream());
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			for (int i = 0; i < len; i++) {
				write(buf[off + i]);
			}
		}

		@Override
		public void write(int b) {
			switch (b) {
			case '\n':
				logMessage();

			case '\r':
				break;

			default:
				sb.append((char) b);
			}
		}

		@Override
		public void close() {
			super.close();

			if (sb.length() > 0) {
				logMessage();
			}
		}

		private void logMessage() {
			logger.info("[XJC] " + sb.toString());

			sb.setLength(0);
		}
	}
}
