package virus;
import java.util.ArrayList;
import java.util.Random;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de virusApp:
 */
public class Initializer implements peersim.core.Control {

    private int virusAppPid;
    private

    public Initializer(String prefix) {
    //recuperation du pid de la couche applicative
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
        
    }
    public void ajoutVoisins(){
        ///méthode qui créé pour chaque noeud une liste de voisins qui sont directement reliés à lui;
        ///associe une arrayList d'entier(s)
        int minVoisins = Configuration.getInt("node.nbVoisinsMin");
        int maxVoisins = Configuration.getInt("node.nbVoisinsMax");
        int range = maxVoisins - minVoisins + 2;
        int rangeNetwork = Network.size();
        for(int i=0;i<rangeNetwork;i++){
            Node myNode = Network.get(i);
            // on recupere la couche applicative et on lui associe le numéro de son noeud
            VirusApp destApp = (VirusApp)myNode.getProtocol(this.virusAppPid);
            //On check dans les autres noeuds s'il n'y a pas le noeud actuel
            //si oui, on l'ajoute dans notre liste de voisins
            for(int k=0;k<rangeNetwork;k++){
                if(k!=i){
                    Node currentNode = Network.get(k);
                    VirusApp currentDestApp = (VirusApp)currentNode.getProtocol(this.virusAppPid);
                    try{
                        for(int l=0;l<currentDestApp.getListVoisins().size();l++){
                            int numVoisin = currentDestApp.getListVoisins().get(l);
                            boolean isIn = destApp.getListVoisins().contains(numVoisin);
                            if(currentDestApp.getListVoisins().get(l).equals(i) && numVoisin!=i && isIn==false){
                                destApp.addVoisins(numVoisin);
                            }
                        }
                    }catch(Exception e){}
                }
            }
            //on regarde les voisins que l'on a deja, au cas où on m'aurait ajouter avant à partir d'un autre noeud
            int voisinAct;
            try{
                voisinAct = destApp.getListVoisins().size();
            }catch(Exception e){
                voisinAct = 0;
            }
            //si je suis deja dans la liste de nbMaxvoisins de noeuds, alors je n'en rajoute pas
            //sinon variable aléatoire entre min et max pour cb de voisins je prend au hasard
            int randVoisins = (int)(Math.random() * range) + minVoisins - voisinAct;
            if(randVoisins<0){
                randVoisins=0;
            }
            //je prend des nombres au hasard qui seront mes voisins
            int[] ints = new Random().ints(0,rangeNetwork).distinct().limit(randVoisins).toArray();
            for(int j=0;j<ints.length;j++){
                int monNb = ints[j];
                boolean isIn2 = false;
                //je check si le nb n'est pas deja dans ma liste de voisins
                try{
                    isIn2 = destApp.getListVoisins().contains(monNb);
                }catch(Exception e){}
                if(ints[j]!=i && isIn2==false){
                    Node verifNode = Network.get(monNb);
                    VirusApp verifDestApp = (VirusApp)verifNode.getProtocol(this.virusAppPid);
                    //Ajout de mon noeud dans le noeud voisin que j'ai tiré aléatoirement
                    try{
                        verifDestApp.addVoisins(i);
                    }catch(Exception e){
                        ArrayList<Integer> listVoisins2 = new ArrayList<Integer>();
                        listVoisins2.add(i);
                        verifDestApp.setListVoisins(listVoisins2);
                    }
                    //j'ajoute le nb tiré dans ma liste de noeuds voisins
                    try{
                        destApp.addVoisins(monNb);
                    }catch(Exception e){
                        ArrayList<Integer> listVoisins3 = new ArrayList<Integer>();
                        listVoisins3.add(monNb);
                        destApp.setListVoisins(listVoisins3);
                    }
                }
            }
            //ajout de la proba aléatoire de contamination entre 0 et 1
            destApp.setProbConta(Math.random());

        }
    }

    public boolean execute() {
        int nodeNb;
        VirusApp appEmitter, destApp, currentNodeApp;
        Node dest, currentNode;
        VirusMessage virusMessage;
		System.out.println("ça marche");
		//Taille reseau
        nodeNb = Network.size();
        //ajout des voisins pour chaque noeud
        
        for (int node=0; node<nodeNb; node++) {
        	
        	Random r = new Random();
        	currentNode = Network.get(node);
        	currentNodeApp = (VirusApp)currentNode.getProtocol(this.virusAppPid);
        	
        	int minGoingOutFrequency = Configuration.getInt("minGoingOutFrequency");
        	int maxGoingOutFrequency = Configuration.getInt("maxGoingOutFrequency");
        	int randomFrequency = r.nextInt(maxGoingOutFrequency - minGoingOutFrequency) + minGoingOutFrequency;
        	currentNodeApp.setGoingOutFrequency(randomFrequency);
        	
        	int minYearOld = Configuration.getInt("minYearOld");
        	int maxYearOld = Configuration.getInt("maxYearOld");
        	int randomYear = r.nextInt(maxYearOld - minYearOld) + minYearOld;
        	currentNodeApp.setYearOld(randomYear);
        	
        	currentNodeApp.setState("Clean");
        	currentNodeApp.setIsVaccined(false);
        	
        	
        	System.out.println("Year Old : "+currentNodeApp.getYearOld());
        	System.out.println("Going out Frequency : "+currentNodeApp.getGoingOutFrequency());
        	System.out.println("I'm vaccined : "+currentNodeApp.getIsVaccined());
        	System.out.println("What's is my state : "+currentNodeApp.getState());

        }
        
        this.ajoutVoisins();
        
        
        
		//Creation message
//        virusMessage = new VirusMessage(0,"Infected");
//        if (nodeNb < 1) {
//            System.err.println("Network size is not positive");
//            System.exit(1);
//        }
//        // on recupere la couche applicative et on lui associe le numéro de son noeud
//        appEmitter = (VirusApp)Network.get(0).getProtocol(this.virusAppPid);
//        appEmitter.setNodeId(0);
//
//        // pour tous les autres noeuds du graph
//        for (int i = 0; i < nodeNb; i++) {
//            dest = Network.get(i);
//            // on recupere la couche applicative et on lui associe le numéro de son noeud
//            destApp = (VirusApp)dest.getProtocol(this.virusAppPid);
//            destApp.setNodeId(i);
//            System.out.println(destApp.toString() + " " + destApp.getListVoisins().toString() + " " + destApp.getProbConta() + " Year Old : "+ destApp.getYearOld() + " Going out Frequency : "+ destApp.getGoingOutFrequency());
//            //on envoit via la couche applicative au destinataire
//            appEmitter.send(virusMessage, dest);
//        }

        controller


        return false;
    }
}