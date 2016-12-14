
package budgetary.commitment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import budgetary.reference.ContractorReferenceType;
import budgetary.reference.PrimaryContractorReferenceType;
import budgetary.reference.SecondaryContractorReferenceType;


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
public class BudgetaryCommitmentType {

    @XmlElementWrapper(name = "ContractorReferences")
    @XmlElements({
        @XmlElement(name = "PrimaryContractorReference", type = PrimaryContractorReferenceType.class, namespace = "budgetary/reference"),
        @XmlElement(name = "SecondaryContractorReference", type = SecondaryContractorReferenceType.class, namespace = "budgetary/reference")
    })
    protected List<ContractorReferenceType> contractorReferences = new ArrayList<ContractorReferenceType>();

    public List<ContractorReferenceType> getContractorReferences() {
        return contractorReferences;
    }

    public void setContractorReferences(List<ContractorReferenceType> contractorReferences) {
        this.contractorReferences = contractorReferences;
    }

    public BudgetaryCommitmentType withContractorReferences(ContractorReferenceType... values) {
        if (values!= null) {
            for (ContractorReferenceType value: values) {
                getContractorReferences().add(value);
            }
        }
        return this;
    }

    public BudgetaryCommitmentType withContractorReferences(Collection<ContractorReferenceType> values) {
        if (values!= null) {
            getContractorReferences().addAll(values);
        }
        return this;
    }

    public BudgetaryCommitmentType withContractorReferences(List<ContractorReferenceType> contractorReferences) {
        setContractorReferences(contractorReferences);
        return this;
    }

}
