
package inner_element_value_objects;

import java.io.Serializable;
import java.util.List;


/**
 * <p>Java class for publisher complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="publisher">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="articles" type="{}articles"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
public interface Publisher
    extends Serializable
{


    public List<Article> getArticles();

    public void setArticles(List<Article> articles);

}
