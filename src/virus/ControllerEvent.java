package virus;
import java.util.ArrayList;
import java.util.Random;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;


public class ControllerEvent implements peersim.core.Control{

        private int virusAppPid;
        private int nbEvent;

        // Simulation duration (days number)
        private int simulationDuration;


        public ControllerEvent(String prefix) {
        //recuperation du pid de la couche applicative
        this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
        this.simulationDuration= Configuration.getInt(prefix + ".simulationDuration");

        }

        public boolean execute(){
                System.out.println("Pass in controller");
                int nbNode = Network.size();
                VirusApp appEmitter,appDest;
                Node emitter,dest;

                //We infected the n first node request in config_file
                for (int infectedNode=0;infectedNode<Configuration.getInt("nbNodeInfected");infectedNode++){
                        VirusApp appInfected = (VirusApp)Network.get(infectedNode).getProtocol(this.virusAppPid);
                        appInfected.setState("Infected");
                    }

                //for each day
                for (int day=0;day<simulationDuration;day++){
                        // look for each node
                        for (int i=0; i<nbNode;i++){
                                emitter = Network.get(i);
                                appEmitter = (VirusApp)emitter.getProtocol(this.virusAppPid);
                                //a mettre dans l'initalizer
                                appEmitter.setNodeId(i);
                                //if is infected, he can transmit the virus
                                if(appEmitter.getState().equals("Infected")){
                                        double probToInfect = appEmitter.getProbToInfect();

                                        //rajouter la frequence de sortie des personnes

                                        for (int j=0; j<appEmitter.getListVoisins().size();j++){
                                                int currentNodeID = appEmitter.getListVoisins().get(j);
                                                dest = Network.get(currentNodeID);
                                                appDest = (VirusApp)dest.getProtocol(this.virusAppPid);
                                                VirusMessage msg = new VirusMessage(0,"TryContamination",day,probToInfect);

                                                //on envoit via la couche applicative au destinataire
                                                //on regarde la réponse de la fonction send afin de changer l'etat du noeud infecté
                                                appEmitter.send(msg,dest,probToInfect);
                                        }
                                }



                        }
                }




                return false;
        }











}