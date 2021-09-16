package digital.srp.diag;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.KeycloakApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.services.Cruncher;

public class RunCalculationsExternally2 {
    public static final String USER_AGENT = "SRP Agent";
    private static final String URL_RETURNS = "https://v3.srp.digital/returns/findCurrentBySurveyNameAndOrg/%s/%s";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RunCalculationsExternally2.class);
    private ObjectMapper objectMapper;
    private Cruncher cruncher;
    private OAuth20Service oAuth20Service;
    
    public RunCalculationsExternally2() throws IOException {
        this.objectMapper = new ObjectMapper();
//        List<CarbonFactor> cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
//        List<WeightingFactor> wfactors = new WeightingFactorCsvImporter().readWeightingFactors();

//        this.cruncher = new Cruncher(cfactors, wfactors);
    }
    
    private OAuth2AccessToken authenticateAndGetToken() throws IOException, InterruptedException, ExecutionException {
        // Replace these with your own api key, secret, callback, base url and realm
        final String apiKey = "srp";
        final String apiSecret = "";
        final String callback = "https://srp.digital";
//        final String apiKey = "srp-cli";
//        final String apiSecret = "81ba0e7a-dcee-49b3-af51-11add3d61ed9";
//        final String callback = "https://v3.srp.digital";
        final String baseUrl = "https://auth.srp.digital/";
        final String realm = "knowprocess";

        final String protectedResourceUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/userinfo";

         oAuth20Service = new ServiceBuilder(apiKey)
                .build(KeycloakApi.instance(baseUrl, realm));
        OAuth2AccessToken accessToken = oAuth20Service.
                .getAccessTokenPasswordGrant("tim@knowprocess.com", "SJcq6Syb23tFrNQf");
//        oAuth20Service = new ServiceBuilder(apiKey)
//                .apiSecret(apiSecret)
//                .defaultScope("openid")
//                .callback(callback)
//                .build(KeycloakApi.instance(baseUrl, realm));
//        final Scanner in = new Scanner(System.in);
//
//        System.out.println("=== Keyloack's OAuth Workflow ===");
//        System.out.println();
//
//        // Obtain the Authorization URL
//        System.out.println("Fetching the Authorization URL...");
//        final String authorizationUrl = oAuth20Service.getAuthorizationUrl();
//        System.out.println("Got the Authorization URL!");
//        System.out.println("Now go and authorize ScribeJava here:");
//        System.out.println(authorizationUrl);
//        System.out.println("And paste the authorization code here");
//        System.out.print(">>");
//        final String code = in.nextLine();
//        System.out.println();
//
//        System.out.println("Trading the Authorization Code for an Access Token...");
//        final OAuth2AccessToken accessToken = oAuth20Service.getAccessToken(code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        
        
        // Now let's go and ask for a protected resource!
//        System.out.println("Now we're going to access a protected resource...");
//        final OAuthRequest request = new OAuthRequest(Verb.GET, protectedResourceUrl);
//        oAuth20Service.signRequest(accessToken, request);
//        try (Response response = oAuth20Service.execute(request)) {
//            System.out.println("Got it! Lets see what we found...");
//            System.out.println();
//            System.out.println(response.getCode());
//            System.out.println(response.getBody());
//        }
        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");

        return accessToken;
    }
    
    private static void printUsage() {
        System.err.println("Require arguments: survey org period:");
        System.err.println("  survey: unique name of the survey, e.g. SDU-2020-21");
        System.err.println("  org: 3 char identifier of organisation, e.g. ZZ1");
        System.err.println("  period: period return applies to, e.g. 2020-21");
    }

    protected void sendData(HttpURLConnection connection, Object data) throws IOException {
        // Send request
        if (data != null) {
            // String bytes = URLEncoder.encode(
            // (String) data.getValue(execution), "UTF-8");
            String bytes = data.toString();
            LOGGER.debug("  Content-Length: "
                    + Integer.toString(bytes.length()));
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(bytes.length()));
            for (Entry<String, List<String>> h : connection.getRequestProperties().entrySet()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("  %1$s: %2$s", h.getKey(),
                            h.getValue()));
                }
            }
            // connection.setRequestProperty("Content-Language", "en-US");
            LOGGER.debug("==================== Data =======================");
            LOGGER.debug(bytes);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(bytes);
            wr.flush();
            wr.close();
        }
    }

    public SurveyReturn fetchReturn(final String survey, final String org, final String period) {
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        
//      HttpURLConnection connection = null;
      try {
        final OAuthRequest request = new OAuthRequest(Verb.GET, 
                String.format(URL_RETURNS, survey, org));
        oAuth20Service.signRequest(authenticateAndGetToken(), request);
        try (Response response = oAuth20Service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
            
            return objectMapper.readValue(response.getBody(), SurveyReturn.class);
        }
//                connection = (HttpURLConnection) new URL(URL_RETURNS).openConnection();
//                connection.setRequestMethod(HttpMethod.POST.name());
//
//                connection.setRequestProperty("Accept",
//                        MediaType.APPLICATION_JSON_VALUE);
//                connection.setRequestProperty("Authorization",
//                        String.format("Bearer: %s", authenticateAndGetToken()));
//                connection.setRequestProperty("User-Agent", USER_AGENT);
//
//                connection.setUseCaches(false);
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//
////                sendData(connection, payload);
//
//                int code = connection.getResponseCode();
//                LOGGER.info("Response code: {}", code);
//
//                try (InputStream is = connection.getInputStream()) {
//                    return objectMapper.readValue(is, SurveyReturn.class);
//                }
            } catch (Throwable e1) {
                LOGGER.error(e1.getMessage(), e1);
                throw new RuntimeException(e1);
//            } finally {
//                connection.disconnect();
            }
        }

    private SurveyReturn clone(SurveyReturn src) throws IOException {
        return objectMapper.readValue(objectMapper.writeValueAsString(src).getBytes(), SurveyReturn.class);
    }

    public void runCalculations(final SurveyReturn rtn) {
        cruncher.calculate(rtn, 1);
    }
    
    public List<Answer> detectChanges(final SurveyReturn before, final SurveyReturn after) {
        ArrayList<Answer> changes = new ArrayList<Answer>();
        for (Answer beforeAns : before.answers()) {
            Optional<Answer> afterAns = after.answer(beforeAns.applicablePeriod(), beforeAns.question().q());
            if (afterAns.isPresent() 
                    && afterAns.get().response().equals(beforeAns.response())) {
                System.out.println(String.format("  No change to %s.%s: %s", 
                        before.name(), beforeAns.question().name(), beforeAns.response()));
            } else {
                changes.add(afterAns.get());
            }
        }
        return changes;
    }
    
    public void reportChanges(final SurveyReturn rtn, final List<Answer> changes, Writer out) {
        for (Answer ans : changes) {
            System.out.println(String.format("%s.%s changed to %s", 
                    rtn.name(), ans.question().name(), ans.response()));
        }
    }
    
    public void uploadChanges(final SurveyReturn rtn, final List<Answer> changes) {
        
    }
    
    public static final void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            System.exit(-1);
        }
        try {
            RunCalculationsExternally2 svc = new RunCalculationsExternally2();
            SurveyReturn before = svc.fetchReturn(args[0], args[1], args[2]);
            SurveyReturn after = svc.clone(before);
            svc.runCalculations(after);
            List<Answer> changes = svc.detectChanges(before, after);
            svc.reportChanges(before, changes, new PrintWriter(System.out));
            svc.uploadChanges(after, changes);
        } catch (Throwable t) {
            System.err.println("  Ooops! " + t.getMessage());
//            t.printStackTrace();
        }
    }

}
