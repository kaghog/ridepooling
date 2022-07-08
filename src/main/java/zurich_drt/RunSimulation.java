package zurich_drt;

import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.drt.routing.DrtRoute;
import org.matsim.contrib.drt.routing.DrtRouteFactory;
import org.matsim.contrib.drt.run.DrtConfigs;
import org.matsim.contrib.drt.run.MultiModeDrtConfigGroup;
import org.matsim.contrib.drt.run.MultiModeDrtModule;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.IOException;


public class RunSimulation {
    public static void main(String[] args) throws CommandLine.ConfigurationException, IOException {
        CommandLine cmd = new CommandLine.Builder(args) //
                .allowOptions("sample-size", "fleet-size","rebalancing") //
                .requireOptions("config-path") //
                .allowPrefixes("mode-parameter", "cost-parameter") //
                .build();

        Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"),
                new SwissRailRaptorConfigGroup());

        cmd.applyConfiguration(config);

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        Scenario scenario = ScenarioUtils.createScenario(config);

        ScenarioUtils.loadScenario(scenario);

        // Configure DVRP and DRT module
        DvrpConfigGroup dvrpConfig = new DvrpConfigGroup();
        config.addModule(dvrpConfig);

        MultiModeDrtConfigGroup multiModeDrtConfig = new MultiModeDrtConfigGroup();
        config.addModule(multiModeDrtConfig);

        //set config parameters
        // DrtConfigGroup drtConfig = new  DrtConfigGroup()
        //DrtConfigGroup drtConfig = DrtConfigGroup.getSingleModeDrtConfig(config);

        //drtConfig.setVehiclesFile(vehicleFile);

        //---commented 'cuz now set in config
        /*drtConfig.setMode("drt");
        drtConfig.setOperationalScheme(DrtConfigGroup.OperationalScheme.door2door);
        drtConfig.setStopDuration(60.0);
        drtConfig.setMaxWaitTime(600.0);
        drtConfig.setMaxTravelTimeAlpha(1.5);
        drtConfig.setMaxTravelTimeBeta(240.0);
        drtConfig.setChangeStartLinkToLastLinkInSchedule(false);
        drtConfig.setNumberOfThreads(1);
        drtConfig.setRejectRequestIfMaxWaitOrTravelTimeViolated(false);


        DrtInsertionSearchParams searchParams = new SelectiveInsertionSearchParams();
        drtConfig.addDrtInsertionSearchParams(searchParams);


        boolean doRebalancing = false;
        if (cmd.hasOption("rebalancing")) {
            doRebalancing = Boolean.parseBoolean(cmd.getOption("rebalancing").get());
        }
        if(doRebalancing) {

            MinCostFlowRebalancingStrategyParams minCostFlowRebalancingStrategyParams = new MinCostFlowRebalancingStrategyParams();
            minCostFlowRebalancingStrategyParams.setTargetAlpha(0.8);
            minCostFlowRebalancingStrategyParams.setTargetBeta(0.3);

            drtConfig.addParameterSet(new DrtZonalSystemParams());
            drtConfig.getZonalSystemParams().get().setZonesGeneration(DrtZonalSystemParams.ZoneGeneration.GridFromNetwork);
            drtConfig.getZonalSystemParams().get().setCellSize(5000.0);


        //drtConfig.getRebalancingParams().get().addParameterSet(minCostFlowRebalancingStrategyParams);

        RebalancingParams rebalancingParams = new RebalancingParams();
        rebalancingParams.addParameterSet(minCostFlowRebalancingStrategyParams);
        //currently default interval for rebalancing of 30 minutes is being used, bischoff says 15 - 90 minutes works same with the above alpha and beta values

        drtConfig.addParameterSet(rebalancingParams);
        }

        multiModeDrtConfig.addDrtConfig(drtConfig);*/
        DrtConfigs.adjustMultiModeDrtConfig(multiModeDrtConfig, config.planCalcScore(), config.plansCalcRoute());

        // Additional config requirements for DRT ---commented 'cuz now set in config
        /*config.qsim().setStartTime(0.0);
        config.qsim().setSimStarttimeInterpretation(QSimConfigGroup.StarttimeInterpretation.onlyUseStarttime);
        config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);*/


        { // Add DRT route factory
            scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DrtRoute.class,
                    new DrtRouteFactory());
        }

        Controler controller = new Controler(scenario);
        controller.addOverridingModule(new DvrpModule());
        controller.addOverridingModule(new MultiModeDrtModule());
        controller.configureQSimComponents(components -> {
            DvrpQSimComponents.activateAllModes(multiModeDrtConfig).configure(components);
        });



        controller.run();

    }
}
