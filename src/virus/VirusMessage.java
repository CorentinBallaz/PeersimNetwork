package virus;

import peersim.edsim.*;

public class VirusMessage{

    public final static int HELLOWORLD = 0;

    private long contaminationDate;
    private int type;
    private String content;
    private int contaminationDay;


    VirusMessage(int type, String content, int contaminationDay) {
        this.type = type;
        this.content = content;
        this.contaminationDay=contaminationDay;
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
}
