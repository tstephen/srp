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
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyAnswer;
import digital.srp.sreport.model.TabularDataSet;
import digital.srp.sreport.model.surveys.SduQuestions;
import digital.srp.sreport.repositories.SurveyAnswerRepository;
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
    protected SurveyAnswerRepository answerRepo;

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

        fillModel(org, period, ORG_HDRS, model);
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

        fillModel(org, period, ENERGY_HDRS, model);
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

        fillModel(org, period, ENERGY_HDRS, model);
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

        fillModel(org, period, ENERGY_CO2E_HDRS, model);
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

        fillModel(org, period, ENERGY_CO2E_HDRS, model);
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

        fillModel(org, period, TRAVEL_HDRS, model);
        return "table";
    }

    /**
     * Return a table of travel data emissions (CO2e) for the specified 
     * organisation and period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/travel-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String travelCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("travelCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, TRAVEL_HDRS, model);
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

        fillModel(org, period, WASTE_HDRS, model);
        return "table";
    }
    
    /**
     * Return a table of waste (CO2e) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/waste-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String wasteCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("wasteCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, WASTE_CO2E_HDRS, model);
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
    public String wasteCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("wasteCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period, WASTE_CO2E_HDRS, model);
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

        fillModel(org, period, WATER_HDRS, model);
        return "table";
    }
    
    /**
     * Return a table of water emissions (CO2e) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/water-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String waterCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("waterCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, WATER_CO2E_HDRS, model);
        return "table";
    }
    
    /**
     * Return a table of water emissions (CO2e) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/water-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String waterCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("waterCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period, WATER_CO2E_HDRS, model);
        return "csv";
    }
    
    /**
     * Return a table of biomass well to tank emissions (CO2e) for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-wtt.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String biomassCO2eWttTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("biomassCO2eWttTable for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_WTT_HDRS, model);
        return "table";
    }
    
    /**
     * Return a table of biomass well to tank emissions (CO2e) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-wtt.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String biomassCO2eWttCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("biomassCO2eWttCsv for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_WTT_HDRS, model);
        return "csv";
    }
    
    /**
     * Return a table of biomass out of scope emissions (CO2e) use for the specified organisation and
     * period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-noscope.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String biomassCO2eNoScopeTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("biomassCO2eNoScopeTable for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_NOSCOPE_HDRS, model);
        return "table";
    }
    
    /**
     * Return a table of biomass out of scope emissions (CO2e) for the specified organisation and
     * period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-noscope.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String biomassCO2eNoScopeCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("biomassCO2eNoScopeCsv for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_NOSCOPE_HDRS, model);
        return "csv";
    }
    
    /**
     * Return a table of categorised carbon emissions for the specified
     * organisation and period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-footprint.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String footprintTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("footprintTable for %1$s %2$s", org, period));

        fillModel(org, period, FOOTPRINT_HDRS, model);
        return "table";
    }

    /**
     * Return a table of categorised carbon emissions for the specified
     * organisation and period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-footprint.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String footprintCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("footprintCsv for %1$s %2$s", org, period));

        fillModel(org, period, FOOTPRINT_HDRS, model);
        return "csv";
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

        fillModel(org, period, PROFILE_HDRS, model);
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
        LOGGER.info(String.format("emissionsProfileCsv for %1$s %2$s", org, period));

        fillModel(org, period, PROFILE_HDRS, model);
        return "csv";
    }
    
    /**
     * Return a table of categorised carbon emissions in ratio to expenditure
     * for the specified organisation and period.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-by-expenditure.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String carbonByExpenditureTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("carbonByExpenditureTable for %1$s %2$s", org, period));

        fillModel(org, period, SPEND_HDRS, model);
        return "table";
    }

    /**
     * Return a table of categorised carbon emissions in ratio to expenditure
     * for the specified organisation and period.
     * 
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-by-expenditure.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String carbonByExpenditureCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("carbonByExpenditureCsv for %1$s %2$s", org, period));

        fillModel(org, period, SPEND_HDRS, model);
        return "csv";
    }
    
    /***** TREASURY OUPUTS ****/
    
    /**
     * Return a summary table of emissions by scope.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-summary.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scopeSummaryTable(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("scopeSummaryTable for %1$s %2$s", org, period));

        fillModel(org, period, SUMMARY_SCOPE_HDRS, model);
        return "table";
    }
    
    /**
     * Return a summary table of emissions by scope.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-1.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope1Table(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("scope1Table for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_1_HDRS, model);
        return "table";
    }
    
    /**
     * Return a summary table of emissions by scope.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-2.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope2Table(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("scope2Table for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_2_HDRS, model);
        return "table";
    }
    
    /**
     * Return a summary table of emissions by scope.
     * 
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-3.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope3Table(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("scope3Table for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_3_HDRS, model);
        return "table";
    }
    
    private void fillModel(String org, String period, Q[] headers, Model model) {
        String[] headerNames = new String[headers.length];
        for (int i = 0 ; i < headers.length ; i++) {
            headerNames[i] = headers[i].name();
        }
        
        List<SurveyAnswer> answers = answerRepo.findByOrgAndQuestion(org,
            headerNames);
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));

        TabularDataSet table = tdsHelper.tabulate(headerNames, answers);
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
