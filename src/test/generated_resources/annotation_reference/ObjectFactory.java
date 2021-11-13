
package annotation_reference;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


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

    private final static QName _Br_QNAME = new QName("", "br");
    private final static QName _Page_QNAME = new QName("", "page");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: annotation_reference
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Para }
     * 
     */
    public Para createPara() {
        return new Para();
    }

    /**
     * Create an instance of {@link Markup }
     * 
     */
    public Markup createMarkup() {
        return new Markup();
    }

    /**
     * Create an instance of {@link SearchEu }
     * 
     */
    public SearchEu createSearchEu() {
        return new SearchEu();
    }

    /**
     * Create an instance of {@link SearchMulti }
     * 
     */
    public SearchMulti createSearchMulti() {
        return new SearchMulti();
    }

    /**
     * Create an instance of {@link ClassCommon }
     * 
     */
    public ClassCommon createClassCommon() {
        return new ClassCommon();
    }

    /**
     * Create an instance of {@link ClassExt }
     * 
     */
    public ClassExt createClassExt() {
        return new ClassExt();
    }

    /**
     * Create an instance of {@link ClassesEu }
     * 
     */
    public ClassesEu createClassesEu() {
        return new ClassesEu();
    }

    /**
     * Create an instance of {@link ClassesUs }
     * 
     */
    public ClassesUs createClassesUs() {
        return new ClassesUs();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "br")
    public JAXBElement<Object> createBr(Object value) {
        return new JAXBElement<Object>(_Br_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "page")
    public JAXBElement<Object> createPage(Object value) {
        return new JAXBElement<Object>(_Page_QNAME, Object.class, null, value);
    }

}
