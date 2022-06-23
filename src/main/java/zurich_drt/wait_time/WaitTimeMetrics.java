package zurich_drt.wait_time;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WaitTimeMetrics {
    private static final Map<Integer, WaitTimeTracker> dailyWaitTimes = new HashMap<>();

    private static Map<String, Set<WaitTimeData>> createZonalStats(WaitTimeTracker waitTimes, WayneCountyDrtZonalSystem zones, Map<String, Set<WaitTimeData>> zonalWaitTimes) {
        Set<DrtTripData> drtTrips = waitTimes.getDrtTrips();

        for (DrtTripData drtTrip : drtTrips) {

            String zone = zones.getZoneForLinkId(drtTrip.startLinkId);
            if (zonalWaitTimes.containsKey(zone)) {
                WaitTimeData wtd = new WaitTimeData();
                wtd.startTime = drtTrip.startTime;
                wtd.waitTime = drtTrip.waitTime;

                zonalWaitTimes.get(zone).add(wtd);
            } else {

                Set<WaitTimeData> newWaitingTimes = new HashSet<>();
                WaitTimeData wtd = new WaitTimeData();
                wtd.startTime = drtTrip.startTime;
                wtd.waitTime = drtTrip.waitTime;
                newWaitingTimes.add(wtd);
                zonalWaitTimes.put(zone, newWaitingTimes);
            }
        }

        return zonalWaitTimes;

    }

    public static Map<String, double[]> calculateZonalAverageWaitTimes(WaitTimeTracker waitTimes, WayneCountyDrtZonalSystem zones) {
        Map<String, Set<WaitTimeData>> zonalWaitTimes = createZonalStats(waitTimes, zones, new HashMap<>());
        Map<String, double[]> avgZonalWaitTimes = new HashMap<>();
        int timeBins = 100; //toDo justify the choice of this time bin now it is hourly and set at 100 to capture multiday trips

        for (String zone : zonalWaitTimes.keySet()) {
            double[] average = new double[timeBins];
            int[] observations = new int[timeBins];

            for (WaitTimeData d : zonalWaitTimes.get(zone)) {

                int index = ((int) (d.startTime / 3600.0));
                average[index] += d.waitTime;
                observations[index]++;

            }
            for (int i = 0; i < average.length; i++) {
                if (observations[i] > 0)
                    average[i] = average[i] / observations[i];
            }

            avgZonalWaitTimes.put(zone, average);
        }
        return avgZonalWaitTimes;
    }

    public static Map<String, double[]> calculateMovingZonalAverageWaitTimes(WaitTimeTracker waitTimes, WayneCountyDrtZonalSystem zones, int iteration, int movingWindow) {
        //add wait times and their iterations
        //should we use a map or an array?
        Set<DrtTripData> newDrtTrips = waitTimes.getDrtTrips();

        //define starting window  //iteration starts from zero so subtract 1 from movingWindow
        int start = 0;
        if (iteration > movingWindow - 1) {
            start = iteration - movingWindow - 1;
        }

        //add past wait times from the starting window
        if(iteration != 0) {
            for (int i = start; i<iteration; i++){
                newDrtTrips.addAll(dailyWaitTimes.get(i).getDrtTrips());
            }
        }

        //update with current wait times of the day
        dailyWaitTimes.put(iteration, waitTimes);

        //get all the wait times and combine per iteration
        WaitTimeTracker updatedWaitTimes = new WaitTimeTracker();
        updatedWaitTimes.setDrtTrips(newDrtTrips);

        //Find total averages for the time period
        return calculateZonalAverageWaitTimes(updatedWaitTimes, zones);
    }
}
