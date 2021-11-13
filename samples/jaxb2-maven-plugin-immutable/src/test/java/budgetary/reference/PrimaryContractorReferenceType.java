
package budgetary.reference;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrimaryContractorReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrimaryContractorReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{budgetary/reference}ContractorReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="primaryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimaryContractorReferenceType", propOrder = {
    "primaryId"
})
public final class PrimaryContractorReferenceType
    extends ContractorReferenceType
{

    private final String primaryId;

    public PrimaryContractorReferenceType(final String contractorId, final String primaryId) {
        super(contractorId);
        this.primaryId = primaryId;
    }

    /**
     * Used by JAX-B
     * 
     */
    protected PrimaryContractorReferenceType() {
        super(null);
        this.primaryId = null;
    }

    /**
     * Gets the value of the primaryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryId() {
        return primaryId;
    }

}
