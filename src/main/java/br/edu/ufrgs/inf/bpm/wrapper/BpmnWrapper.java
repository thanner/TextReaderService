package br.edu.ufrgs.inf.bpm.wrapper;


import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class BpmnWrapper {

    private TDefinitions definitions;

    public BpmnWrapper(TDefinitions definitions) {
        this.definitions = definitions;
    }

    public List<TLane> getLanesByProcess(TProcess tProcess) {
        List<TLane> tLaneList = new ArrayList<>();
        for (TLaneSet laneSet : tProcess.getLaneSet()) {
            for (TLane lane : laneSet.getLane()) {
                tLaneList.add(lane);
            }
        }
        return tLaneList;
    }

    public TLane getLaneByFlowElement(TFlowElement tFlowElement) {
        List<TProcess> processList = getProcessList();
        for (TProcess process : processList) {
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    for (JAXBElement<Object> flowNodeRefObject : lane.getFlowNodeRef()) {
                        if (flowNodeRefObject.getValue() instanceof TFlowNode) {
                            TFlowNode flowNodeAux = (TFlowNode) flowNodeRefObject.getValue();
                            if (tFlowElement.getId().equals(flowNodeAux.getId())) {
                                return lane;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getLaneIdByFlowElementId(String elementId) {
        TFlowElement tFlowElement = getFlowElementById(elementId);
        if (tFlowElement != null) {
            TLane tLane = getLaneByFlowElement(tFlowElement);
            if (tLane != null) {
                return tLane.getId();
            }
        }
        return null;
    }

    public TFlowElement getFlowElementById(String flowElementId) {
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue().getId().equals(flowElementId)) {
                    return flowElement.getValue();
                }
            }
        }
        return null;
    }

    public List<TCollaboration> getCollaborationList() {
        List<TCollaboration> collaborationList = new ArrayList<>();
        List<JAXBElement<? extends TRootElement>> rootElementList = definitions.getRootElement();
        for (JAXBElement<? extends TRootElement> rootElement : rootElementList) {
            if (rootElement.getValue() instanceof TCollaboration) {
                TCollaboration collaboration = (TCollaboration) rootElement.getValue();
                collaborationList.add(collaboration);
            }
        }
        return collaborationList;
    }

    public List<TProcess> getProcessList() {
        List<TProcess> processList = new ArrayList<>();
        List<JAXBElement<? extends TRootElement>> rootElementList = definitions.getRootElement();
        for (JAXBElement<? extends TRootElement> rootElement : rootElementList) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();
                processList.add(process);
            }
        }
        return processList;
    }

    public void addTProcess(TProcess tProcess) {
        List<JAXBElement<? extends TRootElement>> rootElementList = definitions.getRootElement();
        rootElementList.add(new ObjectFactory().createProcess(tProcess));
    }

    public String getProcessName(TProcess process) {
        List<TCollaboration> collaborationList = getCollaborationList();
        for (TCollaboration collaboration : collaborationList) {
            for (TParticipant participant : collaboration.getParticipant()) {
                if (process.getId().equals(participant.getProcessRef().toString())) {
                    return participant.getName();
                }
            }
        }
        return "";
    }

    public List<TFlowNode> getFlowNodeList() {
        List<TFlowNode> tFlowNodeList = new ArrayList<>();
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> tFlowElement : tProcess.getFlowElement()) {
                if (tFlowElement.getValue() instanceof TFlowNode) {
                    tFlowNodeList.add((TFlowNode) tFlowElement.getValue());
                }
            }
        }
        return tFlowNodeList;
    }

    public List<TSequenceFlow> getSequenceFlowList() {
        List<TSequenceFlow> tSequenceFlowList = new ArrayList<>();
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> tFlowElement : tProcess.getFlowElement()) {
                if (tFlowElement.getValue() instanceof TSequenceFlow) {
                    tSequenceFlowList.add((TSequenceFlow) tFlowElement.getValue());
                }
            }
        }
        return tSequenceFlowList;
    }

    public TFlowNode getSourceBySequenceFlow(TSequenceFlow sequenceFlow) {
        Object source = sequenceFlow.getSourceRef();
        if (source instanceof TFlowNode) {
            return (TFlowNode) source;
        }
        return null;
    }

    public TFlowNode getTargetBySequenceFlow(TSequenceFlow sequenceFlow) {
        Object target = sequenceFlow.getTargetRef();
        if (target instanceof TFlowNode) {
            return (TFlowNode) target;
        }
        return null;
    }

    public TDefinitions getDefinitions() {
        return this.definitions;
    }

    public TFlowElement getFlowElementByQName(QName qName) {
        return getFlowElementById(qName.getLocalPart());
    }

    public List<TFlowNode> getFlowNodeSourceList(TFlowNode tFlowNode) {
        List<TFlowNode> elementSourceList = new ArrayList<>();
        for (QName qName : tFlowNode.getIncoming()) {
            TFlowElement tFlowElement = getFlowElementByQName(qName);
            if (tFlowElement instanceof TSequenceFlow) {
                TSequenceFlow tSequenceFlow = (TSequenceFlow) tFlowElement;
                Object source = tSequenceFlow.getSourceRef();
                if (source instanceof TFlowNode) {
                    elementSourceList.add((TFlowNode) source);
                }
            }
        }
        return elementSourceList;
    }

    public List<TFlowNode> getFlowNodeTargetList(TFlowNode tFlowNode) {
        List<TFlowNode> elementTargetList = new ArrayList<>();
        for (QName qName : tFlowNode.getOutgoing()) {
            TFlowElement tFlowElement = getFlowElementByQName(qName);
            if (tFlowElement instanceof TSequenceFlow) {
                TSequenceFlow tSequenceFlow = (TSequenceFlow) tFlowElement;
                Object target = tSequenceFlow.getTargetRef();
                if (target instanceof TFlowNode) {
                    elementTargetList.add((TFlowNode) target);
                }
            }
        }
        return elementTargetList;
    }

    public List<TSequenceFlow> getSequenceFlowTarget(TFlowNode node) {
        List<TSequenceFlow> sequenceFlowList = new ArrayList<>();
        for (QName qName : node.getOutgoing()) {
            TFlowElement tFlowElement = getFlowElementByQName(qName);
            if (tFlowElement instanceof TSequenceFlow) {
                sequenceFlowList.add((TSequenceFlow) tFlowElement);
            }
        }
        return sequenceFlowList;
    }

}