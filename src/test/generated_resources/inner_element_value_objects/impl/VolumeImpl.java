
package inner_element_value_objects.impl;

import inner_element_value_objects.Volume;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
public class VolumeImpl implements Volume
{

    @XmlElement(required = true)
    protected String name;
    protected int capacity;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int value) {
        this.capacity = value;
    }

}
