
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for search-multi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-multi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="classes-eu" type="{}classes-eu"/>
 *         &lt;element name="classes-us" type="{}classes-us"/>
 *       &lt;/choice>
 *       &lt;attribute name="mode" use="required" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-multi", propOrder = {
    "classesEuOrClassesUs"
})
public class SearchMulti {

    @XmlElements({
        @XmlElement(name = "classes-eu", type = ClassesEu.class),
        @XmlElement(name = "classes-us", type = ClassesUs.class)
    })
    protected List<Object> classesEuOrClassesUs;
    @XmlAttribute(name = "mode", required = true)
    protected byte mode;

    /**
     * Gets the value of the classesEuOrClassesUs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classesEuOrClassesUs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassesEuOrClassesUs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassesEu }
     * {@link ClassesUs }
     * 
     * 
     */
    public List<Object> getClassesEuOrClassesUs() {
        if (classesEuOrClassesUs == null) {
            classesEuOrClassesUs = new ArrayList<Object>();
        }
        return this.classesEuOrClassesUs;
    }

    /**
     * Gets the value of the mode property.
     * 
     */
    public byte getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     */
    public void setMode(byte value) {
        this.mode = value;
    }

}
