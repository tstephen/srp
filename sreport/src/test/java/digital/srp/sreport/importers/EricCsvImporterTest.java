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
             // R1A,WORCESTERSHIRE HEALTH AND CARE NHS TRUST,MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION,COMMUNITY,0,0,1,5,1,0,4,1,27,2,46,Yes,No,Yes,Yes,2. Target included but not on track to be met,3. Assessed but not approved by the organisation's board,3. Action plan produced but not approved by the organisations board,"2,864,738",28.33,0,"3,106,612","503,156",0,"2,234,190","1,531,511",0,0,"2,408",0,8,11,0,15,24,0,0,0
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
             // ... etc up to ...
             assertEquals("0", returns.get(0).answers().get(41).response());

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
