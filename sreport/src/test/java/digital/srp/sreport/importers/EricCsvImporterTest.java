package digital.srp.sreport.importers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.Eric1314;
import digital.srp.sreport.model.returns.Eric1415;
import digital.srp.sreport.model.returns.Eric1516;
import digital.srp.sreport.model.returns.Eric1617;

public class EricCsvImporterTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EricCsvImporterTest.class);

    @Test
    public void test1617Import() {
        try {
            String content = readFile(Eric1617.DATA_FILE);
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1617.HEADERS, Eric1617.PERIOD);
            LOGGER.debug(" found {} returns for {}", returns.size(), Eric1617.PERIOD);
            assertEquals(236, returns.size());

            // assert expected values of first return
            // R1A$WORCESTERSHIRE HEALTH AND CARE NHS TRUST$MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION$COMMUNITY$0$0$1$5$1$0$5$1$28$2$46$0$Yes$No$Yes$Yes$2. Target included but not on track to be met$3. Assessed but not approved by the organisation's board$3. Action plan produced but not approved by the organisations board$0$1,526,422$138,878$0$1,223,758$1,156,350$437,000$306,000$0$0$0$5$15$1$0$81.00$5$8$13$0$0$0
            assertEquals(Eric1617.NAME+"-R1A", returns.get(0).name());
            assertEquals(134, returns.get(0).answers().size());

            // Trust data tab
            assertEquals("R1A", returns.get(0).answer(Eric1617.PERIOD, Q.ORG_CODE).orElseThrow(() -> new IllegalArgumentException("No org code found")).response());
            assertEquals(Eric1617.PERIOD, returns.get(0).applicablePeriod());
            assertEquals("Published", returns.get(0).status());
            assertEquals("WORCESTERSHIRE HEALTH AND CARE NHS TRUST", returns.get(0).answer(Eric1617.PERIOD, Q.ORG_NAME).orElseThrow(() -> new IllegalArgumentException("No org name found")).response());
            assertEquals("MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION", returns.get(0).answer(Eric1617.PERIOD, Q.COMMISSIONING_REGION).orElseThrow(() -> new IllegalArgumentException("No commissioning region found")).response());
            assertEquals("COMMUNITY", returns.get(0).answer(Eric1617.PERIOD, Q.ORG_TYPE).orElseThrow(() -> new IllegalArgumentException("No org type found")).response());
            assertEquals("0", returns.get(0).answer(Eric1617.PERIOD, Q.NO_ACUTE_SITES).orElseThrow(() -> new IllegalArgumentException("No acute sites answer found")).response());
            assertEquals("0", returns.get(0).answer(Eric1617.PERIOD, Q.NO_SPECIALIST_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("1", returns.get(0).answer(Eric1617.PERIOD, Q.NO_MIXED_SITES).orElseThrow(() -> new IllegalArgumentException("No mixed service sites answer found")).response());
            assertEquals("5", returns.get(0).answer(Eric1617.PERIOD, Q.NO_MENTAL_HEALTH_SITES).orElseThrow(() -> new IllegalArgumentException("No mental health sites answer found")).response());
            assertEquals("Yes", returns.get(0).answer(Eric1617.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No estate development strategy answer found")).response());

            // Aggregated Site data tab
            assertEquals("7635063", returns.get(0).answer(Eric1617.PERIOD, Q.FM_FINANCE_COSTS).orElseThrow(() -> new IllegalArgumentException("No Estates and facilities finance costs found")).response());
            assertEquals("68989", returns.get(0).answer(Eric1617.PERIOD, Q.FLOOR_AREA).orElseThrow(() -> new IllegalArgumentException("No floor area answer found")).response());
            assertEquals("5625790", returns.get(0).answer(Eric1617.PERIOD, Q.ELEC_USED).orElseThrow(() -> new IllegalArgumentException("No electricity consumed answer found")).response());
            assertEquals("13799131", returns.get(0).answer(Eric1617.PERIOD, Q.GAS_USED).orElseThrow(() -> new IllegalArgumentException("No gas consumed answer found")).response());
            // Check last value to ensure no column mis-match
            assertEquals("19.76", returns.get(0).answer(Eric1617.PERIOD, Q.NO_PORTERING_STAFF).orElseThrow(() -> new IllegalArgumentException("No portering staff answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @Test
    public void test1516Import() {
        try {
            String content = readFile(Eric1516.DATA_FILE);
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1516.HEADERS, Eric1516.PERIOD);
            LOGGER.debug(" found {} returns for {}", returns.size(), Eric1516.PERIOD);
            assertEquals(238, returns.size());

            // assert expected values of return 1
            // R1A$WORCESTERSHIRE HEALTH AND CARE NHS TRUST$MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION$COMMUNITY$0$0$1$5$1$0$4$1$27$2$46$Yes$No$Yes$Yes$2. Target included but not on track to be met$3. Assessed but not approved by the organisation's board$3. Action plan produced but not approved by the organisations board$2864738$28.33$0$3106612$503156$0$2234190$1531511$0$0$2408$0$8$11$0$15$24$0$0$0$4788423$5322142$8009962$2455054$0$0$66055$60871$78.44$160009$59297$20.249$37780$23091$18.75$14.96$5.91$4.8$98$99$0$16.13$4.26$25.27$16.89$15.18$1.52$2.39$18.38$100$0$1812818$5808568$3494531$2340843$11115917$1231320$2487313$14093737$33060$0$0$0$3470312$0$85722$51251$30689$0$0$115151$99546$55221$1588$4.353$52416$43.2415$106490$59791$1872$134$1.21$0.05$2571310$90.2$18594$0$98$18$1438933$55$90$96$572920$38$75$93$76995$7$1331814$330293$11.15$278582$805695$569115$21.73
            assertEquals(128, returns.get(0).answers().size());
            assertEquals("ERIC-2015-16-R1A", returns.get(0).name());

            // Trust data tab
            assertEquals("R1A", returns.get(0).answer(Eric1516.PERIOD, Q.ORG_CODE).orElseThrow(() -> new IllegalArgumentException("No org code found")).response());
            assertEquals(Eric1516.PERIOD, returns.get(0).applicablePeriod());
            assertEquals("Published", returns.get(0).status());
            assertEquals("WORCESTERSHIRE HEALTH AND CARE NHS TRUST", returns.get(0).answer(Eric1516.PERIOD, Q.ORG_NAME).orElseThrow(() -> new IllegalArgumentException("No org name found")).response());
            assertEquals("MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION", returns.get(0).answer(Eric1516.PERIOD, Q.COMMISSIONING_REGION).orElseThrow(() -> new IllegalArgumentException("No commissioning region found")).response());
            assertEquals("COMMUNITY", returns.get(0).answer(Eric1516.PERIOD, Q.ORG_TYPE).orElseThrow(() -> new IllegalArgumentException("No org type found")).response());
            assertEquals("0", returns.get(0).answer(Eric1516.PERIOD, Q.NO_ACUTE_SITES).orElseThrow(() -> new IllegalArgumentException("No acute sites answer found")).response());
            assertEquals("0", returns.get(0).answer(Eric1516.PERIOD, Q.NO_SPECIALIST_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("1", returns.get(0).answer(Eric1516.PERIOD, Q.NO_MIXED_SITES).orElseThrow(() -> new IllegalArgumentException("No mixed service sites answer found")).response());
            assertEquals("5", returns.get(0).answer(Eric1516.PERIOD, Q.NO_MENTAL_HEALTH_SITES).orElseThrow(() -> new IllegalArgumentException("No mental health sites answer found")).response());
            assertEquals("Yes", returns.get(0).answer(Eric1516.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No estate development strategy answer found")).response());

            // Aggregated Site data tab
            assertEquals("2487313", returns.get(0).answer(Eric1516.PERIOD, Q.ELEC_USED).orElseThrow(() -> new IllegalArgumentException("No electricity consumed answer found")).response());
            assertEquals("14093737", returns.get(0).answer(Eric1516.PERIOD, Q.GAS_USED).orElseThrow(() -> new IllegalArgumentException("No gas consumed answer found")).response());
            assertEquals("66055", returns.get(0).answer(Eric1516.PERIOD, Q.FLOOR_AREA).orElseThrow(() -> new IllegalArgumentException("No floor area answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @Test
    public void test1415Import() {
        try {
            String content = readFile(Eric1415.DATA_FILE);
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1415.HEADERS, Eric1415.PERIOD);
            LOGGER.debug(" found {} returns for {}", returns.size(), Eric1415.PERIOD);
            int expectedOrgCount = 241;
            assertEquals(expectedOrgCount, returns.size());

            SurveyReturn lastReturn = returns.get(expectedOrgCount-1);
            assertEquals(108, lastReturn.answers().size());
            assertEquals("ERIC-2014-15-TAJ", lastReturn.name());

            // Trust data tab
            assertEquals("TAJ", lastReturn.answer(Eric1415.PERIOD, Q.ORG_CODE).orElseThrow(() -> new IllegalArgumentException("No org code found")).response());
            assertEquals(Eric1415.PERIOD, lastReturn.applicablePeriod());
            assertEquals("Published", lastReturn.status());
            assertEquals("BLACK COUNTRY PARTNERSHIP NHS FOUNDATION TRUST", lastReturn.answer(Eric1415.PERIOD, Q.ORG_NAME).orElseThrow(() -> new IllegalArgumentException("No org name found")).response());
            assertEquals("MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION", lastReturn.answer(Eric1415.PERIOD, Q.COMMISSIONING_REGION).orElseThrow(() -> new IllegalArgumentException("No commissioning region found")).response());
            assertEquals("CARE TRUST", lastReturn.answer(Eric1415.PERIOD, Q.ORG_TYPE).orElseThrow(() -> new IllegalArgumentException("No org type found")).response());
            assertEquals("0", lastReturn.answer(Eric1415.PERIOD, Q.NO_ACUTE_SITES).orElseThrow(() -> new IllegalArgumentException("No acute sites answer found")).response());
            assertEquals("0", lastReturn.answer(Eric1415.PERIOD, Q.NO_SPECIALIST_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("0", lastReturn.answer(Eric1415.PERIOD, Q.NO_MIXED_SITES).orElseThrow(() -> new IllegalArgumentException("No mixed service sites answer found")).response());
            assertEquals("Yes", lastReturn.answer(Eric1415.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No estate development strategy answer found")).response());

            // Aggregated Site data tab
            assertEquals("5057236", lastReturn.answer(Eric1415.PERIOD, Q.ELEC_USED).orElseThrow(() -> new IllegalArgumentException("No electricity consumed answer found")).response());
            assertEquals("7601604", lastReturn.answer(Eric1415.PERIOD, Q.GAS_USED).orElseThrow(() -> new IllegalArgumentException("No gas consumed answer found")).response());
            assertEquals("31982", lastReturn.answer(Eric1415.PERIOD, Q.FLOOR_AREA).orElseThrow(() -> new IllegalArgumentException("No floor area answer found")).response());

            // Check last value to ensure no column mis-match
            assertEquals("0", lastReturn.answer(Eric1415.PERIOD, Q.NO_PORTERING_STAFF).orElseThrow(() -> new IllegalArgumentException("No portering staff answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @Test
    public void test1314Import() {
        try {
            String content = readFile(Eric1314.DATA_FILE);
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1314.HEADERS, Eric1314.PERIOD);
            LOGGER.debug(" found {} returns for {}", returns.size(), Eric1314.PERIOD);
            int expectedOrgCount = 251;
            assertEquals(expectedOrgCount, returns.size());

            SurveyReturn lastReturn = returns.get(expectedOrgCount-1);
            assertEquals(115, lastReturn.answers().size());
            assertEquals("ERIC-2013-14-TAJ", lastReturn.name());

            // Trust data tab
            assertEquals("TAJ", lastReturn.answer(Eric1314.PERIOD, Q.ORG_CODE).orElseThrow(() -> new IllegalArgumentException("No org code found")).response());
            assertEquals(Eric1314.PERIOD, lastReturn.applicablePeriod());
            assertEquals("Published", lastReturn.status());
            assertEquals("BLACK COUNTRY PARTNERSHIP NHS FOUNDATION TRUST", lastReturn.answer(Eric1314.PERIOD, Q.ORG_NAME).orElseThrow(() -> new IllegalArgumentException("No org name found")).response());
            assertEquals("MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION", lastReturn.answer(Eric1314.PERIOD, Q.COMMISSIONING_REGION).orElseThrow(() -> new IllegalArgumentException("No commissioning region found")).response());
            assertEquals("CARE TRUST", lastReturn.answer(Eric1314.PERIOD, Q.ORG_TYPE).orElseThrow(() -> new IllegalArgumentException("No org type found")).response());
            assertEquals("0", lastReturn.answer(Eric1314.PERIOD, Q.NO_ACUTE_SITES).orElseThrow(() -> new IllegalArgumentException("No acute sites answer found")).response());
            assertEquals("0", lastReturn.answer(Eric1314.PERIOD, Q.NO_SPECIALIST_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("4", lastReturn.answer(Eric1314.PERIOD, Q.NO_LONG_STAY_HOSPITAL_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("5", lastReturn.answer(Eric1314.PERIOD, Q.NO_OUTPATIENT_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("0", lastReturn.answer(Eric1314.PERIOD, Q.NO_OTHER_INPATIENT_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("4", lastReturn.answer(Eric1314.PERIOD, Q.NO_NON_HOSPITAL_PATIENT_SITES).orElseThrow(() -> new IllegalArgumentException("No specialist sites answer found")).response());
            assertEquals("5", lastReturn.answer(Eric1314.PERIOD, Q.NO_SUPPORT_SITES).orElseThrow(() -> new IllegalArgumentException("No acute sites answer found")).response());
            assertEquals("18", lastReturn.answer(Eric1314.PERIOD, Q.NO_SITES_TOTAL).orElseThrow(() -> new IllegalArgumentException("No mixed service sites answer found")).response());
            assertEquals("No", lastReturn.answer(Eric1314.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No estate development strategy answer found")).response());

            // Aggregated Site data tab
            assertEquals("3217696", lastReturn.answer(Eric1314.PERIOD, Q.ELEC_USED).orElseThrow(() -> new IllegalArgumentException("No electricity consumed answer found")).response());
            assertEquals("7634995", lastReturn.answer(Eric1314.PERIOD, Q.GAS_USED).orElseThrow(() -> new IllegalArgumentException("No gas consumed answer found")).response());
            assertEquals("31982", lastReturn.answer(Eric1314.PERIOD, Q.FLOOR_AREA).orElseThrow(() -> new IllegalArgumentException("No floor area answer found")).response());

            // Check last value to ensure no column mis-match
            assertEquals("551", lastReturn.answer(Eric1314.PERIOD, Q.PARKING_SPACES).orElseThrow(() -> new IllegalArgumentException("No parking spaces answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("resource")
    private String readFile(String dataFile) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(dataFile)) {
            assertNotNull(is, String.format("Unable to find test data at %1$s",
                    Eric1516.DATA_FILE));
            return new Scanner(is).useDelimiter("\\A").next();
        }
    }

}
