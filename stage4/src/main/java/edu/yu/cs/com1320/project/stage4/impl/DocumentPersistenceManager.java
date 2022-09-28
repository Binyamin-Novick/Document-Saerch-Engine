package edu.yu.cs.com1320.project.stage4.impl;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Base64;
import java.util.Scanner;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
Gson gson= new Gson();
Type tht;
Type lt;
URI uttt;
    private class ster implements JsonSerializer<DocumentImpl> , JsonDeserializer<DocumentImpl> {

        @Override
        public JsonElement serialize(DocumentImpl src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            tht = src.ht.getClass();
            lt = src.hs.getClass();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", src.text);
            obj.add("u", gson.toJsonTree(src.u));
            obj.add("hs", gson.toJsonTree(src.hs, lt));
            obj.add("ht", gson.toJsonTree(src.ht, tht));
            if (src.bD != null) {
                obj.addProperty("bD", Base64.getEncoder().encodeToString(src.bD));
            }

            return obj;
        }

        @Override
        public DocumentImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject oj = jsonElement.getAsJsonObject();
            JsonElement htj = oj.get("ht");
            if (oj.has("bD")) {
                try {
                            byte[] b = DatatypeConverter.parseBase64Binary((oj.get("bD").getAsString()));
                    return new DocumentImpl(uttt, b);
                } catch (Exception t) {
                }
                return null;
            }
            try {

                return new DocumentImpl(gson.fromJson(oj.get("u"),URI.class),oj.get("text").getAsString(),gson.fromJson(oj.get("ht"),tht));
            } catch (Exception g) {
            }

            return null;
        }
    }
    public DocumentPersistenceManager(){
        base = null;
    }
File base;
    public DocumentPersistenceManager(File baseDir){
        base = baseDir;
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        JsonSerializer<DocumentImpl> serializer = new ster();
        String uriname = uri.toString();
        String fname ;
        if(!(base==null)) {
            fname = base.getAbsolutePath()+"/" + uriname.substring(7);
        }else {
            fname=uriname.substring(7);
        }
       JsonElement file= serializer.serialize((DocumentImpl) val,DocumentImpl.class,null) ;
        File file1= new File(fname + ".json");
        if(!file1.exists()){
            file1.getParentFile().mkdirs();

            file1.createNewFile();

         }else {
            file1.delete();
            file1.createNewFile();
        }
        FileWriter fw = new FileWriter(file1);
        fw.write(file.toString());
        fw.flush();
        fw.close();

    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String uriname = uri.toString();
        uttt=uri;
        JsonDeserializer<DocumentImpl> deserializer= new ster();
        String fname;
        if (!(base==null)) {
            fname = base.getAbsolutePath() +"/" + uriname.substring(7) + ".json";
        }else {
            fname=uriname.substring(7) + ".json";
        }
        Reader fileR = new FileReader(fname);
        BufferedReader br = new BufferedReader(fileR);
        Scanner fileg = new Scanner(new File(fname));
        JsonReader jr = new JsonReader(fileR);
        JsonParser parser = new JsonParser();
       // JsonObject js = jr.peek();
        JsonElement js = parser.parse(jr);
        Document out  =deserializer.deserialize(( js),DocumentImpl.class,null);
        br.close();
        jr.close();
        fileg.close();
        fileR.close();
        return out;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String uriname = uri.toString();
        String fname ;
        if (!(base==null)) {
            fname = base.getPath() +"/"+ uriname.substring(7) + ".json";
        }else {
            fname=uriname.substring(7) + ".json";
        }
        File file1= new File(fname );
        if (file1.exists()){
            boolean t =file1.delete();
            return true;
        }
        return false;
    }
}
