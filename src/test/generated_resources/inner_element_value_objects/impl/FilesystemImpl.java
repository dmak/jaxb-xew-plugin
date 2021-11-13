
package inner_element_value_objects.impl;

import java.util.ArrayList;
import java.util.List;
import inner_element_value_objects.Filesystem;
import inner_element_value_objects.Volume;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fileListing",
    "directoryListing",
    "volumes"
})
@XmlRootElement(name = "filesystem")
public class FilesystemImpl implements Filesystem
{

    @XmlElementWrapper(name = "file-listing", required = true)
    @XmlElement(name = "file-item", type = FilesystemImpl.FileItemImpl.class)
    protected List<Filesystem.FileItem> fileListing = new ArrayList<Filesystem.FileItem>();
    @XmlElementWrapper(name = "directory-listing", required = true)
    @XmlElement(name = "directory-item", defaultValue = "SPAM")
    protected List<String> directoryListing = new ArrayList<String>();
    @XmlElementWrapper(required = true)
    @XmlElement(name = "volume", type = VolumeImpl.class)
    protected List<Volume> volumes = new ArrayList<Volume>();

    public List<Filesystem.FileItem> getFileListing() {
        return fileListing;
    }

    public void setFileListing(List<Filesystem.FileItem> fileListing) {
        this.fileListing = fileListing;
    }

    public List<String> getDirectoryListing() {
        return directoryListing;
    }

    public void setDirectoryListing(List<String> directoryListing) {
        this.directoryListing = directoryListing;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class FileItemImpl implements Filesystem.FileItem
    {

        @XmlElement(required = true)
        protected String name;
        protected int size;

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int value) {
            this.size = value;
        }

    }

}
