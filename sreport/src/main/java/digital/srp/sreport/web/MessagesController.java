package digital.srp.sreport.web;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.sreport.services.MessageService;

/**
 * REST web service for retrieving messages in desired language.
 *
 * @author Tim Stephenson
 */
@Controller
public class MessagesController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MessagesController.class);

    @Autowired
    private MessageService msgSvc;

    /**
     * Retrieve all messages in the closest matching language.
     * 
     * @return full message bundle.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/messages_{lang}.json")
    public @ResponseBody Map<String, String> getMessages(
            @PathVariable(value = "lang") String lang) {
        LOGGER.info("getMessages for {}", lang);
        return msgSvc.getAll(Locale.forLanguageTag(lang));
    }
}
