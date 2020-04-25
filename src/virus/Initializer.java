package virus;
import java.util.ArrayList;
import java.util.Random;

import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de virusApp:
 */
public class Initializer implements peersim.core.Control {

    private int virusAppPid;
    private ArrayList allNodesId;
    //private ControllerEvent controllerEvent;

    public Initializer(String prefix) {
    //recuperation du pid de la couche applicative
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
        this.allNodesId = new ArrayList();
        
    }

    public boolean execute() {
        int nodeNb;
        VirusApp destApp, currentNodeApp;
        Node dest, currentNode;
        VirusMessage virusMessage;
		//Taille reseau
        nodeNb = Network.size();
        //ajout des voisins pour chaque noeud
        int minYearOld = Configuration.getInt("minYearOld");
    	int maxYearOld = Configuration.getInt("maxYearOld");
    	int minGoingOutFrequency = Configuration.getInt("minGoingOutFrequency");
    	int maxGoingOutFrequency = Configuration.getInt("maxGoingOutFrequency");
        for (int node=0; node<nodeNb; node++) {
        	
        	Random r = new Random();
        	currentNode = Network.get(node);
            currentNodeApp = (VirusApp)currentNode.getProtocol(this.virusAppPid);

            currentNodeApp.setNodeId(node);
        	
        	


        	int randomFrequency = r.nextInt(maxGoingOutFrequency - minGoingOutFrequency) + minGoingOutFrequency;
        	currentNodeApp.setGoingOutFrequency(randomFrequency);
        	
        	
        	int randomYear = r.nextInt(maxYearOld - minYearOld) + minYearOld;
        	currentNodeApp.setYearOld(randomYear);
        	
        	currentNodeApp.setState("Sensible");
            currentNodeApp.setIsVaccined(false);
            
            //ajout de la proba aléatoire d'infecter entre 0 et 1
            currentNodeApp.setProbToInfect(Math.random());
            //ajout de la proba aléatoire d'etre infecté entre 0 et 1, (moyenne ponderee d'un random et d'une fonction qui regit les defense imunitaires suivant l'age
            currentNodeApp.setProbToBeInfected((Math.random()+2*currentNodeApp.function1Resistance(currentNodeApp.getYearOld()))/3);
        	// System.out.println("Year Old : "+currentNodeApp.getYearOld());
        	// System.out.println("Going out Frequency : "+currentNodeApp.getGoingOutFrequency());
        	// System.out.println("I'm vaccined : "+currentNodeApp.getIsVaccined());
        	// System.out.println("What's is my state : "+currentNodeApp.getState());
            currentNodeApp.setListVoisins(new ArrayList());
            allNodesId.add(node);
            
        }
        
        // NEIGHBORHOOD
        int minVoisins = Configuration.getInt("node.nbVoisinsMin");
        int maxVoisins = Configuration.getInt("node.nbVoisinsMax");
        Random random = new Random();
        for (int nodeId=0;nodeId<nodeNb;nodeId++) {
        	Node node = Network.get(nodeId);
        	VirusApp nodeApp = (VirusApp)node.getProtocol(this.virusAppPid);
        	if ((nodeApp.getListVoisins().size() < minVoisins)) {
        		while (nodeApp.getListVoisins().size() < minVoisins) {

        			int randomNodeId = random.nextInt(allNodesId.size());
        			Node randomNode = Network.get(randomNodeId);
        			VirusApp randomNodeApp = (VirusApp)randomNode.getProtocol(this.virusAppPid);
        			
        			if ((randomNodeApp.getListVoisins().size() >= maxVoisins-1)) {
        				allNodesId.remove((Object) randomNodeId); 
        			}

        			if ((!(nodeApp.getListVoisins().contains(randomNodeId))) && (randomNodeApp.getListVoisins().size() < maxVoisins) && (!(nodeId == randomNodeId))) {
        				nodeApp.addVoisins(randomNodeId);
        				randomNodeApp.addVoisins(nodeId);
        			}
        		}
        	}
        }
        
        System.out.println("Neighborhood done");
      
        //We infected the n first node request in config_file
        for (int infectedNode=0;infectedNode<Configuration.getInt("nbNodeInfected");infectedNode++){
            VirusApp appInfected = (VirusApp)Network.get(infectedNode).getProtocol(this.virusAppPid);
            appInfected.setState("Infected");
        }
        System.out.println("init done");
        System.out.println(4%5);
        System.out.println(5%5);
        System.out.println(6%5);
        return false;
    }
}