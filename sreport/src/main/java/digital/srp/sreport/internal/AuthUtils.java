package digital.srp.sreport.internal;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AuthUtils.class);

    public static String getUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else if (authentication.getPrincipal() instanceof Principal) {
            String tmp = ((Principal) authentication.getPrincipal())
                    .getName();
            if (!tmp.contains("@")) { // i.e. not an email addr
                LOGGER.warn("Username '{}' is not an email address, ignoring, this may result in errors if the process author expected a username.", tmp);
            }
            return tmp;
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        } else {
            String msg = String.format("Authenticated but principal of unknown type {}",
            authentication.getPrincipal().getClass().getName());
            LOGGER.error(msg);
            throw new IllegalStateException(msg);
        }
    }
}
