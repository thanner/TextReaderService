package br.edu.ufrgs.inf.bpm.application;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.changes.similarity.ActivityContextBuilder;
import br.edu.ufrgs.inf.bpm.changes.similarity.ModelReduction;
import br.edu.ufrgs.inf.bpm.changes.similarity.SimilarityCalculator;
import br.edu.ufrgs.inf.bpm.changes.similarity.SimilarityHandler;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Gateway;
import org.apache.commons.io.FileUtils;
import org.omg.spec.bpmn._20100524.model.*;
//import org.processmining.analysis.epc.similarity.ActivityContextFragment;
import org.processmining.analysis.graphmatching.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class SimilarityApp {

    private static DecimalFormat f_compareFormat = new DecimalFormat("00.00%");

    public static void main(String[] args) {
        SimilarityApp similarityApp = new SimilarityApp();
        similarityApp.configureSystem();

        TDefinitions modelOriginal = getTDefinitions("src/main/others/TestData/3.2 - Conduct Directions Hearing - originalProcess.bpmn");
        //modelOriginal = ModelReduction.makeReduction(modelOriginal);
        modelOriginal = similarityApp.refineProcess(modelOriginal);

        TDefinitions modelGenerated = getTDefinitions("src/main/others/TestData/output/3.2 - Conduct Directions Hearing - originalText - process.bpmn");
        //modelGenerated = ModelReduction.makeReduction(modelGenerated);
        modelGenerated = similarityApp.refineProcess(modelGenerated);

        double similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);
        System.out.println("Similarity (original text): " + f_compareFormat.format(similarity));

        // Test 1 - Nossa abordagem
        modelGenerated = getTDefinitions("src/main/others/TestData/output/modelo - minhaAbordagem - process.bpmn");
        modelGenerated = similarityApp.refineProcess(modelGenerated);
        similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);
        System.out.println("Similaridade pelo modelo (nossa abordagem): " + f_compareFormat.format(similarity));

        // Test 2 - Nossa abordagem
        modelGenerated = getTDefinitions("src/main/others/TestData/output/texto - minhaAbordagem - process.bpmn");
        modelGenerated = similarityApp.refineProcess(modelGenerated);
        similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);
        System.out.println("Similaridade pelo texto (nossa abordadem): " + f_compareFormat.format(similarity));

        // Test 3 - Texto Leopold
        modelGenerated = getTDefinitions("src/main/others/TestData/output/modelo - abordagemLeopold - process.bpmn");
        modelGenerated = similarityApp.refineProcess(modelGenerated);
        similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);
        System.out.println("Similaridade pelo modelo (Leopold): " + f_compareFormat.format(similarity));

        // Test 4 - Texto Leopold
        modelGenerated = getTDefinitions("src/main/others/TestData/output/texto - abordagemLeopold - process.bpmn");
        modelGenerated = similarityApp.refineProcess(modelGenerated);
        similarity = similarityApp.calculateSimilarity(modelOriginal, modelGenerated);
        System.out.println("Similaridade pelo texto (Leopold): " + f_compareFormat.format(similarity));

    }

    private TDefinitions refineProcess(TDefinitions tDefinitions) {
        BpmnWrapper bpmnWrapper = new BpmnWrapper(tDefinitions);
        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            if (tFlowNode.getName() == null || tFlowNode.getName().isEmpty()) {
                if (tFlowNode instanceof TActivity) {
                    tFlowNode.setName("Activity");
                } else if (tFlowNode instanceof TExclusiveGateway) {
                    if (tFlowNode.getOutgoing().size() > 1) {
                        tFlowNode.setName("Xor Split");
                    } else {
                        tFlowNode.setName("Xor Join");
                    }
                } else if (tFlowNode instanceof TInclusiveGateway) {
                    if (tFlowNode.getOutgoing().size() > 1) {
                        tFlowNode.setName("Or Split");
                    } else {
                        tFlowNode.setName("Or Join");
                    }
                } else if (tFlowNode instanceof TParallelGateway) {
                    if (tFlowNode.getOutgoing().size() > 1) {
                        tFlowNode.setName("And Split");
                    } else {
                        tFlowNode.setName("And Join");
                    }
                } else if (tFlowNode instanceof TStartEvent) {
                    tFlowNode.setName("Start event");
                } else if (tFlowNode instanceof TEndEvent) {
                    tFlowNode.setName("End event");
                }
            }
        }

        return tDefinitions;
    }

    private static void print(TDefinitions definitions) {
        BpmnWrapper bpmnWrapper = new BpmnWrapper(definitions);

        for (TFlowNode tFlowNode : bpmnWrapper.getFlowNodeList()) {
            System.out.println(tFlowNode.getName());
            for (TFlowNode tFlowNodeTarget : bpmnWrapper.getFlowNodeTargetList(tFlowNode)) {
                System.out.println("\t-> " + tFlowNodeTarget.getName());
            }
        }
    }

    private static TDefinitions getTDefinitions(String path) {
        TDefinitions definitions = null;
        try {
            String bpmnProcess = FileUtils.readFileToString(new File(path), "UTF-8");
            definitions = JaxbWrapper.convertXMLToObject(bpmnProcess);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return definitions;
    }

    public double calculateSimilarity(TDefinitions modelOriginal, TDefinitions modelGenerated) {
        double similarity = 0.0;
        try {
            //for (int i = 0; i < 100; i++) {
            ActivityContextBuilder builderOriginal = new ActivityContextBuilder();
            //List<ActivityContextFragment> listOriginal = builderOriginal.convertWithContext(modelOriginal);
            Map<TFlowNode, Integer> idMapModelOriginal = builderOriginal.getIdMap();

            ActivityContextBuilder builderGenerated = new ActivityContextBuilder();
            //List<ActivityContextFragment> listGenerated = builderGenerated.convertWithContext(modelGenerated);
            Map<TFlowNode, Integer> idMapModelGenerated = builderGenerated.getIdMap();

            SimilarityCalculator similarityCalculator = new SimilarityCalculator(0, 1, 0, 0.5);
            SimilarityCalculator contextCalculator = new SimilarityCalculator(0, 0, 0, 0);
            similarityCalculator.setContextSimilarityCalculator(contextCalculator);

            //System.out.println("Teste 1");
            //printStructuralMapping(idMapModelOriginal, idMapModelGenerated);

            //double[] simMatrix = similarityCalculator.getSimilarityMatrix(listOriginal, listGenerated);
            //int[] bestMappingArray = similarityCalculator.getBestPossibleMapping(simMatrix, listOriginal, listGenerated);

            //idMapModelGenerated = makeMapping(idMapModelGenerated, bestMappingArray);

            //System.out.println("Teste 2");
            //printStructuralMapping(idMapModelOriginal, idMapModelGenerated);

            SimilarityHandler similarityHandler = new SimilarityHandler();
            SimpleGraph simpleGraphOriginal = similarityHandler.generateSimpleGraph(modelOriginal, idMapModelOriginal);
            SimpleGraph simpleGraphGenerated = similarityHandler.generateSimpleGraph(modelGenerated, idMapModelGenerated);

            similarityHandler.compute(simpleGraphOriginal, simpleGraphGenerated);
            similarity = similarityHandler.getSimilarity();
            //printGraphEditDistanceMapping(similarityHandler);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return similarity;
    }

    public void configureSystem() {
        configureSystem(0.1, 0.2, 0.8, 0.001);
    }

    public void configureSystem(double weightSkippedVertices, double weightskippedEdges, double weightsubstitutedVertices, double notConsideredCutOffSimilarity) {
        SimilarityHandler.setWeightskippedVertices(weightSkippedVertices);
        SimilarityHandler.setWeightskippedEdges(weightskippedEdges);
        SimilarityHandler.setWeightsubstitutedVertices(weightsubstitutedVertices);
        SimilarityHandler.setNotConsideredCutOffSimilarity(notConsideredCutOffSimilarity);
    }

    private Map<TFlowNode, Integer> makeMapping(Map<TFlowNode, Integer> idMapModelGenerated, int[] bestMappingArray) {
        Map<TFlowNode, Integer> newIdMapModelGenerated = new HashMap<>();
        for (int index = 0; index < bestMappingArray.length; index++) {
            int originalModelId = index;
            int generateModelId = bestMappingArray[index];
            Set<TFlowNode> keySet = getKeysByValue(idMapModelGenerated, generateModelId);
            for (TFlowNode tFlowNode : keySet) {
                newIdMapModelGenerated.put(tFlowNode, originalModelId);
            }
        }

        int i = bestMappingArray.length;
        for (Map.Entry<TFlowNode, Integer> entry : idMapModelGenerated.entrySet()) {
            if (!newIdMapModelGenerated.containsKey(entry.getKey())) {
                newIdMapModelGenerated.put(entry.getKey(), i++);
            }
        }
        return newIdMapModelGenerated;
    }

    private void printStructuralMapping(Map<TFlowNode, Integer> idMapModelOriginal, Map<TFlowNode, Integer> idMapModelGenerated) {
        System.out.println("Structural - Mapping:");
        List<String> strings = new ArrayList<>();
        for (Map.Entry<TFlowNode, Integer> entry : idMapModelOriginal.entrySet()) {
            Set<TFlowNode> elementsModelGenerated = getKeysByValue(idMapModelGenerated, entry.getValue());
            String labelOriginal = entry.getKey().getName();
            for (TFlowNode tFlowNode : elementsModelGenerated) {
                strings.add(intToString(entry.getValue(), 2) + ": " + labelOriginal + " -> " + tFlowNode.getName());
            }
        }
        java.util.Collections.sort(strings);
        strings.forEach(System.out::println);
        System.out.println("===============");
    }

    public static String intToString(int num, int digits) {
        StringBuilder output = new StringBuilder(Integer.toString(num));
        while (output.length() < digits) output.insert(0, "0");
        return output.toString();
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    private SimilarityHandler compare(SimilarityHandler similarityHandler, SimpleGraph simpleGraphOriginal, SimpleGraph simpleGraphGenerated) {
        similarityHandler.compute(simpleGraphOriginal, simpleGraphGenerated);
        /*
        System.out.println("Nodes(gen, ori): " + modelGenerated.getNodes().size() + " - " + modelOriginal.getNodes().size());
        System.out.println("Gateways (gen, ori): " + countGateways(modelGenerated.getNodes()) + " - " + countGateways(modelOriginal.getNodes()));
        System.out.println("Edges (gen, ori): " + modelGenerated.getEdges().size() + " - " + modelOriginal.getEdges().size());
        */
        return similarityHandler;
    }

    private void printGraphEditDistanceMapping(SimilarityHandler similarityHandler){
        System.out.println("Graph Edit Distance - Mapping:");
        System.out.println(similarityHandler.getMapping());
        System.out.println("---------------------");
    }

    private int countGateways(List<ProcessNode> nodes) {
        int _result = 0;
        for (ProcessNode n : nodes) {
            if (n instanceof Gateway) {
                _result++;
            }
        }
        return _result;
    }

    /*
     private void configureSystemOld() {
     //HeuristicModelDiff.setUseSyntacticLabelAnalysis(true);
     //HeuristicModelDiff.setUseSemanticLabelAnalysis(false);
     //HeuristicModelDiff.setUseContextAnalysis(true);

     //HeuristicModelDiff.setUseIDAnalysis(false);
     //HeuristicModelDiff.setUseAttributeAnalysis(true);

     try {
     // TODO: A DIFERENÇA ENTRE VECTOR E ActivityContextFragment É QUE O SEGUNDO TAMBÉM FAZ ANÁLISE DE STRUCTURAL
     // SimilarityCalculator similarityCalculator = new SimilarityCalculator(0, 1, 0, 1);
     // SimilarityCalculator contextCalculator = new SimilarityCalculator(0,1,0,1);
     // similarityCalculator.setContextSimilarityCalculator(contextCalculator);

     // Tentativa 1
     /*
     ActivityContextFragment a1 = new ActivityContextFragment("Do activity 1");
     a1.setInType(ActivityContextFragment.NONE);
     a1.setOutType(ActivityContextFragment.NONE);
     a1.addToInputContext("Do activity 2");
     ActivityContextFragment a2 = new ActivityContextFragment("Do activity 2");
     a2.setInType(ActivityContextFragment.NONE);
     a2.setOutType(ActivityContextFragment.NONE);
     a2.addToOutputContext("Do activity 1");
     ActivityContextFragment a3 = new ActivityContextFragment("Do activity 3");
     a2.setInType(ActivityContextFragment.NONE);
     a2.setOutType(ActivityContextFragment.NONE);
     a2.addToOutputContext("Do activity 1");
     List<ActivityContextFragment> list1 = new ArrayList<>();
     list1.add(a1);
     list1.add(a2);
     list1.add(a3);

     ActivityContextFragment b1 = new ActivityContextFragment("Do activity 1");
     b1.setInType(ActivityContextFragment.NONE);
     b1.setOutType(ActivityContextFragment.NONE);
     b1.addToOutputContext("Do activity 2");
     ActivityContextFragment b2 = new ActivityContextFragment("Do activity 1");
     b2.setInType(ActivityContextFragment.NONE);
     b2.setOutType(ActivityContextFragment.NONE);
     b2.addToInputContext("Do activity 1");
     List<ActivityContextFragment> list2 = new ArrayList<>();
     list2.add(b1);
     list2.add(b2);

     double[] simMatrix = similarityCalculator.getSimilarityMatrix(list1, list2);
     int[] sim = similarityCalculator.getBestPossibleMapping(simMatrix, list1, list2);
     System.out.println("Mapeamento 1");
     for (int i = 0; i < sim.length; i++) {
     System.out.println(i + "->" + sim[i]);
     }

     // Tentativa 2
     Vector<String> v1 = new Vector<>();
     v1.add("Do activity 1");
     v1.add("Do activity 2");

     Vector<String> v2 = new Vector<>();
     v2.add("Does activity 2");
     v2.add("Does activity 1");

     sim = similarityCalculator.getBestPossibleMapping(v1, v2);
     System.out.println("Mapeamento 2");
     for (int i = 0; i < sim.length; i++) {
     System.out.println(i + "->" + sim[i]);
     }
     } catch (Exception e) {
     e.printStackTrace();
     }


     //SimilarityHandler.setWeightskippedVertices(0.3);
     //SimilarityHandler.setWeightskippedEdges(0.3);
     //SimilarityHandler.setWeightsubstitutedVertices(0.4);
     //SimilarityHandler.setNotConsideredCutOffSimilarity(0.001);

     // Valores sugeridos pelo artigo
     //SimilarityHandler.setWeightskippedVertices(0.1);
     //SimilarityHandler.setWeightskippedEdges(0.2);
     //SimilarityHandler.setWeightsubstitutedVertices(0.8);
     //SimilarityHandler.setNotConsideredCutOffSimilarity(0.001);

     // Valores do artigo original de GED
     //SimilarityHandler.setWeightskippedVertices(0.1);
     //SimilarityHandler.setWeightskippedEdges(0.3);
     //SimilarityHandler.setWeightsubstitutedVertices(1.0);
     }

     private void calculateSimilarityOld(TDefinitions modelOriginal, TDefinitions modelGenerated) {
     configureSystem();

     try {
     ActivityContextBuilder builderOriginal = new ActivityContextBuilder();
     List<ActivityContextFragment> listOriginal = builderOriginal.convertWithContext(modelOriginal);
     Map<TFlowNode, Integer> idMapModelOriginal = builderOriginal.getIdMap();

     ActivityContextBuilder builderGenerated = new ActivityContextBuilder();
     List<ActivityContextFragment> listGenerated = builderGenerated.convertWithContext(modelGenerated);
     Map<TFlowNode, Integer> idMapModelGenerated = builderGenerated.getIdMap();

     SimilaritySearcher similaritySearcher = new SimilaritySearcher(4, 1, 0.25);

     int i = 0;
     //while(similaritySearcher.hasNext()) {
     //List<Double> values = similaritySearcher.next();

     SimilarityCalculator similarityCalculator = new SimilarityCalculator(0, 1, 0, 0);
     SimilarityCalculator contextCalculator = new SimilarityCalculator(0, 0, 0, 0);
     similarityCalculator.setContextSimilarityCalculator(contextCalculator);

     //SimilarityCalculator similarityCalculator = new SimilarityCalculator(values.get(0), values.get(1), values.get(2), values.get(3));
     //SimilarityCalculator contextCalculator = new SimilarityCalculator(values.get(0), values.get(1), values.get(2), 0);
     //similarityCalculator.setContextSimilarityCalculator(contextCalculator);

     double[] simMatrix = similarityCalculator.getSimilarityMatrix(listOriginal, listGenerated);
     int[] sim = similarityCalculator.getBestPossibleMapping(simMatrix, listOriginal, listGenerated);

     double total = 0;
     for (int a = 0; a < simMatrix.length; a++) {
     if (!Double.isNaN(simMatrix[a])) {
     total = total + (1 - simMatrix[a]);
     }
     }

     double resultado = 1 - (0.4 * (total / simMatrix.length) + 0.3 * ((double) 1 / 7));
     System.out.println(resultado);


     //System.out.println("Mapeamento 1");
     //for (int i = 0; i < sim.length; i++) {
     //    System.out.println(i + "->" + sim[i]);
     //}
     //System.out.println("===============");

     for (int index = 0; index < sim.length; index++) {
     int originalModelId = index;
     int generateModelId = sim[index];

     for (Map.Entry<TFlowNode, Integer> entry : idMapModelGenerated.entrySet()) {
     if (entry.getValue() == generateModelId) {
     entry.setValue(originalModelId);
     }
     }
     }


     //System.out.println("Mapeamento 2");
     //List<String> strings = new ArrayList<>();
     //for (Map.Entry<TFlowNode, Integer> entry : idMapModelOriginal.entrySet()) {
     //    Set<TFlowNode> elementsModelGenerated = getKeysByValue(idMapModelGenerated, entry.getValue());
     //    String labelOriginal = entry.getKey().getName();
     //    for (TFlowNode tFlowNode : elementsModelGenerated) {
     //        strings.add(intToString(entry.getValue(), 2) + ": " + labelOriginal + " -> " + tFlowNode.getName());
     //    }
     //}
     //java.util.Collections.sort(strings);
     //strings.forEach(System.out::println);
     //System.out.println("===============");

     SimilarityHandler similarityHandler = new SimilarityHandler();
     SimpleGraph simpleGraphOriginal = similarityHandler.generateSimpleGraph(modelOriginal, idMapModelOriginal);
     SimpleGraph simpleGraphGenerated = similarityHandler.generateSimpleGraph(modelGenerated, idMapModelGenerated);

     compare(similarityHandler, simpleGraphOriginal, simpleGraphGenerated);

     if (similarityHandler.getSimilarity() > maxValue) {
     maxValue = similarityHandler.getSimilarity();
     System.out.println("Similarity: " + f_compareFormat.format(similarityHandler.getSimilarity()));
     //values.stream().map(result -> String.format("%.2f", result) + " ").forEach(System.out::print);
     System.out.println();
     }

     i++;
     if (Math.floorMod(i, 1) == 0) {
     System.out.println("Análise: " + i);
     }
     //}
     } catch (Exception e) {
     e.printStackTrace();
     }
     }
     */

}
