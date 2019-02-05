package br.edu.ufrgs.inf.bpm.changes.similarity;

import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class ModelReduction {

    private static int id = 0;

    public static TDefinitions makeReduction(TDefinitions definitions) {
        BpmnWrapper model = new BpmnWrapper(definitions);
        BpmnWrapper newModel = new BpmnWrapper(new TDefinitions());

        TProcess tProcess = new TProcess();
        newModel.addTProcess(tProcess);

        for (TFlowNode flowObject : model.getFlowNodeList()) {
            if (flowObject instanceof TActivity) {
                JAXBElement<TTask> jaxbFlowNode = new ObjectFactory().createTask((TTask) flowObject);
                tProcess.getFlowElement().add(jaxbFlowNode);
            }
        }

        for (TFlowNode tFlowNode : model.getFlowNodeList()) {
            if (tFlowNode instanceof TActivity) {
                TActivity activitySource = (TActivity) tFlowNode;
                for (TSequenceFlow sequenceFlow : model.getSequenceFlowTarget(activitySource)) {
                    for (TActivity activityTarget : getTargetActivities(model, sequenceFlow)) {
                        TSequenceFlow tSequenceFlow = new TSequenceFlow();
                        tSequenceFlow.setId("Sequence-" + id++);

                        tSequenceFlow.setSourceRef(activitySource);
                        activitySource.getOutgoing().add(JaxbWrapper.getQName(tSequenceFlow.getClass(), tSequenceFlow));

                        tSequenceFlow.setTargetRef(activityTarget);
                        activityTarget.getIncoming().add(JaxbWrapper.getQName(tSequenceFlow.getClass(), tSequenceFlow));

                        tProcess.getFlowElement().add(new ObjectFactory().createSequenceFlow(tSequenceFlow));
                    }
                }
            }
        }

        return newModel.getDefinitions();
    }

    private static BPMNModel makeReduction(BPMNModel model) {
        BPMNModel newModel = new BPMNModel();
        for (FlowObject flowObject : model.getFlowObjects()) {
            if (flowObject instanceof Activity) {
                newModel.addFlowObject(flowObject);
            }
        }

        for (FlowObject flowObject : model.getFlowObjects()) {
            if (flowObject instanceof Activity) {
                Activity activitySource = (Activity) flowObject;
                List<ProcessEdge> processEdgeList = model.getOutgoingEdges(SequenceFlow.class, activitySource);
                for (ProcessEdge processEdge : processEdgeList) {
                    if (processEdge instanceof SequenceFlow) {
                        SequenceFlow sequenceFlow = (SequenceFlow) processEdge;
                        List<Activity> activityTargetList = getTargetActivities(model, sequenceFlow);
                        for (Activity activityTarget : activityTargetList) {
                            SequenceFlow newSequenceFlow = new SequenceFlow();
                            newSequenceFlow.setSource(activitySource);
                            newSequenceFlow.setTarget(activityTarget);
                            newModel.addFlow(newSequenceFlow);
                        }
                    }
                }
            }
        }

        return newModel;
    }

    private static List<TActivity> getTargetActivities(BpmnWrapper model, TSequenceFlow sequenceFlow) {
        List<TActivity> activityList = new ArrayList<>();
        TFlowNode target = model.getTargetBySequenceFlow(sequenceFlow);
        if (target instanceof TActivity) {
            activityList.add((TActivity) target);
        } else if (target instanceof TGateway) {
            for (TSequenceFlow sequenceFlowTarget : model.getSequenceFlowTarget(target)) {
                activityList.addAll(getTargetActivities(model, sequenceFlowTarget));
            }
        }
        return activityList;
    }

    private static List<Activity> getTargetActivities(BPMNModel model, SequenceFlow sequenceFlow) {
        List<Activity> activityList = new ArrayList<>();
        ProcessNode target = sequenceFlow.getTarget();
        if (target instanceof Activity) {
            activityList.add((Activity) target);
        } else if (target instanceof Gateway) {
            for (SequenceFlow sequenceFlowTarget : getSequenceFlowTarget(model, target)) {
                activityList.addAll(getTargetActivities(model, sequenceFlowTarget));
            }
        }
        return activityList;
    }

    private static List<SequenceFlow> getSequenceFlowTarget(BPMNModel model, ProcessNode processNode) {
        List<SequenceFlow> sequenceFlowList = new ArrayList<>();
        List<ProcessEdge> processEdgeList = model.getOutgoingEdges(SequenceFlow.class, processNode);
        for (ProcessEdge processEdge : processEdgeList) {
            if (processEdge instanceof SequenceFlow) {
                sequenceFlowList.add((SequenceFlow) processEdge);
            }
        }
        return sequenceFlowList;
    }

}
