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

    @Field(PsmFields.PEPTIDE_SEQUENCE)
    private String pepSequence;

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
