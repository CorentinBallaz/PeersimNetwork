package virus;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class VirusApp implements EDProtocol {

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;

    public VirusApp(String prefix){
         this.prefix = prefix;
        this.mypid = Configuration.getPid(prefix + ".myself");
    }

    public Object clone() {
        VirusApp dolly = new VirusApp(this.prefix);
        return dolly;
    }

    public void setNodeId(int nodeId){
        this.nodeId=nodeId;
    }
    private Node getMyNode() {
        return Network.get(this.nodeId);
    }
    public String toString() {
        return "Node "+ this.nodeId;
    }

    //Partie concernat l'envoi de message
    //envoi d'un message (l'envoi se fait via la couche applicative directement)
public void send(VirusMessage msg, Node dest) {
        //methode permettant d'ajouter des evements à la file
        EDSimulator.add((long)0,msg,dest,this.mypid);
    }

    // Partie concernant la reception du message
    public void processEvent( Node node, int pid, Object event ) {
        this.receive((VirusMessage)event);
    }
    private void receive(VirusMessage msg) {
        System.out.println(this + ": Received " + msg.getContent());

    }


}
