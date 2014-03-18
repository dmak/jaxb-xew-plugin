package com.github.jaxb_xew_plugin.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.github.jaxb_xew_plugin.sample.container.Container;
import com.github.jaxb_xew_plugin.sample.entries.Entry;

import org.junit.Test;

public class SampleTest {

	@Test
	public void testUnmarshalling() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Container.class, Entry.class);

		Container container = (Container) context.createUnmarshaller().unmarshal(
					new File("xsd/different-namespaces.xml"));

		assertNotNull(container.getEntries());
		assertEquals(2, container.getEntries().size());
		assertEquals("son", container.getEntries().get(0).getName());
		assertEquals("daughter", container.getEntries().get(1).getName());
	}
}
