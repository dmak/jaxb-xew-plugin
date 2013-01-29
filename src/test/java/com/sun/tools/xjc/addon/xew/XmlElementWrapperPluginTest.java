package com.sun.tools.xjc.addon.xew;

import com.sun.tools.xjc.Driver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tobias Warneke
 */
public class XmlElementWrapperPluginTest {

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Simple start of the available sample file with XEW.
	 */
	@Test
	public void testSimpleStartTestWithXEW() throws Throwable {
		assertEquals(2, assertXsd(this.getClass().getResource("sample.xsd").toString(), "sample_with_xew", true, "src/test/resources/com/sun/tools/xjc/addon/xew/sample_java", "Order.java", "ObjectFactory.java"));
	}

	/**
	 * Simple start of the available sample file without XEW.
	 */
	@Test
	public void testSimpleStartTestWithOutXEW() throws Throwable {
		assertEquals(3, assertXsd(this.getClass().getResource("sample.xsd").toString(), "sample_without_xew", false, null, "Order.java", "ObjectFactory.java", "Items.java"));
	}

	/**
	 * Stanardtest for XSD examples.
	 *
	 * @param xsdUrl URL to xsd file
	 * @param generateDir subfolder of target/test/generated-xsd-classes where
	 * java - files are created
	 * @param useXEWPlugin creating with or without XEW plugin
	 * @param compareDir Exected sourcecodefiles. To content check will be done,
	 * if this is null.
	 * @param filesToCheck Expected files in generation dir. These files content
	 * is checked, when compareDir is set.
	 * @return
	 * @throws Exception
	 */
	private int assertXsd(String xsdUrl, String generateDir, boolean useXEWPlugin, String compareDir, String... filesToCheck) throws Exception {
		final File target = new File("target/test/generated-xsd-classes/" + generateDir);
		target.mkdirs();

		if (useXEWPlugin) {
			Driver.run(new String[]{"-verbose", "-no-header", "-Xxew", "-Xxew:instantiate lazy", "-Xxew:delete", "-d", target.getPath(), xsdUrl}, System.out, System.out);
		} else {
			Driver.run(new String[]{"-verbose", "-no-header", "-d", target.getPath(), xsdUrl}, System.out, System.out);
		}

		String[] list = new File(target, "generated").list();

		List<String> l = Arrays.asList(list);

		for (String ftoc : filesToCheck) {
			assertTrue(l.contains(ftoc));
		}

		if (compareDir != null) {
			for (String ftoc : filesToCheck) {
				//To avoid Linefeed conflicts.
				assertEquals(FileUtils.readLines(new File(compareDir, ftoc)),
						FileUtils.readLines(new File(target, "generated/" + ftoc)));
			}
		}
		return list.length;
	}
}
