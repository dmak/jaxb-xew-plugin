
package different_namespaces;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the different_namespaces package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: different_namespaces
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Container }
     * 
     * @return
     *     the new instance of {@link Container }
     */
    public Container createContainer() {
        return new Container();
    }

    /**
     * Create an instance of {@link BaseContainer }
     * 
     * @return
     *     the new instance of {@link BaseContainer }
     */
    public BaseContainer createBaseContainer() {
        return new BaseContainer();
    }

    /**
     * Create an instance of {@link Entry }
     * 
     * @return
     *     the new instance of {@link Entry }
     */
    public Entry createEntry() {
        return new Entry();
    }

}
