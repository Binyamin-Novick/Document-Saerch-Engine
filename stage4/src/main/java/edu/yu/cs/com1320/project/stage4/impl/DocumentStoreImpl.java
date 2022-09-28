package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.TXT;

public class DocumentStoreImpl implements DocumentStore {
    public class uriholder implements Comparable<uriholder> {
        URI uri ;

        public uriholder(URI uri ){
           this.uri=uri;

        }
        public URI getUri(){
            return uri;
        }

        @Override
        public int compareTo(uriholder o) {

            return ht.get(uri).compareTo(ht.get(o.getUri()));
        }
    }
    BTree<URI,uriholder> ut=new BTreeImpl<URI, uriholder>();
    int maxspace = Integer.MAX_VALUE;
    int curentsapce=0;
    int maxdocs=Integer.MAX_VALUE;
    int numberofdoc=0;
    MinHeap<uriholder> heap = new MinHeapImpl<uriholder>();
    Stack<Undoable> undoableStack= new StackImpl<Undoable>();
    Trie<URI> Try =new TrieImpl<URI>();
    BTree<URI,Document> ht=new BTreeImpl<URI, Document>();
    PersistenceManager<URI,Document>pm = new DocumentPersistenceManager(null);
    public DocumentStoreImpl(){
        ht.setPersistenceManager(pm);
    }
    public Document get(URI k) {
        Document out= ht.get(k);
        settime(ut.get(k));
        Spacecheck();
        return out;
    }///need to do



    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {

        Document doc =composedocument(input,uri,format);
        if(format==TXT){
            if(doc.getDocumentTxt().getBytes().length>maxspace){
                throw new IllegalArgumentException();
            }
        }else {
            if(doc.getDocumentBinaryData().length>maxspace){
                throw new IllegalArgumentException();
            }
        }
        Document dd = ht.put(doc.getKey(),doc);
        undoputincomadstack(doc,dd);
        putintry(doc);
        if(dd== null){
            return 0;
        }
        return  dd.hashCode();
    }
    private boolean hphas(uriholder d){
        boolean out = false;
        Stack<uriholder> temp = new StackImpl<>();
        while (true){
            try {
                temp.push(heap.remove());
                if(temp.peek()==d){
                    out = true;
                    break;
                }
            }catch (NoSuchElementException e){
                break;
            }
        }
        while (temp.size()>0){
            heap.insert(temp.pop());
        }
        return out;
    }
    private boolean settime(uriholder d){

        ht.get(d.getUri()).setLastUseTime(java.lang.System.nanoTime());
        if(hphas(d)==false){
            AddToHeapAndSace(d);
        }
        heap.reHeapify(d);
        return true;
    }
    private boolean purg(Document d)  {
        try {


            ht.moveToDisk(d.getKey());
        }catch (Exception e){}



        return true;
    }


    private boolean Spacecheck(){
        Document d;
        while ((curentsapce>maxspace)||(numberofdoc>maxdocs)){
            numberofdoc=numberofdoc-1;
            d=ht.get(heap.remove().getUri());
            purg(d);
            if(d.getDocumentTxt()==null){
                curentsapce=curentsapce-d.getDocumentBinaryData().length;
            }else {
                curentsapce=curentsapce-d.getDocumentTxt().getBytes().length;
            }
        }
        return true;
    }
    private boolean AddToHeapAndSace(uriholder d){
        ht.get(d.getUri()).setLastUseTime(java.lang.System.nanoTime());

        heap.insert(d);
        heap.reHeapify(d);
        if( ht.get(d.getUri()).getDocumentTxt()==null){
            curentsapce=curentsapce+ ht.get(d.getUri()).getDocumentBinaryData().length;
        }else {
            curentsapce=curentsapce+ ht.get(d.getUri()).getDocumentTxt().getBytes().length;
        }
        numberofdoc=numberofdoc+1;

        Spacecheck();
        return true;
    }
    private Boolean putintry(Document d,URI u){
        if(!(d==null)){
            if(!(d.getDocumentTxt()==null)){
                if(d.getDocumentTxt().getBytes().length>maxspace){
                    throw new IllegalArgumentException();
                }
            }else {
                if(d.getDocumentBinaryData().length>maxspace){
                    throw new IllegalArgumentException();
                }
            }}

        ht.put(u,d);
        if(d==null){
            return true;
        }

        Set<String> hs = d.getWords();
        for(String s: hs){
            Try.put(s,u);
        }
        uriholder uh = new uriholder(u);
        ut.put(u,uh);
        //AddToHeapAndSace(uh);
        return true;
    }
    private Boolean putintry(Document d){
        if(!(d==null)){
            if(!(d.getDocumentTxt()==null)){
                if(d.getDocumentTxt().getBytes().length>maxspace){
                    throw new IllegalArgumentException();
                }
            }else {
                if(d.getDocumentBinaryData().length>maxspace){
                    throw new IllegalArgumentException();
                }
            }}

        ht.put(d.getKey(),d);
        if(d==null){
            return true;
        }

        Set<String> hs = d.getWords();
        for(String s: hs){
            Try.put(s,d.getKey());
        }
        uriholder uh = new uriholder(d.getKey());
        ut.put(d.getKey(),uh);
        AddToHeapAndSace(uh);
        return true;
    }
    private  void undoputincomadstack(Document doc,Document dd){
        Document d = dd;
        Function<URI,Boolean> lb;
        if(d==null) {
            lb = (ui) -> (((((deletedoc(ht.get(ui)) && true) == (putintry(d, ui))) == true)) == tomem(ui, d, true));
        }else {
        if (hphas(ut.get(d.getKey()))){
             lb=(ui)->(((((deletedoc(ht.get(ui))&&true)==(putintry(d,ui)))==true))==tomem(ui,d,false));
        }else {
             lb=(ui)->(((((deletedoc(ht.get(ui))&&true)==(putintry(d,ui)))==true))==tomem(ui,d,true));
        }}


       // Function<URI,Boolean> lb=(ui)->(((((deletedoc(ht.get(ui))&&true)==(putintry(d,ui)))==true))==tomem(ui,d,false));
        undoableStack.push(new GenericCommand<URI>(doc.getKey(),lb));
    }

    private Document composedocument(InputStream input,URI uri,DocumentFormat format)throws IOException{
        if((uri==null)||(format==null)){
            throw new IllegalArgumentException();
        }
        byte[] in = input.readAllBytes();
        String sin;
        Document t;
        if(format == TXT){
            sin= new String(in);
            t = new DocumentImpl(uri,sin);
        }else{
            t = new DocumentImpl(uri,in);
        }
        return t;
    }

    @Override
    public Document getDocument(URI uri) {
        Document tt =ht.get(uri);
        if(!(tt==null)) {
            settime(ut.get(uri));
        }
        return ht.get(uri);
    }
    private boolean removedocfromheap(Document d){
        List<Document> documentList=new LinkedList<Document>();
        Document dd;
        while (hphas(ut.get(d.getKey()))){
            dd=ht.get(heap.remove().getUri());


            documentList.add(dd);
            if(documentList.contains(d)){
                documentList.remove(d);
                for (Document t :documentList){
                    heap.insert(ut.get(t.getKey()));
                }
                break;
            }
        }
        return true;
    }
    private boolean subtractspace(Document d){
        numberofdoc=numberofdoc-1;
        if(d.getDocumentTxt()!=null){
            curentsapce=curentsapce-d.getDocumentTxt().getBytes().length;
        }else {
            curentsapce=curentsapce-d.getDocumentBinaryData().length;
        }
        return true;
    }
    private boolean deletedoc(Document d){
        Set<String>words =  d.getWords();
        for(String w: words){
            Try.delete(w,d.getKey());
        }

        removedocfromheap(d);
        subtractspace(d);
        ht.put(d.getKey(),null);
        return true;
    }
    @Override
    public boolean deleteDocument(URI uri) {
        boolean inmemstat= true;
        Function<URI,Boolean> lb;
        Document doc = ht.get(uri);
        if(doc==null){
            return false;
        }
        Set<String>words =  doc.getWords();
        if (hphas(ut.get(uri))){
            lb=(ur)->(putintry(doc,ur)==(true==(tomem(ur,doc,false))));
        }else {

             lb = (ur) -> (putintry(doc,ur) == (true == (tomem(ur, doc, true))));
        }
        undoableStack.push(new GenericCommand<URI>(uri,lb));/// this is probleme make privat vein that does not do this and putit into the putundo
        deletedoc(doc);
        return true;
    }
    private boolean tomem(URI u,Document d,boolean m){
        ht.put(u,d);
        if(m==true) {
            try {


                ht.moveToDisk(u);
            } catch (Exception b) {

            }
        }else {
            settime(ut.get(u));
            Spacecheck();
        }
        return true;
    }

    @Override
    public void undo()  {

        if(undoableStack.size()==0){
            throw new IllegalStateException();
        }
        Undoable n =undoableStack.pop();
        n.undo();

    }

    @Override
    public void undo(URI uri) {
        Stack<Undoable> temp = new StackImpl<Undoable>();
        Undoable n;
        boolean tt=false;
        while (undoableStack.size()>0){
            temp.push(undoableStack.pop());
            n=temp.peek();
            if(n instanceof CommandSet){
                if((((CommandSet<URI>) n).containsTarget(uri))){
                    ((CommandSet<URI>) temp.peek()).undo(uri);
                    if(((CommandSet<?>) temp.peek()).isEmpty()){
                        temp.pop();
                    }
                    tt=true;

                    break;
                }
            }else{
                if(((GenericCommand<URI>)n).getTarget()==uri){
                    temp.pop().undo();
                    tt=true;
                    break;
                }
            }

        }
        while (temp.size()>0){
            undoableStack.push(temp.pop());

        }
        if(tt==false){
            throw new IllegalStateException();
        }

    }


    @Override
    public List<Document> search(String keyword) {
        Comparator<URI> compe= new Comparator<URI>() {
            @Override
            public int compare(URI o1, URI o2) {
                if(ht.get(o1).wordCount(keyword)>ht.get(o2).wordCount(keyword)){
                    return 1;
                }else {
                    return -1;
                }
            }
        };
        List<Document>dout= new LinkedList<Document>();
        List<URI> out =new LinkedList<URI>();
        out.addAll(Try.getAllSorted(keyword,compe));
        for (URI d :out){
            settime(ut.get(d));

            dout.add(ht.get(d));
        }
        Spacecheck();
        return dout;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<URI> compe= new Comparator<URI>() {
            @Override
        public int compare(URI o1, URI o2) {
            if(ht.get(o1).wordCount(keywordPrefix)>ht.get(o2).wordCount(keywordPrefix)){
                return 1;
            }else {
                return -1;
            }
        }
    };
        List<URI> out =new LinkedList<URI>();
        List<Document> dout =new LinkedList<Document>();
        out.addAll(Try.getAllWithPrefixSorted(keywordPrefix,compe));
        for (URI d :out){
            settime(ut.get(d));
            dout.add(ht.get(d));
        }
        return dout;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Function<URI,Boolean> lb;
        List<Document>todell= search(keyword);
        CommandSet<URI> un = new  CommandSet<URI>();
        GenericCommand<URI> tt;
        for (Document d : todell){
            Set<String>word= d.getWords();
            if (hphas(ut.get(d.getKey()))){
                lb=(ur)->(putintry(d,ur)==(true==(tomem(ur,d,false))));
            }else {

                lb = (ur) -> (putintry(d,ur) == (true == (tomem(ur, d, true))));
            }
            tt=new GenericCommand<URI>(d.getKey(),lb);
            un.addCommand(tt);
            deletedoc(d);

        }
        undoableStack.push(un);
        Set<URI>out = new HashSet<>();
        for (Document d: todell){
            out.add(d.getKey());
        }
        return out;

    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Function<URI,Boolean> lb;
        List<Document>todell= searchByPrefix(keywordPrefix);
        CommandSet<URI> un = new  CommandSet<URI>();
        for (Document d : todell){
            Set<String>word= d.getWords();
            if (hphas(ut.get(d.getKey()))){
                lb=(ur)->(putintry(d,ur)==(true==(tomem(ur,d,false))));
            }else {

                lb = (ur) -> (putintry(d,ur) == (true == (tomem(ur, d, true))));
            }
            un.addCommand(new GenericCommand<URI>(d.getKey(),lb));
            deletedoc(d);
        }


        undoableStack.push(un);
        Set<URI>out = new HashSet<>();
        for (Document d: todell){
            out.add(d.getKey());
        }
        return out;
    }
    @Override
    public void setMaxDocumentCount(int limit) {
        maxdocs=limit;
        Spacecheck();
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        maxspace=limit;
        Spacecheck();
    }
}


