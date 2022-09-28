package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator comparator){
        Node n =get(root,prefix,0);
        List<Value> ll =n.Getv();
        Collections.sort(ll,comparator);
        return ll;  //sortedlist(ll);//figure out
    }

    private List<Value> getvalsof(List<Node> nl){
        List<Value>Vl= new LinkedList<Value>();
        for(Node n:nl){
            Vl.add((Value) n.val);
        }
        return Vl;
    }
    private List<Value> getallnudesunder(Node n){

        return n.Getv();
    }
    private String lowercase(String in){
        String lf= in.toLowerCase();
        for(int i=0;i<lf.length();i++){
            if((lf.charAt(i)>122)||((lf.charAt(i)<97)&&(lf.charAt(i)>57)||(lf.charAt(i)<48))){
                lf.replaceAll(""+lf.charAt(i),"");
            }
        }
        return lf;
    }
    private static final int alphabetSize = 36; // extended ASCII
    private Node root= new Node<Value>(); // root of trie

    public static class Node<Value>
    {
        protected List<Value> val=new LinkedList<Value>();
        protected Node[] links = new Node[36];
        private  List <Value> Getv(){
            List<Value> vv = new LinkedList<Value>();
            vv.addAll(val);
            for(Node n: links){
                if(n!=null) {
                    vv.addAll(n.Getv());
                }
            }
            return vv;

        }
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the trie and {@code null} if not
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator)
    {
        Node x = this.get(this.root, key, 0);
        if (x == null)
        {
            return null;
        }

        List<Value> out =new LinkedList<Value>();
        out.addAll(x.val);
        Collections.sort(out,comparator);

        return out;
    }

    /**
     * A char in java has an int value.
     * see http://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#getNumericValue-char-
     * see http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
     */
    private Node get(Node x, String k, int d)
    {
        String key= lowercase(k);
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return new Node();
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        int c = converttosmalerform(key.charAt(d));
        return this.get(x.links[c], key, d + 1);
    }

    public void put(String k, Value val)
    {
        String key=lowercase(k);
        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }
    /**
     *
     * @param x
     * @param key
     * @param val
     * @param d
     * @return
     */
    private Node put(Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            x.val.add(val) ;
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        int nn=converttosmalerform( key.charAt(d));
        x.links[nn] = this.put(x.links[nn], key, val, d + 1);
        return x;
    }
    /*
     *only use if lredy lowercased function
     */
    private int converttosmalerform(char in){
        int d;
        if(in<57){
            d=(int)in -47;
        }else{
            d=(int)in-87;

        }
        return d;
    }
    public Set<Value> deleteAllWithPrefix(String prefix){
        Node n =get(root,prefix,0);
        List<Value> ll =n.Getv();
        n.links=new Node[37];
        n.val = new LinkedList<Value>();


        Set<Value> out = new HashSet<>();
        out.addAll(ll);
        return out;
    }
    public Value delete(String key, Value val){
        Node n = get(root,key,0);
        if(n== null){
            return null;
        }
        if(n.val.contains(val)){
            n.val.remove(val);
            return val;
        }else{
            return null;
        }

    }
    public Set<Value> deleteAll(String k)
    {
        String key = lowercase(k);
        Node nodetodel=get(root,key,0);
        Set<Value> vals=new HashSet<>();
        vals.addAll(nodetodel.val);
        nodetodel.val.clear();
        return vals;
    }
/*
    private Node deleteAll(Node x, String k, int d)// must change return
    {
        String key = lowercase(k);
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            x.val = new LinkedList();
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.val != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c <TooSimpleTrie.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }*/
}
