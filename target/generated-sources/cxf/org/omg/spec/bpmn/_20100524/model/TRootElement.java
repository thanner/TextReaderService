
package org.omg.spec.bpmn._20100524.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tRootElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tRootElement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tBaseElement">
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRootElement")
@XmlSeeAlso({
    TCollaboration.class,
    TPartnerEntity.class,
    TPartnerRole.class,
    TEndPoint.class,
    TEscalation.class,
    TSignal.class,
    TResource.class,
    TEventDefinition.class,
    TError.class,
    TInterface.class,
    TCorrelationProperty.class,
    TMessage.class,
    TItemDefinition.class,
    TCallableElement.class,
    TDataStore.class,
    TCategory.class
})
public abstract class TRootElement
    extends TBaseElement
{


}
