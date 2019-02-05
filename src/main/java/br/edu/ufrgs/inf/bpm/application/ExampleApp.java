package br.edu.ufrgs.inf.bpm.application;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.metatext.TMetaText;
import br.edu.ufrgs.inf.bpm.wrapper.JsonWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ExampleApp {

    public static void main(String[] args) throws Exception {
        ApplicationStarter.startApplication();

        // "A small company manufactures customized bicycles. Whenever the sales department receives an order, a new process instance is created. A member of the sales department can then reject or accept the order for a customized bike. In the former case, the process instance is finished. In the latter case, the storehouse and the engineering department are informed. The storehouse immediately processes the part list of the order and checks the required quantity of each part. If the part is available in-house, it is reserved. If it is not available, it is back-ordered. This procedure is repeated for each item on the part list. In the meantime, the engineering department prepares everything for the assembling of the ordered bicycle. If the storehouse has successfully reserved or back-ordered every item of the part list and the preparation activity has finished, the engineering department assembles the bicycle. Afterwards, the sales department ships the bicycle to the customer and finishes the process instance."

        String text;
        String process;

        text = FileUtils.readFileToString(new File("src/main/others/DataExtra/Activities.txt"), "UTF-8");
        // process = ProcessGenerator.generateProcess(text);
        // System.out.println("ExampleApp 1:");
        // System.out.println(process);

        TMetaText newText = ProcessGenerator.generateText(text);
        System.out.println("ExampleApp 2");
        System.out.println(JsonWrapper.getJson(newText));

        // text = FileUtils.readFileToString(new File("src/main/others/TestDataExtra/Events.txt"), "UTF-8");
        // process = ProcessGenerator.generateProcess(text);
        // System.out.println(process);
    }

}
