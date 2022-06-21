package zurich_drt.mode_choice;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.ParameterDefinition;
import org.eqasim.core.simulation.mode_choice.cost.CostModel;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.switzerland.mode_choice.parameters.SwissCostParameters;
import org.eqasim.switzerland.mode_choice.parameters.SwissModeParameters;
import org.matsim.core.config.CommandLine;
import zurich_drt.mode_choice.cost.DrtCostModel;
import zurich_drt.mode_choice.parameters.ZurichDrtCostParameters;
import zurich_drt.mode_choice.parameters.ZurichDrtModeParameters;
import zurich_drt.mode_choice.utilities.DrtPredictor;
import zurich_drt.mode_choice.utilities.DrtUtilityEstimator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ZurichDrtModeChoiceModule extends AbstractEqasimExtension {

    private final CommandLine commandLine;

    public ZurichDrtModeChoiceModule(CommandLine commandLine) {
        this.commandLine = commandLine;
    }


    @Override
    protected void installEqasimExtension() {
        // Configure mode availability
        bindModeAvailability(ZurichDrtModeAvailability.NAME).to(ZurichDrtModeAvailability.class);

        // Configure choice alternative for DRT
        bindUtilityEstimator("drt").to(DrtUtilityEstimator.class);
        bindCostModel("drt").to(DrtCostModel.class);
        bind(DrtPredictor.class);


        // Override parameter bindings
        bind(ModeParameters.class).to(ZurichDrtModeParameters.class);
        bind(SwissModeParameters.class).to(ZurichDrtModeParameters.class);
        bind(SwissCostParameters.class).to(ZurichDrtCostParameters.class);
    }

    @Provides
    @Singleton
    public ZurichDrtModeParameters provideSwissIntermodalModeParameters(EqasimConfigGroup config)
            throws IOException, CommandLine.ConfigurationException {
        ZurichDrtModeParameters parameters = ZurichDrtModeParameters.buildASTRA2016();

        if (config.getModeParametersPath() != null) {
            ParameterDefinition.applyFile(new File(config.getModeParametersPath()), parameters);
        }

        ParameterDefinition.applyCommandLine("mode-parameter", commandLine, parameters);
        return parameters;
    }

    @Provides
    @Singleton
    public ZurichDrtCostParameters provideCostParameters(EqasimConfigGroup config) {
        ZurichDrtCostParameters parameters = ZurichDrtCostParameters.buildDefault();

        if (config.getCostParametersPath() != null) {
            ParameterDefinition.applyFile(new File(config.getCostParametersPath()), parameters);
        }

        ParameterDefinition.applyCommandLine("cost-parameter", commandLine, parameters);
        return parameters;
    }

    @Provides
    @Singleton
    public DrtCostModel provideDrtCostModel(ZurichDrtCostParameters parameters) {
        return new DrtCostModel(parameters);
    }

    @Provides
    @Named("drt")
    public CostModel provideCarCostModel(Map<String, Provider<CostModel>> factory, EqasimConfigGroup config) {
        return getCostModel(factory, config, "drt");
    }
}
