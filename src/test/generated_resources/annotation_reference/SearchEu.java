
package annotation_reference;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for search-eu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-eu"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="classes-eu" type="{}classes-eu"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-eu", propOrder = {
    "classesEu"
})
public class SearchEu {

    @XmlElementWrapper(name = "classes-eu", required = true, nillable = true)
    @XmlElement(name = "class-eu")
    protected List<ClassCommon> classesEu = new ArrayList<ClassCommon>();

    public List<ClassCommon> getClassesEu() {
        return classesEu;
    }

    public void setClassesEu(List<ClassCommon> classesEu) {
        this.classesEu = classesEu;
    }

}
