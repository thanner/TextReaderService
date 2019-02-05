package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import com.inubit.research.textToProcess.worldModel.OriginatedElement;
import net.frapu.code.visualization.Cluster;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;
import org.omg.spec.bpmn._20100524.di.*;
import org.omg.spec.bpmn._20100524.model.ObjectFactory;
import org.omg.spec.bpmn._20100524.model.*;
import org.omg.spec.dd._20100524.dc.Bounds;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BpmnBuilder {

    private final String bpmnPath;

    private BPMNModel bpmnModel;
    private TDefinitions definitions;
    private BpmnWrapper bpmnWrapper;

    private boolean hasVisualization;

    private int genericId;
    private Map<String, TProcess> poolMap;
    private Map<String, TLane> laneMap;
    private Map<String, TFlowNode> flowNodeMap;

    private List<String> startEventList = Arrays.asList("StartEvent", "CompesationStartEvent", "ConditionalStartEvent", "ErrorStartEvent", "EscalationStartEvent", "MessageStartEvent", "MultipleStartEvent", "SignalStartEvent", "TimerStartEvent");
    private List<String> intermediateEventList = Arrays.asList("IntermediateEvent", "CancelIntermediateEvent", "CompesationIntermediateEvent", "ConditionalIntermediateEvent", "ErrorIntermediateEvent", "EscalationIntermediateEvent", "MessageIntermediateEvent", "MultipleIntermediateEvent", "ParallelMultipleIntermediateEvent", "SignalIntermediateEvent", "TimerIntermediateEvent");
    private List<String> endEventList = Arrays.asList("EndEvent", "CancelEndEvent", "CompesationEndEvent", "ErrorEndEvent", "EscalationEndEvent", "MessageEndEvent", "MultipleEndEvent", "ParallelMultipleStartEvent", "SignalEndEvent", "TerminateEndEvent", "TimerEndEvent");
    private TProcess dummyProcess;

    public BpmnBuilder(BPMNModel bpmnModel) {
        this.bpmnPath = "net.frapu.code.visualization.bpmn.";

        this.bpmnModel = bpmnModel;
        this.definitions = new TDefinitions();
        this.bpmnWrapper = new BpmnWrapper(definitions);

        this.hasVisualization = false;

        this.genericId = 0;
        this.poolMap = new HashMap<>();
        this.laneMap = new HashMap<>();
        this.flowNodeMap = new HashMap<>();
    }

    public TDefinitions buildBpmnDefinitions() {
        createStructure();
        if (hasVisualization) {
            createVisualizationStructure();
        }

        TFlowNode tFlowNode;
        for (FlowObject flowObject : bpmnModel.getFlowObjects()) {

            // Task
            if (isElement(flowObject, "Task") || isElement(flowObject, "Activity")) {
                tFlowNode = createFlowNode(flowObject, new TTask());
                positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createTask((TTask) tFlowNode));
            }

            // Events
            for (String elementName : startEventList) {
                if (isElement(flowObject, elementName)) {
                    tFlowNode = createFlowNode(flowObject, new TStartEvent());
                    TStartEvent tStartEvent = generatetStartEvent(elementName, tFlowNode);
                    positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createStartEvent(tStartEvent));
                }
            }
            for (String elementName : intermediateEventList) {
                if (isElement(flowObject, elementName)) {
                    if (flowObject.getProperty("event_subtype").equalsIgnoreCase("Catching")) {
                        String boundarySource = flowObject.getProperty("#source");
                        if (boundarySource != null && !boundarySource.isEmpty()) {
                            // Boundary
                            tFlowNode = createFlowNode(flowObject, new TBoundaryEvent());
                            TBoundaryEvent tBoundaryEvent = generatetBoundaryEvent(elementName, tFlowNode);
                            TFlowNode activitySource = flowNodeMap.get(boundarySource);
                            tBoundaryEvent.setAttachedToRef(getQName(activitySource.getId()));
                            positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createBoundaryEvent(tBoundaryEvent));
                        } else {
                            // Catch
                            tFlowNode = createFlowNode(flowObject, new TIntermediateCatchEvent());
                            TIntermediateCatchEvent tIntermediateCatchEvent = generatetIntermediateCatchEvent(elementName, tFlowNode);
                            positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createIntermediateCatchEvent(tIntermediateCatchEvent));
                        }
                    } else {
                        tFlowNode = createFlowNode(flowObject, new TIntermediateThrowEvent());
                        TIntermediateThrowEvent tIntermediateThrowEvent = generatetIntermediateThrowEvent(elementName, tFlowNode);
                        positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createIntermediateThrowEvent(tIntermediateThrowEvent));
                    }
                }
            }
            for (String elementName : endEventList) {
                if (isElement(flowObject, elementName)) {
                    tFlowNode = createFlowNode(flowObject, new TEndEvent());
                    TEndEvent tEndEvent = generatetEndEvent(elementName, tFlowNode);
                    positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createEndEvent(tEndEvent));
                }
            }

            // Gateways
            if (isElement(flowObject, "ExclusiveGateway")) {
                tFlowNode = createFlowNode(flowObject, new TExclusiveGateway());
                TExclusiveGateway tExclusiveGateway = generatetExclusiveGateway(flowObject, tFlowNode);
                positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createExclusiveGateway(tExclusiveGateway));
            }
            if (isElement(flowObject, "InclusiveGateway")) {
                tFlowNode = createFlowNode(flowObject, new TInclusiveGateway());
                TInclusiveGateway tInclusiveGateway = generatetInclusiveGateway(flowObject, tFlowNode);
                positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createInclusiveGateway(tInclusiveGateway));
            }
            if (isElement(flowObject, "ParallelGateway")) {
                tFlowNode = createFlowNode(flowObject, new TParallelGateway());
                positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createParallelGateway((TParallelGateway) tFlowNode));
            }
            if (isElement(flowObject, "EventBasedGateway")) {
                tFlowNode = createFlowNode(flowObject, new TEventBasedGateway());
                positionFlowNode(flowObject, tFlowNode, new ObjectFactory().createEventBasedGateway((TEventBasedGateway) tFlowNode));
            }
        }

        bpmnModel.getSequenceFlows().forEach(this::createSequenceFlow);
        return definitions;
    }

    public String buildBpmnFile() {
        buildBpmnDefinitions();
        return JaxbWrapper.convertToXML(definitions);
    }

    private boolean isElement(FlowObject flowObject, String elementName) {
        return flowObject.getProperty("#type").equalsIgnoreCase(bpmnPath + elementName);
    }

    public Map<OriginatedElement, ProcessElement> getOriginatedProcessElementMap(Map<OriginatedElement, Object> originatedElementFlowObjectMap) {
        Map<OriginatedElement, ProcessElement> originatedElementProcessElementMap = new HashMap<>();

        for (Map.Entry<OriginatedElement, Object> entry : originatedElementFlowObjectMap.entrySet()) {
            if (entry.getValue() instanceof FlowObject) {
                FlowObject flowObject = (FlowObject) entry.getValue();
                String flowObjectId = flowObject.getId();
                if (flowObjectId != null) {
                    TFlowNode tFlowNode = flowNodeMap.get(flowObjectId);
                    TLane tLane = getTLane(flowObject);
                    ProcessElement processElement = new ProcessElement(tFlowNode, tLane);
                    originatedElementProcessElementMap.put(entry.getKey(), processElement);
                }
            }
        }

        return originatedElementProcessElementMap;
    }

    private void createStructure() {
        List<Cluster> clusters = bpmnModel.getClusters();
        for (Cluster cluster : clusters) {
            if (cluster instanceof Pool) {
                Pool pool = (Pool) cluster;
                TProcess tProcess = createProcess(pool);
                TLaneSet tLaneSet = createLaneSet(pool, tProcess);
                for (Lane lane : pool.getLanes()) {
                    createLane(lane, tLaneSet);
                }

            }
        }
        verifyDummyProcess();
    }

    public void verifyDummyProcess() {
        boolean needDummyProcess = false;
        for (FlowObject flowObject : bpmnModel.getFlowObjects()) {
            Pool pool = bpmnModel.getPoolForNode(flowObject);
            if (pool == null) {
                needDummyProcess = true;
            }
        }
        for (SequenceFlow sequenceFlow : bpmnModel.getSequenceFlows()) {
            Pool pool = bpmnModel.getPoolForNode(sequenceFlow.getSource());
            if (pool == null) {
                needDummyProcess = true;
            }
        }
        if (needDummyProcess) {
            this.dummyProcess = createDummyProcess();
        }
    }

    private TProcess createProcess(Pool pool) {
        TProcess tProcess = new TProcess();
        tProcess.setId(getNewId("pool-" + pool.getId()));
        tProcess.setName(pool.getName());
        bpmnWrapper.addTProcess(tProcess);

        poolMap.put(pool.getId(), tProcess);

        return tProcess;
    }

    private TProcess createDummyProcess() {
        TProcess tProcess = new TProcess();
        tProcess.setId(getNewId("pool-" + 0));
        bpmnWrapper.addTProcess(tProcess);
        return tProcess;
    }

    private TLaneSet createLaneSet(Pool pool, TProcess process) {
        TLaneSet tLaneSet = new TLaneSet();
        tLaneSet.setId(getNewId("laneset-" + pool.getId()));
        tLaneSet.setName(pool.getName());
        process.getLaneSet().add(tLaneSet);
        return tLaneSet;
    }

    private TLane createLane(Lane lane, TLaneSet laneSet) {
        TLane tLane = new TLane();
        tLane.setId(getNewId(lane.getId()));
        tLane.setName(lane.getName());
        laneSet.getLane().add(tLane);

        laneMap.put(lane.getId(), tLane);

        if (hasVisualization) {
            createProcessNodeVisualization(lane, getQName(tLane.getId()));
        }

        return tLane;
    }

    private TFlowNode createFlowNode(FlowObject flowObject, TFlowNode tFlowNode) {
        if (!flowObject.getId().isEmpty()) {
            tFlowNode.setId(getNewId(flowObject.getId()));
        }
        if (!flowObject.getName().isEmpty()) {
            tFlowNode.setName(flowObject.getName());
        }
        return tFlowNode;
    }

    private void positionFlowNode(FlowObject flowObject, TFlowNode tFlowNode, JAXBElement<? extends TFlowElement> jaxbFlowNode) {
        Pool pool = bpmnModel.getPoolForNode(flowObject);
        if (pool == null) {
            dummyProcess.getFlowElement().add(jaxbFlowNode);
        } else {
            TProcess tProcess = poolMap.get(pool.getId());
            tProcess.getFlowElement().add(jaxbFlowNode);

            Lane lane = getLaneForNode(flowObject, pool);
            if (lane != null) {
                TLane tLane = laneMap.get(lane.getId());
                tLane.getFlowNodeRef().add(new ObjectFactory().createTLaneFlowNodeRef(tFlowNode));
            }
        }

        if (hasVisualization) {
            createProcessNodeVisualization(flowObject, getQName(tFlowNode.getId()));
        }

        flowNodeMap.put(flowObject.getId(), tFlowNode);
    }

    private TStartEvent generatetStartEvent(String elementName, TFlowNode tFlowNode) {
        TStartEvent tEvent = (TStartEvent) tFlowNode;
        JAXBElement<? extends TEventDefinition> eventDefinition = getTEventDefinition(elementName);
        if (eventDefinition != null) {
            tEvent.getEventDefinition().add(eventDefinition);
        }
        return tEvent;
    }

    private TIntermediateCatchEvent generatetIntermediateCatchEvent(String elementName, TFlowNode tFlowNode) {
        TIntermediateCatchEvent tEvent = (TIntermediateCatchEvent) tFlowNode;
        JAXBElement<? extends TEventDefinition> eventDefinition = getTEventDefinition(elementName);
        if (eventDefinition != null) {
            tEvent.getEventDefinition().add(eventDefinition);
        }
        return tEvent;
    }

    private TIntermediateThrowEvent generatetIntermediateThrowEvent(String elementName, TFlowNode tFlowNode) {
        TIntermediateThrowEvent tEvent = (TIntermediateThrowEvent) tFlowNode;
        JAXBElement<? extends TEventDefinition> eventDefinition = getTEventDefinition(elementName);
        if (eventDefinition != null) {
            tEvent.getEventDefinition().add(eventDefinition);
        }
        return tEvent;
    }

    private TBoundaryEvent generatetBoundaryEvent(String elementName, TFlowNode tFlowNode) {
        TBoundaryEvent tEvent = (TBoundaryEvent) tFlowNode;
        JAXBElement<? extends TEventDefinition> eventDefinition = getTEventDefinition(elementName);
        if (eventDefinition != null) {
            tEvent.getEventDefinition().add(eventDefinition);
        }
        return tEvent;
    }

    private TEndEvent generatetEndEvent(String elementName, TFlowNode tFlowNode) {
        TEndEvent tEvent = (TEndEvent) tFlowNode;
        JAXBElement<? extends TEventDefinition> eventDefinition = getTEventDefinition(elementName);
        if (eventDefinition != null) {
            tEvent.getEventDefinition().add(eventDefinition);
        }
        return tEvent;
    }

    private TExclusiveGateway generatetExclusiveGateway(FlowObject flowObject, TFlowNode tFlowNode) {
        TExclusiveGateway tExclusiveGateway = (TExclusiveGateway) tFlowNode;
        tExclusiveGateway.setDefault(flowObject.getProperty("default"));
        return tExclusiveGateway;
    }

    private TInclusiveGateway generatetInclusiveGateway(FlowObject flowObject, TFlowNode tFlowNode) {
        TInclusiveGateway tInclusiveGateway = (TInclusiveGateway) tFlowNode;
        tInclusiveGateway.setDefault(flowObject.getProperty("default"));
        return tInclusiveGateway;
    }

    private JAXBElement<? extends TEventDefinition> getTEventDefinition(String eventDefinition) {

        if (eventDefinition.equalsIgnoreCase("CancelIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("CancelEndEvent")) {
            return new ObjectFactory().createCancelEventDefinition(new TCancelEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("CompesationStartEvent") ||
                eventDefinition.equalsIgnoreCase("CompesationIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("CompesationEndEvent")) {
            return new ObjectFactory().createCompensateEventDefinition(new TCompensateEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("ConditionalStartEvent") ||
                eventDefinition.equalsIgnoreCase("ConditionalIntermediateEvent")) {
            return new ObjectFactory().createConditionalEventDefinition(new TConditionalEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("ErrorStartEvent") ||
                eventDefinition.equalsIgnoreCase("ErrorIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("ErrorEndEvent")) {
            return new ObjectFactory().createErrorEventDefinition(new TErrorEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("EscalationStartEvent") ||
                eventDefinition.equalsIgnoreCase("EscalationIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("EscalationEndEvent")) {
            return new ObjectFactory().createEscalationEventDefinition(new TEscalationEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("MessageStartEvent") ||
                eventDefinition.equalsIgnoreCase("MessageIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("MessageEndEvent")) {
            return new ObjectFactory().createMessageEventDefinition(new TMessageEventDefinition());
        }

        /*
        if (eventDefinition.equalsIgnoreCase("MultipleStartEvent") ||
                eventDefinition.equalsIgnoreCase("MultipleIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("MultipleEndEvent")) {
        }

        if (eventDefinition.equalsIgnoreCase("ParallelMultipleIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("ParallelMultipleStartEvent")) {
        }
        */

        if (eventDefinition.equalsIgnoreCase("SignalStartEvent") ||
                eventDefinition.equalsIgnoreCase("SignalIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("SignalEndEvent")) {
            return new ObjectFactory().createSignalEventDefinition(new TSignalEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("TerminateEndEvent")) {
            return new ObjectFactory().createTerminateEventDefinition(new TTerminateEventDefinition());
        }

        if (eventDefinition.equalsIgnoreCase("TimerStartEvent") ||
                eventDefinition.equalsIgnoreCase("TimerIntermediateEvent") ||
                eventDefinition.equalsIgnoreCase("TimerEndEvent")) {
            return new ObjectFactory().createTimerEventDefinition(new TTimerEventDefinition());
        }

        return null;
    }

    private TSequenceFlow createSequenceFlow(SequenceFlow sequenceFlow) {
        TFlowNode sourceNode = sequenceFlow.getSource() != null ? flowNodeMap.get(sequenceFlow.getSource().getId()) : null;
        TFlowNode targetNode = sequenceFlow.getTarget() != null ? flowNodeMap.get(sequenceFlow.getTarget().getId()) : null;

        TSequenceFlow tSequenceFlow = new TSequenceFlow();
        tSequenceFlow.setId(getNewId(sequenceFlow.getId()));
        tSequenceFlow.setName(sequenceFlow.getName());

        Pool pool = bpmnModel.getPoolForNode(sequenceFlow.getSource());
        if (pool != null) {
            TProcess tProcess = poolMap.get(pool.getId());
            tProcess.getFlowElement().add(new ObjectFactory().createSequenceFlow(tSequenceFlow));
        } else {
            dummyProcess.getFlowElement().add(new ObjectFactory().createSequenceFlow(tSequenceFlow));
        }

        if (sourceNode != null) {
            tSequenceFlow.setSourceRef(sourceNode);
            sourceNode.getOutgoing().add(JaxbWrapper.getQName(tSequenceFlow.getClass(), tSequenceFlow));
        }

        if (targetNode != null) {
            tSequenceFlow.setTargetRef(targetNode);
            targetNode.getIncoming().add(JaxbWrapper.getQName(tSequenceFlow.getClass(), tSequenceFlow));
        }

        if (hasVisualization) {
            createEdgeVis(sequenceFlow, getQName(tSequenceFlow.getId()));
        }

        return tSequenceFlow;
    }

    private TLane getTLane(FlowObject flowObject) {
        Lane lane = getLane(flowObject);
        return lane != null ? laneMap.get(lane.getId()) : null;
    }

    private Lane getLane(FlowObject flowObject) {
        Pool pool = bpmnModel.getPoolForNode(flowObject);
        Lane lane = null;
        if (pool != null) {
            lane = getLaneForNode(flowObject, pool);
        }
        return lane;
    }

    private Lane getLaneForNode(FlowObject flowObject, Pool pool) {
        for (Lane lane : pool.getLanes()) {
            for (ProcessNode containedNode : lane.getProcessNodes()) {
                if (flowObject.getId().equals(containedNode.getId())) {
                    return lane;
                }
            }
        }
        return null;
    }

    private String getNewId(String id) {
        return "id-" + genericId++;
    }

    private void createVisualizationStructure() {
        TCollaboration tCollaboration = new TCollaboration();
        tCollaboration.setId(getNewId(""));

        definitions.getRootElement().add(new ObjectFactory().createCollaboration(tCollaboration));
        createDiagramVisualization(tCollaboration);

        for (TProcess tProcess : bpmnWrapper.getProcessList()) {
            TParticipant tParticipant = new TParticipant();
            tParticipant.setId(getNewId(""));
            tParticipant.setName("Participant - " + tProcess.getName());
            tParticipant.setProcessRef(tProcess.getDefinitionalCollaborationRef());

            tCollaboration.getParticipant().add(tParticipant);
            createParticipant(tParticipant);
        }
    }

    private BPMNDiagram createDiagramVisualization(TCollaboration tCollaboration) {
        BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId(getNewId(""));

        BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnPlane.setId(getNewId(""));
        bpmnPlane.setBpmnElement(getQName(tCollaboration.getId()));
        bpmnDiagram.setBPMNPlane(bpmnPlane);

        this.definitions.getBPMNDiagram().add(bpmnDiagram);

        return bpmnDiagram;
    }

    private BPMNShape createParticipant(TParticipant tParticipant) {
        Bounds shapeBounds = new Bounds();
        shapeBounds.setX(0);
        shapeBounds.setY(0);
        shapeBounds.setHeight(0);
        shapeBounds.setWidth(0);

        BPMNShape bpmnShape = new BPMNShape();
        bpmnShape.setId(getNewId(tParticipant.getId()));
        bpmnShape.setBpmnElement(getQName(tParticipant.getId()));
        //bpmnShape.setBPMNLabel();
        bpmnShape.setBounds(shapeBounds);

        JAXBElement<BPMNShape> jaxbElement = new org.omg.spec.bpmn._20100524.di.ObjectFactory().createBPMNShape(bpmnShape);
        this.definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().add(jaxbElement);

        return bpmnShape;
    }

    private BPMNShape createProcessNodeVisualization(ProcessNode flowObject, QName elementId) {
        Bounds labelBounds = new Bounds();
        labelBounds.setX(flowObject.getPos().getX() + flowObject.getSize().getWidth());
        labelBounds.setY(flowObject.getPos().getY() + flowObject.getSize().getHeight());
        labelBounds.setWidth(0);
        labelBounds.setHeight(13);

        BPMNLabel bpmnLabel = new BPMNLabel();
        bpmnLabel.setBounds(labelBounds);

        Bounds shapeBounds = new Bounds();
        shapeBounds.setX(flowObject.getPos().getX());
        shapeBounds.setY(flowObject.getPos().getY());
        shapeBounds.setHeight(flowObject.getSize().getHeight());
        shapeBounds.setWidth(flowObject.getSize().getWidth());

        BPMNShape bpmnShape = new BPMNShape();
        bpmnShape.setId(getNewId(flowObject.getId()));
        bpmnShape.setBpmnElement(elementId);
        bpmnShape.setBPMNLabel(bpmnLabel);
        bpmnShape.setBounds(shapeBounds);

        JAXBElement<BPMNShape> jaxbElement = new org.omg.spec.bpmn._20100524.di.ObjectFactory().createBPMNShape(bpmnShape);
        this.definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().add(jaxbElement);

        return bpmnShape;
    }

    private BPMNEdge createEdgeVis(SequenceFlow sequenceFlow, QName elementId) {
        Bounds bounds = new Bounds();
        bounds.setX(sequenceFlow.getLabelPosition().getX());
        bounds.setY(sequenceFlow.getLabelPosition().getY());
        bounds.setWidth(0);
        bounds.setHeight(13);

        BPMNLabel bpmnLabel = new BPMNLabel();
        bpmnLabel.setBounds(bounds);

        BPMNEdge bpmnEdge = new BPMNEdge();
        bpmnEdge.setBpmnElement(elementId);
        bpmnEdge.setBPMNLabel(bpmnLabel);

        JAXBElement<BPMNEdge> jaxbElement = new org.omg.spec.bpmn._20100524.di.ObjectFactory().createBPMNEdge(bpmnEdge);
        this.definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().add(jaxbElement);

        return bpmnEdge;
    }

    private QName getQName(String elementId) {
        return new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", elementId, "");
    }

}
