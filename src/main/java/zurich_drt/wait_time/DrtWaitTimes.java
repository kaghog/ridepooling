package zurich_drt.wait_time;


import com.google.inject.Inject;
import org.matsim.contrib.drt.analysis.zonal.DrtZonalSystem;
import org.matsim.core.config.Config;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;

import java.util.HashMap;
import java.util.Map;

public class DrtWaitTimes implements IterationEndsListener {

    private final WaitTimeTracker trackedWaitTimes;
    private Map<String, double[]> avgWaitTimes;

    @Inject
    public DrtWaitTimes(WaitTimeTracker trackedWaitTimes, DrtZonalSystem zones, Config config){

        this.trackedWaitTimes = trackedWaitTimes;
        this.avgWaitTimes = new HashMap<>();
    }

    @Override
    public void notifyIterationEnds(IterationEndsEvent event) {
        //generate wait times for use.
        this.avgWaitTimes = WaitTimeMetrics.calculateZonalAverageWaitTimes(trackedWaitTimes);

        //if...then configuration for what method to use
        //this.avgWaitTimes = WaitTimeMetrics.calculateSuccessiveZonalAverageWaitTimes(event.getIteration(), trackedWaitTimes);
    }

    public Map<String, double[]> getAvgWaitTimes() {

        return avgWaitTimes;
    }
}
