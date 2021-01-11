package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.model.CarbonFactor;

public class RoadEmissionsServiceTest {

    private static List<CarbonFactor> cfactors;
    private static final String PERIOD = "2020-21";
    private RoadEmissionsService svc;
    private CarbonFactor carMileageFactor;
    private CarbonFactor carMileageWttFactor;
    private CarbonFactor dieselFactor;
    private CarbonFactor dieselWttFactor;
    private CarbonFactor petrolFactor;
    private CarbonFactor petrolWttFactor;

    @Before
    public void setUp() throws IOException {
        svc = new RoadEmissionsService();
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        carMileageFactor = cfactors.stream()
                .filter(e -> "CAR_AVERAGE_SIZE".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "CAR_AVERAGE_SIZE"));
        carMileageWttFactor = cfactors.stream()
                .filter(e -> "CAR_WTT_AVERAGE_SIZE".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "CAR_WTT_AVERAGE_SIZE"));
        dieselFactor = cfactors.stream()
                .filter(e -> "DIESEL_DIRECT".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "DIESEL_DIRECT"));
        dieselWttFactor = cfactors.stream()
                .filter(e -> "DIESEL_WTT".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "DIESEL_WTT"));
        petrolFactor = cfactors.stream()
                .filter(e -> "PETROL_DIRECT".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "PETROL_DIRECT"));
        petrolWttFactor = cfactors.stream()
                .filter(e -> "PETROL_WTT".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "PETROL_WTT"));
    }

    @Test
    public void testCalcByDiesel() {
        BigDecimal dieselUsed = new BigDecimal("1000.00");
        Optional<BigDecimal> emissions = svc.calculate(dieselFactor, 
                Optional.of(dieselUsed), carMileageFactor,
                Optional.empty());
        Optional<BigDecimal> wttEmissions = svc.calculate(dieselWttFactor, 
                Optional.of(dieselUsed), carMileageWttFactor,
                Optional.empty());
        assertEquals(new BigDecimal("3.16"), emissions.get().add(wttEmissions.get()));
    }

    @Test
    public void testCalcByDieselMileage() {
        BigDecimal dieselMileage = new BigDecimal("6000.00");
        Optional<BigDecimal> emissions = svc.calculate(dieselFactor, 
                Optional.empty(), carMileageFactor,
                Optional.of(dieselMileage));
        Optional<BigDecimal> wttEmissions = svc.calculate(dieselWttFactor, 
                Optional.empty(), carMileageWttFactor,
                Optional.of(dieselMileage));
        assertEquals(new BigDecimal("2.09"), emissions.get().add(wttEmissions.get()));
    }

    @Test
    public void testCalcByPetrol() {
        BigDecimal petrolUsed = new BigDecimal("1000.00");
        Optional<BigDecimal> emissions = svc.calculate(petrolFactor, 
                Optional.of(petrolUsed), carMileageFactor,
                Optional.empty());
        Optional<BigDecimal> wttEmissions = svc.calculate(petrolWttFactor, 
                Optional.of(petrolUsed), carMileageWttFactor,
                Optional.empty());
        assertEquals(new BigDecimal("2.76"), emissions.get().add(wttEmissions.get()));
    }

    @Test
    public void testCalcByPetrolMileage() {
        BigDecimal petrolMileage = new BigDecimal("6000.00");
        Optional<BigDecimal> emissions = svc.calculate(petrolFactor, 
                Optional.empty(), carMileageFactor,
                Optional.of(petrolMileage));
        Optional<BigDecimal> wttEmissions = svc.calculate(petrolFactor, 
                Optional.empty(), carMileageWttFactor,
                Optional.of(petrolMileage));
        assertEquals(new BigDecimal("2.09"), emissions.get().add(wttEmissions.get()));
    }
}
