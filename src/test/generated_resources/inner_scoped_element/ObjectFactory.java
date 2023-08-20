
package inner_scoped_element;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the inner_scoped_element package. 
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

    private final static QName _CatalogueStockageStockageCollectionEffect_QNAME = new QName("", "effect");
    private final static QName _CatalogueStockageStockageCollectionTerm_QNAME = new QName("", "term");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_scoped_element
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Catalogue }
     * 
     */
    public Catalogue createCatalogue() {
        return new Catalogue();
    }

    /**
     * Create an instance of {@link Catalogue.StockageStockage }
     * 
     */
    public Catalogue.StockageStockage createCatalogueStockageStockage() {
        return new Catalogue.StockageStockage();
    }

    @XmlElementDecl(namespace = "", name = "effect", scope = Catalogue.StockageStockage.class)
    public JAXBElement<String> createCatalogueStockageStockageEffect(String value) {
        return new JAXBElement<String>(new QName("", "effect"), String.class, Catalogue.StockageStockage.class, value);
    }

    @XmlElementDecl(namespace = "", name = "term", scope = Catalogue.StockageStockage.class)
    public JAXBElement<String> createCatalogueStockageStockageTerm(String value) {
        return new JAXBElement<String>(new QName("", "term"), String.class, Catalogue.StockageStockage.class, value);
    }

}
