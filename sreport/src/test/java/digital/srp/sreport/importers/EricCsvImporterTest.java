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

public class EricCsvImporterTest {

    @Test
    public void test1516Download() {
        String content = readFile();
        try {
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(new StringReader(content), Eric1516.HEADERS, "2015-16");
            System.out.println(
                    String.format(" found %1$d returns", returns.size()));
            assertEquals(238, returns.size());

             // assert expected values of return 1
             // R1A$WORCESTERSHIRE HEALTH AND CARE NHS TRUST$MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION$COMMUNITY$0$0$1$5$1$0$4$1$27$2$46$Yes$No$Yes$Yes$2. Target included but not on track to be met$3. Assessed but not approved by the organisation's board$3. Action plan produced but not approved by the organisations board$2864738$28.33$0$3106612$503156$0$2234190$1531511$0$0$2408$0$8$11$0$15$24$0$0$0$4788423$5322142$8009962$2455054$0$0$66055$60871$78.44$160009$59297$20.249$37780$23091$18.75$14.96$5.91$4.8$98$99$0$16.13$4.26$25.27$16.89$15.18$1.52$2.39$18.38$100$0$1812818$5808568$3494531$2340843$11115917$1231320$2487313$14093737$33060$0$0$0$3470312$0$85722$51251$30689$0$0$115151$99546$55221$1588$4.353$52416$43.2415$106490$59791$1872$134$1.21$0.05$2571310$90.2$18594$0$98$18$1438933$55$90$96$572920$38$75$93$76995$7$1331814$330293$11.15$278582$805695$569115$21.73
            assertEquals("ERIC-2015-16-R1A", returns.get(0).name());
            assertEquals("R1A", returns.get(0).answers().get(0).response());
            assertEquals("2015-16", returns.get(0).applicablePeriod());
            assertEquals("Published", returns.get(0).status());
             assertEquals(Q.ORG_CODE.code(), returns.get(0).answers().get(0).question().name());
             assertEquals("WORCESTERSHIRE HEALTH AND CARE NHS TRUST", returns.get(0).answers().get(1).response());
             assertEquals("MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION", returns.get(0).answers().get(2).response());
             assertEquals(Q.COMMISSIONING_REGION.code(), returns.get(0).answers().get(2).question().name());
             assertEquals("COMMUNITY", returns.get(0).answers().get(3).response());
             assertEquals("0", returns.get(0).answers().get(4).response());
             assertEquals("0", returns.get(0).answers().get(5).response());
             assertEquals("1", returns.get(0).answers().get(6).response());
             assertEquals("5", returns.get(0).answers().get(7).response());
             assertEquals("1", returns.get(0).answers().get(8).response());
             assertEquals("0", returns.get(0).answers().get(9).response());
             assertEquals("4", returns.get(0).answers().get(10).response());
             assertEquals("1", returns.get(0).answers().get(11).response());
             assertEquals("27", returns.get(0).answers().get(12).response());
             assertEquals("2", returns.get(0).answers().get(13).response());
             assertEquals("46", returns.get(0).answers().get(14).response());
             assertEquals("Yes", returns.get(0).answers().get(15).response());
             assertEquals("4788423", returns.get(0).answers().get(42).response());
             assertEquals("5322142", returns.get(0).answers().get(43).response());
             assertEquals("8009962", returns.get(0).answers().get(44).response());
             assertEquals("2455054", returns.get(0).answers().get(45).response());
             // ... etc up to ...
             assertEquals("21.73", returns.get(0).answers().get(127).response());

        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

    @SuppressWarnings("resource")
    private String readFile() {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(Eric1516.DATA_FILE);
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
