
package inner_element_value_objects;

import java.io.Serializable;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="file-listing">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="file-item" maxOccurs="unbounded">
 *                     <complexType>
 *                       <complexContent>
 *                         <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           <all>
 *                             <element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             <element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                           </all>
 *                         </restriction>
 *                       </complexContent>
 *                     </complexType>
 *                   </element>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="directory-listing">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="directory-item" maxOccurs="unbounded">
 *                     <simpleType>
 *                       <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       </restriction>
 *                     </simpleType>
 *                   </element>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="volumes" type="{}volumes"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
public interface Filesystem
    extends Serializable
{


    public List<Filesystem.FileItem> getFileListing();

    public void setFileListing(List<Filesystem.FileItem> fileListing);

    public List<String> getDirectoryListing();

    public void setDirectoryListing(List<String> directoryListing);

    public List<Volume> getVolumes();

    public void setVolumes(List<Volume> volumes);


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <all>
     *         <element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         <element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       </all>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    public interface FileItem
        extends Serializable
    {


        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        String getName();

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        void setName(String value);

        /**
         * Gets the value of the size property.
         * 
         */
        int getSize();

        /**
         * Sets the value of the size property.
         * 
         */
        void setSize(int value);

    }

}
