
package inner_element_value_objects.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import inner_element_value_objects.Article;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "article", propOrder = {
    "title",
    "author"
})
public class ArticleImpl implements Article
{

    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String value) {
        this.author = value;
    }

}
