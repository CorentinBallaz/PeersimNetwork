package virus;

import peersim.edsim.*;

public class VirusMessage{

    public final static int HELLOWORLD = 0;

    private int type;
    private String content;

    VirusMessage(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public int getType() {
        return this.type;
    }

}
