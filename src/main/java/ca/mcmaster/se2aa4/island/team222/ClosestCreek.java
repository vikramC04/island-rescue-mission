package ca.mcmaster.se2aa4.island.team222;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClosestCreek {

    private final Logger logger = LogManager.getLogger();
    private AllPOIS allPOIS;
    private POI closestCreek;

    public ClosestCreek(AllPOIS allPOIS) {
       this.allPOIS = allPOIS;
    }

    public POI findClosestCreek() {

        POI emergencySite = allPOIS.getEmergencySite();
        List<POI> creeks = allPOIS.getCreeks();

        if (emergencySite != null) {
            double x = emergencySite.getX();
            double y = emergencySite.getY();
            double minDistance = Double.MAX_VALUE; 
            closestCreek = null; 
        
            for (int i = 0; i < creeks.size(); i++) {
                POI creek = creeks.get(i);
                double creekX = creek.getX();
                double creekY = creek.getY();
                
                double distance = Math.sqrt(Math.pow(x - creekX, 2) + Math.pow(y - creekY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCreek = creek;
                }
            }

        }
        // return the creek at the start of the scan
        closestCreek = creeks.get(0);
        return closestCreek;
    }
}
