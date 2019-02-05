//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package br.edu.ufrgs.inf.bpm.changes.similarity;

import org.processmining.analysis.epc.similarity.ActivityContextFragment;
import org.processmining.analysis.epc.similarity.Checker;
import org.processmining.framework.ui.Message;
import org.processmining.framework.ui.UISettings;
import org.processmining.framework.util.ProMLpSolve;
import org.processmining.framework.util.ProMLpSolveException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class SimilarityCalculator {
    private Checker checker = null;
    private double threshold;
    private double semanticWeight = 0.0D;
    private double syntaxWeight = 1.0D;
    private double structureWeight = 0.0D;
    private SimilarityCalculator contextCalculator;

    public SimilarityCalculator(double threshold, double syntaxWeight, double semanticWeight, double structureWeight) {
        this.threshold = threshold;
        this.semanticWeight = semanticWeight;
        this.syntaxWeight = syntaxWeight;
        this.structureWeight = structureWeight;
        if (this.checker == null) {
            Message.add("Initializing semantic checker... (might take some time, but happens only once in a ProM session)");
            this.checker = new Checker(UISettings.getProMDirectoryPath() + "lib" + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") + "similarity" + System.getProperty("file.separator"));
            Message.add("Semantic checker ready for use.");
        }

    }

    public void setContextSimilarityCalculator(SimilarityCalculator calculator) {
        this.contextCalculator = calculator;
    }

    private double getSyntacticEquivalence(String f1, String f2) {
        return this.syntaxWeight <= 0.0D ? 0.0D : this.checker.syntacticEquivalenceScore(f1, f2);
    }

    private double getSemanticEquivalence(String f1, String f2) {
        return this.semanticWeight <= 0.0D ? 0.0D : this.checker.semanticEquivalenceScore(f1, f2);
    }

    private double getStructuralEquivalence(ActivityContextFragment f1, ActivityContextFragment f2) throws Exception {
        return this.structureWeight <= 0.0D ? 0.0D : (this.getContextSimilarity(f1.getInputContext(), f2.getInputContext()) + this.getContextSimilarity(f1.getOutputContext(), f2.getOutputContext())) / 2.0D;
    }

    private double getContextSimilarity(Vector<String> input1, Vector<String> input2) throws Exception {
        if(input1.size() == 0 && input2.size() == 0){
            return 1;
        } else {
            if (this.contextCalculator == null) {
                throw new Exception() {
                };
            } else {
                int[] mapping = this.contextCalculator.getBestPossibleMapping(input1, input2);
                int number = 0;

                for (int i = 0; i < mapping.length; ++i) {
                    if (mapping[i] > -1) {
                        ++number;
                    }
                }

                return (double) number / (Math.sqrt((double) input1.size()) * Math.sqrt((double) input2.size()));
            }
        }
    }

    private double getSimilarity(Object f1, Object f2) throws Exception {
        return f1 instanceof ActivityContextFragment && f2 instanceof ActivityContextFragment ? this.getSimilarity((ActivityContextFragment) f1, (ActivityContextFragment) f2) : (this.syntaxWeight * this.getSyntacticEquivalence(f1.toString(), f2.toString()) + this.semanticWeight * this.getSemanticEquivalence(f1.toString(), f2.toString())) / (this.syntaxWeight + this.semanticWeight);
    }

    public double getSimilarity(ActivityContextFragment f1, ActivityContextFragment f2) throws Exception {
        double syntax = this.getSyntacticEquivalence(f1.getActivityName(), f2.getActivityName());
        double semantic = this.getSemanticEquivalence(f1.getActivityName(), f2.getActivityName());
        double structural = this.getStructuralEquivalence(f1, f2);
        double overall = (this.syntaxWeight * syntax + this.semanticWeight * semantic + this.structureWeight * structural) / (this.syntaxWeight + this.semanticWeight + this.structureWeight);
        return overall;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return this.threshold;
    }

    public double[] getSimilarityMatrix(List frags1, List frags2) throws Exception {
        double[] similarities = new double[frags1.size() * frags2.size()];
        int index = 0;
        Iterator it1 = frags1.iterator();

        while (it1.hasNext()) {
            Object frag1 = it1.next();

            Object frag2;
            for (Iterator it2 = frags2.iterator(); it2.hasNext(); similarities[index++] = this.getSimilarity(frag1, frag2)) {
                frag2 = it2.next();
            }
        }

        return similarities;
    }

    public int[] getBestPossibleMapping(Vector<String> frags1, Vector<String> frags2) throws Exception {
        double[] similarities = this.getSimilarityMatrix(frags1, frags2);
        return this.getBestPossibleMapping(similarities, frags1, frags2);
    }

    public int[] getBestPossibleMapping(double[] similarities, List from, List to) {
        int[] result = new int[from.size()];
        Arrays.fill(result, -1);
        ProMLpSolve solver = null;

        try {
            solver = new ProMLpSolve(from.size() + to.size(), similarities.length);
        } catch (ProMLpSolveException var10) {
            return result;
        }

        try {
            solver.setMaximizing();

            for (int i = 0; i < similarities.length; ++i) {
                solver.setColName(i + 1, "y_" + i);
                solver.setBinary(i + 1, true);
                solver.setLowbo(i + 1, 0.0D);
                solver.setUpbo(i + 1, (double) (similarities[i] > this.getThreshold() ? 1 : 0));
            }

            solver.setAddRowmode(true);
            solver.addTarget(similarities);
            int[] constraint = new int[similarities.length];

            int j;
            int i;
            for (j = 0; j < from.size(); ++j) {
                Arrays.fill(constraint, 0);

                for (i = 0; i < to.size(); ++i) {
                    constraint[j * to.size() + i] = 1;
                }

                solver.addConstraint(constraint, 1, 1.0D);
            }

            for (j = 0; j < to.size(); ++j) {
                Arrays.fill(constraint, 0);

                for (i = 0; i < from.size(); ++i) {
                    constraint[i * to.size() + j] = 1;
                }

                solver.addConstraint(constraint, 1, 1.0D);
            }

            solver.setAddRowmode(false);
            solver.solve();
            double[] solution = solver.getColValuesSolution();

            for (i = 0; i < from.size(); ++i) {
                for (j = 0; j < to.size(); ++j) {
                    if (solution[i * to.size() + j] == 1.0D) {
                        result[i] = j;
                    }
                }
            }

            solver.deleteLp();
        } catch (ProMLpSolveException var11) {
            solver.deleteLp();
        }

        solver = null;
        return result;
    }
}
