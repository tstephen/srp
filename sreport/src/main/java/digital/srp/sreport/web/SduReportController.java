package digital.srp.sreport.web;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.TabularDataSet;
import digital.srp.sreport.model.surveys.SduQuestions;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.tds.Aggregator;
import digital.srp.sreport.services.tds.TabularDataSetHelper;
import digital.srp.sreport.services.tds.Totaller;

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

    private static final String DEFAULT_MAX_PERIODS = "4";

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

    private DecimalFormat rawDecimalFormat = new DecimalFormat("#");
    private Format prettyPrintDecimalFormat = new SignificantFiguresFormat();

    private Aggregator totaller = new Totaller();

    /**
     * A table of organisation data for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/org.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String orgTable(@PathVariable("org") String org,
            @PathVariable("period") String period,
            @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("orgTable for %1$s %2$s", org, period));

        fillModel(org, period, ORG_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.empty());
        return "table-period-as-col";
    }

    /**
     * A table of energy use (kWh) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/energy.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String energyTable(@PathVariable("org") String org,
            @PathVariable("period") String period,
            @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("energyTable for %1$s %2$s", org, period));

        fillModel(org, period, ENERGY_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of energy use (kWh) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/energy.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String energyCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("energyCsv for %1$s %2$s", org, period));

        fillModel(org, period, ENERGY_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of energy use (CO2e) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/energy-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String energyCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(
                String.format("energyCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, ENERGY_CO2E_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of energy use (CO2e) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/energy-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String energyCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("energyCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period, ENERGY_CO2E_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of travel data for the specified organisation and period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/travel.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String travelTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("travelTable for %1$s %2$s", org, period));

        fillModel(org, period, TRAVEL_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of travel data emissions (CO2e) for the specified
     * organisation and period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/travel-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String travelCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("travelCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_3_TRAVEL_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of waste (tonnes) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/waste.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String wasteTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("wasteTable for %1$s %2$s", org, period));

        fillModel(org, period, WASTE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of waste (CO2e) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/waste-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String wasteCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("wasteCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, WASTE_CO2E_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of waste (CO2e) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/waste-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String wasteCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("wasteCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period, WASTE_CO2E_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of water use for the specified organisation and period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/water.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String waterTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("waterTable for %1$s %2$s", org, period));

        fillModel(org, period, WATER_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.empty());
        return "table-period-as-col";
    }

    /**
     * A table of water emissions (CO2e) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/water-co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String waterCO2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("waterCO2eTable for %1$s %2$s", org, period));

        fillModel(org, period, WATER_CO2E_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of water emissions (CO2e) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/water-co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String waterCO2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("waterCO2eCsv for %1$s %2$s", org, period));

        fillModel(org, period, WATER_CO2E_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of biomass well to tank emissions (CO2e) for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-wtt.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String biomassCO2eWttTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("biomassCO2eWttTable for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_WTT_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of biomass well to tank emissions (CO2e) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-wtt.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String biomassCO2eWttCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("biomassCO2eWttCsv for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_WTT_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of biomass out of scope emissions (CO2e) use for the specified organisation and
     * period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-noscope.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String biomassCO2eNoScopeTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("biomassCO2eNoScopeTable for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_NOSCOPE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of biomass out of scope emissions (CO2e) for the specified organisation and
     * period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/biomass-co2e-noscope.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String biomassCO2eNoScopeCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("biomassCO2eNoScopeCsv for %1$s %2$s", org, period));

        fillModel(org, period, BIOMASS_CO2E_NOSCOPE_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of categorised carbon emissions for the specified
     * organisation and period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-footprint.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String footprintTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("footprintTable for %1$s %2$s", org, period));

        // If only fetching one period make sure it is not lost off right edge
        boolean ascending = maxPeriods.equals(1) ? false : true;
        fillModel(org, period, FOOTPRINT_PCT_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, ascending, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of categorised carbon emissions for the specified
     * organisation and period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-footprint.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String footprintCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, Model model) {
        LOGGER.info(String.format("footprintCsv for %1$s %2$s", org, period));

        String[] headerNames = new String[FOOTPRINT_PCT_HDRS.length];
        for (int i = 0 ; i < FOOTPRINT_PCT_HDRS.length ; i++) {
            headerNames[i] = FOOTPRINT_PCT_HDRS[i].name();
        }
        model.addAttribute("periods", headerNames);
        
        List<Answer> answers = answerRepo.findByOrgPeriodAndQuestion(org, period, headerNames);
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));
        if (LOGGER.isDebugEnabled()) {
            for (Answer answer : answers) {
                LOGGER.debug(answer.toString());
            }
        }
        
        TabularDataSet table = tdsHelper.tabulate(headerNames, answers, rawDecimalFormat, Optional.empty());
//            Collections.reverse(periods);
            table = table.transpose();
        
        model.addAttribute("table", table);
        model.addAttribute("messages",
                ResourceBundle.getBundle("digital.srp.sreport.Messages"));
        return "csv-period-as-col";
    }

    /**
     * A table of categorised carbon emissions for the specified
     * organisation and period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-emissions-profile.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String emissionsProfileTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("emissionsProfileTable for %1$s %2$s", org, period));

        fillModel(org, period, PROFILE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of categorised carbon emissions for the specified
     * organisation and period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-emissions-profile.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String emissionsProfileCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("emissionsProfileCsv for %1$s %2$s", org, period));

        fillModel(org, period, PROFILE_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * A table of categorised carbon emissions in ratio to expenditure
     * for the specified organisation and period.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-by-expenditure.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String carbonByExpenditureTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("carbonByExpenditureTable for %1$s %2$s", org, period));

        fillModel(org, period, SPEND_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of categorised carbon emissions in ratio to expenditure
     * for the specified organisation and period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-by-expenditure.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String carbonByExpenditureCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("carbonByExpenditureCsv for %1$s %2$s", org, period));

        fillModel(org, period, SPEND_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /***** TREASURY OUPUTS ****/

    /**
     * A summary table of emissions by scope.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-summary.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scopeSummaryTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("scopeSummaryTable for %1$s %2$s", org, period));

        fillModel(org, period, SUMMARY_SCOPE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A summary table of emissions by scope.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-1.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope1Table(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("scope1Table for %1$s %2$s", org, period));

         fillModel(org, period, SCOPE_1_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
//        fillModel(org, period, SCOPE_1_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.empty());
        return "table-period-as-col";
    }

    /**
     * A summary table of emissions by scope.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-2.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope2Table(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("scope2Table for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_2_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A summary table of emissions by scope.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/scope-3.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String scope3Table(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("scope3Table for %1$s %2$s", org, period));

        fillModel(org, period, SCOPE_3_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A summary table of emissions by scope.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/sdu-carbon-profile.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String sduCarbonProfileTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("sduCarbonProfileTable for %1$s %2$s", org, period));

        fillModel(org, period, SDU_CARBON_PROFILE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A summary table of emissions by E-Class.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/eclass-carbon-profile.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String eclassCarbonProfileTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("eclassCarbonProfileTable for %1$s %2$s", org, period));

        fillModel(org, period, ECLASS_PROFILE_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A trajectory table of emissions by SDU groupings.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-trajectory.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String carbonTrajectoryTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("carbonTrajectoryTable for %1$s %2$s", org, period));

        fillModel(org, period, SDU_TREND_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * A table of categorised carbon emissions compared to target for the
     * specified organisation and period.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/carbon-trajectory.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String carbonTrajectoryCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("carbonTrajectoryCsv for %1$s %2$s", org, period));

        fillModel(org, period, SDU_TREND_HDRS, model, false, rawDecimalFormat, 
                maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by population.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-population.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByPopulationTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByPopulationTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_POPULATION_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by population.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-population.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByPopulationCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByPopulationTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_POPULATION_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by gross internal area.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-floor-area.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByFloorAreaTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByFloorAreaTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_FLOOR_AREA_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by gross internal area.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-floor-area.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByFloorAreaCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByFloorAreaTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_FLOOR_AREA_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by number of staff.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-wte.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByWteTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByWteTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_WTE_HDRS, model, true, 
                prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by number of staff.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-wte.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByWteCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByWteTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_WTE_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by number of occupied beds.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-beds.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByBedsTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByBedsTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_BEDS_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by number of occupied beds.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-beds.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByBedsCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByBedsTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_BEDS_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by patient contacts.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-patient-contacts.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByPatientContactsTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByPatientContactsTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_PATIENT_CONTACTS_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by patient contacts.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-patient-contacts.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByPatientContactsCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByPatientContactsTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_PATIENT_CONTACTS_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    /**
     * Total Carbon footprint by operating expenditure.
     *
     * @return HTML table.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-opex.html", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String co2eByOpExTable(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByOpExTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_OPEX_HDRS, model, true, prettyPrintDecimalFormat, maxPeriods, true, Optional.of(totaller));
        return "table-period-as-col";
    }

    /**
     * Total Carbon footprint by operating expenditure.
     *
     * @return CSV with header row.
     */
    @RequestMapping(value = "/{org}/{period}/co2e-by-opex.csv", method = RequestMethod.GET, produces = "text/csv")
    @Transactional
    public String co2eByOpExCsv(@PathVariable("org") String org,
            @PathVariable("period") String period, @RequestParam(value = "maxPeriods", required = false, defaultValue = DEFAULT_MAX_PERIODS) Integer maxPeriods,
            Model model) {
        LOGGER.info(String.format("co2eByOpExTable for %1$s %2$s", org, period));

        fillModel(org, period, BENCHMARK_BY_OPEX_HDRS, model, false, rawDecimalFormat, maxPeriods, true, Optional.empty());
        return "csv";
    }

    private void fillModel(String org, String period, Q[] headers, Model model,
            boolean periodAsCol, Format decimalFormat, Integer maxPeriods, 
            boolean ascending, Optional<Aggregator> aggregator) {
        String[] headerNames = new String[headers.length];
        for (int i = 0 ; i < headers.length ; i++) {
            headerNames[i] = headers[i].name();
        }

        List<Answer> answers;
        if (ascending) {
            answers = answerRepo.findByOrgAndQuestionAsc(org, headerNames);
        } else {
            answers = answerRepo.findByOrgAndQuestion(org, headerNames);
        }
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));
        if (LOGGER.isDebugEnabled()) {
            for (Answer answer : answers) {
                LOGGER.debug(answer.toString());
            }
        }

        TabularDataSet table = tdsHelper.tabulate(headerNames, answers, decimalFormat, aggregator);
        List<String> periods = PeriodUtil.fillBackwards(period,
                table.rows().length < maxPeriods ? table.rows().length : maxPeriods);
                model.addAttribute("periods", periods);
        if (ascending) {
            Collections.reverse(periods);
        }
        if (periodAsCol) {
            table = table.transpose();
        }
        
        model.addAttribute("table", table);
        model.addAttribute("messages",
                ResourceBundle.getBundle("digital.srp.sreport.Messages"));
    }

}
