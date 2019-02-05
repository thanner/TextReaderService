package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.metatext.TMetaText;
import br.edu.ufrgs.inf.bpm.metatext.TSentence;
import br.edu.ufrgs.inf.bpm.metatext.TSnippet;
import br.edu.ufrgs.inf.bpm.metatext.TText;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import com.inubit.research.textToProcess.text.T2PSentence;
import com.inubit.research.textToProcess.worldModel.OriginatedElement;
import org.omg.spec.bpmn._20100524.model.TDefinitions;

import java.util.Map;

public class TextGenerator {

    private TMetaText tMetaText;
    private TText tText;

    public TextGenerator() {
        tMetaText = new TMetaText();
        tText = new TText();
    }

    public TMetaText generateText(com.inubit.research.textToProcess.text.Text f_text, Map<OriginatedElement, ProcessElement> originatedElementProcessElementMap, TDefinitions tDefinitions) {
        // Generate sentence based on text
        for (T2PSentence sentence : f_text.getSentences()) {
            generateNewSentence(sentence.toString());
        }

        for (Map.Entry<OriginatedElement, ProcessElement> entry : originatedElementProcessElementMap.entrySet()) {
            if (entry.getKey().getOrigin() != null) {
                String entrySentence = entry.getKey().getOrigin().toString();
                TSentence sentence = getSentence(entrySentence);
                if (sentence != null) {
                    ProcessElement processElement = entry.getValue();
                    if (processElement != null) {
                        TSnippet subsentence = new TSnippet();
                        subsentence.setStartIndex(0);
                        subsentence.setEndIndex(entrySentence.length());
                        subsentence.setProcessElementId(processElement.getProcessElementId());
                        subsentence.setProcessElementType(processElement.getProcessElement());

                        sentence.getSnippetList().add(subsentence);
                    }
                }
            }
        }

        addResourceIds(tDefinitions);

        tMetaText.setText(tText);
        tMetaText.getProcessList().addAll(MetaTextProcessGenerator.generateMetaTextProcess(tDefinitions));

        return tMetaText;
    }

    private TSentence generateNewSentence(String entrySentence) {
        TSentence sentence = new TSentence();
        sentence.setValue(entrySentence);
        sentence.setLateral(false);
        sentence.setLevel(0);
        tText.getSentenceList().add(sentence);
        return sentence;
    }

    private TSentence getSentence(String entrySentence) {
        for (TSentence sentence : tText.getSentenceList()) {
            if (sentence.getValue().equals(entrySentence)) {
                return sentence;
            }
        }
        return null;
    }

    private void addResourceIds(TDefinitions tDefinitions) {
        BpmnWrapper bpmnWrapper = new BpmnWrapper(tDefinitions);
        for (TSentence tSentence : tText.getSentenceList()) {
            for (TSnippet tSnippet : tSentence.getSnippetList()) {
                tSnippet.setResourceId(bpmnWrapper.getLaneIdByFlowElementId(tSnippet.getProcessElementId()));
            }
        }
    }

}
