package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.changes.transform.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.metatext.TMetaText;
import com.inubit.research.textToProcess.TextToProcess;
import com.inubit.research.textToProcess.processing.T2PStanfordWrapper;
import com.inubit.research.textToProcess.text.Text;
import com.inubit.research.textToProcess.transform.TextAnalyzer;
import com.inubit.research.textToProcess.worldModel.OriginatedElement;
import net.frapu.code.visualization.bpmn.BPMNModel;
import org.apache.commons.io.FileUtils;
import org.omg.spec.bpmn._20100524.model.TDefinitions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProcessGenerator {

    public static String generateProcess(String text) throws IOException {
        Text f_text;

        T2PStanfordWrapper f_stanford = new T2PStanfordWrapper(true);
        TextToProcess f_processor = new TextToProcess(f_stanford);

        TextAnalyzer f_analyzer = new TextAnalyzer();
        BPMNModel f_generatedModel;

        File file = getFile(text);

        f_text = f_stanford.createText(file, null);

        f_analyzer.clear();

        f_analyzer.analyze(f_text);

        ProcessModelBuilder _builder = new ProcessModelBuilder(f_processor);

        f_generatedModel = _builder.createProcessModel(f_analyzer.getWorld());

        BpmnBuilder bpmnBuilder = new BpmnBuilder(f_generatedModel);
        return bpmnBuilder.buildBpmnFile();
    }

    public static TMetaText generateText(String text) throws IOException {
        Text f_text;

        T2PStanfordWrapper f_stanford = new T2PStanfordWrapper(true);
        TextToProcess f_processor = new TextToProcess(f_stanford);

        TextAnalyzer f_analyzer = new TextAnalyzer();
        BPMNModel f_generatedModel;

        File file = getFile(text);

        f_text = f_stanford.createText(file, null);

        f_analyzer.clear();

        f_analyzer.analyze(f_text);

        ProcessModelBuilder _builder = new ProcessModelBuilder(f_processor);

        f_generatedModel = _builder.createProcessModel(f_analyzer.getWorld());

        BpmnBuilder bpmnBuilder = new BpmnBuilder(f_generatedModel);
        TDefinitions tDefinitions = bpmnBuilder.buildBpmnDefinitions();

        TextGenerator textGenerator = new TextGenerator();
        Map<OriginatedElement, Object> originatedElementFlowObjectMap = _builder.getOriginatedElementFlowObjectMap();

        Map<OriginatedElement, ProcessElement> originatedElementProcessElementMap = bpmnBuilder.getOriginatedProcessElementMap(originatedElementFlowObjectMap);

        return textGenerator.generateText(f_text, originatedElementProcessElementMap, tDefinitions);
    }

    public static String generateProcessFromModel(File file) throws Exception {
        StringBuilder bpmnProcess = new StringBuilder();

        ModelToBpmn modelToBpmn = new ModelToBpmn();
        List<BPMNModel> bpmnModelList = modelToBpmn.generateBpmn(file);
        for (BPMNModel bpmnModel : bpmnModelList) {
            BpmnBuilder bpmnBuilder = new BpmnBuilder(bpmnModel);
            bpmnProcess.append(bpmnBuilder.buildBpmnFile());
        }

        return bpmnProcess.toString();
    }

    private static File getFile(String text) throws IOException {
        File file = File.createTempFile("text", ".txt");
        FileUtils.writeStringToFile(file, text, "UTF-8");
        return file;
    }

}
