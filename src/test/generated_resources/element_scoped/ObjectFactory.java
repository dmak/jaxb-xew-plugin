
package element_scoped;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the element_scoped package. 
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

    private final static QName _Age_QNAME = new QName("http://example.com/extra", "age");
    private final static QName _ReturnUsersName_QNAME = new QName("http://example.com/scope", "name");
    private final static QName _ReturnUsersAge_QNAME = new QName("http://example.com/scope", "age");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: element_scoped
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Return }
     * 
     */
    public Return createReturn() {
        return new Return();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://example.com/extra", name = "age")
    public JAXBElement<String> createAge(String value) {
        return new JAXBElement<String>(_Age_QNAME, String.class, null, value);
    }

    @XmlElementDecl(namespace = "http://example.com/scope", name = "name", scope = Return.class)
    public JAXBElement<String> createReturnName(String value) {
        return new JAXBElement<String>(new QName("http://example.com/scope", "name"), String.class, Return.class, value);
    }

    @XmlElementDecl(namespace = "http://example.com/scope", name = "age", scope = Return.class)
    public JAXBElement<String> createReturnAge(String value) {
        return new JAXBElement<String>(new QName("http://example.com/scope", "age"), String.class, Return.class, value);
    }

}
