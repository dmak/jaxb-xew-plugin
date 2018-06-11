
package com.github.jaxb_xew_plugin.sample.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.github.jaxb_xew_plugin.sample.entries.Entry;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="entries" type="{http://sample.jaxb-xew-plugin.github.com/entries}entries"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entries"
})
@XmlRootElement(name = "container")
public final class Container {

    @XmlElementWrapper(required = true)
    @XmlElement(name = "entry", namespace = "http://sample.jaxb-xew-plugin.github.com/entries")
    private final List<Entry> entries;

    /**
     * Used by JAX-B
     * 
     */
    protected Container() {
        this.entries = null;
    }

    public Container(final List<Entry> entries) {
        if (entries == null) {
            this.entries = null;
        } else {
            this.entries = new ArrayList<Entry>(entries);
        }
    }

    public List<Entry> getEntries() {
        List<Entry> ret;
        if (entries == null) {
            ret = Collections.emptyList();
        } else {
            ret = Collections.unmodifiableList(entries);
        }
        return ret;
    }

}
