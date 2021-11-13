
package element_mixed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="formatted-text" type="{http://foo.org/}formatted-text"/&gt;
 *         &lt;element name="fixed-text" type="{http://foo.org/}fixed-text"/&gt;
 *         &lt;element name="prefixed-text" type="{http://foo.org/}prefixed-text"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "formattedText",
    "fixedText",
    "prefixedText"
})
@XmlRootElement(name = "any-text")
public class AnyText {

    @XmlElementWrapper(name = "formatted-text", required = true)
    @XmlElements({
        @XmlElement(name = "i", type = I.class, namespace = "http://foo.org/"),
        @XmlElement(name = "b", type = B.class, namespace = "http://foo.org/"),
        @XmlElement(name = "br", type = Br.class, namespace = "http://foo.org/")
    })
    protected List<Object> formattedText = new ArrayList<Object>();
    @XmlElementWrapper(name = "fixed-text", required = true)
    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "title", namespace = "http://foo.org/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "year", namespace = "http://foo.org/", type = JAXBElement.class, required = false)
    })
    protected List<Serializable> fixedText = new ArrayList<Serializable>();
    @XmlElementWrapper(name = "prefixed-text", required = true)
    @XmlElementRefs({
        @XmlElementRef(name = "prefix", namespace = "http://foo.org/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "title", namespace = "http://foo.org/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "suffix", namespace = "http://foo.org/", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<String>> prefixedText = new ArrayList<JAXBElement<String>>();

    public List<Object> getFormattedText() {
        return formattedText;
    }

    public void setFormattedText(List<Object> formattedText) {
        this.formattedText = formattedText;
    }

    public List<Serializable> getFixedText() {
        return fixedText;
    }

    public void setFixedText(List<Serializable> fixedText) {
        this.fixedText = fixedText;
    }

    public List<JAXBElement<String>> getPrefixedText() {
        return prefixedText;
    }

    public void setPrefixedText(List<JAXBElement<String>> prefixedText) {
        this.prefixedText = prefixedText;
    }

}
