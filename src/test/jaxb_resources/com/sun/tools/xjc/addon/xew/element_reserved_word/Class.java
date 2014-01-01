
package element_reserved_word;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for class complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="class">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extends" type="{}types"/>
 *         &lt;element name="implements" type="{}types"/>
 *         &lt;element name="methods" type="{}methods"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    @XmlElement(name = "type")
    protected List<String> _extends = new ArrayList<String>();
    @XmlElementWrapper(name = "implements", required = true)
    @XmlElement(name = "type")
    protected List<String> _implements = new ArrayList<String>();
    @XmlElementWrapper(name = "methods", required = true)
    @XmlElement(name = "method")
    protected List<Method> methods = new ArrayList<Method>();

    public List<String> getExtends() {
        return _extends;
    }

    public void setExtends(List<String> _extends) {
        this._extends = _extends;
    }

    public List<String> getImplements() {
        return _implements;
    }

    public void setImplements(List<String> _implements) {
        this._implements = _implements;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

}
