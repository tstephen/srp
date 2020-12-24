package digital.srp.sreport.services;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.junit.Test;

import digital.srp.sreport.internal.StringUtils;

public class PropertiesFixerTest {

    @Test
    public void test() {
        try (InputStream is = getClass().getResourceAsStream("/digital/srp/sreport/Messages.properties");
                OutputStream os = new FileOutputStream(new File("target", "messages.properties"))) {
            Properties props = new Properties();

            props.load(is);

            SortedProperties sp = new SortedProperties();
            sp.putAll(props);
            sp.store(os, "");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private class SortedProperties extends Properties {
        private static final long serialVersionUID = 823027721880514243L;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Enumeration keys() {
            Enumeration<Object> keysEnum = super.keys();
            Vector<String> keyList = new Vector<String>();
            while (keysEnum.hasMoreElements()) {
                keyList.add(StringUtils.camelCaseToConst((String) keysEnum.nextElement()));
            }
            Collections.sort(keyList);
            return keyList.elements();
        }

    }

}
