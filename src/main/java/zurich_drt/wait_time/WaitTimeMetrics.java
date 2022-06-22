package zurich_drt.wait_time;

import org.matsim.contrib.drt.analysis.zonal.DrtZonalSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author kaghog created on 22.06.2022
 * @project ridepooling
 */
public class WaitTimeMetrics {
    private static DrtZonalSystem zones;
    private static Map<String, Set<WaitTimeData>> successiveZonalWaitTimes;

    private static Map<String, Set<WaitTimeData>> createZonalStats(WaitTimeTracker waitTimes, Map<String, Set<WaitTimeData>> zonalWaitTimes) {
        Set<DrtTripData> drtTrips = waitTimes.getDrtTrips();

        for (DrtTripData drtTrip : drtTrips) {

            String zone = zones.getZoneForLinkId(drtTrip.startLinkId).getId();
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

    public static Map<String, double[]> calculateZonalAverageWaitTimes(WaitTimeTracker waitTimes) {
        Map<String, Set<WaitTimeData>> zonalWaitTimes = createZonalStats(waitTimes, new HashMap<>());
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

    public static Map<String, double[]> calculateSuccessiveZonalAverageWaitTimes(int iteration, WaitTimeTracker waitTimes) {
        Map<String, Set<WaitTimeData>> zonalWaitTimes = createZonalStats(waitTimes, new HashMap<>());

        return null;
    }
}
