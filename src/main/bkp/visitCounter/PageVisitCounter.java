package com.test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

class VisitCounter {
    private final int pages;
    private final AtomicIntegerArray atomicIntegerArray;

    public VisitCounter(int pages){
        this.pages = pages;
        this.atomicIntegerArray = new AtomicIntegerArray(pages);
    }

    public void increment(int page){
        this.atomicIntegerArray.incrementAndGet(page);
        //System.out.println("Incremented page :"+page);
    }

    public int get(int page){
        return this.atomicIntegerArray.get(page);
    }
}

class VisitCounterArray {
    private final int pages;
    private final List<Integer> arrayList;

    public VisitCounterArray(int pages){
        this.pages = pages;
        this.arrayList = new ArrayList<>(Collections.nCopies(pages,0));
    }

    public void increment(int page){
        this.arrayList.set(page,1+ arrayList.get(page));
        //System.out.println("Incremented page :"+page);
    }

    public int get(int page){
        return this.arrayList.get(page);
    }
}

class VisitCounterSync {
    private final int pages;
    private final List<Integer> arrayList;

    public VisitCounterSync(int pages){
        this.pages = pages;
        this.arrayList = new ArrayList<>(Collections.nCopies(pages,0));
    }

    public void increment(int page){
        synchronized (arrayList) {
            this.arrayList.set(page, 1 + arrayList.get(page));
            //System.out.println("Incremented page :" + page);
        }
    }

    public int get(int page){
        return this.arrayList.get(page);
    }
}

class VisitCounterAtomicIntArray {
    private final int pages;
    private final List<AtomicInteger> arrayList;

    public VisitCounterAtomicIntArray(int pages){
        this.pages = pages;
        arrayList = new ArrayList<>();
        for(int i=0;i<pages;i++){
            arrayList.add(new AtomicInteger(0));
        }
    }

    public void increment(int page){
            arrayList.get(page).incrementAndGet();
            //System.out.println("Incremented page :" + page);
    }

    public int get(int page){
        return this.arrayList.get(page).get();
    }
}

class VisitCounterAtomicArray {
    private final int pages;
    private final AtomicInteger[] arrayList;

    public VisitCounterAtomicArray(int pages){
        this.pages = pages;
        arrayList = new AtomicInteger[pages];
        for(int i=0;i<pages;i++){
            arrayList[i] = new AtomicInteger(0);
        }
    }

    public void increment(int page){
        arrayList[page].incrementAndGet();
        //System.out.println("Incremented page :" + page);
    }

    public int get(int page){
        return this.arrayList[page].get();
    }
}

class VisitCounterAtomicIntHash {
    private final int pages;
    private final Map<Integer,AtomicInteger> hashMap;

    public VisitCounterAtomicIntHash(int pages){
        this.pages = pages;
        this.hashMap = new HashMap<>();
        for(int i=0;i<pages;i++){
            hashMap.put(i,new AtomicInteger(0));
        }
    }

    public void increment(int page){
        hashMap.get(page).incrementAndGet();
        //System.out.println("Incremented page :" + page);
    }

    public int get(int page){
        return this.hashMap.get(page).get();
    }
}

class VisitCounterConcurrent {
    private final int pages;
    private final ConcurrentHashMap<Integer,Integer> concurrentHashMap;

    public VisitCounterConcurrent(int pages){
        this.pages = pages;
        concurrentHashMap = new ConcurrentHashMap<>();
    }

    public void increment(int key){
        concurrentHashMap.compute(key,(k,v) -> v == null ? 1 : v+1);

    }
    public int get(int page){
        Integer value = this.concurrentHashMap.get(page);
        return value == null ? 0 : value ;
    }
}

/*
simple : VisitCounterArray : 727
AtomicIntegerArray : 687
synchronised : 734
ArrayOfAtomicInteger : 664
MapOfAtomicInteger: 676
ConcurrentHashMap : 730
AtomicIntArray : 702
* */
public class PageVisitCounter {
    public static void main(String args[]) throws InterruptedException {
        int pageCount = 3;
        VisitCounterAtomicArray visitCounter = new VisitCounterAtomicArray(pageCount);
        int threadCount = 30000;
        Thread[] threads = new Thread[threadCount];

        for(int i=0;i<threadCount;i++) {
            final int userId = i;
            threads[i] = new Thread(() ->{
                int pageIndex = userId % pageCount;
                for(int j=0;j<10;j++){
                    visitCounter.increment(pageIndex);
               }
            });
        }

        long timeInMillis = System.currentTimeMillis();

        for(Thread thread : threads){
            thread.start();
        }
        for(Thread thread : threads){
            thread.join();
        }

        long timeInMillisEnd = System.currentTimeMillis();

        System.out.println("Time taken : "+(timeInMillisEnd-timeInMillis));

        for(int i=0;i<pageCount;i++){
            System.out.println(i+" :"+visitCounter.get(i));
        }
    }

}
