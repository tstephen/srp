package digital.srp.diag;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.services.Cruncher;

public class RunCalculationsExternally {
    public static final String USER_AGENT = "SRP Agent";
    private static final String URL_AUTH = "https://auth.srp.digital/auth/realms/knowprocess/protocol/openid-connect/token";
    private static final String URL_RETURNS = "https://v3.srp.digital/returns/";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RunCalculationsExternally.class);
    private ObjectMapper objectMapper;
    private Cruncher cruncher;
    
    private String usr;
    private String pwd;

    public RunCalculationsExternally() throws IOException {
        this.objectMapper = new ObjectMapper();
        List<CarbonFactor> cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        List<WeightingFactor> wfactors = new WeightingFactorCsvImporter().readWeightingFactors();

        this.cruncher = new Cruncher(cfactors, wfactors);
    }
    
    private String authenticateAndGetToken(String psr, String pwd) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(URL_AUTH).openConnection();
            connection.setRequestMethod(HttpMethod.POST.name());

            connection.setRequestProperty("Accept",
                    MediaType.APPLICATION_JSON_VALUE);
//            connection.setRequestProperty("User-Agent", USER_AGENT);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

//            sendData(connection, payload);

            int code = connection.getResponseCode();
            LOGGER.info("Response code: {}", code);

            try (InputStream is = connection.getInputStream()) {
//                return objectMapper.readValue(is, SurveyReturn.class);
            }
        } catch (IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
            throw new RuntimeException(e1);
        } finally {
            connection.disconnect();
        }

        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJwMDVBSWluZXJLa2FYeUNkQTJUZkp2RlNxM0lBTnZwY1owNW1FUmx2WWZnIn0.eyJleHAiOjE2MzE3MTg3NzksImlhdCI6MTYzMTcxODQ3OSwianRpIjoiNDMwMTY5ODItMThiOC00NmE4LWEyYjUtOTQyMDA1Y2RhZTRiIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnNycC5kaWdpdGFsL2F1dGgvcmVhbG1zL2tub3dwcm9jZXNzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImI4ODc4YjI0LWYxNTUtNGNlYy1iMzQwLWI2MTBlYjlkMmVhMCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNycCIsInNlc3Npb25fc3RhdGUiOiI1NWNjMDIzOC1iNTU1LTRhMWMtYjZmMy0wN2UzYzUxNDUwODEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vdjMuc3JwLmRpZ2l0YWwiLCJodHRwczovL3NycC5kaWdpdGFsIiwiaHR0cDovL2xvY2FsaG9zdDo4MDAwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJwcm9kdWN0LW93bmVyIiwidmlldyIsInVwbG9hZCIsIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJhbmFseXN0IiwidW1hX2F1dGhvcml6YXRpb24iLCJvZmZpY2UiLCJwb3dlci11c2VyIiwiZGVsZXRlIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNycCI6eyJyb2xlcyI6WyJhbmFseXN0IiwiYWRtaW4iLCJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlRpbSBTdGVwaGVuc29uIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGltQGtub3dwcm9jZXNzLmNvbSIsImdpdmVuX25hbWUiOiJUaW0iLCJmYW1pbHlfbmFtZSI6IlN0ZXBoZW5zb24iLCJlbWFpbCI6InRpbUBrbm93cHJvY2Vzcy5jb20ifQ.mATOstegEMouY8FXDiHwYGcu7ctMyI8AUbsE1EvSvswPtt-hWMe2s2k4vS3gEC4mbQn2gaQFJVUI3EjzLIaZWWQ95oyxvfetOQdJA8R8pR5e406bpDlSAMTdP2HwzYkV890CwIlVkqBAiZrVCCLoCMdjjdX2i6np7BwuuE28nKzjAWe8oj5iNxWVWESqlJvPVROhiIca9Gx9xRcyPBIUoNfEeRdn_OaD8hpiNuww9AkaEXOwRIV-7dcLG_gjaINv8cHoKISU8I9R5j2PwhRJldEWTK2O7YQL3R19ElZ_hH9LbkMl1CAaevPU56Y6NaP2F5Luo_yVKoSWTOZqQrJe8Q";
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
        String token = authenticateAndGetToken(this.usr, this.pwd);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(URL_RETURNS).openConnection();
                connection.setRequestMethod(HttpMethod.POST.name());

                connection.setRequestProperty("Accept",
                        MediaType.APPLICATION_JSON_VALUE);
                connection.setRequestProperty("Authorization",
                        String.format("Bearer: %s", token));
//                connection.setRequestProperty("User-Agent", USER_AGENT);

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

//                sendData(connection, payload);

                int code = connection.getResponseCode();
                LOGGER.info("Response code: {}", code);

                try (InputStream is = connection.getInputStream()) {
                    return objectMapper.readValue(is, SurveyReturn.class);
                }
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
                throw new RuntimeException(e1);
            } finally {
                connection.disconnect();
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
            RunCalculationsExternally svc = new RunCalculationsExternally();
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
