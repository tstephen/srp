package digital.srp.sreport.importers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.Eric1516;

public class EricCsvImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EricCsvImporter.class);

    private List<Question> questions;

    public List<SurveyReturn> readEricReturns() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(Eric1516.DATA_FILE))) {
            return readEricReturns(isr, Eric1516.HEADERS, "2015-16");
        }
    }

    public List<SurveyReturn> readEricReturns(Reader in, String[] headers, String period)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readEricReturns"));

        questions = new ArrayList<Question>();
        for (String hdr : headers) {
            questions.add(new Question().q(lookupQuestion(hdr)));
        }
        LOGGER.debug("Found {} questions", questions.size());

        List<SurveyReturn> surveyResponses = new ArrayList<SurveyReturn>();
        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            if (record.getRecordNumber() > 2) { // skip headers
                List<Answer> surveyAnswers = new ArrayList<Answer>();
                String org = record.get(0);
                for (int i = 0; i < record.size(); i++) {
                    surveyAnswers.add(new Answer()
                            .question(getQuestion(i)).response(record.get(i))
                            .applicablePeriod(period)
                            .status(StatusType.Published.name()));
                }

                surveyResponses.add(new SurveyReturn()
                        .name(String.format("ERIC-%1$s-%2$s", period, org))
                        .applicablePeriod(period)
                        .org(org)
                        .status(StatusType.Published.name())
                        .answers(surveyAnswers));
            }
        }
        parser.close();

        return surveyResponses;
    }

    private Q lookupQuestion(String hdr) {
        switch (hdr) {

        case "Organisation Code":
            return Q.ORG_CODE;
        case "Organisation Name":
            return Q.ORG_NAME;
        case "Commissioning Region":
            return Q.COMMISSIONING_REGION;
        case "Organisation Type":
            return Q.ORG_TYPE;
        case "Number of sites - General acute hospital (No.)":
            return Q.NO_ACUTE_SITES;
        case "Number of sites - Specialist hospital (acute only) (No.)":
            return Q.NO_SPECIALIST_SITES;
        case "Number of sites - Mixed service hospital (No.)":
            return Q.NO_MIXED_SITES;
        case "Number of sites - Mental Health (including Specialist services) (No.)":
            return Q.NO_MENTAL_HEALTH_SITES;
        case "Number of sites - Learning Disabilities (No.)":
            return Q.NO_LD_SITES;
        case "Number of sites - Mental Health and Learning Disabilities (No.)":
            return Q.NO_MENTAL_HEALTH_LD_SITES;
        case "Number of sites - Community hospital (with inpatient beds) (No.)":
            return Q.NO_COMMUNITY_HOSPITAL_SITES;
        case "Number of sites - Other inpatient (No.)":
            return Q.NO_OTHER_INPATIENT_SITES;
        case "Number of sites - Non inpatient (No.)":
            return Q.NO_OUTPATIENT_SITES;
        case "Number of sites - Support facilities (No.)":
            return Q.NO_SUPPORT_SITES;
        case "Number of sites - Unreported sites (No.)":
            return Q.NO_OTHER_SITES;
        case "Estates Development Strategy (Yes/No)":
            return Q.ESTATES_DEV_STRATEGY;
        case "Healthy transport plan (Yes/No)":
            return Q.HEALTHY_TRANSPORT_PLAN;
        case "Board approved Adaptation Plan (Yes/No)":
            return Q.BOARD_ADAPTATION_PLAN;
        case "Sustainable Development Management Plan/Carbon Reduction Management Plan (Yes/No)":
            return Q.SDMP_CRMP;
        case "Carbon reduction target (Select)":
            return Q.CARBON_REDUCTION_TARGET;
        case "NHS Premises and Facilities Assurance - Assessment/Approval (Select)":
            return Q.PFA_ASSESSMENT;
        case "NHS Premises and Facilities Assurance - action plan (Select)":
            return Q.PFA_ACTION_PLAN;
        case "Value of contracted out services (£)":
            return Q.CONTRACTING_OUT_VAL;
        case "Percentage of hard FM (estates) and soft FM (hotel services) contracted out (%)":
            return Q.CONTRACTING_OUT_PCT;
        case "Capital investment for new build (£)":
            return Q.CAPITAL_NEW_BUILD;
        case "Capital investment for improving existing buildings (£)":
            return Q.CAPITAL_IMPROVING_EXISTING;
        case "Capital investment for equipment (£)":
            return Q.CAPITAL_EQUIPMENT;
        case "Private Sector investment (£)":
            return Q.PRIVATE_SECTOR;
        case "Investment to reduce backlog maintenance (£)":
            return Q.BACKLOG_MAINTENANCE_VAL;
        case "Cost to meet NHS Premises and Facilities Assurance action plan (£)":
            return Q.PFA_ACTION_PLAN_VAL;
        case "Non-emergency patient transport (£)":
            return Q.NON_EMERGENCY_TRANSPORT_VAL;
        case "Income from services provided to other organisations - catering (£)":
            return Q.INCOME_CATERING;
        case "Income from services provided to other organisations - laundry and linen (£)":
            return Q.INCOME_LAUNDRY;
        case "Income from services provided to other organisations - other (£)":
            return Q.INCOME_OTHER;
        case "RIDDOR incidents (No.)":
            return Q.NO_RIDDOR_INCIDENTS;
        case "Estates and facilities related incidents (No.)":
            return Q.NO_FM_INCIDENTS;
        case "Clinical service incidents caused by estates and infrastructure failure (No.)":
            return Q.NO_FM_CLINICAL_INCIDENTS;
        case "Fires recorded (No.)":
            return Q.NO_FIRES;
        case "False alarms (No.)":
            return Q.NO_FALSE_ALARMS;
        case "Number of deaths resulting from fire(s) (No.)":
            return Q.NO_FIRE_DEATHS;
        case "Number of people injured resulting from fire(s) (No.)":
            return Q.NO_FIRE_INJURIES;
        case "Number of patients sustaining injuries during evacuation (No.)":
            return Q.NO_EVAC_INJURIES;
        }
        throw new IllegalArgumentException(
                String.format("ERIC data not expected to include %1$s", hdr));
    }

    private Question getQuestion(int i) {
        return questions.get(i);
    }

}
