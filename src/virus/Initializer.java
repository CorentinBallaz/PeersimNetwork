package virus;

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
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
    }

    public boolean execute() {
        int nodeNb;
        VirusApp appEmitter, destApp;
        Node dest;
        VirusMessage virusMessage;
		System.out.println("ça marche");
		//Taille reseau
		nodeNb = Network.size();
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
        for (int i = 1; i < nodeNb; i++) {

            dest = Network.get(i);
            // on recupere la couche applicative et on lui associe le numéro de son noeud
            destApp = (VirusApp)dest.getProtocol(this.virusAppPid);
            destApp.setNodeId(i);
            //on envoit via la couche applicative au destinataire
            appEmitter.send(virusMessage, dest);
        }


        return false;
    }
}