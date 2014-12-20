package derivatives.financial.americanoptionpricingsolver;

import java.util.HashMap;

import american.options.AmericanPutOption;
import american.options.BinomialModel;
import mathematical.solver.Function;

/**
 * Created by casa on 5/12/14.
 */
public class AmericanPutSolverFunction implements Function {
    @Override
    public Double evaluate(HashMap<String, Double> parameters){
        BinomialModel myBinomialModel = new BinomialModel(parameters.get("SPOT_LEVEL"),
                                                          parameters.get("RATE"),
                                                          parameters.get("DIVIDEND"),
                                                          parameters.get("VOLATILITY"),
                                                          10, parameters.get("MATURITY"));
        AmericanPutOption myAmericanPutOption = new AmericanPutOption(myBinomialModel,
                parameters.get("STRIKE"));

        return parameters.get("PRICE") - myAmericanPutOption.evaluate();
    }
}
