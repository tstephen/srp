package digital.srp.sreport.model.surveys;

import java.util.Arrays;

import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;

/**
 * 
 * @author Tim Stephenson
 */
public class Sdu1516 {
    
    public static final String ID = "Sdu-201516";

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Name of organisation").required(true),
                        new SurveyQuestion().text("Organisation code e.g. RAA").required(true),
                        new SurveyQuestion().text("Organisation type").required(true),
                        new SurveyQuestion().text("Commissioning region").required(true)
                ));

        SurveyCategory catProfile = new SurveyCategory()
                .name("Trust Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Number of sites - General acute hospital (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Specialist hospital (acute only) (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Mixed service hospital (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Mental Health (including Specialist services) (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Learning Disabilities (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Mental Health and Learning Disabilities (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Community hospital (with inpatient beds) (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Other inpatient (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Non inpatient (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Support facilities (No.)").required(true),
                        new SurveyQuestion().text("Number of sites - Unreported sites (No.)").required(true)
                ));
        
        SurveyCategory catStrategy = new SurveyCategory()
                .name("Strategies and Policies")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Estates Development Strategy").required(true),
                        new SurveyQuestion().text("Healthy transport plan").required(true),
                        new SurveyQuestion().text("Board approved Adaptation Plan").required(true),
                        new SurveyQuestion().text("Sustainable Development Management Plan/Carbon Reduction Management Plan ").required(true),
                        new SurveyQuestion().text("Carbon reduction target").required(true),
                        new SurveyQuestion().text("NHS Premises and Facilities Assurance - Assessment/Approval").required(true),
                        new SurveyQuestion().text("NHS Premises and Facilities Assurance - action plan").required(true)
                ));

        SurveyCategory catContractingOut = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Value of contracted out services").required(true),
                        new SurveyQuestion().text("Percentage of hard FM (estates) and soft FM (hotel services) contracted out").required(true)
                ));

        SurveyCategory catFinance = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Capital investment for new build").required(true),
                        new SurveyQuestion().text("Capital investment for improving existing buildings").required(true),
                        new SurveyQuestion().text("Capital investment for equipment").required(true),
                        new SurveyQuestion().text("Private Sector investment").required(true),
                        new SurveyQuestion().text("Investment to reduce backlog maintenance").required(true),
                        new SurveyQuestion().text("Cost to meet NHS Premises and Facilities Assurance action plan").required(true),
                        new SurveyQuestion().text("Non-emergency patient transport").required(true),
                        new SurveyQuestion().text("Income from services provided to other organisations - catering").required(true),
                        new SurveyQuestion().text("Income from services provided to other organisations - laundry and linen").required(true),
                        new SurveyQuestion().text("Income from services provided to other organisations - other").required(true)
                ));
        
        SurveyCategory catSafety = new SurveyCategory()
                .name("Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("RIDDOR incidents").required(true),
                        new SurveyQuestion().text("Estates and facilities related incidents").required(true),
                        new SurveyQuestion().text("Clinical service incidents caused by estates and infrastructure failure").required(true)
                ));

        SurveyCategory catFireSafety = new SurveyCategory()
                .name("Fire Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Fires recorded").required(true),
                        new SurveyQuestion().text("False alarms").required(true),
                        new SurveyQuestion().text("Number of deaths resulting from fire(s)").required(true),
                        new SurveyQuestion().text("Number of people injured resulting from fire(s)").required(true),
                        new SurveyQuestion().text("Number of patients sustaining injuries during evacuation").required(true)
                ));
        
        Survey survey = new Survey().name(ID).status("Draft")
                .applicablePeriod("2015-16")
                .categories(Arrays.asList(catOrg, catProfile, catStrategy, catContractingOut, catFinance, catSafety, catFireSafety));
        return survey;
    }
}