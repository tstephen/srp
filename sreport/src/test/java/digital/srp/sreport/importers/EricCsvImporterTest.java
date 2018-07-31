package digital.srp.sreport.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.Eric1516;
import digital.srp.sreport.model.returns.Eric1617;

public class EricCsvImporterTest {

    @Test
    public void test1617Import() {
        String content = readFile(Eric1617.DATA_FILE);
        try {
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1617.HEADERS, Eric1617.PERIOD);
            System.out.println(
                    String.format(" found %1$d returns", returns.size()));
            assertEquals(236, returns.size());

            // assert expected values of first return
            // R1A$WORCESTERSHIRE HEALTH AND CARE NHS TRUST$MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION$COMMUNITY$0$0$1$5$1$0$5$1$28$2$46$0$Yes$No$Yes$Yes$2. Target included but not on track to be met$3. Assessed but not approved by the organisation's board$3. Action plan produced but not approved by the organisations board$0$1,526,422$138,878$0$1,223,758$1,156,350$437,000$306,000$0$0$0$5$15$1$0$81.00$5$8$13$0$0$0
            assertEquals(Eric1617.NAME+"-R1A", returns.get(0).name());
            assertEquals(45, returns.get(0).answers().size());
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
            assertEquals("Yes", returns.get(0).answer(Eric1617.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No esate development strategy answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @Test
    public void test1516Import() {
        String content = readFile(Eric1516.DATA_FILE);
        try {
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1516.HEADERS, Eric1516.PERIOD);
            System.out.println(
                    String.format(" found %1$d returns", returns.size()));
            assertEquals(238, returns.size());

            // assert expected values of return 1
            // R1A$WORCESTERSHIRE HEALTH AND CARE NHS TRUST$MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION$COMMUNITY$0$0$1$5$1$0$4$1$27$2$46$Yes$No$Yes$Yes$2. Target included but not on track to be met$3. Assessed but not approved by the organisation's board$3. Action plan produced but not approved by the organisations board$2864738$28.33$0$3106612$503156$0$2234190$1531511$0$0$2408$0$8$11$0$15$24$0$0$0$4788423$5322142$8009962$2455054$0$0$66055$60871$78.44$160009$59297$20.249$37780$23091$18.75$14.96$5.91$4.8$98$99$0$16.13$4.26$25.27$16.89$15.18$1.52$2.39$18.38$100$0$1812818$5808568$3494531$2340843$11115917$1231320$2487313$14093737$33060$0$0$0$3470312$0$85722$51251$30689$0$0$115151$99546$55221$1588$4.353$52416$43.2415$106490$59791$1872$134$1.21$0.05$2571310$90.2$18594$0$98$18$1438933$55$90$96$572920$38$75$93$76995$7$1331814$330293$11.15$278582$805695$569115$21.73
            assertEquals(128, returns.get(0).answers().size());
            assertEquals("ERIC-2015-16-R1A", returns.get(0).name());
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
            assertEquals("Yes", returns.get(0).answer(Eric1516.PERIOD, Q.ESTATES_DEV_STRATEGY).orElseThrow(() -> new IllegalArgumentException("No esate development strategy answer found")).response());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }

    @SuppressWarnings("resource")
    private String readFile(String dataFile) {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(dataFile);
            assertNotNull(String.format("Unable to find test data at %1$s",
                    Eric1516.DATA_FILE), is);
            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

}
