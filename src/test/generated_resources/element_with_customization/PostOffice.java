
package element_with_customization;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}type-of-message"/>
 *         &lt;element ref="{}class"/>
 *         &lt;element ref="{}misc" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "typeOfMessages",
    "classes",
    "miscs"
})
@XmlRootElement(name = "post-office")
public class PostOffice {

    @XmlElementWrapper(name = "class", required = true)
    @XmlElement(name = "name")
    protected List<String> classes = new LinkedList<String>();
    @XmlElementWrapper(name = "type-of-message", required = true)
    @XmlElement(name = "type")
    protected List<String> typeOfMessages = new Vector<String>();
    @XmlElementWrapper(name = "misc")
    @XmlAnyElement(lax = true)
    protected List<Object> miscs = new LinkedList<Object>();

    public List<String> getTypeOfMessages() {
        return typeOfMessages;
    }

    public void setTypeOfMessages(List<String> typeOfMessages) {
        this.typeOfMessages = typeOfMessages;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<Object> getMiscs() {
        return miscs;
    }

    public void setMiscs(List<Object> miscs) {
        this.miscs = miscs;
    }

}
