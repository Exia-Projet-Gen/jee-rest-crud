/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exia.service;

import com.exia.domain.JAXFile;
import java.io.StringReader;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author hyaci
 */
@Path("decodedFile")
@RequestScoped
public class DecodedFileResource {

    @EJB(lookup = "java:global/data-business-ejb/FileBean")
    private FileBeanRemote decodedFileService;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendFile(String content) {
        StringReader reader = new StringReader(content);
        String decodedText;
        String keyValue;
        String fileName;
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject fileToDecode = jreader.readObject();
            decodedText = fileToDecode.getString("decodedText");
            keyValue = fileToDecode.getString("keyValue");
            fileName = fileToDecode.getString("fileName");
        }
        
        Boolean isValid = decodedFileService.sendDecodedText(decodedText, keyValue, fileName);
                
        Response resp;
        if (isValid) {
            resp = Response.accepted().build();
        } else {
            resp = Response.status(400).entity("A problem occured while deleting the word in database.").build();
        }
        return resp;
    } 
    
    @Path("save")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveFile(String content) {
        StringReader reader = new StringReader(content);
        String decodedText;
        String keyValue;
        String fileName;
        Double matchPercent;
        String mailAddress;
        try (JsonReader jreader = Json.createReader(reader)) {
            JsonObject fileToDecode = jreader.readObject();
            System.out.println(fileToDecode);
            decodedText = fileToDecode.getString("decodedText");
            keyValue = fileToDecode.getString("key");
            fileName = fileToDecode.getString("fileName");           
            matchPercent = fileToDecode.getJsonNumber("matchPercent").doubleValue();
            mailAddress = fileToDecode.getString("mailAddress");
        }
        
        Boolean isValid = decodedFileService.saveValidMessage(decodedText, keyValue, fileName, matchPercent, mailAddress);
                       
        Response resp;
        if (isValid) {
            resp = Response.accepted().build();
        } else {
            resp = Response.status(400).entity("A problem occured while deleting the word in database.").build();
        }
        return resp;
    } 

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<JAXFile> words = decodedFileService.getDecodedFiles();
        GenericEntity<List<JAXFile>> genericList = new GenericEntity<List<JAXFile>>(words){};
        
        Response resp = Response.ok(genericList).build();
        return resp;
    } 
}
