
package element_with_customization;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
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
 *         &lt;element ref="{}type-of-message"/&gt;
 *         &lt;element ref="{}args"/&gt;
 *         &lt;element ref="{}class"/&gt;
 *         &lt;element ref="{}misc" minOccurs="0"/&gt;
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
    "typeOfMessages",
    "args",
    "classes",
    "misc"
})
@XmlRootElement(name = "post-office")
public class PostOffice {

    @XmlElement(required = true)
    protected Args args;
    @XmlElementWrapper(name = "class", required = true)
    @XmlElement(name = "name")
    protected Collection<String> classes = new Vector<String>();
    @XmlElementWrapper(name = "misc")
    @XmlAnyElement(lax = true)
    protected Collection<Object> misc = new LinkedList<Object>();
    @XmlElementWrapper(name = "type-of-message", required = true)
    @XmlElement(name = "type")
    protected Collection<String> typeOfMessages = new LinkedList<String>();

    /**
     * Gets the value of the args property.
     * 
     * @return
     *     possible object is
     *     {@link Args }
     *     
     */
    public Args getArgs() {
        return args;
    }

    /**
     * Sets the value of the args property.
     * 
     * @param value
     *     allowed object is
     *     {@link Args }
     *     
     */
    public void setArgs(Args value) {
        this.args = value;
    }

    public Collection<String> getTypeOfMessages() {
        return typeOfMessages;
    }

    public void setTypeOfMessages(Collection<String> typeOfMessages) {
        this.typeOfMessages = typeOfMessages;
    }

    public Collection<String> getClasses() {
        return classes;
    }

    public void setClasses(Collection<String> classes) {
        this.classes = classes;
    }

    public Collection<Object> getMisc() {
        return misc;
    }

    public void setMisc(Collection<Object> misc) {
        this.misc = misc;
    }

}
