
package inner_element_value_objects;

import java.io.Serializable;
import java.util.List;


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
public interface ArticlesCollections extends Serializable
{


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
    List<Articles> getArticles();

}
