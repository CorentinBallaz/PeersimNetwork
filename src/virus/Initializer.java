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
            currentNodeApp.setProbToInfect(Math.random());
            currentNodeApp.setProbToBeInfected((Math.random()+2*currentNodeApp.function1Resistance(currentNodeApp.getYearOld()))/3);

            currentNodeApp.setListVoisins(new ArrayList());
            allEligibleNodeIds.add(node);
            allNodes.add(node);
            
        }
        
        // NEIGHBORHOOD
        int minVoisins = Configuration.getInt("node.nbVoisinsMin");
        int maxVoisins = Configuration.getInt("node.nbVoisinsMax");
        int randomSeed = Configuration.getInt("random.seed");
        Random random = new Random();
        random.setSeed(randomSeed);
        for (int nodeId=0;nodeId<nodeNb;nodeId++) {
        	
        	Node node = Network.get(nodeId);
        	VirusApp nodeApp = (VirusApp)node.getProtocol(this.virusAppPid);
        	
        	// Si le noeud n'a pas déjà son minimum de voisin requis
        	if ((nodeApp.getListVoisins().size() < minVoisins)) {
        		// On récupère les noeuds éligibles auquels il peut se lier (ceux n'ayant pas atteint leur max et on retire le noeud actuel)
        		List eligibleNodes = new ArrayList(allEligibleNodeIds);
        		eligibleNodes.remove((Object) nodeId);
        		
        		// Tant qu'on a pas atteint le nb minimal de voisin
        		while (nodeApp.getListVoisins().size() < minVoisins) {
        			// Ici on gère le cas où le noeud n'a pas atteint son nb min de voisin et qu'il n'a plus de noeud auquels se lier
        			// Pour gérer ceci, le noeud va se connecter à un autre noeud ayant déjà atteint son nb de voisin max
        			if (eligibleNodes.isEmpty()) {
        				boolean isNotFix = true;
        				// Tant qu'on a pas fait la connexion
        				while (isNotFix) {
        					// On récupère un noeud random
        					int randomIndex = random.nextInt(allNodes.size());
                			int randomNodeId = (int) allNodes.get(randomIndex);
                			Node randomNode = Network.get(randomNodeId);
                			VirusApp randomNodeApp = (VirusApp)randomNode.getProtocol(this.virusAppPid);
                			// On vérifit que le noeud random n'est pas déjà un voisin
                			if (!(nodeApp.getListVoisins().contains(randomNodeId))) {
                				nodeApp.addVoisins(randomNodeId);
                				randomNodeApp.addVoisins(nodeId);
                				isNotFix = false;
                			}
        				}
        			}
        			else {
        				// On récupère un noeud random
        				int randomIndex = random.nextInt(eligibleNodes.size());
            			int randomNodeId = (int) eligibleNodes.get(randomIndex);
            			Node randomNode = Network.get(randomNodeId);
            			VirusApp randomNodeApp = (VirusApp)randomNode.getProtocol(this.virusAppPid);
            			// On vérifit que le noeud random n'est pas déjà un voisin
            			if (!(nodeApp.getListVoisins().contains(randomNodeId))) {
            				nodeApp.addVoisins(randomNodeId);
            				randomNodeApp.addVoisins(nodeId);
            				eligibleNodes.remove((Object) randomNodeId); // On enlève le noeud random des voisins éligibles pour pas se re-relier à lui
            				// Si le noeud random a atteint son nombre max de voisin suite à l'ajout, on le retire des noeuds éligibles globales
            				if (randomNodeApp.getListVoisins().size() >= maxVoisins) {
            					allEligibleNodeIds.remove((Object) randomNodeId);
            				}
            			}
            			// Si le noeud est déjà connecté au noeud random, on retire le random de la liste des noeuds éligibles
            			else {
            				eligibleNodes.remove((Object) randomNodeId);
            			}
        			}
        			
        		}
        	}
        }
        
        
        // On regarde comment le réseau est constitué (nombre de noeuds ayant x voisins)
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
        System.out.println(4%5);
        System.out.println(5%5);
        System.out.println(6%5);
        return false;
    }
}