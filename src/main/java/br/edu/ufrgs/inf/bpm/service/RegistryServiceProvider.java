package br.edu.ufrgs.inf.bpm.service;


import com.inubit.research.textToProcess.processing.FrameNetWrapper;
import com.inubit.research.textToProcess.processing.WordNetWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RegistryServiceProvider {

    public static void main(String[] args) {
        initializeService();
        SpringApplication.run(RegistryServiceProvider.class, args);
    }

    public static void initializeService() {
        br.edu.ufrgs.inf.bpm.wrapper.WordNetWrapper.changeWordNetDictionayPath();
        WordNetWrapper.init();
        FrameNetWrapper.init();
    }

}
