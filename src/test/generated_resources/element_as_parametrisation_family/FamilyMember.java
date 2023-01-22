
package element_as_parametrisation_family;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for family-member complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="family-member">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="id" maxOccurs="unbounded">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="number" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   <element name="type" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "family-member", propOrder = {
    "id"
})
public class FamilyMember {

    @XmlElement(required = true)
    protected List<FamilyMember.Id> id;

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FamilyMember.Id }
     * 
     * 
     * @return
     *     The value of the id property.
     */
    public List<FamilyMember.Id> getId() {
        if (id == null) {
            id = new ArrayList<>();
        }
        return this.id;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="number" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         <element name="type" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "number",
        "type"
    })
    public static class Id {

        @XmlElement(required = true)
        protected Object number;
        @XmlElement(required = true)
        protected Object type;

        /**
         * Gets the value of the number property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getNumber() {
            return number;
        }

        /**
         * Sets the value of the number property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setNumber(Object value) {
            this.number = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setType(Object value) {
            this.type = value;
        }

    }

}
