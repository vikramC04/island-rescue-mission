package ca.mcmaster.se2aa4.island.team222;

public class POI {

    public enum POIS{
       CREEK,SITE;
    }
    private int xPosition;
    private int yPosition;
    private String id;
    private POIS type;
    


    public POI(int x, int y, String id, POIS type){
        this.xPosition = x;
        this.yPosition = y;
        this.id = id;

        if(type.equals(POIS.CREEK)){
            this.type = type;
        }
        else if(type.equals(POIS.SITE)){
            this.type = type;
        }
    }

    public int getX(){
        return xPosition;
    }

    public int getY(){
        return yPosition;
    }

    public String getID(){
        return id;
    }
    
    public POIS getPoiType(){
        return type;
    }
    
}
