
package br.edu.ufrgs.inf.bpm.metatext;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tMetaText complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tMetaText">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processList" type="{http://br/edu/ufrgs/inf/bpm/metatext}tProcess" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="text" type="{http://br/edu/ufrgs/inf/bpm/metatext}tText"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMetaText", propOrder = {
    "processList",
    "text"
})
public class TMetaText {

    protected List<TProcess> processList;
    @XmlElement(required = true)
    protected TText text;

    /**
     * Gets the value of the processList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TProcess }
     * 
     * 
     */
    public List<TProcess> getProcessList() {
        if (processList == null) {
            processList = new ArrayList<TProcess>();
        }
        return this.processList;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link TText }
     *     
     */
    public TText getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link TText }
     *     
     */
    public void setText(TText value) {
        this.text = value;
    }

}
