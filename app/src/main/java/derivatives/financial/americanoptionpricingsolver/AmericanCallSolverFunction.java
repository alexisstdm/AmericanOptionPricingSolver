package derivatives.financial.americanoptionpricingsolver;

import american.options.AmericanCallOption;
import american.options.BinomialModel;
import mathematical.solver.Function;

import java.util.HashMap;

/**
 * Created by casa on 5/12/14.
 */
public class AmericanCallSolverFunction implements Function{
    @Override
    public Double evaluate(HashMap<String, Double> parameters){
        BinomialModel myBinomialModel = new BinomialModel(parameters.get("SPOT_LEVEL"),
                                                          parameters.get("RATE"),
                                                          parameters.get("DIVIDEND"),
                                                          parameters.get("VOLATILITY"),
                                                          50, parameters.get("MATURITY"));
        AmericanCallOption myAmericanCallOption = new AmericanCallOption(myBinomialModel,
                                                                         parameters.get("STRIKE"));

        return myAmericanCallOption.evaluate() - parameters.get("PRICE");
    }
}
