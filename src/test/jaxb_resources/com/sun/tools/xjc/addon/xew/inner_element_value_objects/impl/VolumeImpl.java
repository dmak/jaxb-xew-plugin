
package inner_element_value_objects.impl;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import inner_element_value_objects.Volume;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
public class VolumeImpl implements Volume
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected BigInteger capacity;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public BigInteger getCapacity() {
        return capacity;
    }

    public void setCapacity(BigInteger value) {
        this.capacity = value;
    }

}
