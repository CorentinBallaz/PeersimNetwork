package virus;
import java.lang.Math;
import java.util.ArrayList;
import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

public class VirusApp implements EDProtocol {

    // identifiant de la couche courante (la couche applicative)
    private int mypid;

    // le numero de noeud
    private int nodeId;

    // prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;

    // ajout de la probabilité de d'infecté quelqu'un
    private double probToInfect;

    // ajout de la probabilité d'être infecté
    private double probToBeInfected;
    
    // ajout de la frequence de sortie (en jour)
    private int goingOutFrequency;
    
    // ajout de l'age d'une personne
    private int yearOld;
    
    // ajout de l'�tat d'une personne
    private String state;
    
    // ajout de si la personne est vaccin�e
    private boolean isVaccined;

    // ajout de la liste de voisins pour de petits noeuds
    private ArrayList<Integer> listVoisins;

    private int dayInfected;
    private int timeToRevocered;

    public VirusApp(String prefix) {
        this.prefix = prefix;
        this.mypid = Configuration.getPid(prefix + ".myself");
        this.timeToRevocered= Configuration.getInt(prefix+".timeToRecovered");
        this.dayInfected=0;
    }
    public int getDayInfected(){
        return this.dayInfected;
    }
    public int getTimeToRevocered(){
        return this.timeToRevocered;
    }
    public double getProbToInfect() {
        return this.probToInfect;
    }

    public void setProbToInfect(double probToInfect) {
        this.probToInfect = probToInfect;
    }

    public double getProbToBeInfected() {
        return this.probToBeInfected;
    }

    public void setProbToBeInfected(double probToBeInfected) {
        this.probToBeInfected = probToBeInfected;
    }
    
    public void setGoingOutFrequency(int frequency) {
    	this.goingOutFrequency = frequency;
    }
    
    public int getGoingOutFrequency() {
    	return this.goingOutFrequency;
    }
    
    public void setYearOld(int yearOld) {
    	this.yearOld = yearOld;
    }
    
    public int getYearOld() {
    	return this.yearOld;
    }
    

    public void setState(String state) {

        this.state = state;
    }
    
    public String getState() {
    	return this.state;
    }
    
    public void setIsVaccined(boolean isVaccined) {
    	this.isVaccined = isVaccined;
    }
    
    public boolean getIsVaccined() {
    	return this.isVaccined;
    }
    
    public ArrayList<Integer> getListVoisins() {
        return this.listVoisins;
    }

    public void setListVoisins(ArrayList<Integer> listVoisins) {
        this.listVoisins = listVoisins;
    }
    public void addVoisins(int numVoisins){
        this.listVoisins.add(numVoisins);
    }

    public Object clone() {
        VirusApp dolly = new VirusApp(this.prefix);
        return dolly;
    }

    public void setNodeId(int nodeId){
        this.nodeId=nodeId;
    }
    private Node getMyNode() {
        return Network.get(this.nodeId);
    }
    public int getMyID(){
        return this.nodeId;
    }
    public String toString() {
        return "Node "+ this.nodeId;
    }

    //Partie concernant l'envoi de message
    //envoi d'un message (l'envoi se fait via la couche applicative directement)
    public void send(VirusMessage msg, Node dest, double proba) {        
        if(Math.random() < proba){
        //methode permettant d'ajouter des evements à la file
            EDSimulator.add((long)0,msg,dest,this.mypid);
        }
    }

    // Partie concernant la reception du message
    public void processEvent( Node node, int pid, Object event ) {
        this.receive((VirusMessage)event);
    }
    private void receive(VirusMessage msg) {
        //System.out.println(this + ": Received " + msg.getContent());
    	if (!(this.isVaccined) && (msg.getProbToInfect() > this.getProbToBeInfected())){

            this.setState("Infected");
        }




    }

    public void setNewState(){

        double temp;
        if (state.equals("Infected")){
            if (dayInfected< timeToRevocered){
//                System.out.println(dayInfected);
                if(dayInfected<= timeToRevocered/2){
                     temp = dayInfected;
                }else {
                    temp = timeToRevocered-dayInfected;
                }
                double probToDeathWithAge = this.probToDeath(this.yearOld);


                double randomProbToDeath =((probToDeathWithAge))*((float)temp/((float)timeToRevocered/2.0));
                if (Math.random() < randomProbToDeath){

                    state ="Death";
                }
                dayInfected++;
            }else if  (dayInfected== timeToRevocered){
                state="Rejected";
            }

        }
    }

    public double function1Resistance(int yearOld){

        return 0.65*(Math.pow((float)yearOld/100,4)) + 0.35*(Math.pow((float)yearOld/100,2));
    }

    public double probToDeath(int yearOld){
        double test = (Math.pow((float)yearOld/100,4)) + 0.5*(Math.pow((float)yearOld/100,2));
        return  test;
    }


}
