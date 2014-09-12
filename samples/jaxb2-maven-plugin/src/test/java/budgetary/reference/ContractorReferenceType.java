
package budgetary.reference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContractorReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContractorReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contractorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContractorReferenceType", propOrder = {
    "contractorId"
})
@XmlSeeAlso({
    SecondaryContractorReferenceType.class,
    PrimaryContractorReferenceType.class
})
public class ContractorReferenceType {

    protected String contractorId;

    /**
     * Gets the value of the contractorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractorId() {
        return contractorId;
    }

    /**
     * Sets the value of the contractorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractorId(String value) {
        this.contractorId = value;
    }

}
