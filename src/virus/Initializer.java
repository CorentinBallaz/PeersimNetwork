package virus;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    
    private int virusAppPid;


    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
    }

    public boolean execute() {
        int nodeNb;
        VirusApp appEmitter, current;
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

        appEmitter = (VirusApp)Network.get(0).getProtocol(this.virusAppPid);
//        appEmitter.setTransportLayer(0);
        System.out.println("le noeud emmeteur " + appEmitter);
        for (int i = 1; i < nodeNb; i++) {
            dest = Network.get(i);

            current = (VirusApp) dest.getProtocol(dest.get);
            System.out.println(current);
//            current.setTransportLayer(i);
            appEmitter.send(virusMessage, dest);
        }


        return false;
    }
}