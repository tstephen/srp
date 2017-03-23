package digital.srp.sreport.web;

import java.util.List;
import java.util.ResourceBundle;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.SurveyAnswer;
import digital.srp.sreport.model.TabularDataSet;
import digital.srp.sreport.model.surveys.SduQuestions;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.TabularDataSetHelper;

/**
 * REST web service for accessing various reports.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/reports")
public class SduReportController implements SduQuestions {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SduReportController.class);

    @Value("${spring.data.rest.baseUri}")
    protected String baseUrl;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    private TabularDataSetHelper tdsHelper;

    /**
     * Return a table of organisation data for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/org.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String orgTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("orgTable for %1$s %2$s", org, period));

        // Survey survey = surveyRepo.findByName(sduSurveyFromPeriod(period));
        String[] headers = new String[] { "floorArea", "noStaff" };
        List<SurveyAnswer> answers = answerRepo.findByOrgAndQuestion(org,
                headers);
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));

        // HttpHeaders headers = new HttpHeaders();
        // HttpEntity<String> response = new HttpEntity<String>("TODO",
        // headers);
        TabularDataSet table = tdsHelper.tabulate(headers, answers);
        model.addAttribute("table", table);
        model.addAttribute("periods",
                PeriodUtil.fillBackwards(period, table.rows().length));

        ResourceBundle messages = ResourceBundle
                .getBundle("digital.srp.sreport.Messages");
        model.addAttribute("messages", messages);
        return "table";
    }

    /**
     * Return a table of energy use (kWh) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/energy.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String energyTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("energyTable for %1$s %2$s", org, period));

        fillModel(org, period,
                new String[] { ELEC_USED, GAS_USED, OIL_USED, COAL_USED,
                        STEAM_USED, HOT_WATER_USED, ELEC_USED_GREEN_TARIFF,
                        "elecUsed3rdPtyRenewable", RENEWABLE_USED,
                        ELEC_EXPORTED, WATER_VOL },
                model);
        return "table";
    }

    /**
     * Return a table of energy use (kWh) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/energy.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String energyCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("energyCsv for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { ELEC_USED, GAS_USED, OIL_USED,
                COAL_USED, STEAM_USED, HOT_WATER_USED, ELEC_RENEWABLE },
                model);
        return "csv";
    }

    /**
     * Return a table of energy use (CO2e) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/energy-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String energyCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(
                String.format("energyCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period,
                new String[] { ELEC_CO2E, GAS_CO2E, OIL_CO2E, COAL_CO2E,
                        STEAM_CO2E, HOT_WATER_CO2E, ELEC_RENEWABLE_CO2E },
                model);
        return "table";
    }

    /**
     * Return a table of energy use (CO2e) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/energy-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String energyCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("energyCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period,
                new String[] { ELEC_CO2E, GAS_CO2E, OIL_CO2E, COAL_CO2E,
                        STEAM_CO2E, HOT_WATER_CO2E, ELEC_RENEWABLE_CO2E },
                model);
        return "csv";
    }

    /**
     * Return a table of travel data for the specified organisation and period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/travel.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String travelTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("travelTable for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { PATIENT_AND_VISITOR_MILEAGE,
                BIZ_MILEAGE, STAFF_COMMUTE_MILES }, model);
        return "table";
    }

    /**
     * Return a table of travel data emissions (CO2e) for the specified 
     * organisation and period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/travel-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String travelCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("travelCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { PATIENT_AND_VISITOR_MILEAGE_CO2E,
                BIZ_MILEAGE_CO2E, STAFF_COMMUTE_MILES_CO2E }, model);
        return "table";
    }

    /**
     * Return a table of waste (tonnes) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/waste.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String wasteTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("wasteTable for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { RECYCLING_WEIGHT,
                OTHER_RECOVERY_WEIGHT, INCINERATION_WEIGHT, LANDFILL_WEIGHT },
                model);
        return "table";
    }

    /**
     * Return a table of waste (CO2e) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/waste-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String wasteCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("wasteCsv for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { RECYCLING_CO2E, 
                OTHER_RECOVERY_CO2E, INCINERATION_CO2E, LANDFILL_CO2E },
                model);
        return "csv";
    }
    
    /**
     * Return a table of water use for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/water.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String waterTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("waterTable for %1$s %2$s", org, period));

        fillModel(org, period, new String[] { WATER_VOL, WASTE_WATER, WATER_COST },
                model);
        return "table";
    }
    
    /**
     * Return a table of categorised carbon emissions for the specified
     * organisation and period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-emissions-profile.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String emissionsProfileTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("emissionsProfileTable for %1$s %2$s", org, period));

        fillModel(org, period,
                new String[] { COMMISSIONING_CO2E, ANAESTHETIC_GASES_CO2E,
                        PHARMA_CO2E, PAPER_CO2E, OTHER_PROCUREMENT_CO2E,
                        OTHER_MANUFACTURED_CO2E, MED_INSTR_CO2E,
                        CHEMS_AND_GASES_CO2E, ICT_CO2E, FREIGHT_CO2E,
                        CATERING_CO2E, CONSTUCTION_CO2E, BIZ_SVCS_CO2E,
                        CAPITAL_CO2E, WATER_CO2E, WASTE_CO2E, TRAVEL_CO2E,
                        /* TODO should be heat & steam */
                        ELEC_RENEWABLE_CO2E, STEAM_CO2E, ELEC_CO2E, COAL_CO2E, 
                        OIL_CO2E, GAS_CO2E},
                model);
        return "table";
    }

    /**
     * Return a table of categorised carbon emissions for the specified
     * organisation and period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-emissions-profile.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String emissionsProfileCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("emissionsProfileTable for %1$s %2$s", org, period));

        fillModel(org, period,
                new String[] { COMMISSIONING_CO2E, ANAESTHETIC_GASES_CO2E,
                        PHARMA_CO2E, PAPER_CO2E, OTHER_PROCUREMENT_CO2E,
                        OTHER_MANUFACTURED_CO2E, MED_INSTR_CO2E,
                        CHEMS_AND_GASES_CO2E, ICT_CO2E, FREIGHT_CO2E,
                        CATERING_CO2E, CONSTUCTION_CO2E, BIZ_SVCS_CO2E,
                        CAPITAL_CO2E, WATER_CO2E, WASTE_CO2E, TRAVEL_CO2E,
                        /* TODO should be heat an steam */
                        ELEC_RENEWABLE_CO2E, STEAM_CO2E, ELEC_CO2E, COAL_CO2E, 
                        OIL_CO2E, GAS_CO2E},
                model);
        return "table";
    }
    
    private void fillModel(String org, String period, String[] headers,
            Model model) {
            List<SurveyAnswer> answers = answerRepo.findByOrgAndQuestion(org,
                headers);
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));

        TabularDataSet table = tdsHelper.tabulate(headers, answers);
        model.addAttribute("table", table);
        model.addAttribute("periods",
                PeriodUtil.fillBackwards(period, table.rows().length));
        model.addAttribute("messages",
                ResourceBundle.getBundle("digital.srp.sreport.Messages"));
    }

    // private List<SurveyReturn> addLinks(List<SurveyReturn> returns) {
    // for (SurveyReturn rtn : returns) {
    // addLinks(rtn);
    // }
    // return returns;
    // }
    //
    // private SurveyReturn addLinks(SurveyReturn rtn) {
    // List<Link> links = new ArrayList<Link>();
    // links.add(new
    // Link(baseUrl+getClass().getAnnotation(RequestMapping.class).value()[0] +
    // "/" + rtn.id()));
    //
    // return rtn.links(links);
    // }
}
