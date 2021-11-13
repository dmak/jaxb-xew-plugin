
package com.github.jaxb_xew_plugin.sample.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.github.jaxb_xew_plugin.sample.entries.Entry;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
public class Container {

    @XmlElementWrapper(required = true)
    @XmlElement(name = "entry", namespace = "http://sample.jaxb-xew-plugin.github.com/entries")
    protected List<Entry> entries = new ArrayList<Entry>();

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Container withEntries(Entry... values) {
        if (values!= null) {
            for (Entry value: values) {
                getEntries().add(value);
            }
        }
        return this;
    }

    public Container withEntries(Collection<Entry> values) {
        if (values!= null) {
            getEntries().addAll(values);
        }
        return this;
    }

    public Container withEntries(List<Entry> entries) {
        setEntries(entries);
        return this;
    }

}
