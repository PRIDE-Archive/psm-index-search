package uk.ac.ebi.pride.psmindex.search.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 */
public class Psm {

    @Field(PsmFields.ID)
    private String id;

    @Field(PsmFields.PEP_SEQUENCE)
    private String pepSequence;

    @Field(PsmFields.SPECTRUM_ID)
    private String spectrumId;

    @Field(PsmFields.PROTEIN_ACCESSION)
    private String proteinAccession;

    @Field(PsmFields.PROJECT_ACCESSION)
    private String projectAccession;

    @Field(PsmFields.ASSAY_ACCESSION)
    private String assayAccession;

    //At this moment it stores (position, {mod_accession,mod_name})
    @Field(PsmFields.MODS_WITH_LOCATIONS)
    private Map<String,List<String>> modsAccessions;

    //At this moment it stores (taxid, name)
    @Field(PsmFields.SPECIES)
    private Map<String,List<String>> species;


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

    public Map<String, List<String>> getModsAccessions() {
        return modsAccessions;
    }

    public void setModsAccessions(Map<String, List<String>> modsAccessions) {
        this.modsAccessions = modsAccessions;
    }

    public Map<String, List<String>> getSpecies() {
        return species;
    }

    public void setSpecies(Map<String, List<String>> species) {
        this.species = species;
    }
}
