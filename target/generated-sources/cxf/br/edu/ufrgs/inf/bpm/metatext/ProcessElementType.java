
package br.edu.ufrgs.inf.bpm.metatext;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processElementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="processElementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="activity"/>
 *     &lt;enumeration value="startevent"/>
 *     &lt;enumeration value="intermediateevent"/>
 *     &lt;enumeration value="endevent"/>
 *     &lt;enumeration value="xorsplit"/>
 *     &lt;enumeration value="xorjoin"/>
 *     &lt;enumeration value="andsplit"/>
 *     &lt;enumeration value="andjoin"/>
 *     &lt;enumeration value="orsplit"/>
 *     &lt;enumeration value="orjoin"/>
 *     &lt;enumeration value="gatewaybasedeventsplit"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "processElementType")
@XmlEnum
public enum ProcessElementType {

    @XmlEnumValue("activity")
    ACTIVITY("activity"),
    @XmlEnumValue("startevent")
    STARTEVENT("startevent"),
    @XmlEnumValue("intermediateevent")
    INTERMEDIATEEVENT("intermediateevent"),
    @XmlEnumValue("endevent")
    ENDEVENT("endevent"),
    @XmlEnumValue("xorsplit")
    XORSPLIT("xorsplit"),
    @XmlEnumValue("xorjoin")
    XORJOIN("xorjoin"),
    @XmlEnumValue("andsplit")
    ANDSPLIT("andsplit"),
    @XmlEnumValue("andjoin")
    ANDJOIN("andjoin"),
    @XmlEnumValue("orsplit")
    ORSPLIT("orsplit"),
    @XmlEnumValue("orjoin")
    ORJOIN("orjoin"),
    @XmlEnumValue("gatewaybasedeventsplit")
    GATEWAYBASEDEVENTSPLIT("gatewaybasedeventsplit"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    ProcessElementType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProcessElementType fromValue(String v) {
        for (ProcessElementType c: ProcessElementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
