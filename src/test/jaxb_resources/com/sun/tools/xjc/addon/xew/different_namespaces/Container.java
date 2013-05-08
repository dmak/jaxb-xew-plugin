
package different_namespaces;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://example.com/entries}entries" minOccurs="0"/>
 *         &lt;element ref="{http://example.com/items}items"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entries",
    "items"
})
@XmlRootElement(name = "container", namespace = "http://example.com/container")
public class Container {

    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry", namespace = "http://example.com/entry")
    protected List<Entry> entries;
    @XmlElementWrapper(name = "items", required = true, namespace = "http://example.com/items")
    @XmlElement(name = "entry", namespace = "http://example.com/entry")
    protected List<Entry> items;

    public List<Entry> getEntries() {
        if (entries == null) {
            entries = new LinkedList<Entry>();
        }
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getItems() {
        if (items == null) {
            items = new LinkedList<Entry>();
        }
        return items;
    }

    public void setItems(List<Entry> items) {
        this.items = items;
    }

}
