/*
package br.edu.ufrgs.inf.bpm.serviceregistry;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.rest.textReader.modelText.Text;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Service {

    @RequestMapping(value = "/hasConnected", method = RequestMethod.GET)
    public boolean hasConnected() {
        return true;
    }

    @RequestMapping(value = "/getProcess", method = RequestMethod.POST)
    public String getBpmnXml(@RequestBody String text) throws IOException {
        return ProcessGenerator.generateProcess(text);
    }

    @RequestMapping(value = "/getMetaText", method = RequestMethod.POST)
    public Text getMetaTextXml(@RequestBody String text) throws IOException {
        return ProcessGenerator.generateText(text);
    }

}
*/