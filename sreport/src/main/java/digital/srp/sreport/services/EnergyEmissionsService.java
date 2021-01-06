package digital.srp.sreport.services;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;

@Service
public class EnergyEmissionsService extends AbstractEmissionsService {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(EnergyEmissionsService.class);

    public void calcGridElecCo2e(BigDecimal gridElecUse, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(gridElecUse, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcGridElecWttCo2e(BigDecimal gridElecUse, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(gridElecUse, cFactor).orElse(BigDecimal.ZERO));
    }
}
