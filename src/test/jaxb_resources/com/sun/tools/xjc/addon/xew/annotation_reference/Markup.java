
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for markup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="markup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}br"/>
 *         &lt;element ref="{}u"/>
 *         &lt;element ref="{}p"/>
 *         &lt;element ref="{}sub"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "markup", propOrder = {
    "brOrUOrP"
})
public class Markup {

    @XmlElementRefs({
        @XmlElementRef(name = "p", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "u", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sub", type = Sub.class, required = false),
        @XmlElementRef(name = "br", type = JAXBElement.class, required = false)
    })
    protected List<Object> brOrUOrP;

    /**
     * Gets the value of the brOrUOrP property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the brOrUOrP property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrOrUOrP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Markup }{@code >}
     * {@link Sub }
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * 
     */
    public List<Object> getBrOrUOrP() {
        if (brOrUOrP == null) {
            brOrUOrP = new ArrayList<Object>();
        }
        return this.brOrUOrP;
    }

}
