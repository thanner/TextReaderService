
package br.edu.ufrgs.inf.bpm.metatext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tSnippet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tSnippet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="startIndex" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="endIndex" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="resourceId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="processElementId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="processElementType" type="{http://br/edu/ufrgs/inf/bpm/metatext}processElementType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSnippet")
public class TSnippet {

    @XmlAttribute(name = "startIndex")
    protected Integer startIndex;
    @XmlAttribute(name = "endIndex")
    protected Integer endIndex;
    @XmlAttribute(name = "resourceId")
    protected String resourceId;
    @XmlAttribute(name = "processElementId")
    protected String processElementId;
    @XmlAttribute(name = "processElementType")
    protected ProcessElementType processElementType;

    /**
     * Gets the value of the startIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the value of the startIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStartIndex(Integer value) {
        this.startIndex = value;
    }

    /**
     * Gets the value of the endIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEndIndex() {
        return endIndex;
    }

    /**
     * Sets the value of the endIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEndIndex(Integer value) {
        this.endIndex = value;
    }

    /**
     * Gets the value of the resourceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceId(String value) {
        this.resourceId = value;
    }

    /**
     * Gets the value of the processElementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessElementId() {
        return processElementId;
    }

    /**
     * Sets the value of the processElementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessElementId(String value) {
        this.processElementId = value;
    }

    /**
     * Gets the value of the processElementType property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessElementType }
     *     
     */
    public ProcessElementType getProcessElementType() {
        return processElementType;
    }

    /**
     * Sets the value of the processElementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessElementType }
     *     
     */
    public void setProcessElementType(ProcessElementType value) {
        this.processElementType = value;
    }

}
