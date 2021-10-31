
package inner_element_value_objects;

import inner_element_value_objects.impl.ArticleImpl;
import inner_element_value_objects.impl.ArticlesCollectionsImpl;
import inner_element_value_objects.impl.ArticlesImpl;
import inner_element_value_objects.impl.FilesystemImpl;
import inner_element_value_objects.impl.PublisherImpl;
import inner_element_value_objects.impl.VolumeImpl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the inner_element_value_objects package. 
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

    private final static Void _useJAXBProperties = null;

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_element_value_objects
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Filesystem }
     * 
     */
    public Filesystem createFilesystem() {
        return new FilesystemImpl();
    }

    /**
     * Create an instance of {@link Article }
     * 
     */
    public Article createArticle() {
        return new ArticleImpl();
    }

    /**
     * Create an instance of {@link Articles }
     * 
     */
    public Articles createArticles() {
        return new ArticlesImpl();
    }

    /**
     * Create an instance of {@link ArticlesCollections }
     * 
     */
    public ArticlesCollections createArticlesCollections() {
        return new ArticlesCollectionsImpl();
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public Publisher createPublisher() {
        return new PublisherImpl();
    }

    /**
     * Create an instance of {@link Volume }
     * 
     */
    public Volume createVolume() {
        return new VolumeImpl();
    }

    /**
     * Create an instance of {@link Filesystem.FileItem }
     * 
     */
    public Filesystem.FileItem createFilesystemFileItem() {
        return new FilesystemImpl.FileItemImpl();
    }

}
