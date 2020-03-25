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


    public Initializer(String prefix) {
    //recuperation du pid de la couche applicative
        System.out.println(Network.size());
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
        
    }
    public void ajoutVoisins(){
        int minVoisins = 1;//Configuration.getPid("nbVoisinsMin");
        int maxVoisins = 10;//Configuration.getPid("nbVoisinsMax");
        int range = maxVoisins - minVoisins + 2;
        int rangeNetwork = Network.size();
        for(int i=0;i<rangeNetwork;i++){
            Node myNode = Network.get(i);
            // on recupere la couche applicative et on lui associe le numéro de son noeud
            VirusApp destApp = (VirusApp)myNode.getProtocol(this.virusAppPid);
            ArrayList listVoisins = new ArrayList<>();
            for(int k=0;k<rangeNetwork;k++){
                if(k!=i){
                    Node currentNode = Network.get(k);
                    VirusApp currentDestApp = (VirusApp)currentNode.getProtocol(this.virusAppPid);
                    try{
                        for(int l=0;l<currentDestApp.getListVoisins().size();l++){
                            int numVoisin = (int)currentDestApp.getListVoisins().get(l);
                            boolean isIn = false;
                            for(int m=0;m<destApp.getListVoisins().size();m++){
                                if(destApp.getListVoisins().get(m).equals(numVoisin)){
                                    isIn = true;
                                }
                            }
                            if(currentDestApp.getListVoisins().get(l).equals(i) && numVoisin!=i && isIn==false){
                                destApp.addVoisins(numVoisin);
                            }
                        }
                    }catch(Exception e){}
                }
            }
            int voisinAct;
            try{
                voisinAct = destApp.getListVoisins().size();
            }catch(Exception e){
                voisinAct = 0;
            }
            int randVoisins = (int)(Math.random() * range) + minVoisins - voisinAct;
            if(randVoisins<0){
                randVoisins=0;
            }
            int[] ints = new Random().ints(0,rangeNetwork).distinct().limit(randVoisins).toArray();
            //System.out.println(ints.length);
            for(int j=0;j<ints.length;j++){
                int monNb = ints[j];
                boolean isIn2 = false;
                try{
                    for(int n=0;n<destApp.getListVoisins().size();n++){
                        if(destApp.getListVoisins().get(n).equals(monNb)){
                            isIn2 = true;
                        }
                    }
                }catch(Exception e){}
                if(ints[j]!=i && isIn2==false){
                    //System.out.println("noeud : " + i + "; voisin : " + monNb);
                    Node verifNode = Network.get(monNb);
                    VirusApp verifDestApp = (VirusApp)verifNode.getProtocol(this.virusAppPid);
                    try{
                        verifDestApp.addVoisins(i);
                        //System.out.println("j'ajoute");
                    }catch(Exception e){
                        ArrayList listVoisins2 = new ArrayList<>();
                        listVoisins2.add(i);
                        verifDestApp.setListVoisins(listVoisins2);
                        //System.out.println("Je crée");
                    }
                    //System.out.println("noeud ajouté : " + verifDestApp.getListVoisins().toString());
                    try{
                        destApp.addVoisins(monNb);
                    }catch(Exception e){
                        ArrayList listVoisins3 = new ArrayList<>();
                        listVoisins3.add(monNb);
                        destApp.setListVoisins(listVoisins3);
                    }
                    //System.out.println("noeud actuel : " + destApp.getListVoisins().toString());
                }
            }
            destApp.setProbConta(Math.random());

        }
    }

    public boolean execute() {
        int nodeNb;
        VirusApp appEmitter, destApp;
        Node dest;
        VirusMessage virusMessage;
		System.out.println("ça marche");
		//Taille reseau
        nodeNb = Network.size();
        //ajout des voisins pour chaque noeud
        this.ajoutVoisins();
		//Creation message
        virusMessage = new VirusMessage(0,"Infected");
        if (nodeNb < 1) {
            System.err.println("Network size is not positive");
            System.exit(1);
        }
        // on recupere la couche applicative et on lui associe le numéro de son noeud
        appEmitter = (VirusApp)Network.get(0).getProtocol(this.virusAppPid);
        appEmitter.setNodeId(0);

        // pour tous les autres noeuds du graph
        for (int i = 0; i < nodeNb; i++) {
            dest = Network.get(i);
            // on recupere la couche applicative et on lui associe le numéro de son noeud
            destApp = (VirusApp)dest.getProtocol(this.virusAppPid);
            destApp.setNodeId(i);
            System.out.println(destApp.toString() + " " + destApp.getListVoisins().toString() + " " + destApp.getProbConta());
            //on envoit via la couche applicative au destinataire
            appEmitter.send(virusMessage, dest);
        }


        return false;
    }
}