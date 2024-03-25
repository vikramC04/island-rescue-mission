package ca.mcmaster.se2aa4.island.team222;

import java.util.List;

public class ClosestCreek {

    private AllPOIS allPOIS;

    public ClosestCreek(AllPOIS allPOIS) {
       this.allPOIS = allPOIS;
    }

    public POI findClosestCreek() {

        POI emergencySite = allPOIS.getEmergencySite();
        List<POI> creeks = allPOIS.getCreeks();
        POI creekClosest = null;

        if (emergencySite != null) {
            double x = emergencySite.getX();
            double y = emergencySite.getY();
            double minDistance = Double.MAX_VALUE; 
        
            for (int i = 0; i < creeks.size(); i++) {
                POI creek = creeks.get(i);
                double creekX = creek.getX();
                double creekY = creek.getY();
                
                double distance = Math.sqrt(Math.pow(x - creekX, 2) + Math.pow(y - creekY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    creekClosest = creek;
                }
            }

        }
        else{
            // return the creek at the start of the scan
            creekClosest = creeks.get(0);
        }
        
        return creekClosest;
    }
}
