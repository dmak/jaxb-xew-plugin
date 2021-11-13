
package element_scoped;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for search-parameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-parameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="provider-ids" type="{http://example.com/scope}id-list" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-parameters", propOrder = {
    "providerIds"
})
public class SearchParameters {

    @XmlElementWrapper(name = "provider-ids")
    @XmlElementRef(name = "id", namespace = "http://example.com/scope", type = JAXBElement.class)
    protected List<JAXBElement<List<String>>> providerIds = new ArrayList<JAXBElement<List<String>>>();

    public List<JAXBElement<List<String>>> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<JAXBElement<List<String>>> providerIds) {
        this.providerIds = providerIds;
    }

}
