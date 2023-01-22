
package substitution_groups;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="customerType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="contact-infos" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element ref="{}contact-info" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "customerType", propOrder = {
    "contactInfos"
})
public class Customer {

    @XmlElementWrapper(name = "contact-infos")
    @XmlElementRef(name = "contact-info", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends ContactInfo>> contactInfos = new ArrayList<>();

    public List<JAXBElement<? extends ContactInfo>> getContactInfos() {
        return contactInfos;
    }

    public void setContactInfos(List<JAXBElement<? extends ContactInfo>> contactInfos) {
        this.contactInfos = contactInfos;
    }

}
