package digital.srp.sreport.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Test;

import digital.srp.sreport.internal.StringUtils;

public class PropertiesFixerTest {

    @Test
    public void test() {
        InputStream is = null;
        OutputStream os = null ;
        try {
            Properties props = new Properties();
            is = getClass().getResourceAsStream("Messages.properties");
            props.load(is);
            
            Properties newProps = new Properties();
            for (Entry<Object, Object> entry : props.entrySet()) {
                newProps.put(StringUtils.camelCaseToConst((String) entry.getKey()), entry.getValue());
            }
            os = new FileOutputStream(new File("target","messages.properties"));
            newProps.store(os, "");
        } catch (Exception e) {
            try {
                is.close();
            } catch (IOException e1) {
                ;
            }
            try {
                os.close();
            } catch (IOException e1) {
                ;
            }
        }
    }

}
