package com.sun.tools.xjc.addon.xew;

import static com.sun.tools.xjc.addon.xew.XmlElementWrapperPluginTest.assertXsd;

import org.junit.Test;

public class XmlElementWrapperPluginSpecificTest {

	/**
	 * This test unfortunately does not run on Java8.
	 */
	@Test
	public void testDifferentNamespacesForWrapperAndElement() throws Exception {
		// Plural form in this case will have no impact as all properties are already in plural:
		assertXsd("different-namespaces", new String[] { "-Xxew:collection", "java.util.LinkedList",
		        "-Xxew:instantiate", "lazy", "-Xxew:plural" }, false, "BaseContainer", "Container", "Entry",
		            "package-info");
	}
}
