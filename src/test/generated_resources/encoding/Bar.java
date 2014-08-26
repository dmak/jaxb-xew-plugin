
package encoding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bar.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="bar">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ä"/>
 *     &lt;enumeration value="ü"/>
 *     &lt;enumeration value="ö"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "bar")
@XmlEnum
public enum Bar {

    @XmlEnumValue("\u00e4")
    Ä("\u00e4"),
    @XmlEnumValue("\u00fc")
    Ü("\u00fc"),
    @XmlEnumValue("\u00f6")
    Ö("\u00f6");
    private final String value;

    Bar(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Bar fromValue(String v) {
        for (Bar c: Bar.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
