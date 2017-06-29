package com.exia.service;

import com.exia.domain.JAXWord;
import java.io.StringReader;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author hyaci
 */
@Path("dictionary")
@RequestScoped
public class DictionaryResource {
   
    @EJB(lookup = "java:global/data-business-ejb/DictionaryBean")
    private DictionaryBeanRemote dictionaryService;
    
    @Path("search/{word}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("word") String wordName) {
        List<JAXWord> matchingWords = dictionaryService.searchWord(wordName);
                
        if (matchingWords != null) {
            GenericEntity<List<JAXWord>> genericList = new GenericEntity<List<JAXWord>>(matchingWords){};
            Response resp = Response.ok(genericList).build();
            return resp;
        }
        Response failedResp = Response.serverError().build();
        return failedResp;
    }   
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<JAXWord> words = dictionaryService.getWords();
        GenericEntity<List<JAXWord>> genericList = new GenericEntity<List<JAXWord>>(words){};
        
        Response resp = Response.ok(genericList).build();
        return resp;
    } 
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(String content) {
                
        StringReader reader = new StringReader(content);
        String wordName;
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject wordInfo = jreader.readObject();
            wordName = wordInfo.getString("value");
        }
        
        JAXWord addedWord = dictionaryService.addWord(wordName);
        
        Response resp = null;
        
        if (addedWord != null) {
            resp = Response.ok(addedWord).build();
        } else {
            resp = Response.status(400).entity("A problem occured while adding the word in database.")
                    .build();
        }
        return resp;
    }   
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(String content) {
        
        StringReader reader = new StringReader(content);
        Long id;
        String wordValue;
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject wordInfo = jreader.readObject();
            id = wordInfo.getJsonNumber("id").longValue();
            wordValue = wordInfo.getString("value");
        }
        
        Boolean isValid = dictionaryService.updateWord(id, wordValue);
        
        Response resp = null;
        if (isValid) {
            resp = Response.accepted()
                    .build();
        } else {
            resp = Response.status(400).entity("A problem occured while updating the word in database.").build();
        }
        return resp;
    }   
    
    @Path("{wordId}")
    @DELETE
    public Response delete(@PathParam("wordId") Long wordId) {
        Boolean isValid = dictionaryService.deleteWord(wordId);
             
        Response resp = null;
        if (isValid) {
            resp = Response.accepted().build();
        } else {
            resp = Response.status(400)
                    .entity("A problem occured while deleting the word in database.")
                    .build();
        }
        return resp;
    }  
}
