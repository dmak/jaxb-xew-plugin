
package inner_element_value_objects.impl;

import java.util.ArrayList;
import java.util.List;
import inner_element_value_objects.Article;
import inner_element_value_objects.Articles;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "articles", propOrder = {
    "article"
})
public class ArticlesImpl implements Articles
{

    @XmlElement(required = true, type = ArticleImpl.class)
    protected List<Article> article;

    public List<Article> getArticle() {
        if (article == null) {
            article = new ArrayList<Article>();
        }
        return this.article;
    }

}
