package virus;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Arrays;
import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;
import java.util.Random;
import java.io.File;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





public class ControllerEvent implements peersim.core.Control{
        private ArrayList<HashMap> arrayData;
        private int virusAppPid;
        private int nbEvent;
        private int endTimeSimulation;
        private int vaccineBegin;
        private double percentageVaccinated;
        private boolean[] vaccinated;
        private int numberOfPeopleTovaccinated;
        private int timeToRecover;

        public ControllerEvent(String prefix) {
                //recuperation du pid de la couche applicative
                this.virusAppPid = Configuration.getPid(prefix+ ".virusAppProtocolPid");
                this.endTimeSimulation = Configuration.getInt("simulation.endtime");
                this.arrayData = new ArrayList<HashMap>();
                this.nbEvent = 0;
                this.vaccineBegin =(int)(Configuration.getDouble(prefix+".vaccineBegin") * endTimeSimulation);
                this.percentageVaccinated=Configuration.getDouble(prefix+".percentageVaccinated");
                this.vaccinated = new boolean[Network.size()];
                Arrays.fill(vaccinated,false);
                this.numberOfPeopleTovaccinated = Network.size();
        }

        public boolean execute(){


                HashMap temp = new HashMap();
                int nbNode = Network.size();
                VirusApp appEmitter,appDest;
                Node emitter,dest;
                int nbInfected = 0;
                int nbClean = 0;
                int nbRejected=0;
                int nbDeath = 0;

                //Vaccination part
                if (nbEvent>= vaccineBegin){

                        int toVaccinated = (int)(numberOfPeopleTovaccinated*percentageVaccinated);
                        Random r = new Random();
                        for (int i= 0 ; i<toVaccinated; i++){
                                int random = r.nextInt((numberOfPeopleTovaccinated - 0));
                                dest = Network.get(random);
                                appDest = (VirusApp)dest.getProtocol(this.virusAppPid);

                                if (vaccinated[random]==false && appDest.getState().equals("Sensible")) {
                                        appDest.setIsVaccined(true);
                                        vaccinated[random] = true;
                                        numberOfPeopleTovaccinated--;
                                }
//                        }else{
//                                        i--;
//                                }
                        }



                }


                // look for each node
                //Virus broadcasting part
                for (int i=0; i<nbNode;i++){
                        emitter = Network.get(i);
                        appEmitter = (VirusApp)emitter.getProtocol(this.virusAppPid);


                        //a mettre dans l'initalizer
//                        appEmitter.setNodeId(i);
                        //if is infected, he can transmit the virus

                        //Disease evolution
                        if(appEmitter.getState().equals("Infected")){
//                                System.out.println("rentrer dans set New state");
                                appEmitter.setNewState();


                        }
                        if(appEmitter.getState().equals("Death")){
//                                System.out.println(appEmitter.getState());
                        }

                        //Infection evolution

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
                                }else if (appEmitter.getState().equals("Sensible")){
                                        nbClean += 1;
//                                }


                        }else if (appEmitter.getState().equals("Rejected")){
                                        nbRejected+=1;
                                }else if (appEmitter.getState().equals("Death")){
                                        nbDeath+=1;
                                }

                }
                temp.put("nbEvent",nbEvent);
                temp.put("nbSensible",nbClean);
                temp.put("nbInfected",nbInfected);
                temp.put("nbRejected",nbRejected);
                temp.put("nbDeath",nbDeath);

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