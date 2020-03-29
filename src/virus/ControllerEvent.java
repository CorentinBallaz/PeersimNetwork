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

                int nbNode = Network.size();
                VirusApp appEmitter,appToSend;
                //for each day
                for (int day=0;day<simulationDuration;day++){
                        // look for each node
                        for (int i=0; i<nbNode;i++){

                                appEmitter = (VirusApp)Network.get(i).getProtocol(this.virusAppPid);
                                //a mettre dans l'initalizer
                                appEmitter.setNodeId(i);
                                //if is infected, he can transmit the virus
                                if appEmitter.getState().equals("Infected"){

                                        //rajouter la frequence de sortie des personnes

                                        for (int j=0; j<appEmitter.listVoisins;j++){
                                                appToSend= (VirusApp)Network.get(j).getProtocol(this.virusAppPid);
                                                VirusMessage msg = new VirusMessage(0,"TryContamination",day);

                                                appEmitter.send(msg,appToSend,appEmitter);
                                        }
                                }



                        }
                }




                return false;
        }











}