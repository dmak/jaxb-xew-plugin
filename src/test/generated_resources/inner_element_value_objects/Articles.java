
package inner_element_value_objects;

import java.io.Serializable;
import java.util.List;


/**
 * <p>Java class for articles complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="articles">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="article" type="{}article" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
public interface Articles extends Serializable
{


    /**
     * Gets the value of the article property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the article property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArticle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Article }
     * 
     * 
     * @return
     *     The value of the article property.
     */
    List<Article> getArticle();

}
