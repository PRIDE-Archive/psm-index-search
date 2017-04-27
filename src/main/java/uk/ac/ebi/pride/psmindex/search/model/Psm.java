package uk.ac.ebi.pride.psmindex.search.model;

import org.apache.solr.client.solrj.beans.Field;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.archive.dataprovider.identification.PeptideSequenceProvider;
import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;
import uk.ac.ebi.pride.indexutils.helpers.CvParamHelper;
import uk.ac.ebi.pride.indexutils.helpers.ModificationHelper;

import java.util.ArrayList;
import java.util.List;

public class Psm implements PeptideSequenceProvider {

  @Field(PsmFields.ID)
  private String id;

  @Field(PsmFields.REPORTED_ID)
  private String reportedId;

  @Field(PsmFields.PEPTIDE_SEQUENCE)
  private String peptideSequence;

  @Field(PsmFields.PROTEIN_ACCESSION)
  private String proteinAccession;

  @Field(PsmFields.PROJECT_ACCESSION)
  private String projectAccession;

  @Field(PsmFields.ASSAY_ACCESSION)
  private String assayAccession;

  @Field(PsmFields.MOD_NAMES)
  private List<String> modificationNames;


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

  public String getPeptideSequence() {
    return peptideSequence;
  }

  public void setPeptideSequence(String peptideSequence) {
    this.peptideSequence = peptideSequence;
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

  public Iterable<String> getModificationNames() {
    return modificationNames;
  }

  public void setModificationNames(List<ModificationProvider> modifications) {
    this.modificationNames = new ArrayList<>();
    if (modifications!=null && modifications.size()>0) {
      for (ModificationProvider modification : modifications) {
        addModificationNames(modification);
      }
    }
  }

  public void addModificationNames(ModificationProvider modification) {
    if (modificationNames==null) {
      modificationNames = new ArrayList<>();
    }
    modificationNames.add(modification.getName());
  }
}
