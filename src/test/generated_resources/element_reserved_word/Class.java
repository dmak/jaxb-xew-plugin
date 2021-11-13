
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
 * <pre>
 * &lt;complexType name="class"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="extends" type="{}types"/&gt;
 *         &lt;element name="implements" type="{}types"/&gt;
 *         &lt;element name="methods" type="{}methods"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
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
    protected List<Serializable> _extends = new ArrayList<Serializable>();
    @XmlElementWrapper(name = "implements", required = true)
    @XmlElements({
        @XmlElement(name = "type", type = String.class),
        @XmlElement(name = "primitive", type = Byte.class)
    })
    protected List<Serializable> _implements = new ArrayList<Serializable>();
    @XmlElementWrapper(required = true)
    @XmlElement(name = "method")
    protected List<Method> methods = new ArrayList<Method>();

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
