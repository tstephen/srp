package digital.srp.sreport.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.internal.StringUtils;
import digital.srp.sreport.model.Q;

public class MessageServiceTest {

    protected static MessageService svc;

    @BeforeAll
    public static void setUp() {
        svc = new MessageService();
    }

    @Test
    public void testCheckAllQuestionsHaveLabels() {
        Map<String, String> msgs = svc.getAll(Locale.UK);
        assertThat(msgs.size(), is(Matchers.greaterThan(0)));
        List<String> missingMsgs = new ArrayList<String>();

        for (Q q : Q.values()) {
            if (!msgs.containsKey(q.name())
                    || "TODO".equals(msgs.get(q.name()))) {
                missingMsgs.add(q.name());
                msgs.put(q.name(), StringUtils.toSentanceCase(q.name()));
            }
        }

        // have a stab at automating any required update
        if (missingMsgs.size() > 0) {
            System.err.println("Additional messages needed: " + missingMsgs);
            try (OutputStream os = new FileOutputStream(
                    new File("target", "updated-messages.properties"));) {
                for (Entry<String, String> entry : msgs.entrySet()) {
                    os.write(String
                            .format("%1$s=%2$s\n", entry.getKey(), entry.getValue())
                            .getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
        assertThat(missingMsgs.size(), is(0));
    }

}
