package zurich_drt.mode_choice.parameters;


import org.eqasim.switzerland.mode_choice.parameters.SwissCostParameters;

public class ZurichDrtCostParameters extends SwissCostParameters {
    public double drtCost_CHF;

    public static ZurichDrtCostParameters buildDefault() {

        ZurichDrtCostParameters parameters = new ZurichDrtCostParameters();

        parameters.drtCost_CHF = 0;

        return parameters;
    }
}
