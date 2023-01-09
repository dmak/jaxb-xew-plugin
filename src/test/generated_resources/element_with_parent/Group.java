
package element_with_parent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.sun.tools.xjc.addon.xew.CommonBean;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for group complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="group">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="person" maxOccurs="unbounded">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <all>
 *                   <element name="first-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   <element name="second-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 </all>
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
@XmlType(name = "group", propOrder = {
    "person"
})
@XmlSeeAlso({
    Organization.class
})
public class Group
    extends CommonBean
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected List<Group.Person> person;

    /**
     * Gets the value of the person property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the person property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group.Person }
     * 
     * 
     * @return
     *     The value of the person property.
     */
    public List<Group.Person> getPerson() {
        if (person == null) {
            person = new ArrayList<>();
        }
        return this.person;
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
     *       <all>
     *         <element name="first-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         <element name="second-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       </all>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Person
        extends CommonBean
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "first-name", required = true)
        protected String firstName;
        @XmlElement(name = "second-name", required = true)
        protected String secondName;

        /**
         * Gets the value of the firstName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         * Sets the value of the firstName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFirstName(String value) {
            this.firstName = value;
        }

        /**
         * Gets the value of the secondName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSecondName() {
            return secondName;
        }

        /**
         * Sets the value of the secondName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSecondName(String value) {
            this.secondName = value;
        }

    }

}
