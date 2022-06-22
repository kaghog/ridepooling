package zurich_drt.wait_time;

import com.google.common.base.Preconditions;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.drt.analysis.zonal.DrtGridUtils;
import org.matsim.contrib.drt.analysis.zonal.DrtZonalSystem;
import org.matsim.contrib.drt.analysis.zonal.DrtZonalSystemParams;
import org.matsim.contrib.drt.run.DrtConfigGroup;

import java.util.Map;

/**
 * @author kaghog created on 22.06.2022
 * @project ridepooling
 */
public class ZurichDrtWaitTimeModule extends AbstractEqasimExtension {

    private final DrtConfigGroup drtConfig;
    private final Scenario scenario;

    public ZurichDrtWaitTimeModule(DrtConfigGroup drtConfig, Scenario scenario) {
        this.drtConfig = drtConfig;
        this.scenario = scenario;
    }

    @Override
    protected void installEqasimExtension() {

        addEventHandlerBinding().to(WaitTimeTracker.class);

        bind(WaitTimeTracker.class).asEagerSingleton();
        addControlerListenerBinding().to(DrtWaitTimes.class);
        bind(DrtWaitTimes.class).asEagerSingleton();


        DrtZonalSystemParams params = this.drtConfig.getZonalSystemParams().orElseThrow();
        Preconditions.checkNotNull(params.getCellSize());
        Network network = scenario.getNetwork();
        Map<String, PreparedGeometry> gridZones = DrtGridUtils.createGridFromNetwork(network, params.getCellSize());
        DrtZonalSystem drtZonalSystem =  DrtZonalSystem.createFromPreparedGeometries(network, gridZones);
        bind(DrtZonalSystem.class).toInstance(drtZonalSystem);
        bind(DrtZonalSystem.class).asEagerSingleton();
    }
}
