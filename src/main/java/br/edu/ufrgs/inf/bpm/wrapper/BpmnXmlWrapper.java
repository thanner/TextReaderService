/*
package br.edu.ufrgs.inf.bpm.wrapper;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.ModelInstance;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class BpmnXmlWrapper {

    public BpmnModelInstance modelInstance;
    public Definitions definitions;
    private Collaboration collaboration;

    public BpmnXmlWrapper() {
        this.modelInstance = Bpmn.createEmptyModel();
        definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);
        collaboration = createElement(definitions, "Collaboration", "", Collaboration.class);
    }

    public BpmnXmlWrapper(String str) throws UnsupportedEncodingException {
        this.modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name())));
        definitions = modelInstance.getDefinitions();
    }

    public Participant createParticipant(String id, String text) {
        Participant participant = createElement(collaboration, id, text, Participant.class);
        collaboration.getParticipants().add(participant);
        return participant;
    }

    public Process createProcess(String id, String text, Participant participant) {
        Process process = createElement(definitions, id, text, Process.class);
        participant.setProcess(process);
        return process;
    }

    public LaneSet createLaneSet(String id, String text, Process process) {
        LaneSet laneSet = createElement(process, id, text, LaneSet.class);
        process.getLaneSets().add(laneSet);
        return laneSet;
    }

    public <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, String name, Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        if (!id.isEmpty()) {
            element.setAttributeValue("id", id, true);
        }
        if (!name.isEmpty()) {
            element.setAttributeValue("name", name);
        }
        parentElement.addChildElement(element);
        return element;
    }

    public SequenceFlow createSequenceFlow(BpmnModelElementInstance process, String name, FlowNode from, FlowNode to) {
        String idFrom = "nullIdFrom";
        if (from != null) {
            idFrom = from.getId();
        }

        String idTo = "nullIdTo";
        if (to != null) {
            idTo = to.getId();
        }

        SequenceFlow sequenceFlow = createElement(process, idFrom + "-" + idTo, name, SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        // FIXME: Error when run the Events.txt
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

    public FlowNode getElementById(String id) {
        return modelInstance.getModelElementById(id);
    }

    public String getBPMNString() {
        return Bpmn.convertToString(modelInstance);
    }

    public ModelInstance read(String filePath) {
        return Bpmn.readModelFromFile(new File(filePath));
    }

    public void writeToFile(String pathname) {
        File file = new File(pathname);
        Bpmn.writeModelToFile(file, modelInstance);
    }
}
*/