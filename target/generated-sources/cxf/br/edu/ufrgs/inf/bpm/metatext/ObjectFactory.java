
package br.edu.ufrgs.inf.bpm.metatext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.edu.ufrgs.inf.bpm.metatext package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Sentence_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "sentence");
    private final static QName _Text_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "text");
    private final static QName _Snippet_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "snippet");
    private final static QName _Resource_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "resource");
    private final static QName _MetaText_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "metaText");
    private final static QName _Process_QNAME = new QName("http://br/edu/ufrgs/inf/bpm/metatext", "process");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.edu.ufrgs.inf.bpm.metatext
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TText }
     * 
     */
    public TText createTText() {
        return new TText();
    }

    /**
     * Create an instance of {@link TResource }
     * 
     */
    public TResource createTResource() {
        return new TResource();
    }

    /**
     * Create an instance of {@link TSnippet }
     * 
     */
    public TSnippet createTSnippet() {
        return new TSnippet();
    }

    /**
     * Create an instance of {@link TProcess }
     * 
     */
    public TProcess createTProcess() {
        return new TProcess();
    }

    /**
     * Create an instance of {@link TMetaText }
     * 
     */
    public TMetaText createTMetaText() {
        return new TMetaText();
    }

    /**
     * Create an instance of {@link TSentence }
     * 
     */
    public TSentence createTSentence() {
        return new TSentence();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSentence }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "sentence")
    public JAXBElement<TSentence> createSentence(TSentence value) {
        return new JAXBElement<TSentence>(_Sentence_QNAME, TSentence.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TText }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "text")
    public JAXBElement<TText> createText(TText value) {
        return new JAXBElement<TText>(_Text_QNAME, TText.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSnippet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "snippet")
    public JAXBElement<TSnippet> createSnippet(TSnippet value) {
        return new JAXBElement<TSnippet>(_Snippet_QNAME, TSnippet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TResource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "resource")
    public JAXBElement<TResource> createResource(TResource value) {
        return new JAXBElement<TResource>(_Resource_QNAME, TResource.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TMetaText }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "metaText")
    public JAXBElement<TMetaText> createMetaText(TMetaText value) {
        return new JAXBElement<TMetaText>(_MetaText_QNAME, TMetaText.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TProcess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://br/edu/ufrgs/inf/bpm/metatext", name = "process")
    public JAXBElement<TProcess> createProcess(TProcess value) {
        return new JAXBElement<TProcess>(_Process_QNAME, TProcess.class, null, value);
    }

}
