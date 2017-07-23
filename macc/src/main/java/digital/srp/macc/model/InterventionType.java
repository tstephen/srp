/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.maths.Finance;
import digital.srp.macc.views.InterventionTypeViews;
import digital.srp.macc.views.InterventionViews;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An action or related set of actions aimed at providing Carbon Abatement or
 * other sustainability benefit.
 *
 * @author Tim Stephenson
 */
@Data
@Entity
@Table(name = "TR_INTVN_TYPE")
@AllArgsConstructor
public class InterventionType implements CsvSerializable {

    private static final String DEFAULT_FURTHER_INFO_BASE_URL = "http://srp.digital/interventions";

    private static final long serialVersionUID = -7820188383611260653L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionType.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    private Long id;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @NotNull
    @Column(name = "name")
    private String name;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Column(name = "description")
    @Lob
    private String description;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "existing")
    private boolean existing;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Column(name = "status")
    private String status;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "data_status")
    private String dataStatus;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "analysis_status")
    private String analysisStatus;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "strategic_focus")
    private String strategicFocus;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "tactical_driver")
    private String tacticalDriver;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "operational_sub_category")
    private String operationalSubCategory;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Column(name = "classification")
    private String classification;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "cross_organisation")    
    private boolean crossOrganisation;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "note")
    @Lob
    private String note;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "client_note")
    @Lob
    private String clientNote;

    /**
     * The year this intervention's analysis was last updated.
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Min(1970)
    @Max(2099)
    @Column(name = "modelling_year")
    private int modellingYear;

    /**
     * Lifetime time (Years)
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Column(name = "LIFETIME", nullable = true)
    @NotNull
    private Short lifetime;

    /**
     * Lifetime time (Years)
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "LEAD_TIME")
    private int leadTime;

    /**
     * Percentage of suitable organisations predicted to employ this
     * intervention.
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Min(value = 0)
    @Max(100)
    @Column(name = "UPTAKE", nullable = true)
    @NotNull
    private Short uptake;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Min(value = 0)
    @Max(100)
    @Column(name = "SCALING", nullable = true)
    @NotNull
    private Float scaling;

    /**
     * Confidence factor for this intervention.
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Summary.class })
    @Min(value = 0)
    @Max(100)
    @Column(name = "confidence")
    private Short confidence = 50;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Detailed.class })
    @Column(name = "further_info")
    private String furtherInfo;

    @Basic
    @Column(name = "overlapping_interventions")
    @Lob
    private String overlappingInterventions;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    private double costPerTonneCo2e;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "annual_cash_inflows")
    private BigDecimal annualCashInflows;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "annual_cash_inflowsts")
    @Lob
    private String annualCashInflowsTS;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Detailed.class })
    @Column(name = "cash_outflows_up_front")
    private BigDecimal cashOutflowsUpFront;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "annual_cash_outflows")
    private BigDecimal annualCashOutflows;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "annual_cash_outflowsts")
    @Lob
    private String annualCashOutflowsTS;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    private double totalNpv;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "TONNES_CO2E_SAVED_PA", nullable = true)
    private BigDecimal annualTonnesCo2eSaved;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "annual_tonnes_co2e_savedts")
    @Lob
    private String annualTonnesCo2eSavedTS;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Basic
    @Column(name = "UNIT_GAS_SAVED_PA", nullable = true)
    private BigDecimal annualGasSaved;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "UNIT_ELEC_SAVED_PA", nullable = true)
    private BigDecimal annualElecSaved;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "TONNES_CO2E_SAVED", nullable = true)
    private Integer tonnesCo2eSaved;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "UNIT_COUNT", nullable = true)
    private Integer unitCount;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "UNIT", nullable = true)
    private String unit;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "UNIT_DESC", nullable = true)
    @Lob
    private String unitDescription;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Column(name = "tenant_id")
    private String tenantId;

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Detailed.class })
    @Column(name = "TONNES_CO2E_SAVED_TY", nullable = true)
    @Transient
    private BigDecimal tonnesCo2eSavedTargetYear;

    @Transient
    private ModelParameter targetYear;

    @Transient
    private ModelParameter gasPrice;

    @Transient
    private ModelParameter gasCIntensity;

    @Transient
    private ModelParameter elecPrice;

    @Transient
    private ModelParameter elecCIntensity;

    @Value("${srp.interventions.baseurl:http://srp.digital/interventions}")
    private String furtherInfoBaseUrl;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView({ InterventionTypeViews.Summary.class })
    private List<Link> links;

    public InterventionType() {
        setTargetYear(new ModelParameter("targetYear", new BigDecimal(2020)));
        setGasPrice(new ModelParameter(
                "gasPrice",
                "2.630438796,2.6758257306,2.6927658257,2.704685584,2.8355425019,2.9457682014,3.055993901,3.1662,3.2764,3.3867,3.4393483324,3.4393483324,3.4393483324,3.44938211,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212,3.4443652212"));
        setGasCIntensity(new ModelParameter("gasCIntensity",
                BigDecimal.valueOf(0.2093).setScale(8)));
        setElecPrice(new ModelParameter(
                "elecPrice",
                "10.778497544,11.3254211661,11.2764200234,11.9594510085,12.4989878402,13.0661699331,13.3246184308,13.5857994648,14.4215706393,14.994396824,15.0842780665,15.1942317008,14.7130438837,14.6465132209,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098,14.7868165098"));
        setElecCIntensity(new ModelParameter(
                "elecCIntensity",
                "0.471625378,0.4033756698,0.3900141326,0.3664321951,0.3264792461,0.2875202196,0.2647940548,0.2254603806,0.2367575244,0.2218887201,0.1972458379,0.1950581396,0.1636321693,0.1430514555,0.1379336593,0.1301656179,0.122843638,0.1056435916,0.104390507,0.086765916,0.0850489651,0.0742383586,0.0664869042,0.0674138255,0.0629367669,0.0559701421,0.0554960843,0.0493361816,0.0436137087,0.0442696444,0.0403077739,0.0373145099,0.0377322418,0.0341612201,0.0338541865"));
    }

    public int getModellingYear() {
        if (modellingYear == 0) {
            modellingYear = 2015;
        }
        return modellingYear;
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public List<BigDecimal> getAnnualCashOutflowsTimeSeries() {
        return new TimeSeries(annualCashOutflowsTS).asList();
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public void setAnnualCashOutflowsTimeSeries(List<BigDecimal> values) {
        annualCashOutflowsTS = TimeSeries.asString(values);
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public List<BigDecimal> getAnnualCashInflowsTimeSeries() {
        return new TimeSeries(annualCashInflowsTS).asList();
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public void setAnnualCashInflowsTimeSeries(List<BigDecimal> values) {
        annualCashInflowsTS = TimeSeries.asString(values);
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public List<BigDecimal> getAnnualTonnesCo2eSavedTimeSeries() {
        return new TimeSeries(annualTonnesCo2eSavedTS).asList();
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    @Transient
    public void setAnnualTonnesCo2eSavedTimeSeries(List<BigDecimal> values) {
        annualTonnesCo2eSavedTS = TimeSeries.asString(values);
    }
    
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getCashOutflowsUpFront() {
        return cashOutflowsUpFront == null ? Finance.ZERO_BIG_DECIMAL
                : cashOutflowsUpFront;
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getCashOutflowsUpFrontNational() {
        return getCashOutflowsUpFront().multiply(getUptakeFactor()).divide(
                getScaleFactor(), Finance.ROUND_MODE);
    }

    public BigDecimal getAnnualCashOutflows(int year) {
        if (getAnnualCashOutflowsTimeSeries() != null
                && getAnnualCashOutflowsTimeSeries().size() > year) {
            return getAnnualCashOutflowsTimeSeries().get(year);
        } else if (annualCashOutflows == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return annualCashOutflows;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getAnnualCashOutflowsNationalTargetYear() {
        return getAnnualCashOutflows(getTargetYearIndex()).multiply(
                getUptakeFactor()).divide(getScaleFactor(), Finance.ROUND_MODE);
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getAnnualCashOutflowsTargetYear() {
        return getAnnualCashOutflows(getTargetYearIndex());
    }

    public BigDecimal getAnnualCashInflows(int year) {
        if (annualGasSaved != null || annualElecSaved != null) {
            BigDecimal fromGas = annualGasSaved == null ? Finance.ZERO_BIG_DECIMAL
                    : getAnnualCashInflowsFromGas(year);
            BigDecimal fromElec = annualElecSaved == null ? Finance.ZERO_BIG_DECIMAL
                    : getAnnualCashInflowsFromElec(year);
            return fromGas.add(fromElec);
        } else if (getAnnualCashInflowsTimeSeries() != null
                && getAnnualCashOutflowsTimeSeries().size() >= year) {
            return getAnnualCashInflowsTimeSeries().get(year);
        } else if (annualCashInflows == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else if (getLeadTime() > year) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return annualCashInflows;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getAnnualCashInflowsNationalTargetYear() {
        return getAnnualCashInflows(getTargetYearIndex()).multiply(
                getUptakeFactor()).divide(getScaleFactor(), Finance.ROUND_MODE);
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getAnnualCashInflowsTargetYear() {
        return getAnnualCashInflows(getTargetYearIndex());
    }

    public BigDecimal getAnnualCashInflowsFromGas(int year) {
        if (gasPrice == null) {
            throw new IllegalStateException(
                    "Must inject gasPrice before calling getAnnualCashInflowsFromGas");
        }
        return gasPrice.getValueTimeSeries().get(year)
                .divide(new BigDecimal(100)).multiply(annualGasSaved);
    }

    public BigDecimal getAnnualCashInflowsFromElec(int year) {
        if (elecPrice == null) {
            throw new IllegalStateException(
                    "Must inject elecPrice before calling getAnnualCashInflowsFromElec");
        }
        return elecPrice.getValueTimeSeries().get(year)
                .divide(new BigDecimal(100), Finance.ROUND_MODE)
                .multiply(annualElecSaved);
    }

    /**
     * @return Total of all cash outflows adjusted for uptake in future value
     *         terms.
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public Long getTotalCashOutflows() {
        try {
            BigDecimal total = new BigDecimal("0.00");
            for (int i = 0; i < getLifetime(); i++) {
                total = total.add(getAnnualCashOutflows(i));
            }
            total = total.add(cashOutflowsUpFront);
            return total.multiply(getUptakeFactor()).longValue();
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTotalNpv", e);
            return 0l;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public Long getTotalCashInflows() {
        try {
            BigDecimal total = new BigDecimal("0.00");
            for (int i = 0; i < getLifetime(); i++) {
                total = total.add(getAnnualCashInflows(i));
            }
            return total.multiply(getUptakeFactor()).longValue();
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTotalNpv", e);
            return 0l;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public Long getMeanCashInflows() {
        try {
            return (long) Math.round((getTotalCashInflows() / getLifetime()));
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTotalNpv", e);
            return 0l;
        }
    }

    /**
     * @return simple outflows - inflows in target year, excluding capital costs
     *         and not converted into today's prices.
     */
    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getTargetYearSavings() {
        try {
            int yearIndex = getTargetYearIndex();
            BigDecimal inflows = getAnnualCashInflows(yearIndex).multiply(
                    getUptakeFactor());
            BigDecimal outflows = getAnnualCashOutflows(yearIndex).multiply(
                    getUptakeFactor());

            return outflows.subtract(inflows);
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTargetYearSavings", e);
            return Finance.ZERO_BIG_DECIMAL;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getTotalNpv() {
        try {
            BigDecimal ongoingFlows = new BigDecimal("0.00");
            for (int i = 0; i < getLifetime(); i++) {
                BigDecimal inflows = new BigDecimal("0.00");
                if (getAnnualCashInflows(i).longValue() > 0) {
                    inflows = getAnnualCashInflows(i).multiply(
                            getUptakeFactor());
                }
                BigDecimal outflows = getAnnualCashOutflows(i).multiply(
                        getUptakeFactor());
                // System.out.println("  " + i + ": in=" + inflows +
                // ", out="
                // + outflows);
                BigDecimal npv = Finance.presentValue(
                        outflows.subtract(inflows), i + 1, 0.035);
                // System.out.println("  npv:" + npv);
                ongoingFlows = ongoingFlows.add(npv);
            }
            BigDecimal upFront = getUptakeFactor().multiply(
                    getCashOutflowsUpFront());
            return upFront.add(ongoingFlows).divide(getScaleFactor(),
                    Finance.ROUND_MODE);
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTotalNpv", e);
            return Finance.ZERO_BIG_DECIMAL;
        }
    }

    public BigDecimal getTonnesCo2eSavedFromGasInYear(int year) {
        if (gasCIntensity == null) {
            throw new IllegalStateException(
                    "Must inject gasCIntensity before calling getTonnesCo2eSavedFromGasInYear");
        }
        return annualGasSaved == null ? Finance.ZERO_BIG_DECIMAL
                : annualGasSaved.multiply(gasCIntensity.getValue().divide(
                        new BigDecimal(1000), Finance.ROUND_MODE));
    }

    public BigDecimal getTonnesCo2eSavedFromElecInYear(int year) {
        if (elecCIntensity == null) {
            throw new IllegalStateException(
                    "Must inject elecCIntensity before calling getTonnesCo2eSavedFromElecInYear");
        }
        return annualElecSaved == null ? Finance.ZERO_BIG_DECIMAL
                : annualElecSaved.multiply(
                        elecCIntensity.getValueTimeSeries().get(year)).divide(
                        new BigDecimal(1000), Finance.ROUND_MODE);
    }

    public BigDecimal getTonnesCo2eSavedTargetYear() {
        try {
            if (annualGasSaved != null || annualElecSaved != null) {
                BigDecimal tonnesCo2eSavedTY = getTonnesCo2eSavedFromGasInYear(
                        getTargetYearIndex()).add(
                        getTonnesCo2eSavedFromElecInYear(getTargetYearIndex()));
                return tonnesCo2eSavedTY.multiply(getUptakeFactor())
                        .divideToIntegralValue(getScaleFactor());
            } else {
                BigDecimal tonnes = getAnnualTonnesCo2eSaved();
                if (tonnes == null) {
                    int idx = getTargetYearIndex();
                    tonnes = getAnnualTonnesCo2eSavedTimeSeries().get(idx);
                }
                return tonnes.multiply(getUptakeFactor().divide(
                        getScaleFactor(), Finance.ROUND_MODE));
            }
        } catch (NullPointerException e) {
            LOGGER.error("NullPointerException in getTonnesCo2eSavedTargetYear");
            return new BigDecimal(0);
        }
    }

    public int getTargetYearIndex() {
        return targetYear.getValue().intValue() - getModellingYear() - 1;
    }

    public Long getTonnesCo2eSaved() {
        if (tonnesCo2eSaved == null) {
            return 0l;
        } else {
            return (long) Math.round(tonnesCo2eSaved);
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class })
    public BigDecimal getTotalTonnesCo2eSaved() {
        if (annualTonnesCo2eSaved == null && annualTonnesCo2eSavedTS == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else if (annualTonnesCo2eSaved == null) {
            BigDecimal total = new BigDecimal(0.0f);
            for (BigDecimal tonnes : getAnnualTonnesCo2eSavedTimeSeries()) {
                total = total.add(tonnes);
            }
            return total.multiply(getUptakeFactor());
        } else if (this.annualGasSaved != null || this.annualElecSaved != null) {
            BigDecimal tonnes = new BigDecimal(0.0f);
            for (int i = 0; i < getLifetime(); i++) {
                BigDecimal fromGas = getTonnesCo2eSavedFromGasInYear(i);
                BigDecimal fromElec = getTonnesCo2eSavedFromElecInYear(i);
                tonnes = tonnes.add(fromGas).add(fromElec);
            }
            return tonnes.multiply(getUptakeFactor().divide(getScaleFactor(),
                    Finance.ROUND_MODE));
        } else {
            return getAnnualTonnesCo2eSaved().multiply(
                    getUptakeFactor().multiply(new BigDecimal(getLifetime())));
        }
    }

    @JsonProperty
    public BigDecimal getCostPerTonneCo2e() {
        if (costPerTonneCo2e == 0) {
            return getTotalNpv().divide(getTotalTonnesCo2eSaved(),
                    Finance.ROUND_MODE);
        } else {
            return new BigDecimal(costPerTonneCo2e);
        }
    }

    public String toCsvHeader() {
        return "Internal Id,Intervention Type,Organisation Type,"
                + "Annual Cash Inflows,Annual Cash Inflows Time Series,Mean Cash Inflows,"
                + "Annual Cash Outflows,Annual Cash Outflows Time Series,"
                + "Total NPV,"
                + "Annual Tonnes CO2e Saved,Annual Tonnes Co2e Saved Time Series,"
                + "Total Tonnes CO2e Saved,Tonnes CO2e Saved Target Year,"
                + "Cost Per Tonne CO2e";
    }

    public BigDecimal getScaleFactor() {
        return scaling == null ? null : new BigDecimal(
                scaling.floatValue() / 100);
    }

    public BigDecimal getUptakeFactor() {
        return uptake == null ? null
                : new BigDecimal(uptake.floatValue() / 100);
    }

    public String getFurtherInfoBaseUrl() {
        if (furtherInfoBaseUrl == null) {
            furtherInfoBaseUrl = DEFAULT_FURTHER_INFO_BASE_URL;
        }
        if (!furtherInfoBaseUrl.endsWith("/")) {
            furtherInfoBaseUrl += "/";
        }
        return furtherInfoBaseUrl;
    }

    public String getFurtherInfo() {
        if (furtherInfo == null) {
            return getFurtherInfoBaseUrl()
                    + getName().toLowerCase().replaceAll("[ '\"()/]", "-")
                            .replaceAll("---", "-");
        } else {
            return furtherInfo;
        }
    }

    @JsonProperty
    @JsonView({ InterventionTypeViews.Summary.class, InterventionViews.Detailed.class })
    public List<String> getOverlappingInterventionList() {
        if (overlappingInterventions == null
                || overlappingInterventions.length() == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(overlappingInterventions.split(","));
        }
    }

    public void setOverlappingInterventionList(List<String> interventions) {
        overlappingInterventions = interventions.toString()
                .substring(1, interventions.toString().length() - 1)
                .replaceAll(", ", ",");
    }

    public String toCsv() {
        return String
                .format("%s,\"%s\",\"%s\",%s,%s,%s,\"%s\",%s,%s,\"%s\",%s,%s,%s",
                        id, name, annualCashInflows,
                        annualCashInflowsTS == null ? ""
                                : getAnnualCashInflowsTimeSeries(),
                        getMeanCashInflows() == null ? ""
                                : getMeanCashInflows(), annualCashOutflows,
                        annualCashOutflowsTS == null ? ""
                                : getAnnualCashOutflowsTimeSeries(),
                        getTotalNpv(), annualTonnesCo2eSaved,
                        annualTonnesCo2eSavedTS == null ? ""
                                : getAnnualTonnesCo2eSavedTimeSeries(),
                        getTotalTonnesCo2eSaved(),
                        getTonnesCo2eSavedTargetYear() == null ? ""
                                : getTonnesCo2eSavedTargetYear(),
                        getCostPerTonneCo2e());
    }

}
