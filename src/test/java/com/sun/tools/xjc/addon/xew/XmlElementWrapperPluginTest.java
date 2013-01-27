package com.sun.tools.xjc.addon.xew;

import com.sun.tools.xjc.XJCFacade;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tobias Warneke
 */
public class XmlElementWrapperPluginTest {
	
	public XmlElementWrapperPluginTest() {
	}
	
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
	 * Test of getOptionName method, of class XmlElementWrapperPlugin.
	 */
	@Test
	public void testSimpleStartTest() throws Throwable {
		new File("target/generated/sample").mkdirs();
		XJCFacade.main(new String[] {"-no-header","-Xxew","-Xxew:instantiate lazy","-Xxew:delete","-d","target/generated/sample",this.getClass().getResource("sample.xsd").toString()});
	}
}
