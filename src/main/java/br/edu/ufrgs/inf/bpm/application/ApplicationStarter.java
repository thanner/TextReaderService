package br.edu.ufrgs.inf.bpm.application;

import com.inubit.research.textToProcess.processing.FrameNetWrapper;
import com.inubit.research.textToProcess.processing.WordNetWrapper;

public class ApplicationStarter {

    public static void startApplication() {
        br.edu.ufrgs.inf.bpm.wrapper.WordNetWrapper.changeWordNetDictionayPath();
        WordNetWrapper.init();
        FrameNetWrapper.init();
    }

}
