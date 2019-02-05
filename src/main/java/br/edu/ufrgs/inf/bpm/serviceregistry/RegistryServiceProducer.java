/*
package br.edu.ufrgs.inf.bpm.serviceregistry;

import com.inubit.research.textToProcess.processing.FrameNetWrapper;
import com.inubit.research.textToProcess.processing.WordNetWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RegistryServiceProducer {

    public static void main(String[] args) {
        initializeService();
        SpringApplication.run(RegistryServiceProducer.class, args);
    }

    public static void initializeService() {
        br.edu.ufrgs.inf.bpm.wrapper.WordNetWrapper.changeWordNetDictionayPath();
        WordNetWrapper.init();
        FrameNetWrapper.init();
    }

}
*/