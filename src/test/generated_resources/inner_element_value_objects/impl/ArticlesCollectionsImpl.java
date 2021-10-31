
package inner_element_value_objects.impl;

import java.util.List;
import java.util.Vector;
import inner_element_value_objects.Articles;
import inner_element_value_objects.ArticlesCollections;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "articles-collections", propOrder = {
    "articles"
})
public class ArticlesCollectionsImpl implements ArticlesCollections
{

    @XmlElement(required = true, type = ArticlesImpl.class)
    protected List<Articles> articles = new Vector<Articles>();

    public List<Articles> getArticles() {
        if (articles == null) {
            articles = new Vector<Articles>();
        }
        return this.articles;
    }

}
