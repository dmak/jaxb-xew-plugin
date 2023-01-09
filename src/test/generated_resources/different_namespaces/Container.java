
package different_namespaces;

import java.util.LinkedList;
import java.util.List;
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
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <extension base="{http://example.com/container}base_container">
 *       <sequence>
 *         <element ref="{http://example.com/entries}entries" minOccurs="0"/>
 *         <element ref="{http://example.com/items}items"/>
 *         <element name="tests" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="test" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entries",
    "items",
    "tests"
})
@XmlRootElement(name = "container", namespace = "http://example.com/namespaces")
public class Container
    extends BaseContainer
{

    @XmlElementWrapper(name = "entries", namespace = "http://example.com/entries")
    @XmlElement(name = "entry", namespace = "http://example.com/entry")
    protected List<Entry> entries;
    @XmlElementWrapper(name = "items", required = true, namespace = "http://example.com/items")
    @XmlElement(name = "entry", namespace = "http://example.com/entry")
    protected List<Entry> items;
    @XmlElementWrapper(name = "tests", namespace = "http://example.com/namespaces")
    @XmlElement(name = "test", namespace = "http://example.com/namespaces")
    protected List<String> tests;

    public List<Entry> getEntries() {
        if (entries == null) {
            entries = new LinkedList<>();
        }
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<Entry> items) {
        this.items = items;
    }

    public List<String> getTests() {
        if (tests == null) {
            tests = new LinkedList<>();
        }
        return tests;
    }

    public void setTests(List<String> tests) {
        this.tests = tests;
    }

}
