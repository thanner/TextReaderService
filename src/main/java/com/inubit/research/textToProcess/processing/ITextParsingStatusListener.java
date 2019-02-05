/**
 * copyright
 * Inubit AG
 * Schoeneberger Ufer 89
 * 10785 Berlin
 * Germany
 */
package com.inubit.research.textToProcess.processing;

/**
 * @author ff
 */
public interface ITextParsingStatusListener {

    void setNumberOfSentences(int number);

    void sentenceParsed(int number);

}
