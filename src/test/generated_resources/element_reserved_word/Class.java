
package element_reserved_word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for class complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="class">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="extends" type="{}types"/>
 *         <element name="implements" type="{}types"/>
 *         <element name="methods" type="{}methods"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "class", propOrder = {
    "_extends",
    "_implements",
    "methods"
})
public class Class {

    @XmlElementWrapper(name = "extends", required = true)
    @XmlElements({
        @XmlElement(name = "type", type = String.class),
        @XmlElement(name = "primitive", type = Byte.class)
    })
    protected List<Serializable> _extends = new ArrayList<>();
    @XmlElementWrapper(name = "implements", required = true)
    @XmlElements({
        @XmlElement(name = "type", type = String.class),
        @XmlElement(name = "primitive", type = Byte.class)
    })
    protected List<Serializable> _implements = new ArrayList<>();
    @XmlElementWrapper(required = true)
    @XmlElement(name = "method")
    protected List<Method> methods = new ArrayList<>();

    public List<Serializable> getExtends() {
        return _extends;
    }

    public void setExtends(List<Serializable> _extends) {
        this._extends = _extends;
    }

    public List<Serializable> getImplements() {
        return _implements;
    }

    public void setImplements(List<Serializable> _implements) {
        this._implements = _implements;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

}
