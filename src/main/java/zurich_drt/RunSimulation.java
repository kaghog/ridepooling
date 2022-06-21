package zurich_drt;

import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.switzerland.SwitzerlandConfigurator;
import org.eqasim.switzerland.mode_choice.SwissModeChoiceModule;
import org.matsim.api.core.v01.Scenario;
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
                //.requireOptions("config-path") //
                .allowOptions() //
                .allowPrefixes("mode-parameter", "cost-parameter") //
                .build();


        String config_path = cmd.getOptionStrict("config-path");

        Config config = ConfigUtils.loadConfig(config_path, ZurichDrtConfigurator.getConfigGroups());
        ZurichDrtConfigurator.configure(config);
        cmd.applyConfiguration(config);

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        Scenario scenario = ScenarioUtils.createScenario(config);

        SwitzerlandConfigurator.configureScenario(scenario);
        ScenarioUtils.loadScenario(scenario);
        SwitzerlandConfigurator.adjustScenario(scenario);
        ZurichDrtConfigurator.adjustScenario(scenario);

        Controler controller = new Controler(scenario);
        SwitzerlandConfigurator.configureController(controller);
        controller.addOverridingModule(new SwissModeChoiceModule(cmd));
        controller.addOverridingModule(new EqasimModeChoiceModule());

        //add dvrp and drt module to controller
        ZurichDrtConfigurator.configureController(controller, cmd, config);


        controller.run();
    }
}
