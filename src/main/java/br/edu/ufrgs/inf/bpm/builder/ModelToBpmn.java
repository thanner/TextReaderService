package br.edu.ufrgs.inf.bpm.builder;

import net.frapu.code.converter.ConverterHelper;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.bpmn.BPMNModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelToBpmn {

    public List<BPMNModel> generateBpmn(File file) throws Exception {
        List<BPMNModel> bpmnModelList = new ArrayList<>();
        List processModelList = ConverterHelper.importModels(file);
        if (processModelList == null) {
            throw new Exception("Model type not recognized");
        }

        Iterator iterator = processModelList.iterator();
        while (iterator.hasNext()) {
            ProcessModel processModel = (ProcessModel) iterator.next();
            processModel.setProcessModelURI(file.getAbsolutePath());
            // this.pei.processModelOpened(processModel);

            if (processModel instanceof BPMNModel) {
                bpmnModelList.add((BPMNModel) processModel);
            }

        }

        return bpmnModelList;
    }

}
