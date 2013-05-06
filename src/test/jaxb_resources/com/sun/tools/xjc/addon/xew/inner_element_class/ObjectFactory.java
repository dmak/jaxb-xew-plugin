
package inner_element_class;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the inner_element_class package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_element_class
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Filesystem }
     * 
     */
    public Filesystem createFilesystem() {
        return new Filesystem();
    }

    /**
     * Create an instance of {@link Volume }
     * 
     */
    public Volume createVolume() {
        return new Volume();
    }

    /**
     * Create an instance of {@link Filesystem.File }
     * 
     */
    public Filesystem.File createFilesystemFile() {
        return new Filesystem.File();
    }

}
