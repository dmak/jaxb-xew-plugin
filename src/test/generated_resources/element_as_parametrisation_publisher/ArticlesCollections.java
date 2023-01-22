
package element_as_parametrisation_publisher;

import java.util.List;
import java.util.Vector;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for articles-collections complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="articles-collections">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="articles" type="{}articles" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "articles-collections", propOrder = {
    "articles"
})
public class ArticlesCollections {

    @XmlElement(required = true)
    protected List<Articles> articles = new Vector<>();

    /**
     * Gets the value of the articles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the articles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArticles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Articles }
     * 
     * 
     * @return
     *     The value of the articles property.
     */
    public List<Articles> getArticles() {
        if (articles == null) {
            articles = new Vector<>();
        }
        return this.articles;
    }

}
