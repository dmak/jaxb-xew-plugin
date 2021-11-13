
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
public final class SecondaryContractorReferenceType
    extends ContractorReferenceType
{

    private final String secondaryId;

    public SecondaryContractorReferenceType(final String contractorId, final String secondaryId) {
        super(contractorId);
        this.secondaryId = secondaryId;
    }

    /**
     * Used by JAX-B
     * 
     */
    protected SecondaryContractorReferenceType() {
        super(null);
        this.secondaryId = null;
    }

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

}
