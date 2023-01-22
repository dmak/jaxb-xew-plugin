
package inner_element;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the inner_element package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: inner_element
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Filesystem }
     * 
     * @return
     *     the new instance of {@link Filesystem }
     */
    public Filesystem createFilesystem() {
        return new Filesystem();
    }

    /**
     * Create an instance of {@link Volumes }
     * 
     * @return
     *     the new instance of {@link Volumes }
     */
    public Volumes createVolumes() {
        return new Volumes();
    }

    /**
     * Create an instance of {@link Filesystem.FileListing }
     * 
     * @return
     *     the new instance of {@link Filesystem.FileListing }
     */
    public Filesystem.FileListing createFilesystemFileListing() {
        return new Filesystem.FileListing();
    }

    /**
     * Create an instance of {@link Volumes.Volume }
     * 
     * @return
     *     the new instance of {@link Volumes.Volume }
     */
    public Volumes.Volume createVolumesVolume() {
        return new Volumes.Volume();
    }

    /**
     * Create an instance of {@link Filesystem.FileListing.FileItem }
     * 
     * @return
     *     the new instance of {@link Filesystem.FileListing.FileItem }
     */
    public Filesystem.FileListing.FileItem createFilesystemFileListingFileItem() {
        return new Filesystem.FileListing.FileItem();
    }

}
