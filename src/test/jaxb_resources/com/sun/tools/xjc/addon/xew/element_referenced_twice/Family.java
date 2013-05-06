
package element_referenced_twice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for family complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="family">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="family-member" type="{}family-member" maxOccurs="unbounded"/>
 *         &lt;element name="parent-member" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "family", propOrder = {
    "familyMember",
    "parentMember"
})
public class Family {

    @XmlElement(name = "family-member")
    protected List<FamilyMember> familyMember;
    @XmlElement(name = "parent-member")
    protected Object parentMember;

    /**
     * Gets the value of the familyMember property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the familyMember property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFamilyMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FamilyMember }
     * 
     * 
     */
    public List<FamilyMember> getFamilyMember() {
        if (familyMember == null) {
            familyMember = new ArrayList<FamilyMember>();
        }
        return this.familyMember;
    }

    /**
     * Gets the value of the parentMember property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getParentMember() {
        return parentMember;
    }

    /**
     * Sets the value of the parentMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setParentMember(Object value) {
        this.parentMember = value;
    }

}
