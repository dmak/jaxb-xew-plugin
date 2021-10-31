
package inner_element_value_objects.impl;

import inner_element_value_objects.Article;
import inner_element_value_objects.Articles;
import inner_element_value_objects.ArticlesCollections;
import inner_element_value_objects.Filesystem;
import inner_element_value_objects.Publisher;
import inner_element_value_objects.Volume;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the inner_element_value_objects.impl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_element_value_objects.impl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Filesystem }
     * 
     */
    public FilesystemImpl createFilesystem() {
        return new FilesystemImpl();
    }

    /**
     * Create an instance of {@link Article }
     * 
     */
    public ArticleImpl createArticle() {
        return new ArticleImpl();
    }

    /**
     * Create an instance of {@link Articles }
     * 
     */
    public ArticlesImpl createArticles() {
        return new ArticlesImpl();
    }

    /**
     * Create an instance of {@link ArticlesCollections }
     * 
     */
    public ArticlesCollectionsImpl createArticlesCollections() {
        return new ArticlesCollectionsImpl();
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public PublisherImpl createPublisher() {
        return new PublisherImpl();
    }

    /**
     * Create an instance of {@link Volume }
     * 
     */
    public VolumeImpl createVolume() {
        return new VolumeImpl();
    }

    /**
     * Create an instance of {@link Filesystem.FileItem }
     * 
     */
    public FilesystemImpl.FileItemImpl createFilesystemFileItem() {
        return new FilesystemImpl.FileItemImpl();
    }

}
