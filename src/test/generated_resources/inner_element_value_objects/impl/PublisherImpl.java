
package inner_element_value_objects.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import inner_element_value_objects.Article;
import inner_element_value_objects.Publisher;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publisher", propOrder = {
    "articles"
})
public class PublisherImpl implements Publisher
{

    @XmlElementWrapper(required = true)
    @XmlElement(name = "article", type = ArticleImpl.class)
    protected List<Article> articles = new ArrayList<Article>();

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

}
