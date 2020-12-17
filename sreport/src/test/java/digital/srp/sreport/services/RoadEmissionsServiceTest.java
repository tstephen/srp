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
    private CarbonFactor carTotal;
    private CarbonFactor dieselFactor;
    private CarbonFactor petrolFactor;

    @Before
    public void setUp() throws IOException {
        svc = new RoadEmissionsService();
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        carTotal = cfactors.stream()
                .filter(e -> "CAR_TOTAL".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "CAR_TOTAL"));
        dieselFactor = cfactors.stream()
                .filter(e -> "DIESEL_TOTAL".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "DIESEL_TOTAL"));
        petrolFactor = cfactors.stream()
                .filter(e -> "PETROL_TOTAL".equals(e.getName()) && PERIOD.equals(e.getApplicablePeriod())).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, "PETROLTOTAL"));
    }

    @Test
    public void testCalcByDiesel() {
        BigDecimal dieselUsed = new BigDecimal("1000.00");
        Optional<BigDecimal> emissions = svc.calculate(dieselFactor, 
                Optional.of(dieselUsed), carTotal,
                Optional.empty());
        assertEquals(new BigDecimal("3.16"), emissions.get());
    }

    @Test
    public void testCalcByDieselMileage() {
        BigDecimal dieselMileage = new BigDecimal("6000.00");
        Optional<BigDecimal> emissions = svc.calculate(dieselFactor, 
                Optional.empty(), carTotal,
                Optional.of(dieselMileage));
        assertEquals(new BigDecimal("2.08"), emissions.get());
    }

    @Test
    public void testCalcByPetrol() {
        BigDecimal petrolUsed = new BigDecimal("1000.00");
        Optional<BigDecimal> emissions = svc.calculate(petrolFactor, 
                Optional.of(petrolUsed), carTotal,
                Optional.empty());
        assertEquals(new BigDecimal("2.76"), emissions.get());
    }

    @Test
    public void testCalcByPetrolMileage() {
        BigDecimal petrolMileage = new BigDecimal("6000.00");
        Optional<BigDecimal> emissions = svc.calculate(petrolFactor, 
                Optional.empty(), carTotal,
                Optional.of(petrolMileage));
        assertEquals(new BigDecimal("2.08"), emissions.get());
    }
}
