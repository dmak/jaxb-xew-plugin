
package budgetary.reference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SecondaryContractorReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecondaryContractorReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{budgetary/reference}ContractorReferenceType">
 *       &lt;sequence>
 *         &lt;element name="secondaryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecondaryContractorReferenceType", propOrder = {
    "secondaryId"
})
public class SecondaryContractorReferenceType
    extends ContractorReferenceType
{

    protected String secondaryId;

    /**
     * Gets the value of the secondaryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryId() {
        return secondaryId;
    }

    /**
     * Sets the value of the secondaryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryId(String value) {
        this.secondaryId = value;
    }

}
