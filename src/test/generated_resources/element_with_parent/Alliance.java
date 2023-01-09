
package element_with_parent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.sun.tools.xjc.addon.xew.CommonBean;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <choice maxOccurs="unbounded">
 *         <element name="organization" type="{}organization"/>
 *         <element name="group" type="{}group"/>
 *       </choice>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "organizationOrGroup"
})
@XmlRootElement(name = "alliance")
public class Alliance
    extends CommonBean
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElements({
        @XmlElement(name = "organization", type = Organization.class),
        @XmlElement(name = "group")
    })
    protected List<Group> organizationOrGroup;

    /**
     * Gets the value of the organizationOrGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the organizationOrGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganizationOrGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * {@link Organization }
     * 
     * 
     * @return
     *     The value of the organizationOrGroup property.
     */
    public List<Group> getOrganizationOrGroup() {
        if (organizationOrGroup == null) {
            organizationOrGroup = new ArrayList<>();
        }
        return this.organizationOrGroup;
    }

}
