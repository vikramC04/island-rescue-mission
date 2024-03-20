package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AllPOIS{

    private List<POI> creekLocations = new ArrayList<POI>();
    private POI emergencySite;

    public AllPOIS(List<POI> creekLocations){
        this.creekLocations = creekLocations;
    }

    public void addPoi(POI poi, POIType type){
        if(type.equals(POIType.CREEK)){
            this.creekLocations.add(poi);
        }
        else if(type.equals(POIType.SITE)){
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