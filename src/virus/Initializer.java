package virus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de virusApp:
 */
public class Initializer implements peersim.core.Control {

    private int virusAppPid;
    //private ControllerEvent controllerEvent;
    private ArrayList allEligibleNodeIds;
    private List allNodes;

    public Initializer(String prefix) {
    //recuperation du pid de la couche applicative
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
        // Tous les noeuds n'ayant pas atteint leur nb de voisin max
        this.allEligibleNodeIds = new ArrayList();
        // Tous les noeuds
        this.allNodes = new ArrayList();
        
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
    	Random randomForAttributes = new Random();
    	try {
        	int randomSeed = Configuration.getInt("random.seed.attributes");
        	randomForAttributes.setSeed(randomSeed);
        }
        catch (Exception e) {

        }
        for (int node=0; node<nodeNb; node++) {
        	
        	currentNode = Network.get(node);
            currentNodeApp = (VirusApp)currentNode.getProtocol(this.virusAppPid);

            currentNodeApp.setNodeId(node);

        	int randomFrequency = randomForAttributes.nextInt(maxGoingOutFrequency - minGoingOutFrequency) + minGoingOutFrequency;
        	currentNodeApp.setGoingOutFrequency(randomFrequency);
        	
        	int randomYear = randomForAttributes.nextInt(maxYearOld - minYearOld) + minYearOld;
        	currentNodeApp.setYearOld(randomYear);
        	
        	currentNodeApp.setState("Sensible");
            currentNodeApp.setIsVaccined(false);
            currentNodeApp.setProbToInfect(Math.random());
            currentNodeApp.setProbToBeInfected((Math.random()+2*currentNodeApp.function1Resistance(currentNodeApp.getYearOld()))/3);

            currentNodeApp.setListVoisins(new ArrayList());
            allEligibleNodeIds.add(node);
            allNodes.add(node);
            
        }
        
        // NEIGHBORHOOD
        int minVoisins = Configuration.getInt("node.nbVoisinsMin");
        int maxVoisins = Configuration.getInt("node.nbVoisinsMax");
        Random randomForNeighbor = new Random();
        try {
        	int randomSeed = Configuration.getInt("random.seed.neighbor");
            randomForNeighbor.setSeed(randomSeed);
        }
        catch (Exception e) {
        	
        }
        for (int nodeId=0;nodeId<nodeNb;nodeId++) {
        	
        	Node node = Network.get(nodeId);
        	VirusApp nodeApp = (VirusApp)node.getProtocol(this.virusAppPid);
        	
        	// Si le noeud n'a pas deja son minimum de voisin requis
        	if ((nodeApp.getListVoisins().size() < minVoisins)) {
        		// On recupere les noeuds eligibles auquels il peut se lier (ceux n'ayant pas atteint leur max et on retire le noeud actuel)
        		List eligibleNodes = new ArrayList(allEligibleNodeIds);
        		eligibleNodes.remove((Object) nodeId);
        		
        		// Tant qu'on a pas atteint le nb minimal de voisin
        		while (nodeApp.getListVoisins().size() < minVoisins) {
        			// Ici on gere le cas ou le noeud n'a pas atteint son nb min de voisin et qu'il n'a plus de noeud auquels se lier
        			// Pour gerer ceci, le noeud va se connecter a un autre noeud ayant deja atteint son nb de voisin max
        			if (eligibleNodes.isEmpty()) {
        				boolean isNotFix = true;
        				// Tant qu'on a pas fait la connexion
        				while (isNotFix) {
        					// On recupere un noeud random
        					int randomIndex = randomForNeighbor.nextInt(allNodes.size());
                			int randomNodeId = (int) allNodes.get(randomIndex);
                			Node randomNode = Network.get(randomNodeId);
                			VirusApp randomNodeApp = (VirusApp)randomNode.getProtocol(this.virusAppPid);
                			// On verifit que le noeud random n'est pas deja un voisin
                			if (!(nodeApp.getListVoisins().contains(randomNodeId))) {
                				nodeApp.addVoisins(randomNodeId);
                				randomNodeApp.addVoisins(nodeId);
                				isNotFix = false;
                			}
        				}
        			}
        			else {
        				// On recupere un noeud random
        				int randomIndex = randomForNeighbor.nextInt(eligibleNodes.size());
            			int randomNodeId = (int) eligibleNodes.get(randomIndex);
            			Node randomNode = Network.get(randomNodeId);
            			VirusApp randomNodeApp = (VirusApp)randomNode.getProtocol(this.virusAppPid);
            			// On verifit que le noeud random n'est pas deje un voisin
            			if (!(nodeApp.getListVoisins().contains(randomNodeId))) {
            				nodeApp.addVoisins(randomNodeId);
            				randomNodeApp.addVoisins(nodeId);
            				eligibleNodes.remove((Object) randomNodeId); // On enleve le noeud random des voisins eligibles pour pas se re-relier e lui
            				// Si le noeud random a atteint son nombre max de voisin suite e l'ajout, on le retire des noeuds eligibles globales
            				if (randomNodeApp.getListVoisins().size() >= maxVoisins) {
            					allEligibleNodeIds.remove((Object) randomNodeId);
            				}
            			}
            			// Si le noeud est deje connecte au noeud random, on retire le random de la liste des noeuds eligibles
            			else {
            				eligibleNodes.remove((Object) randomNodeId);
            			}
        			}
        			
        		}
        	}
        }
        
        
        // On regarde comment le reseau est constitue (nombre de noeuds ayant x voisins)
        HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();
        for (int nodeId=0;nodeId<nodeNb;nodeId++) {
        	Node node = Network.get(nodeId);
        	VirusApp nodeApp = (VirusApp)node.getProtocol(this.virusAppPid);
        	if (nodeApp.getListVoisins().contains(nodeId)) {
        		System.out.println("It's a fail");
        	}
        	myMap.merge(nodeApp.getListVoisins().size(), 1, Integer::sum);
        }
        System.out.println(myMap);
        
      
        //We infected the n first node request in config_file
        for (int infectedNode=0;infectedNode<Configuration.getInt("nbNodeInfected");infectedNode++){
            VirusApp appInfected = (VirusApp)Network.get(infectedNode).getProtocol(this.virusAppPid);
            appInfected.setState("Infected");
        }
        System.out.println("init done");

        return false;
    }
}