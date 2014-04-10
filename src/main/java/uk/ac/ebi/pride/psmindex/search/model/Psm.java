package uk.ac.ebi.pride.psmindex.search.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 */
public class Psm {

    @Field(PsmFields.ID)
    private String id;

    //Internal id in the mzTabFile
    @Field(PsmFields.REPORTED_ID)
    private String reportedId;

    @Field(PsmFields.PEPTIDE_SEQUENCE)
    private String pepSequence;

    // If the psm was discovered as a combination of several spectra, we will
    // simplify the case choosing only the first spectrum
    @Field(PsmFields.SPECTRUM_ID)
    private String spectrumId;

    @Field(PsmFields.PROTEIN_ACCESSION)
    private String proteinAccession;

    @Field(PsmFields.PROJECT_ACCESSION)
    private String projectAccession;

    @Field(PsmFields.ASSAY_ACCESSION)
    private String assayAccession;

    @Field(PsmFields.MODIFICATIONS)
    private List<String> modifications;

    @Field(PsmFields.UNIQUE)
    private Boolean unique;

    @Field(PsmFields.SEARCH_ENGINE)
    private List<String> searchEngine;

    @Field(PsmFields.SEARCH_ENGINE_SCORE)
    private List<String> searchEngineScore;

    // If the psm was discovered as a combination of several spectra, we will
    // simplify the case choosing only the first spectrum and the first retention time
    @Field(PsmFields.RETENTION_TIME)
    private Double retentionTime;

    @Field(PsmFields.CHARGE)
    private Integer charge;

    @Field(PsmFields.EXPERIMENTAL_MASS_TO_CHARGE)
    private Double expMassToCharge;

    @Field(PsmFields.CALCULATED_MASS_TO_CHARGE)
    private Double calculatedMassToCharge;

    @Field(PsmFields.PRE_AMINO_ACID)
    private String preAminoAcid;

    @Field(PsmFields.POST_AMINO_ACID)
    private String postAminoAcid;

    @Field(PsmFields.START_POSITION)
    private Integer startPosition;

    @Field(PsmFields.END_POSITION)
    private Integer endPosition;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public String getPepSequence() {
        return pepSequence;
    }

    public void setPepSequence(String pepSequence) {
        this.pepSequence = pepSequence;
    }

    public String getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(String spectrumId) {
        this.spectrumId = spectrumId;
    }

    public String getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(String proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public String getProjectAccession() {
        return projectAccession;
    }

    public void setProjectAccession(String projectAccession) {
        this.projectAccession = projectAccession;
    }

    public String getAssayAccession() {
        return assayAccession;
    }

    public void setAssayAccession(String assayAccession) {
        this.assayAccession = assayAccession;
    }

    public List<String> getModifications() {
        return modifications;
    }

    public void setModifications(List<String> modifications) {
        this.modifications = modifications;
    }

    public Boolean isUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public List<String> getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(List<String> searchEngine) {
        this.searchEngine = searchEngine;
    }

    public List<String> getSearchEngineScore() {
        return searchEngineScore;
    }

    public void setSearchEngineScore(List<String> searchEngineScore) {
        this.searchEngineScore = searchEngineScore;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Double getExpMassToCharge() {
        return expMassToCharge;
    }

    public void setExpMassToCharge(Double expMassToCharge) {
        this.expMassToCharge = expMassToCharge;
    }

    public Double getCalculatedMassToCharge() {
        return calculatedMassToCharge;
    }

    public void setCalculatedMassToCharge(Double calculatedMassToCharge) {
        this.calculatedMassToCharge = calculatedMassToCharge;
    }

    public String getPreAminoAcid() {
        return preAminoAcid;
    }

    public void setPreAminoAcid(String preAminoAcid) {
        this.preAminoAcid = preAminoAcid;
    }

    public String getPostAminoAcid() {
        return postAminoAcid;
    }

    public void setPostAminoAcid(String postAminoAcid) {
        this.postAminoAcid = postAminoAcid;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }
}
