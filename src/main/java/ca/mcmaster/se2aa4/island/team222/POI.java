package ca.mcmaster.se2aa4.island.team222;

public class POI {
    
    private Coordinate coordinate;
    private String id;
    private POIType type;

    public POI(Coordinate coordinate, String id, POIType type){
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        this.id = id;

        if(type.equals(POIType.CREEKS)){
            this.type = type;
        }
        else if(type.equals(POIType.SITES)){
            this.type = type;
        }
    }

    public int getX(){
        return coordinate.getX();
    }

    public int getY(){
        return coordinate.getY();
    }

    public String getID(){
        return id;
    }
    
    public POIType getPoiType(){
        return this.type;
    }
    
}
