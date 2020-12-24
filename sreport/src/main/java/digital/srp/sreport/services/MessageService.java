package digital.srp.sreport.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MessageService.class);

    public Map<String, String> getAll() {
        return getAll(Locale.UK);
    }

    public Map<String, String> getAll(Locale locale) {
        LOGGER.info("getAll massaged for {}", locale);
        ResourceBundle bundle = ResourceBundle.getBundle("digital.srp.sreport.Messages", locale);
        Map<String, String> map = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        LOGGER.info("Found {} messages", map.size());
        return map;
    }
}
