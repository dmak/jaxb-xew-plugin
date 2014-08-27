
package encoding;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the encoding package. 
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

    private final static QName _Foo_QNAME = new QName("", "foo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: encoding
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Bar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "foo")
    public JAXBElement<Bar> createFoo(Bar value) {
        return new JAXBElement<Bar>(_Foo_QNAME, Bar.class, null, value);
    }

}
