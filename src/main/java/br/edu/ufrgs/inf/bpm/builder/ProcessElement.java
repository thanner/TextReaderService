package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.metatext.ProcessElementType;
import org.omg.spec.bpmn._20100524.model.*;

public class ProcessElement {

    private TFlowNode tFlowNode;
    private TLane tLane;

    public ProcessElement(TFlowNode tFlowNode, TLane tLane) {
        this.tFlowNode = tFlowNode;
        this.tLane = tLane;
    }

    public String getResource() {
        return tLane != null ? tLane.getName() : "";
    }

    public String getProcessElementId() {
        return tFlowNode != null ? tFlowNode.getId() : "";
    }

    public ProcessElementType getProcessElement() {
        if (tFlowNode instanceof TStartEvent) {
            return ProcessElementType.STARTEVENT;
        } else if (tFlowNode instanceof TActivity) {
            return ProcessElementType.ACTIVITY;
        } else if (tFlowNode instanceof TExclusiveGateway) {
            return tFlowNode.getIncoming().size() < tFlowNode.getOutgoing().size() ? ProcessElementType.XORSPLIT : ProcessElementType.XORJOIN;
        } else if (tFlowNode instanceof TParallelGateway) {
            return tFlowNode.getIncoming().size() < tFlowNode.getOutgoing().size() ? ProcessElementType.ANDSPLIT : ProcessElementType.ANDJOIN;
        } else if (tFlowNode instanceof TInclusiveGateway) {
            return tFlowNode.getIncoming().size() < tFlowNode.getOutgoing().size() ? ProcessElementType.ORSPLIT : ProcessElementType.ORJOIN;
        } else if (tFlowNode instanceof TEndEvent) {
            return ProcessElementType.ENDEVENT;
        } else {
            return ProcessElementType.UNKNOWN;
        }
    }

}
