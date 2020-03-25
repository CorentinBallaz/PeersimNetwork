package virus;

import java.util.ArrayList;
import java.util.Random;
import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class VirusApp implements EDProtocol {

    // identifiant de la couche courante (la couche applicative)
    private int mypid;

    // le numero de noeud
    private int nodeId;

    // prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;

    // ajout de la probabilité de contaminer quelqu'un
    private double probConta;

    // ajout de la liste de voisins pour de petits noeuds
    private ArrayList listVoisins;

    public VirusApp(String prefix) {
        this.prefix = prefix;
        this.mypid = Configuration.getPid(prefix + ".myself");
    }

    public double getProbConta() {
        return probConta;
    }

    public void setProbConta(double probConta) {
        this.probConta = probConta;
    }

    public ArrayList getListVoisins() {
        return listVoisins;
    }

    public void setListVoisins(ArrayList listVoisins) {
        this.listVoisins = listVoisins;
    }
    public void addVoisins(int numVoisins){
        this.listVoisins.add(numVoisins);
    }
    // public void addListVoisins(){
    //     int minVoisins = 1;//Configuration.getPid("nbVoisinsMin");
    //     int maxVoisins = 5;//Configuration.getPid("nbVoisinsMax");
    //     int range = maxVoisins - minVoisins + 1;
    //     int randVoisins = (int)(Math.random() * range) + minVoisins;
    //     int rangeNetwork = Network.size();
    //     int[] ints = new Random().ints(1, rangeNetwork).distinct().limit(randVoisins).toArray();
    //     for(int i=0;i<ints.length;i++){
    //         System.out.println(ints[i]);
    //     }         
    // }

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
    public int getMyID(){
        return this.nodeId;
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
