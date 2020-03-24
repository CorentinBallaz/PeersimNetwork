package virus;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

public class VirusTransport implements Protocol{
  //  private final long min;

    //private final long range;
    public VirusTransport(String prefix){

    }

    public Object clone() {
        return this;
    }


    //envoi d'un message: il suffit de l'ajouter a la file d'evenements
    public void send(Node src, Node dest, Object msg, int pid) {
       // long delay = getLatency(src,dest);
        EDSimulator.add((long)0, msg, dest, pid);
    }


 /*   //latence random entre la borne min et la borne max
    public long getLatency(Node src, Node dest) {
        return (range==1?min:min + CommonState.r.nextLong(range));
    }*/
}