/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

public class InterventionTest {

    protected static final String TENANT_ID = "sdu";

    @Test
    @Ignore
    public void testBoilerPlantOptimisation() {

        Intervention intervention = new Intervention(
                getBoilerPlantOptimisation(), getAcuteHospitalOrgType());
        intervention.setShareOfTotal(53.48f);

        log(intervention);

        // assertEquals(
        // 128000,
        // SignificantFiguresFormat.round(
        // intervention.getCashOutflowUpFront()).intValue());
        // int targetYear = intervention.getInterventionType()
        // .getTargetYearIndex();
        // assertEquals(
        // 29700,
        // SignificantFiguresFormat.round(
        // intervention.getAnnualCashInflows(targetYear))
        // .intValue());
        assertEquals(219l, intervention.getTonnesCo2eSavedTargetYear()
                .longValue() * .2f, 0.1);
    }

    private OrganisationType getAcuteHospitalOrgType() {
        OrganisationType orgType = new OrganisationType();
        orgType.setName("Acute Trust");
        orgType.setTenantId(TENANT_ID);
        return orgType;
    }

    private InterventionType getBoilerPlantOptimisation() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("BoilerPlantOptimisation");
        interventionType.setLifetime((short) 17);
        interventionType.setUptake((short) 20);
        assertEquals(0.20, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.04f);
        assertEquals(0.0004, interventionType.getScaleFactor().doubleValue(),
                0.01);

//        interventionType.setAnnualGasSaved(new BigDecimal("196114"));
//        interventionType.setGasCIntensity(gasCIntensity);
        interventionType.setCashOutflowsUpFront(new BigDecimal(24000));
        interventionType.setAnnualCashInflows(new BigDecimal("5560"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(41f));
        return interventionType;
    }

    private void log(Intervention intervention) {
        System.out.println(String.format("  total cash out: %1$d", intervention
                .getInterventionType().getTotalCashOutflows().longValue()));
        System.out.println("  Mean total outflows: "
                + intervention.getInterventionType().getTotalCashOutflows());
        System.out.println(String.format("  cash out target year: %1$s",
                intervention.getInterventionType()
                        .getAnnualCashOutflowsNationalTargetYear()
                        .toPlainString()));
        System.out.println(String.format("  total NPV: %1$d", intervention
                .getTotalNpv().longValue()));
        System.out.println("  Mean cash inflows: "
                + intervention.getInterventionType().getMeanCashInflows());
        System.out.println(String.format("  cash in target year: %1$s",
                intervention.getInterventionType()
                        .getAnnualCashInflowsNationalTargetYear()
                        .toPlainString()));

        System.out.println(String.format("  tonnes saved target year: %1$s",
                intervention.getInterventionType()
                        .getTonnesCo2eSavedTargetYear().toPlainString()));
        System.out.println(String.format("  MAC: %1$s", intervention
                .getInterventionType().getCostPerTonneCo2e().toPlainString()));

    }

}
