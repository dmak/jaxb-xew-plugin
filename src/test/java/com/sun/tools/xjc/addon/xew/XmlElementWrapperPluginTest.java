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
import java.util.Arrays;
import java.util.List;

import com.sun.tools.xjc.Driver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

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
		assertXsd("different-namespaces-for-wrapper-and-element.xsd", "different_namespaces",
		            "-Xxew:collection java.util.LinkedList", 4, "Composed.java", "ObjectFactory.java");
	}

	@Test
	public void testInnerElementClass() throws Throwable {
		assertXsd("inner-element-class.xsd", "inner_element_class", "-Xxew:instantiate lazy", 3, "Filesystem.java",
		            "Volume.java", "ObjectFactory.java");
	}

	@Test
	public void testAnnotationReferenceInChoice() throws Throwable {
		// "Markup.java" cannot be tested for content because it contents is changing from one compilation to other
		// as order of @XmlElementRef annotations is not pre-defined (set is used as container).
		assertXsd("annotation-reference-in-choice.xsd", "annotation_reference", "-debug", 3, "Sub.java",
		            "ObjectFactory.java");
	}

	@Test
	public void testElementWithParent() throws Throwable {
		assertXsd("element-with-parent.xsd", "element_with_parent", "-verbose", 3, "Group.java", "Organization.java",
		            "ObjectFactory.java");
	}

	@Test
	public void testElementReferencedTwice() throws Throwable {
		assertXsd("element-referenced-twice.xsd", "element_referenced_twice", "-Xxew:summaryFile "
		            + GENERATED_SOURCES_PREFIX + "summary.txt", 3, "Family.java", "FamilyMember.java",
		            "ObjectFactory.java");
	}

	@Test
	public void testElementAny() throws Throwable {
		assertXsd("element-any.xsd", "element_any", "-verbose", 2, "Data.java", "ObjectFactory.java");
	}

	/**
	 * Standard test for XSD examples.
	 * 
	 * @param xsdUrl
	 *            URL to xsd file
	 * @param testName
	 *            the name of this test, also target package name
	 * @param totalNumberOfFiles
	 *            total number of generated files
	 * @param extraXxewOption
	 *            to be passed to plugin
	 * @param filesToCheck
	 *            expected files in target directory; these files content is checked
	 */
	private void assertXsd(String resourceXsd, String testName, String extraXxewOption, int totalNumberOfFiles,
	            String... filesToCheck) throws Exception {
		String xsdUrl = getClass().getResource(resourceXsd).getFile();

		File target = new File(GENERATED_SOURCES_PREFIX);

		target.mkdirs();

		LoggingPrintStream loggingPrintStream = new LoggingPrintStream();

		Driver.run(new String[] { "-no-header", extraXxewOption, "-Xxew", "-Xxew:delete", "-d", target.getPath(),
		        xsdUrl }, loggingPrintStream, loggingPrintStream);

		target = new File(target, testName);

		String[] list = target.list();

		List<String> generatedFileList = Arrays.asList(list);

		assertEquals(totalNumberOfFiles, list.length);

		for (String fileName : filesToCheck) {
			assertTrue(generatedFileList.contains(fileName));
		}

		String compareToDir = PREGENERATED_SOURCES_PREFIX + getClass().getPackage().getName().replace('.', '/') + "/"
		            + testName;

		for (String fileName : filesToCheck) {
			// To avoid CR/LF conflicts:
			assertEquals("For file \"" + fileName + "\"", FileUtils.readFileToString(new File(compareToDir, fileName))
			            .replace("\r", ""), FileUtils.readFileToString(new File(target, fileName)).replace("\r", ""));
		}
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
			logger.debug("[XJC] " + sb.toString());

			sb.setLength(0);
		}
	}
}
