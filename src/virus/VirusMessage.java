package virus;

import peersim.edsim.*;

public class VirusMessage{

    public final static int HELLOWORLD = 0;

    private long contaminationDate;
    private int type;
    private String content;
    private int contaminationDay;
    private double probToInfect;


    VirusMessage(int type, String content, int contaminationDay, double probToInfect) {
        this.type = type;
        this.content = content;
        this.contaminationDay=contaminationDay;
        this.probToInfect = probToInfect;
    }

    public String getContent() {
        return this.content;
    }

    public int getType() {
        return this.type;
    }

    public void setContaminationDate(long time){
            this.contaminationDate=time;
    }
    public double getProbToInfect(){
        return this.probToInfect;
    }
}
