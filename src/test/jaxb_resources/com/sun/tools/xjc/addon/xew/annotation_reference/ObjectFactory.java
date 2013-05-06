
package annotation_reference;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the annotation_reference package. 
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

    private final static QName _U_QNAME = new QName("", "u");
    private final static QName _P_QNAME = new QName("", "p");
    private final static QName _Br_QNAME = new QName("", "br");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: annotation_reference
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Sub }
     * 
     */
    public Sub createSub() {
        return new Sub();
    }

    /**
     * Create an instance of {@link Markup }
     * 
     */
    public Markup createMarkup() {
        return new Markup();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Markup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "u")
    public JAXBElement<Markup> createU(Markup value) {
        return new JAXBElement<Markup>(_U_QNAME, Markup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "p")
    public JAXBElement<Object> createP(Object value) {
        return new JAXBElement<Object>(_P_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "br")
    public JAXBElement<Object> createBr(Object value) {
        return new JAXBElement<Object>(_Br_QNAME, Object.class, null, value);
    }

}
