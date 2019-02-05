package br.edu.ufrgs.inf.bpm.changes.similarity;

import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.bpmn.*;
import org.omg.spec.bpmn._20100524.model.*;
import org.processmining.analysis.graphmatching.algos.GraphEditDistanceGreedy;
import org.processmining.analysis.graphmatching.graph.SimpleGraph;
import org.processmining.analysis.graphmatching.graph.TwoVertices;

import javax.xml.bind.JAXBElement;
import java.util.*;

public class SimilarityHandler {

    private static Object weights[] = {"vweight", 0.0, "eweight", 0.0, "sweight", 0.0, "ledcutoff", 0.0};
    private GraphEditDistanceGreedy graphEditDistanceGreedy;
    private double graphEditDistance;
    private int id = 0;

    public SimilarityHandler() {
        graphEditDistanceGreedy = new GraphEditDistanceGreedy();
    }

    public static void setWeightskippedVertices(double value) {
        weights[1] = value;
    }

    public static void setWeightskippedEdges(double value) {
        weights[3] = value;
    }

    public static void setWeightsubstitutedVertices(double value) {
        weights[5] = value;
    }

    public static void setNotConsideredCutOffSimilarity(double value) {
        weights[7] = value;
    }

    public void compute(SimpleGraph simpleGraphOriginal, SimpleGraph simpleGraphGenerated) {
        graphEditDistanceGreedy.setWeight(weights);
        graphEditDistance = graphEditDistanceGreedy.compute(simpleGraphOriginal, simpleGraphGenerated);
    }

    public SimpleGraph generateSimpleGraph(TDefinitions definitions, Map<TFlowNode, Integer> elementMap) {
        Set<Integer> vertices = new HashSet<>();
        Map<Integer, String> labels = new HashMap<>();

        Map<Integer, Set<Integer>> incomingEdges = new HashMap<>();
        Map<Integer, Set<Integer>> outgoingEdges = new HashMap<>();

        Set<Integer> functionVertices = new HashSet<>();
        Set<Integer> eventVertices = new HashSet<>();
        Set<Integer> connectorVertices = new HashSet<>();

        BpmnWrapper bpmnWrapper = new BpmnWrapper(definitions);

        for (TFlowNode flowObject : bpmnWrapper.getFlowNodeList()) {
            vertices.add(elementMap.get(flowObject));
            String label = flowObject.getName() != null ? flowObject.getName() : "";
            labels.put(elementMap.get(flowObject), label);

            incomingEdges.put(elementMap.get(flowObject), new HashSet<>());
            outgoingEdges.put(elementMap.get(flowObject), new HashSet<>());
        }

        for (TSequenceFlow sequenceFlow : bpmnWrapper.getSequenceFlowList()) {
            incomingEdges.get(elementMap.get(bpmnWrapper.getTargetBySequenceFlow(sequenceFlow))).add(elementMap.get(bpmnWrapper.getSourceBySequenceFlow(sequenceFlow)));
            outgoingEdges.get(elementMap.get(bpmnWrapper.getSourceBySequenceFlow(sequenceFlow))).add(elementMap.get(bpmnWrapper.getTargetBySequenceFlow(sequenceFlow)));
        }

        return new SimpleGraph(vertices, outgoingEdges, incomingEdges, labels, functionVertices, eventVertices, connectorVertices);
    }

    public SimpleGraph generateSimpleGraph(BPMNModel model) {
        Map<ProcessObject, Integer> elementMap = mapElements(model);

        Set<Integer> vertices = new HashSet<>();
        Map<Integer, String> labels = new HashMap<>();

        Map<Integer, Set<Integer>> incomingEdges = new HashMap<>();
        Map<Integer, Set<Integer>> outgoingEdges = new HashMap<>();

        Set<Integer> functionVertices = new HashSet<>();
        Set<Integer> eventVertices = new HashSet<>();
        Set<Integer> connectorVertices = new HashSet<>();

        for (FlowObject flowObject : model.getFlowObjects()) {
            vertices.add(elementMap.get(flowObject));
            String label = flowObject.getName() != null ? flowObject.getName() : "";
            labels.put(elementMap.get(flowObject), label);

            incomingEdges.put(elementMap.get(flowObject), new HashSet<>());
            outgoingEdges.put(elementMap.get(flowObject), new HashSet<>());
        }

        for (SequenceFlow sequenceFlow : model.getSequenceFlows()) {
            incomingEdges.get(elementMap.get(sequenceFlow.getTarget())).add(elementMap.get(sequenceFlow.getSource()));
            outgoingEdges.get(elementMap.get(sequenceFlow.getSource())).add(elementMap.get(sequenceFlow.getTarget()));
        }

        return new SimpleGraph(vertices, outgoingEdges, incomingEdges, labels, functionVertices, eventVertices, connectorVertices);
    }

    private Map<TFlowNode, Integer> mapElements(BpmnWrapper bpmnWrapper) {
        Map<TFlowNode, Integer> elementMap = new HashMap<>();

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            elementMap.put(tFlowNode, id++);
        }

        return elementMap;
    }

    private Map<ProcessObject, Integer> mapElements(BPMNModel model) {
        Map<ProcessObject, Integer> elementMap = new HashMap<>();

        for (FlowObject flowObject : model.getFlowObjects()) {
            elementMap.put(flowObject, id++);
        }

        /*
        for (SequenceFlow sequenceFlow : model.getSequenceFlows()) {
            elementMap.put(sequenceFlow, id++);
        }
        */

        return elementMap;
    }

    public double getSimilarity() {
        return 1 - graphEditDistance;
    }

    public Set<TwoVertices> getMapping() {
        return graphEditDistanceGreedy.bestMapping();
    }

}
