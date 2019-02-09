package br.edu.ufrgs.inf.bpm.application;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.metatext.TMetaText;
import br.edu.ufrgs.inf.bpm.util.Paths;
import br.edu.ufrgs.inf.bpm.wrapper.JsonWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DataGeneratorApp {

    private static Logger logger = Logger.getLogger("DataGeneratorLog");

    public static void main(String[] args) {
        prepareLogger();
        ApplicationStarter.startApplication();

        File folder = new File(Paths.LocalOthersPath + Paths.dataInputPath);
        for (File fileEntry : folder.listFiles()) {
            generateData(fileEntry, true);
        }
    }

    private static void prepareLogger() {
        try {
            FileHandler fileHandler = new FileHandler(Paths.LocalOthersPath + Paths.dataGeneratorLogPath);
            logger.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateData(File inputFile, boolean verifyOnlyNewFiles) {
        try {
            String inputFileName = FilenameUtils.removeExtension(inputFile.getName());
            logger.info("Input File - " + inputFileName);

            if (isFileText(inputFile)) {
                handleText(inputFile, verifyOnlyNewFiles);
            } else if (isFileModel(inputFile)) {
                handleModel(inputFile, verifyOnlyNewFiles);
            }
        } catch (Exception e) {
            logger.warning(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void handleText(File inputFile, boolean verifyOnlyNewFiles) throws IOException {
        String inputFileName = FilenameUtils.removeExtension(inputFile.getName());
        String text = FileUtils.readFileToString(inputFile, "UTF-8");

        String processFileName = Paths.LocalOthersPath + Paths.dataOutputPath + inputFileName + " - process.bpmn";
        File processFile = new File(processFileName);
        if (verifyOnlyNewFiles && processFile.exists()) {
            logger.info("Process already exists");
        } else {
            String process = ProcessGenerator.generateProcess(text);
            FileUtils.writeStringToFile(processFile, process, "UTF-8");
            logger.info("Process - Done");
        }

        String metaTextFileName = Paths.LocalOthersPath + Paths.dataOutputPath + inputFileName + " - metaText.json";
        File metaTextFile = new File(metaTextFileName);
        if (verifyOnlyNewFiles && metaTextFile.exists()) {
            logger.info("Metatext already exists");
        } else {
            TMetaText metaText = ProcessGenerator.generateText(text);
            String metaTextString = JsonWrapper.getJson(metaText);
            FileUtils.writeStringToFile(metaTextFile, metaTextString, "UTF-8");
            logger.info("Metatext - Done");
        }
    }

    private static void handleModel(File inputFile, boolean verifyOnlyNewFiles) throws Exception {
        String inputFileName = FilenameUtils.removeExtension(inputFile.getName());

        String processFileName = Paths.LocalOthersPath + Paths.dataOutputPath + inputFileName + " - originalProcess.bpmn";
        File processFile = new File(processFileName);
        if (verifyOnlyNewFiles && processFile.exists()) {
            logger.info("Process already exists");
        } else {
            String process = ProcessGenerator.generateProcessFromModel(inputFile);
            FileUtils.writeStringToFile(processFile, process, "UTF-8");
            logger.info("Process - Done");
        }
    }

    private static boolean isFileText(File inputFile) {
        return isFileType(inputFile, "txt");
    }

    private static boolean isFileModel(File inputFile) {
        return isFileType(inputFile, "model");
    }

    private static boolean isFileType(File inputFile, String type) {
        return FilenameUtils.getExtension(inputFile.getName()).equalsIgnoreCase(type);
    }

}
