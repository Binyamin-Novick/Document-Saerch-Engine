package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key,Value> implements HashTable<Key,Value> {
    SinglyLinkedItem<Key, Value>[] vals = new SinglyLinkedItem[5];
    int m = 5;
    int n = 0;

    @Override
    public Value put(Key key, Value val) {
        int numb = key.hashCode() % m;
        if (numb < 0) {
            numb = numb * -1;
        }
        if (vals[numb] == null) {
            if (val == null) {
                return null;
            }
            vals[numb] = new SinglyLinkedItem<Key, Value>();
            vals[numb].data = val;
            vals[numb].datak = key;
            n++;
            doubleifneed();
            return null;
        } else {
            return replacevale(key, val, vals[numb]);
        }
    }

    public Value get(Key key) {// get
        int numb = key.hashCode() % m;
        if (numb < 0) {
            numb = numb * -1;
        }
        if (vals[numb] == null) {
            return null;
        }

        return getvale(key, vals[numb]);

    }


    private class SinglyLinkedItem<Key, Value> {
        private Value data;
        private Key datak;
        private Key prev;
        private SinglyLinkedItem<Key, Value> next;
    }

    private Value getvale(Key v, SinglyLinkedItem head) {// getv of k
        SinglyLinkedItem h = head;
        if (h == null) {
            return null;
        }
        while (h != null) {
            if (h.datak.equals(v) == true) {
                return (Value) h.data;
            } else {
                h = h.next;
            }

        }
        return null;
    }

    private Value replacevale(Key k, Value in, SinglyLinkedItem head) {// replace key
        if (in == null) {
            return dell(k, head);
        }
        SinglyLinkedItem h = head;
        Value v = null;
        SinglyLinkedItem t = h;
        while ((h != null)) {
            if (h.datak.equals(k)) {
                v = (Value) h.data;
                h.data = in;

                return v;
            } else {
                t = h;
                h = h.next;
            }
        }
        addkeyitemtoend(t, k, in);
        return null;

    }


    private void addkeyitemtoend(SinglyLinkedItem head, Key input, Value v) {// only use for keys at the last link other wise it will cut off the rest
        head.next = new SinglyLinkedItem();
        head.next.datak = input;
        head.next.data = v;
        head.next.prev = head;
        n++;
        doubleifneed();
    }

    private void doubleifneed() {
        float tntnt = (n);
        float mtmtmt = m;
        if ((tntnt / mtmtmt) >= (3.0 / 4.0)) {
            int tm = m;
            m = m * 2;
            n = 0;
            arrarysize(m, tm);
        }
    }

    private void arrarysize(int nm, int tm) {
        int oldl = tm;
        SinglyLinkedItem<Key, Value>[] tempv = new SinglyLinkedItem[tm];
        for (int wi = 0; wi < tm; wi++) {
            tempv[wi] = vals[wi];
        }
        vals = new SinglyLinkedItem[nm];
        SinglyLinkedItem t;
        for (int i = 0; i < oldl; i++) {
            while (tempv[i] != null) {
                t = popt(i, tempv);
                this.put((Key) t.datak, (Value) t.data);
            }
        }

    }

    private SinglyLinkedItem popt(int t, SinglyLinkedItem[] tm) {
        SinglyLinkedItem temp = tm[t];
        tm[t] = tm[t].next;
        return temp;
    }

    private Value dell(Key k, SinglyLinkedItem s) {
        SinglyLinkedItem out;
        Value vo;
        if (s == null) {
            return null;
        }
        if (k == s.datak) {// if first is the corect key
            out = popt(s.datak.hashCode() % m, vals);
            n--;
            return (Value) out.data;
        }
        if (s.next == null) {
            return null;
        }
        if (s.next.datak == k) { //if corect key fond but not firsttttttttt
            vo = (Value) s.next.data;
            s.next = s.next.next;
            n--;
            return vo;
        }
        return (Value) dell(k, s.next);
    }
}
