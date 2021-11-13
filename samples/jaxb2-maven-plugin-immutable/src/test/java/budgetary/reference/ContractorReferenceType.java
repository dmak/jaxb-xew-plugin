
package budgetary.reference;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContractorReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContractorReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contractorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContractorReferenceType", propOrder = {
    "contractorId"
})
@XmlSeeAlso({
    PrimaryContractorReferenceType.class,
    SecondaryContractorReferenceType.class
})
public class ContractorReferenceType {

    private final String contractorId;

    public ContractorReferenceType(final String contractorId) {
        this.contractorId = contractorId;
    }

    /**
     * Used by JAX-B
     * 
     */
    protected ContractorReferenceType() {
        this.contractorId = null;
    }

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

}
