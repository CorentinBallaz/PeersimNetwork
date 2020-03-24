package virus;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class VirusApp implements EDProtocol {

    //identifiant de la couche transport
//    private int transportPid;

    //objet couche transport
//    private VirusTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;

    public VirusApp(String prefix){

         this.prefix = prefix;
//        this.transportPid = Configuration.getPid(prefix + ".transport");
        this.mypid = Configuration.getPid(prefix + ".myself");
//        this.transport = null;

    }

    public void processEvent( Node node, int pid, Object event ) {
        this.receive((VirusMessage)event);
    }

    public Object clone() {
        VirusApp dolly = new VirusApp(this.prefix);

        return dolly;
    }

//    //envoi d'un message (l'envoi se fait via la couche transport)
//    public void send(VirusMessage msg, Node dest) {
//        this.transport.send(getMyNode(), dest, msg, this.mypid);
//    }
    //envoi d'un message (l'envoi se fait via la couche transport)
    public void send(VirusMessage msg, Node dest) {
//        this.transport.send(getMyNode(), dest, msg, this.mypid);
        EDSimulator.add((long)0,msg,dest,this.mypid);
    }


    //liaison entre un objet de la couche applicative et un
    //objet de la couche transport situes sur le meme noeud
//    public void setTransportLayer(int nodeId) {
//        this.nodeId = nodeId;
//        this.transport = (VirusTransport) Network.get(this.nodeId).getProtocol(this.transportPid);
//    }

    private void receive(VirusMessage msg) {
        System.out.println(this + ": Received " + msg.getContent());
    }

    private Node getMyNode() {
        return Network.get(this.nodeId);
    }
    public int getMypid(){return this.mypid}
    public String toString() {
        return "Node "+ this.nodeId;
    }
}
