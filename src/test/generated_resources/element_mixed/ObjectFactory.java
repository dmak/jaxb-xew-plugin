
package element_mixed;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the element_mixed package. 
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

    private final static QName _IB_QNAME = new QName("http://foo.org/", "b");
    private final static QName _BI_QNAME = new QName("http://foo.org/", "i");
    private final static QName _PrefixedTextPrefix_QNAME = new QName("http://foo.org/", "prefix");
    private final static QName _PrefixedTextTitle_QNAME = new QName("http://foo.org/", "title");
    private final static QName _PrefixedTextSuffix_QNAME = new QName("http://foo.org/", "suffix");
    private final static QName _FixedTextYear_QNAME = new QName("http://foo.org/", "year");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: element_mixed
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AnyText }
     * 
     */
    public AnyText createAnyText() {
        return new AnyText();
    }

    /**
     * Create an instance of {@link B }
     * 
     */
    public B createB() {
        return new B();
    }

    /**
     * Create an instance of {@link I }
     * 
     */
    public I createI() {
        return new I();
    }

    /**
     * Create an instance of {@link Br }
     * 
     */
    public Br createBr() {
        return new Br();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link B }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link B }{@code >}
     */
    @XmlElementDecl(namespace = "http://foo.org/", name = "b", scope = I.class)
    public JAXBElement<B> createIB(B value) {
        return new JAXBElement<B>(_IB_QNAME, B.class, I.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link I }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link I }{@code >}
     */
    @XmlElementDecl(namespace = "http://foo.org/", name = "i", scope = B.class)
    public JAXBElement<I> createBI(I value) {
        return new JAXBElement<I>(_BI_QNAME, I.class, B.class, value);
    }

    @XmlElementDecl(namespace = "http://foo.org/", name = "title", scope = AnyText.class)
    public JAXBElement<String> createAnyTextTitle(String value) {
        return new JAXBElement<String>(new QName("http://foo.org/", "title"), String.class, AnyText.class, value);
    }

    @XmlElementDecl(namespace = "http://foo.org/", name = "year", scope = AnyText.class)
    public JAXBElement<Integer> createAnyTextYear(Integer value) {
        return new JAXBElement<Integer>(new QName("http://foo.org/", "year"), Integer.class, AnyText.class, value);
    }

    @XmlElementDecl(namespace = "http://foo.org/", name = "prefix", scope = AnyText.class)
    public JAXBElement<String> createAnyTextPrefix(String value) {
        return new JAXBElement<String>(new QName("http://foo.org/", "prefix"), String.class, AnyText.class, value);
    }

    @XmlElementDecl(namespace = "http://foo.org/", name = "suffix", scope = AnyText.class)
    public JAXBElement<String> createAnyTextSuffix(String value) {
        return new JAXBElement<String>(new QName("http://foo.org/", "suffix"), String.class, AnyText.class, value);
    }

}
