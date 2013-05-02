
package generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}AX_SubItems"/>
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
    "axSubItems"
})
@XmlRootElement(name = "AX_Test")
public class AXTest {

    @XmlElementWrapper(name = "AX_SubItems", required = true)
    @XmlElement(name = "AX_Item")
    protected List<String> axSubItems;

    public List<String> getAXSubItems() {
        if (axSubItems == null) {
            axSubItems = new ArrayList<String>();
        }
        return axSubItems;
    }

    public void setAXSubItems(List<String> axSubItems) {
        this.axSubItems = axSubItems;
    }

}
