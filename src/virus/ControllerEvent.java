package virus;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;


public class ControllerEvent implements peersim.core.Control{
        private ArrayList<HashMap> arrayData;
        private int virusAppPid;
        private int nbEvent;
        private int endTimeSimulation;


        public ControllerEvent(String prefix) {
                //recuperation du pid de la couche applicative
                this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
                this.endTimeSimulation = Configuration.getInt("simulation.endtime");
                this.arrayData = new ArrayList<HashMap>();
                this.nbEvent = 0;
        }

        public boolean execute(){

                HashMap temp = new HashMap();
                int nbNode = Network.size();
                VirusApp appEmitter,appDest;
                Node emitter,dest;
                int nbInfected = 0;
                int nbClean = 0;

                // look for each node
                for (int i=0; i<nbNode;i++){
                        emitter = Network.get(i);
                        appEmitter = (VirusApp)emitter.getProtocol(this.virusAppPid);
                        //a mettre dans l'initalizer
                        appEmitter.setNodeId(i);
                        //if is infected, he can transmit the virus
                        if((appEmitter.getState().equals("Infected")) && (this.nbEvent%appEmitter.getGoingOutFrequency()==0)){
                                double probToInfect = appEmitter.getProbToInfect();
                                for (int j=0; j<appEmitter.getListVoisins().size();j++){
                                        int currentNodeID = appEmitter.getListVoisins().get(j);
                                        dest = Network.get(currentNodeID);
                                        appDest = (VirusApp)dest.getProtocol(this.virusAppPid);
                                        VirusMessage msg = new VirusMessage(0,"TryContamination",probToInfect);

                                        //on envoit via la couche applicative au destinataire
                                        //on regarde la réponse de la fonction send afin de changer l'etat du noeud infecté
                                        appEmitter.send(msg,dest,probToInfect);
                                }
                        }
//                        if(this.nbEvent == this.endTimeSimulation-1){
                                if(appEmitter.getState().equals("Infected")){
                                        nbInfected += 1;
                                }else{
                                        nbClean += 1;
//                                }


                        }
                }
                temp.put("nbEvent",nbEvent);
                temp.put("nbClean",nbClean);
                temp.put("nbInfected",nbInfected);
                this.arrayData.add((HashMap)temp.clone());
                temp.clear();


                if(this.nbEvent == this.endTimeSimulation-1){
                        System.out.println("Nombre d'infectés : " + nbInfected);
                        System.out.println("Nombre de sains : " + nbClean);
                        System.out.println(arrayData);
                }
                this.nbEvent += 1;



                return false;
        }











}