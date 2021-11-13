
package inner_element_value_objects.impl;

import inner_element_value_objects.Article;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

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
