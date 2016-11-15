package io.muic.dcom.p2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DataModel {
    //Inner class
    public static class ParcelObserved {
        private String parcelId;
        private String stationId;
        private long timeStamp;

        ParcelObserved(String parcelId_, String stationId_, long ts_) {
            this.parcelId = parcelId_;
            this.stationId = stationId_;
            this.timeStamp = ts_;
        }
        public String getParcelId() { return parcelId; }
        public String getStationId() { return stationId; }
        public long getTimeStamp() { return timeStamp; }
    }
    private ConcurrentHashMap<String,SortedSet<ParcelObserved>> Hmaptransactions;
    private ConcurrentHashMap<String,Integer> Hcount;
//    private HashMap<String,ArrayList<ParcelObserved>> Hmaptransactions;
//    private HashMap<String,Integer> Hcount;

    DataModel() {

        Hmaptransactions = new ConcurrentHashMap<>();
        Hcount = new ConcurrentHashMap<>();

//        Hmaptransactions = new HashMap<>();
//        Hcount = new HashMap<>();
    }
    static Comparator<ParcelObserved> comp = new Comparator<ParcelObserved>() {
        @Override
        public int compare(ParcelObserved o1, ParcelObserved o2) {
            long a=o1.getTimeStamp();
            long b=o2.getTimeStamp();
            if (a<=b){return -1;}
            else{return 1;}
        }
    };

    public void postObserve(String parcelId, String stationId, long timestamp) {
        ParcelObserved parcelObserved = new ParcelObserved(parcelId, stationId, timestamp);

        if (!Hmaptransactions.containsKey(parcelId)) {
            SortedSet<ParcelObserved> magic = new ConcurrentSkipListSet<>((o1, o2) -> Long.compare(o1.getTimeStamp(), o2.getTimeStamp()));
//            ArrayList<ParcelObserved> magic = new ArrayList<>();
            magic.add(parcelObserved);
            Hmaptransactions.put(parcelId,magic);
        }
        else {
            SortedSet<ParcelObserved> magic = Hmaptransactions.get(parcelId);

//            ArrayList<ParcelObserved> magic = Hmaptransactions.get(parcelId);
            magic.add(parcelObserved);
            Hmaptransactions.put(parcelId,magic);
        }
        if (!Hcount.containsKey(stationId)) {
            Hcount.put(stationId,1);

        }
        else {Hcount.put(stationId,Hcount.get(stationId)+1);}


    }

    public List<ParcelObserved> getParcelTrail(String parcelId) {
        SortedSet<ParcelObserved> ans =Hmaptransactions.get(parcelId);

//        List<ParcelObserved> ans =Hmaptransactions.get(parcelId);
//        Collections.sort(ans, new Comparator<ParcelObserved>() {
//            @Override
//            public int compare(ParcelObserved o1, ParcelObserved o2) {
//                long a=o1.getTimeStamp();
//                long b=o2.getTimeStamp();
//                if (a<=b){return -1;}
//                else{return 1;}
//            }
//        });
        List<ParcelObserved> A = new ArrayList<>();
        A.addAll(ans);
        //System.out.println(A.toString());
        return A;
    }

    public long getStopCount(String stationId) {
        if (!Hcount.containsKey(stationId)) {return 0;}
        return Hcount.get(stationId);
    }
}