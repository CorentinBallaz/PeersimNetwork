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

    // ajout de la probabilitÃ© de contaminer quelqu'un
    private double probConta;
    
    // ajout de la frequence de sortie (en jour)
    private int goingOutFrequency;
    
    // ajout de l'age d'une personne
    private int yearOld;
    
    // ajout de l'état d'une personne
    private String state;
    
    // ajout de si la personne est vaccinée
    private boolean isVaccined;

    // ajout de la liste de voisins pour de petits noeuds
    private ArrayList<Integer> listVoisins;

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
    
    public void setGoingOutFrequency(int frequency) {
    	this.goingOutFrequency = frequency;
    }
    
    public int getGoingOutFrequency() {
    	return goingOutFrequency;
    }
    
    public void setYearOld(int yearOld) {
    	this.yearOld = yearOld;
    }
    
    public int getYearOld() {
    	return yearOld;
    }
    

    public void setState(String state) {
    	this.state = state;
    }
    
    public String getState() {
    	return state;
    }
    
    public void setIsVaccined(boolean isVaccined) {
    	this.isVaccined = isVaccined;
    }
    
    public boolean getIsVaccined() {
    	return isVaccined;
    }
    
    public ArrayList<Integer> getListVoisins() {
        return listVoisins;
    }

    public void setListVoisins(ArrayList<Integer> listVoisins) {
        this.listVoisins = listVoisins;
    }
    public void addVoisins(int numVoisins){
        this.listVoisins.add(numVoisins);
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
    public int getMyID(){
        return this.nodeId;
    }
    public String toString() {
        return "Node "+ this.nodeId;
    }

    //Partie concernat l'envoi de message
    //envoi d'un message (l'envoi se fait via la couche applicative directement)
public void send(VirusMessage msg, Node dest) {
        //methode permettant d'ajouter des evements Ã  la file
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
