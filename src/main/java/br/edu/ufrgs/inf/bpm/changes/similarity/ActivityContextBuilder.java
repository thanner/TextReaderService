package br.edu.ufrgs.inf.bpm.changes.similarity;

import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import org.omg.spec.bpmn._20100524.model.*;
import org.processmining.analysis.epc.similarity.ActivityContextFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityContextBuilder {

    private Map<TFlowNode, Integer> idMap;

    public ActivityContextBuilder() {
        idMap = new HashMap<>();
    }

    public List<ActivityContextFragment> convertWithContext(TDefinitions definitions) {
        List<ActivityContextFragment> activityContextFragmentList = new ArrayList<>();
        BpmnWrapper bpmnWrapper = new BpmnWrapper(definitions);

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            if (tFlowNode.getName() == null) {
                tFlowNode.setName("");
            }
        }

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            ActivityContextFragment activityContextFragment = new ActivityContextFragment(tFlowNode.getName());

            activityContextFragment.setInType(getInType(tFlowNode, bpmnWrapper));
            activityContextFragment.setOutType(getOutType(tFlowNode, bpmnWrapper));

            for (TFlowNode tFlowNodeInput : bpmnWrapper.getFlowNodeSourceList(tFlowNode)) {
                activityContextFragment.addToInputContext(tFlowNodeInput.getName());
            }

            for (TFlowNode tFlowNodeInput : bpmnWrapper.getFlowNodeTargetList(tFlowNode)) {
                activityContextFragment.addToOutputContext(tFlowNodeInput.getName());
            }

            idMap.put(tFlowNode, activityContextFragmentList.size());
            activityContextFragmentList.add(activityContextFragment);
        }

        return activityContextFragmentList;
    }

    /**
     * // Used from ActivityFragmentBuilder
     * private List<ActivityContextFragment> getFragments(ConfigurableEPC epc) {
     * ArrayList<ActivityContextFragment> fragments = new ArrayList();
     * Iterator functions = epc.getFunctions().iterator();
     * <p>
     * while(functions.hasNext()) {
     * EPCFunction function = (EPCFunction)functions.next();
     * ActivityContextFragment fragment = new ActivityContextFragment(function.getIdentifier());
     * <p>
     * ArrayList<EPCEvent> preEventList = epc.getPreceedingEvents(function);
     * Iterator preEvent = preEventList.iterator();
     * <p>
     * while(preEvent.hasNext()) {
     * EPCEvent event = (EPCEvent)preEvent.next();
     * fragment.addToInputContext(event.getIdentifier());
     * }
     * <p>
     * ArrayList<EPCEvent> postEventList = epc.getSucceedingEvents(function);
     * Iterator postEvent = postEventList.iterator();
     * <p>
     * while(postEvent.hasNext()) {
     * EPCEvent event = (EPCEvent)postEvent.next();
     * fragment.addToOutputContext(event.getIdentifier());
     * }
     * <p>
     * fragment.setInType(3);
     * <p>
     * //idMap.put(tFlowNode, fragments.size());
     * fragments.add(fragment);
     * }
     * <p>
     * return fragments;
     * }
     */

    public List<ActivityContextFragment> convert(TDefinitions definitions) {
        List<ActivityContextFragment> activityContextFragmentList = new ArrayList<>();

        BpmnWrapper bpmnWrapper = new BpmnWrapper(definitions);

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            if (tFlowNode.getName() == null) {
                tFlowNode.setName("");
            }
        }

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            if (tFlowNode instanceof TActivity) {
                ActivityContextFragment activityContextFragment = new ActivityContextFragment(tFlowNode.getName());

                for (TFlowNode tFlowNodeInput : bpmnWrapper.getFlowNodeSourceList(tFlowNode)) {
                    if (tFlowNodeInput instanceof TEvent) {
                        activityContextFragment.addToInputContext(tFlowNodeInput.getName());
                    }
                }

                for (TFlowNode tFlowNodeInput : bpmnWrapper.getFlowNodeTargetList(tFlowNode)) {
                    if (tFlowNodeInput instanceof TEvent) {
                        activityContextFragment.addToOutputContext(tFlowNodeInput.getName());
                    }
                }

                activityContextFragment.setInType(3);
                activityContextFragment.setOutType(3);

                idMap.put(tFlowNode, activityContextFragmentList.size());
                activityContextFragmentList.add(activityContextFragment);
            }
        }

        // ConfigurableEPC epc = new ConfigurableEPC();
        // ActivityFragmentBuilder getFragments(epc)
        return activityContextFragmentList;
    }

    private int getInType(TFlowNode tFlowNode, BpmnWrapper bpmnWrapper) {
        List<TFlowNode> flowNodeInputList = bpmnWrapper.getFlowNodeSourceList(tFlowNode);
        return getType(flowNodeInputList);
    }

    private int getOutType(TFlowNode tFlowNode, BpmnWrapper bpmnWrapper) {
        List<TFlowNode> flowNodeOutputList = bpmnWrapper.getFlowNodeTargetList(tFlowNode);
        return getType(flowNodeOutputList);
    }

    private int getType(List<TFlowNode> flowNodeList) {
        if (flowNodeList.size() > 1) {
            return ActivityContextFragment.NONE;
        } else if (flowNodeList.size() == 1) {
            TFlowNode flowNodeInput = flowNodeList.get(0);
            if (flowNodeInput instanceof TExclusiveGateway) {
                return ActivityContextFragment.XOR;
            } else if (flowNodeInput instanceof TInclusiveGateway) {
                return ActivityContextFragment.OR;
            } else if (flowNodeInput instanceof TParallelGateway) {
                return ActivityContextFragment.AND;
            }
        }

        return ActivityContextFragment.NONE;
    }

    public Map<TFlowNode, Integer> getIdMap() {
        return idMap;
    }
}
