package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.CarbonFactor;

@Service
public class RoadEmissionsService extends AbstractEmissionsService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RoadEmissionsService.class);

    public Optional<BigDecimal> calculate(CarbonFactor fuelFactor,
            Optional<BigDecimal> fuelUsed, CarbonFactor mileageTotalFactor,
            Optional<BigDecimal> mileage) {
        LOGGER.info("calculate({},{},{},{})", fuelFactor, fuelUsed,
                mileageTotalFactor, mileage);
        if (fuelUsed.isPresent()) {
            return Optional.of(fuelUsed.get().multiply(fuelFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } else if (mileage.isPresent()) {
            return Optional.of(mileage.get()
                    .multiply(mileageTotalFactor.value()).multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } else {
            return Optional.empty();
        }
    }
}
