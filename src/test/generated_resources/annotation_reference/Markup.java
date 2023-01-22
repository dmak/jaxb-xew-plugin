
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for markup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="markup">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <choice maxOccurs="unbounded" minOccurs="0">
 *         <element ref="{}br"/>
 *         <element ref="{}page"/>
 *         <element ref="{}para"/>
 *       </choice>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "markup", propOrder = {
    "brOrPageOrPara"
})
public class Markup {

    @XmlElementRefs({
        @XmlElementRef(name = "br", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "page", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "para", type = Para.class, required = false)
    })
    protected List<Object> brOrPageOrPara;

    /**
     * Gets the value of the brOrPageOrPara property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the brOrPageOrPara property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrOrPageOrPara().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Para }
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * 
     * @return
     *     The value of the brOrPageOrPara property.
     */
    public List<Object> getBrOrPageOrPara() {
        if (brOrPageOrPara == null) {
            brOrPageOrPara = new ArrayList<>();
        }
        return this.brOrPageOrPara;
    }

}
