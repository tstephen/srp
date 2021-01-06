package digital.srp.sreport.services;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;

@Service
public class WasteEmissionsService extends AbstractEmissionsService {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(WasteEmissionsService.class);

    //    public void calcReuseCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
    //        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    //    }

    public void calcOffensiveWasteCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcAltDisposalCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcFoodWasteCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcTextilesCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcOnsiteCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcWeeeCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcLandfillCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcIncinerationCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcOtherRecoveryCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcRecylingCo2e(BigDecimal waste, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(waste, cFactor).orElse(BigDecimal.ZERO));
    }
}
