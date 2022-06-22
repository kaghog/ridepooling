package zurich_drt.wait_time;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

/**
 * @author kaghog created on 22.06.2022
 * @project ridepooling
 */
public class DrtTripData {
    public Id<Link> startLinkId;
    public double startTime;
    public double pickUpTime;
    public double waitTime;
    public boolean rejected = false;
}
