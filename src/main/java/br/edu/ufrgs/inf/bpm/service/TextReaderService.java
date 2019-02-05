package br.edu.ufrgs.inf.bpm.service;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.metatext.TMetaText;
import br.edu.ufrgs.inf.bpm.wrapper.JsonWrapper;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.io.IOException;

@Api("/service")
@Service
public class TextReaderService implements ITextReaderService {

    @Override
    public Response generateProcess(String text) {
        try {
            String process = ProcessGenerator.generateProcess(text);
            return Response.ok().entity(process).build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Override
    public Response generateText(String text) {
        try {
            TMetaText tMetaText = ProcessGenerator.generateText(text);
            return Response.ok().entity(JsonWrapper.getJson(tMetaText)).build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}