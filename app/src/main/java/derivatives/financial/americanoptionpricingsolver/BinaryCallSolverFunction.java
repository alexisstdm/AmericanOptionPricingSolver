package derivatives.financial.americanoptionpricingsolver;

/**
 * Created by casa on 5/12/14.
 */

import java.util.HashMap;

import american.options.BinaryCallOption;
import american.options.BinomialModel;
import mathematical.solver.Function;

public class BinaryCallSolverFunction implements Function{
    @Override
    public Double evaluate(HashMap<String, Double> parameters){
        BinomialModel myBinomialModel = new BinomialModel(parameters.get("SPOT_LEVEL"),
                                                          parameters.get("RATE"),
                                                          parameters.get("DIVIDEND"),
                                                          parameters.get("VOLATILITY"),
                                                          10, parameters.get("MATURITY"));
        BinaryCallOption myBinaryCallOption = new BinaryCallOption(myBinomialModel,
                parameters.get("STRIKE"));

        return parameters.get("PRICE") - myBinaryCallOption.evaluate();
    }
}
