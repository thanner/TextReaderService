/**
 * Created by Apache CXF WadlToJava code generator
**/
package br.edu.ufrgs.inf.bpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/service")
public interface ITextReaderService {

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"application/json", "application/xml" })
    @Path("/generateProcess")
    Response generateProcess(@FormParam("text") String text);

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"application/json", "application/xml" })
    @Path("/generateText")
    Response generateText(@FormParam("text") String text);

}