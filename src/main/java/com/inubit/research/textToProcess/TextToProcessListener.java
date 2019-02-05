/**
 * copyright
 * Inubit AG
 * Schoeneberger Ufer 89
 * 10785 Berlin
 * Germany
 */
package com.inubit.research.textToProcess;

import com.inubit.research.textToProcess.textModel.TextModel;
import com.inubit.research.textToProcess.worldModel.SpecifiedElement;
import edu.stanford.nlp.trees.Tree;
import net.frapu.code.visualization.bpmn.BPMNModel;
import net.frapu.code.visualization.bpmn.FlowObject;

/**
 * @author ff
 */
public interface TextToProcessListener {

    void textModelChanged(TextModel model);

    /**
     * @param model
     */
    void modelGenerated(BPMNModel model);

    /**
     * @param tree
     */
    void displayTree(Tree tree);

    /**
     * @param string
     */
    void displayDependencies(String string);

    /**
     * @param _element
     * @param _corr
     */
    void textElementClicked(SpecifiedElement _element, FlowObject _corr);

}
