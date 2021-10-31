
package element_with_adapter;

import java.util.Date;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (com.sun.tools.xjc.addon.xew.XmlGregorianCalendarAdapter.fromXml(value));
    }

    public String marshal(Date value) {
        return (com.sun.tools.xjc.addon.xew.XmlGregorianCalendarAdapter.toXml(value));
    }

}
