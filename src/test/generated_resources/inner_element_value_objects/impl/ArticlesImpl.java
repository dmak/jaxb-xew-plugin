
package inner_element_value_objects.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import inner_element_value_objects.Article;
import inner_element_value_objects.Articles;

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
