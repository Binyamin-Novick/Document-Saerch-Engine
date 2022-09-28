package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.MinHeap;

    public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{
        public MinHeapImpl(){
            super.elements = (E[]) new Comparable [1];

        }

        @Override
        public void reHeapify(E element) {
            super.downHeap(getArrayIndex(element));
            super.upHeap(getArrayIndex(element));
        }

        @Override
        protected int getArrayIndex(E element) {
            for(int i =0; i<super.elements.length;i++){
                if(!(super.elements[i] == null))
                    if(super.elements[i].equals(element)){
                        return i;
                    }
            }
            return -1;
        }

        @Override
        protected void doubleArraySize() {
            E[] temp = (E[]) new Comparable[super.elements.length];
            for (int i =0;i<super.elements.length;i++){
                temp[i]=super.elements[i];
            }
            super.elements = (E[]) new Comparable[super.elements.length*2];
            for (int w =0;w<temp.length;w++){
                super.elements[w]=temp[w];
            }

        }
    }
