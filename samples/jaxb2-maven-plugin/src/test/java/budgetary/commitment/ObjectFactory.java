
package budgetary.commitment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the budgetary.commitment package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ContractorReferences_QNAME = new QName("budgetary/commitment", "ContractorReferences");
    private final static QName _BudgetaryCommitment_QNAME = new QName("budgetary/commitment", "BudgetaryCommitment");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: budgetary.commitment
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BudgetaryCommitmentType }
     * 
     */
    public BudgetaryCommitmentType createBudgetaryCommitmentType() {
        return new BudgetaryCommitmentType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BudgetaryCommitmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "budgetary/commitment", name = "BudgetaryCommitment")
    public JAXBElement<BudgetaryCommitmentType> createBudgetaryCommitment(BudgetaryCommitmentType value) {
        return new JAXBElement<BudgetaryCommitmentType>(_BudgetaryCommitment_QNAME, BudgetaryCommitmentType.class, null, value);
    }

}
