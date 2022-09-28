package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<C> implements Stack<C> {
    stackitem n;
    @Override
    public void push(C element) {
        if (n==null) {
            addfirst(element);
        }else{
            addNotfirst(element);
        }

    }

    @Override
    public C pop() {
        if(n==null) {
            return null;
        }else{
            return popit();
        }
    }

    @Override
    public C peek() {
        if(n==null) {
            return null;
        }else{
            return n.data;
        }
    }

    @Override
    public int size() {
        if(n==null) {
            return 0;
        }else{
            return n.number();
        }
    }

    private class stackitem{
        C data;
        stackitem next;
        private int number(){
            if(next==null){
                return 1;
            }else{
                return next.number()+1;
            }
        }
    }
    private void addfirst(C in){
        n= new stackitem();
        n.data=in;
    }
    private void addNotfirst(C in){
        stackitem oldt=n;
        n= new stackitem();
        n.data=in;
        n.next=oldt;
    }
    private C popit(){
        stackitem oldt=n;
        n=oldt.next;
        return oldt.data;
    }

}
