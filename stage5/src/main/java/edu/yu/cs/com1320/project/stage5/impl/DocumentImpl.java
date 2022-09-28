package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DocumentImpl implements Document {

    HashMap<String,Integer> ht =new HashMap<String,Integer>();
    Set hs = new HashSet<String>();
    long time=0;
    URI u;
    String text;
    byte[] bD;
    public DocumentImpl(URI uri, String txt){
        u =uri;
        text= txt;
        if((u==null)  ){
            throw new IllegalArgumentException();
        }
        if((txt==null)||(txt.length()<1)){
            throw new IllegalArgumentException();
            // fillhash(txt);
        }
        fillhash(txt);
    }
    //ssss
    public DocumentImpl(URI uri, byte[] binaryData){
        u = uri;
        bD =binaryData;
        if((u==null)  ){
            throw new IllegalArgumentException();
        }
        if(bD==null){
            throw new IllegalArgumentException();
        }
        if(bD.length==0){
            throw new IllegalArgumentException();
        }
        boolean gg=false;

        // fillhash(getDocumentBinaryData().toString());


    }
    private void fillhash(String s){
        HashSet< String> redytofill= new HashSet<String>();
        String[] stsp =splitword(filter(s));
        for(int i =0;i<stsp.length;i++){

            if(ht.containsKey(stsp[i])){
                ht.put(stsp[i],ht.get(stsp[i])+1);
            }else{
                ht.put(stsp[i],1);
            }}
        hs.addAll(ht.keySet());
    }
    private String filter(String s){

        String lf= s.toLowerCase();
        int w;
        char t;
        lf= lf.replaceAll("\\s", " ");
        for(int i=0;i<lf.length();i++){
            if(((lf.charAt(i)>122)||((lf.charAt(i)<97)&&(lf.charAt(i)>57)||(lf.charAt(i)<48)))&&(lf.charAt(i)!=' ')){
                w=lf.length();
                t=lf.charAt(i);
                lf = lf.replace(""+lf.charAt(i),"");

            }
        }
        return lf.stripLeading().stripTrailing();
    }
    /**
     * @return content of text document
     */
    public String getDocumentTxt(){
        return text;
    }
    private String[] splitword(String s){
        return s.split(" ");
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        return  bD;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return u;
    }
    @Override
    public int hashCode() {
        int result = u.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result +  + Arrays.hashCode(bD);
        return result;
    }

    public boolean equals(DocumentImpl d){
        if(this.hashCode()== d.hashCode()) {
            return true;
        }else{
            return false;
        }
    }




    @Override
    public int wordCount(String word) {

        if(ht.get(word.toLowerCase())==null) {
            return 0;
        }
        return ht.get(word.toLowerCase());
    }

    @Override
    public Set<String> getWords() {
        return hs;
    }
    @Override
    public long getLastUseTime() {
        return time;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
            time=timeInNanoseconds;
    }

    @Override
    public int compareTo(Document o) {
        if(o.getLastUseTime()>time){
            return -1;
        }
        if(o.getLastUseTime()<time){
            return 1;
        }
        return 0;
    }
}
