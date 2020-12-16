package digital.srp.sreport.services;

import java.math.BigDecimal;

public abstract class AbstractEmissionsService {

    protected static final BigDecimal ONE_THOUSAND = new BigDecimal(
                "1000.000000");
    protected static final BigDecimal ONE_HUNDRED = new BigDecimal(
                "100.000000");
    protected static final BigDecimal m2km = new BigDecimal("1.60934");
    protected int divisionScale = 2;

}
