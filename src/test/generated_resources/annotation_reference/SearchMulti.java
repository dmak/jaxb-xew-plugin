
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for search-multi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="search-multi">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <choice maxOccurs="unbounded">
 *         <element name="classes-eu" type="{}classes-eu"/>
 *         <element name="classes-us" type="{}classes-us"/>
 *       </choice>
 *       <attribute name="mode" use="required" fixed="OTH">
 *         <simpleType>
 *           <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             <minLength value="3"/>
 *             <maxLength value="3"/>
 *           </restriction>
 *         </simpleType>
 *       </attribute>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
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
    /**
     * 
     * 
     */
    @XmlAttribute(name = "mode", required = true)
    public final static String MODE = "OTH";

    /**
     * Gets the value of the classesEuOrClassesUs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the classesEuOrClassesUs property.
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
     * @return
     *     The value of the classesEuOrClassesUs property.
     */
    public List<Object> getClassesEuOrClassesUs() {
        if (classesEuOrClassesUs == null) {
            classesEuOrClassesUs = new ArrayList<>();
        }
        return this.classesEuOrClassesUs;
    }

}
