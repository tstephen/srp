package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.CarbonFactor;

@Service
public class WasteEmissionsService extends AbstractEmissionsService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WasteEmissionsService.class);

    public Optional<BigDecimal> calculate(BigDecimal waste, CarbonFactor cFactor) {
        try {
            return Optional.of(waste
                    .multiply(cFactor.value())
                    .divide(Cruncher.ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate {}", cFactor.name());
            return Optional.empty();
        }
    }
}
