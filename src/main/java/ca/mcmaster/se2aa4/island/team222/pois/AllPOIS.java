package ca.mcmaster.se2aa4.island.team222.pois;

import java.util.ArrayList;
import java.util.List;

public class AllPOIS{

    private List<POI> creekLocations = new ArrayList<>();
    private POI emergencySite;

    public AllPOIS(List<POI> creekLocations){
        this.creekLocations = creekLocations;
    }

    public void addPoi(POI poi, POIType type){
        if(type.equals(POIType.CREEKS)){
            this.creekLocations.add(poi);
        }
        else if(type.equals(POIType.SITES)){
            this.emergencySite = poi;
        }
       
    }
    public POI getEmergencySite(){
        return this.emergencySite;
    }

    public List<POI> getCreeks(){
        return this.creekLocations;
    }

    


} 