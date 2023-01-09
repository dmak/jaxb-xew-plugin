
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

    private final static QName _CatalogueStockageCollectionStockageCollectionEffect_QNAME = new QName("", "effect");
    private final static QName _CatalogueStockageCollectionStockageCollectionTerm_QNAME = new QName("", "term");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_scoped_element
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Catalogue }
     * 
     * @return
     *     the new instance of {@link Catalogue }
     */
    public Catalogue createCatalogue() {
        return new Catalogue();
    }

    /**
     * Create an instance of {@link Catalogue.Stockage }
     * 
     * @return
     *     the new instance of {@link Catalogue.Stockage }
     */
    public Catalogue.Stockage createCatalogueStockage() {
        return new Catalogue.Stockage();
    }

    @XmlElementDecl(namespace = "", name = "effect", scope = Catalogue.Stockage.class)
    public JAXBElement<String> createCatalogueStockageEffect(String value) {
        return new JAXBElement<>(new QName("", "effect"), String.class, Catalogue.Stockage.class, value);
    }

    @XmlElementDecl(namespace = "", name = "term", scope = Catalogue.Stockage.class)
    public JAXBElement<String> createCatalogueStockageTerm(String value) {
        return new JAXBElement<>(new QName("", "term"), String.class, Catalogue.Stockage.class, value);
    }

}
