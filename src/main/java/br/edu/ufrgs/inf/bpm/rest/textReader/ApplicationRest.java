/*
package br.edu.ufrgs.inf.bpm.rest.textReader;

import br.edu.ufrgs.inf.bpm.builder.ProcessGenerator;
import br.edu.ufrgs.inf.bpm.rest.textReader.modelText.Text;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/application")
public class ApplicationRest {

    @POST
    @Path("/hasConnected")
    public boolean hasConnected() {
        return true;
    }

    @POST
    @Path("/getProcess")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_XML)
    public String getBpmnXml(String text) throws IOException {
        return ProcessGenerator.generateProcess(text);
    }

    @POST
    @Path("/getMetaText")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Text getMetaTextXml(String text) throws IOException {
        return ProcessGenerator.generateText(text);
    }

}
*/