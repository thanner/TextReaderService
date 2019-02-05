package test;

import br.edu.ufrgs.inf.bpm.application.SimilarityApp;
import br.edu.ufrgs.inf.bpm.builder.BpmnBuilder;
import br.edu.ufrgs.inf.bpm.changes.similarity.ModelReduction;
import junit.framework.TestCase;
import net.frapu.code.visualization.bpmn.*;
import org.omg.spec.bpmn._20100524.model.TDefinitions;

public class SimilarityGraphEditDistanceTest extends TestCase {

    private static TDefinitions generateModelOriginal() {
        BPMNModel bpmnModel = new BPMNModel();

        Activity a1 = new Activity();
        a1.setText("Order");
        bpmnModel.addNode(a1);

        Activity a2 = new Activity();
        a2.setText("Verification Invoice");
        bpmnModel.addNode(a2);

        SequenceFlow s1 = new SequenceFlow();
        s1.setSource(a1);
        s1.setTarget(a2);
        bpmnModel.addFlow(s1);

        BpmnBuilder bpmnBuilder = new BpmnBuilder(bpmnModel);
        TDefinitions definitions = bpmnBuilder.buildBpmnDefinitions();
        return definitions;
    }

    private static TDefinitions generateModelGenerated() {
        BPMNModel bpmnModel = new BPMNModel();

        Activity a1 = new Activity();
        a1.setText("Order");
        bpmnModel.addNode(a1);

        Activity a2 = new Activity();
        a2.setText("Receive goods");
        bpmnModel.addNode(a2);

        SequenceFlow s1 = new SequenceFlow();
        s1.setSource(a1);
        s1.setTarget(a2);
        bpmnModel.addFlow(s1);

        Gateway g1 = new ParallelGateway();
        g1.setText("");
        bpmnModel.addNode(g1);

        SequenceFlow sx = new SequenceFlow();
        sx.setSource(a2);
        sx.setTarget(g1);
        bpmnModel.addFlow(sx);

        Activity a3 = new Activity();
        a3.setText("Verify invoice");
        bpmnModel.addNode(a3);

        Activity a4 = new Activity();
        a4.setText("Store goods");
        bpmnModel.addNode(a4);

        SequenceFlow sg1a3 = new SequenceFlow();
        sg1a3.setSource(g1);
        sg1a3.setTarget(a3);
        bpmnModel.addFlow(sg1a3);

        SequenceFlow sg1a4 = new SequenceFlow();
        sg1a4.setSource(g1);
        sg1a4.setTarget(a4);
        bpmnModel.addFlow(sg1a4);

        Gateway g2 = new ParallelGateway();
        g2.setText("");
        bpmnModel.addNode(g2);

        SequenceFlow sg1a5 = new SequenceFlow();
        sg1a5.setSource(a3);
        sg1a5.setTarget(g2);
        bpmnModel.addFlow(sg1a5);

        SequenceFlow sg1a6 = new SequenceFlow();
        sg1a6.setSource(a4);
        sg1a6.setTarget(g2);
        bpmnModel.addFlow(sg1a6);

        BpmnBuilder bpmnBuilder = new BpmnBuilder(bpmnModel);
        TDefinitions definitions = bpmnBuilder.buildBpmnDefinitions();
        return definitions;
    }

    public void testSimilarity() {
        TDefinitions modelOriginal = generateModelOriginal();
        modelOriginal = ModelReduction.makeReduction(modelOriginal);

        TDefinitions modelGenerated = generateModelGenerated();
        modelGenerated = ModelReduction.makeReduction(modelGenerated);

        SimilarityApp similarityApp = new SimilarityApp();
        similarityApp.configureSystem(0.1, 0.3, 1.0, 0.001);
        double similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);

        assertEquals(similarity, 0.73, 0.01);
    }

}
