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

	private static final String	PRECOMPILED_SOURCES_PREFIX	= "src/test/jaxb_resources/";
	private static final String	GENERATED_SOURCES_PREFIX	= "target/test/generated-xsd-classes/";

	private static final Log	logger						= LogFactory.getLog(XmlElementWrapperPluginTest.class);

	/**
	 * Simple start of the available sample file with XEW.
	 */
	@Test
	public void testSimpleStartTestWithXEW() throws Throwable {
		assertXsd("sample.xsd", "sample_with_xew", true, 3, "Order.java", "ObjectFactory.java");
	}

	/**
	 * Simple start of the available sample file without XEW.
	 */
	@Test
	public void testSimpleStartTestWithOutXEW() throws Throwable {
		assertXsd("sample.xsd", "sample_without_xew", false, 5, "Order.java", "ObjectFactory.java", "Items.java");
	}

	@Test
	public void testSimpleSpecialCase() throws Throwable {
		assertXsd("sample_specialcase.xsd", "sample_specialcase", true, 2, "AXTest.java", "ObjectFactory.java");
	}

	/**
	 * Standard test for XSD examples.
	 * 
	 * @param xsdUrl
	 *            URL to xsd file
	 * @param testName
	 *            subfolder of target/test/generated-xsd-classes where java - files are created
	 * @param useXewPlugin
	 *            creating with or without XEW plugin
	 * @param totalFiles
	 *            total number of generated files
	 * @param filesToCheck
	 *            Expected files in generation dir. These files content is checked, when compareDir is set.
	 * @return
	 * @throws Exception
	 */
	private void assertXsd(String resourceXsd, String testName, boolean useXewPlugin, int totalFiles,
				String... filesToCheck) throws Exception {
		String xsdUrl = getClass().getResource(resourceXsd).getFile();

		File target = new File(GENERATED_SOURCES_PREFIX + testName);

		target.mkdirs();

		LoggingPrintStream loggingPrintStream = new LoggingPrintStream();

		if (useXewPlugin) {
			Driver.run(new String[]{"-verbose", "-no-header", "-Xxew", "-Xxew:instantiate lazy", "-Xxew:delete", "-d",
					target.getPath(), xsdUrl}, loggingPrintStream, loggingPrintStream);
		}
		else {
			Driver.run(new String[]{"-verbose", "-no-header", "-d", target.getPath(), xsdUrl}, loggingPrintStream,
						loggingPrintStream);
		}

		String[] list = new File(target, "generated").list();

		List<String> l = Arrays.asList(list);

		assertEquals(totalFiles, list.length);

		for (String ftoc : filesToCheck) {
			assertTrue(l.contains(ftoc));
		}

		String compareDir = PRECOMPILED_SOURCES_PREFIX + getClass().getPackage().getName().replace('.', '/') + "/"
					+ testName;

		for (String ftoc : filesToCheck) {
			// To avoid CR/LF conflicts.
			assertEquals(FileUtils.readLines(new File(compareDir, ftoc)),
						FileUtils.readLines(new File(target, "generated/" + ftoc)));
		}
	}

	/**
	 * Class will redirect everything printed to {@link PrintStream} to logger.
	 */
	private static class LoggingPrintStream extends PrintStream {

		private final StringBuilder	sb	= new StringBuilder();

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
				case '\n' :
					logger.debug(sb.toString());

					sb.setLength(0);

				case '\r' :
					break;

				default :
					sb.append((char) b);
			}
		}
	}
}
