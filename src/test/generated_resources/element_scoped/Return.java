
package element_scoped;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElementWrapper;
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
 *       <sequence minOccurs="0">
 *         <element name="users">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence maxOccurs="unbounded">
 *                   <element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   <element name="age" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element ref="{http://example.com/extra}age"/>
 *         <element name="search-parameters" type="{http://example.com/scope}search-parameters"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "users",
    "age",
    "searchParameters"
})
@XmlRootElement(name = "return")
public class Return {

    @XmlElementWrapper
    @XmlElementRefs({
        @XmlElementRef(name = "name", namespace = "http://example.com/scope", type = JAXBElement.class),
        @XmlElementRef(name = "age", namespace = "http://example.com/scope", type = JAXBElement.class)
    })
    protected List<JAXBElement<String>> users = new ArrayList<>();
    @XmlElement(namespace = "http://example.com/extra")
    protected String age;
    @XmlElement(name = "search-parameters")
    protected SearchParameters searchParameters;

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAge(String value) {
        this.age = value;
    }

    /**
     * Gets the value of the searchParameters property.
     * 
     * @return
     *     possible object is
     *     {@link SearchParameters }
     *     
     */
    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    /**
     * Sets the value of the searchParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchParameters }
     *     
     */
    public void setSearchParameters(SearchParameters value) {
        this.searchParameters = value;
    }

    public List<JAXBElement<String>> getUsers() {
        return users;
    }

    public void setUsers(List<JAXBElement<String>> users) {
        this.users = users;
    }

}
