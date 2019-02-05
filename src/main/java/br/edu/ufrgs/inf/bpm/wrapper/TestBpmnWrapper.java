package br.edu.ufrgs.inf.bpm.wrapper;

import br.edu.ufrgs.inf.bpm.builder.BpmnBuilder;
import net.frapu.code.visualization.bpmn.Activity;
import net.frapu.code.visualization.bpmn.BPMNModel;
import net.frapu.code.visualization.bpmn.Lane;
import net.frapu.code.visualization.bpmn.Pool;

public class TestBpmnWrapper {
    public static void main(String[] args) {
        BPMNModel model = new BPMNModel();

        Activity activity = new Activity();
        activity.setId("activity1");
        activity.setText("activity 1");
        model.addNode(activity);

        Lane lane = new Lane();
        lane.setId("lane1");
        lane.addProcessNode(activity);
        model.addNode(lane);

        Pool pool = new Pool();
        pool.addLane(lane);
        model.addNode(pool);

        BpmnBuilder processBuilder = new BpmnBuilder(model);
        String process = processBuilder.buildBpmnFile();
        System.out.println(process);
    }
}
