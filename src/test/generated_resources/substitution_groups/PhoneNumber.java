
package substitution_groups;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for phoneNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneNumberType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}contactInfoType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="area-number" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="phone-number" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneNumberType", propOrder = {
    "areaNumber",
    "phoneNumber"
})
public class PhoneNumber
    extends ContactInfo
{

    @XmlElement(name = "area-number", required = true)
    protected String areaNumber;
    @XmlElement(name = "phone-number", required = true)
    protected String phoneNumber;

    /**
     * Gets the value of the areaNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaNumber() {
        return areaNumber;
    }

    /**
     * Sets the value of the areaNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaNumber(String value) {
        this.areaNumber = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

}
