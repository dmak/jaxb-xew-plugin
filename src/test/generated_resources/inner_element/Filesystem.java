
package inner_element;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.sun.tools.xjc.addon.xew.CommonBean;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="file-listing">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="file-item" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="directory-listing">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="directory-item" maxOccurs="unbounded">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="volumes" type="{}volumes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fileListing",
    "directoryListing",
    "volumes"
})
@XmlRootElement(name = "filesystem")
public class Filesystem
    extends CommonBean
    implements Cloneable
{

    @XmlElement(name = "file-listing", required = true)
    protected Filesystem.FileListing fileListing;
    @XmlElementWrapper(name = "directory-listing", required = true)
    @XmlElement(name = "directory-item")
    protected List<String> directoryListing;
    @XmlElement(required = true)
    protected Volumes volumes;

    /**
     * Gets the value of the fileListing property.
     * 
     * @return
     *     possible object is
     *     {@link Filesystem.FileListing }
     *     
     */
    public Filesystem.FileListing getFileListing() {
        return fileListing;
    }

    /**
     * Sets the value of the fileListing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Filesystem.FileListing }
     *     
     */
    public void setFileListing(Filesystem.FileListing value) {
        this.fileListing = value;
    }

    /**
     * Gets the value of the volumes property.
     * 
     * @return
     *     possible object is
     *     {@link Volumes }
     *     
     */
    public Volumes getVolumes() {
        return volumes;
    }

    /**
     * Sets the value of the volumes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Volumes }
     *     
     */
    public void setVolumes(Volumes value) {
        this.volumes = value;
    }

    public List<String> getDirectoryListing() {
        return directoryListing;
    }

    public void setDirectoryListing(List<String> directoryListing) {
        this.directoryListing = directoryListing;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="file-item" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                 &lt;/all>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "fileItem"
    })
    public static class FileListing
        extends CommonBean
        implements Cloneable
    {

        @XmlElement(name = "file-item", required = true)
        protected List<Filesystem.FileListing.FileItem> fileItem;

        /**
         * Gets the value of the fileItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fileItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFileItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Filesystem.FileListing.FileItem }
         * 
         * 
         */
        public List<Filesystem.FileListing.FileItem> getFileItem() {
            if (fileItem == null) {
                fileItem = new ArrayList<Filesystem.FileListing.FileItem>();
            }
            return this.fileItem;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;all>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class FileItem
            extends CommonBean
            implements Cloneable
        {

            @XmlElement(required = true)
            protected String name;
            protected int size;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the size property.
             * 
             */
            public int getSize() {
                return size;
            }

            /**
             * Sets the value of the size property.
             * 
             */
            public void setSize(int value) {
                this.size = value;
            }

        }

    }

}
