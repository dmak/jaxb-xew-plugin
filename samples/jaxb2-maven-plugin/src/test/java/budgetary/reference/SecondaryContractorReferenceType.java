
package budgetary.reference;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SecondaryContractorReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecondaryContractorReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{budgetary/reference}ContractorReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="secondaryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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

    public SecondaryContractorReferenceType withSecondaryId(String value) {
        setSecondaryId(value);
        return this;
    }

    @Override
    public SecondaryContractorReferenceType withContractorId(String value) {
        setContractorId(value);
        return this;
    }

}
