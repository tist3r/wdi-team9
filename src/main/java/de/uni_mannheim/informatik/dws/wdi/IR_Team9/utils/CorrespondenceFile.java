package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

public class CorrespondenceFile {
    public String id1;
    public String id2;
    public double sim1 = 0;
    public  double sim2 = 0;
    public  double sim3 = 0;
    public  String name1;
    public  String name2;
    
    public void setSimAtID(Integer simID, Double sim){
        switch (simID){
            case 1: this.sim1 = sim; break;
            case 2: this.sim2 = sim; break;
            case 3: this.sim3 = sim; break;
        }
    }
}
