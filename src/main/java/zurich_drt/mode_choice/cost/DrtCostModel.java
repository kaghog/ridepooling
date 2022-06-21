package zurich_drt.mode_choice.cost;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.cost.AbstractCostModel;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import zurich_drt.mode_choice.parameters.ZurichDrtCostParameters;

public class DrtCostModel extends AbstractCostModel {
    private final ZurichDrtCostParameters parameters;

    public DrtCostModel(ZurichDrtCostParameters parameters) {
        super("drt");
        this.parameters = parameters;
    }

    @Override
    public double calculateCost_MU(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        double tripDistance_km = getInVehicleDistance_km(elements);
        return parameters.drtCost_CHF; //Todo add other pricing schemes
    }
}
