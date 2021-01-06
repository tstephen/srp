package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.CarbonFactor;

public abstract class AbstractEmissionsService {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractEmissionsService.class);

    protected static final BigDecimal ONE_THOUSAND = new BigDecimal(
                "1000.000000");
    protected static final BigDecimal ONE_HUNDRED = new BigDecimal(
                "100.000000");
    protected static final BigDecimal m2km = new BigDecimal("1.60934");

    protected int divisionScale = 2;

    public Optional<BigDecimal> calculate(BigDecimal use, CarbonFactor cFactor) {
        try {
            if (cFactor.value().floatValue() < 0) {
                LOGGER.warn("No Carbon factor for {}", cFactor.name());
                throw new IllegalStateException();
            }
            return Optional.of(use
                    .multiply(cFactor.value())
                    .divide(Cruncher.ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate {}", cFactor.name());
            return Optional.empty();
        }
    }

}
