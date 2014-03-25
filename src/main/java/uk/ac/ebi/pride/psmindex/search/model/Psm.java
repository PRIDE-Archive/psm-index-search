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

    @Field(PsmFields.PROTEIN_ACCESSIONS)
    private Set<String> proteinAccessions;

    @Field(PsmFields.PROJECT_ACCESSIONS)
    private Set<String> projectAccessions;

    @Field(PsmFields.ASSAY_ACCESSIONS)
    private Set<String> assayAccessions;

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

    public Set<String> getProteinAccessions() {
        return proteinAccessions;
    }

    public void setProteinAccessions(Set<String> proteinAccessions) {
        this.proteinAccessions = proteinAccessions;
    }

    public Set<String> getProjectAccessions() {
        return projectAccessions;
    }

    public void setProjectAccessions(Set<String> projectAccessions) {
        this.projectAccessions = projectAccessions;
    }

    public Set<String> getAssayAccessions() {
        return assayAccessions;
    }

    public void setAssayAccessions(Set<String> assayAccessions) {
        this.assayAccessions = assayAccessions;
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
