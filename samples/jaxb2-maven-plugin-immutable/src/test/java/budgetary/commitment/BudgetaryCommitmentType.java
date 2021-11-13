
package budgetary.commitment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import budgetary.reference.ContractorReferenceType;
import budgetary.reference.PrimaryContractorReferenceType;
import budgetary.reference.SecondaryContractorReferenceType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BudgetaryCommitmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BudgetaryCommitmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{budgetary/commitment}ContractorReferences" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BudgetaryCommitmentType", propOrder = {
    "contractorReferences"
})
public final class BudgetaryCommitmentType {

    @XmlElementWrapper(name = "ContractorReferences")
    @XmlElements({
        @XmlElement(name = "PrimaryContractorReference", type = PrimaryContractorReferenceType.class, namespace = "budgetary/reference"),
        @XmlElement(name = "SecondaryContractorReference", type = SecondaryContractorReferenceType.class, namespace = "budgetary/reference")
    })
    private final List<ContractorReferenceType> contractorReferences;

    public BudgetaryCommitmentType(final List<ContractorReferenceType> contractorReferences) {
        if (contractorReferences == null) {
            this.contractorReferences = null;
        } else {
            this.contractorReferences = new ArrayList<ContractorReferenceType>(contractorReferences);
        }
    }

    /**
     * Used by JAX-B
     * 
     */
    protected BudgetaryCommitmentType() {
        this.contractorReferences = null;
    }

    public List<ContractorReferenceType> getContractorReferences() {
        List<ContractorReferenceType> ret;
        if (contractorReferences == null) {
            ret = Collections.emptyList();
        } else {
            ret = Collections.unmodifiableList(contractorReferences);
        }
        return ret;
    }

}
