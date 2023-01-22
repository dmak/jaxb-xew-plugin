
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for classes-eu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="classes-eu">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="class-eu" type="{}class-common" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classes-eu", propOrder = {
    "classEu"
})
public class ClassesEu {

    @XmlElement(name = "class-eu", required = true)
    protected List<ClassCommon> classEu;

    /**
     * Gets the value of the classEu property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the classEu property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassEu().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassCommon }
     * 
     * 
     * @return
     *     The value of the classEu property.
     */
    public List<ClassCommon> getClassEu() {
        if (classEu == null) {
            classEu = new ArrayList<>();
        }
        return this.classEu;
    }

}
