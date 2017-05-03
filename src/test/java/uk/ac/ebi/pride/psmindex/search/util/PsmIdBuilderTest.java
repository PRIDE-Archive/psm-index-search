package uk.ac.ebi.pride.psmindex.search.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static uk.ac.ebi.pride.psmindex.search.util.PsmIdBuilder.*;

public class PsmIdBuilderTest {

  private static final String PROJECT_ACCESSION = "PXT000111";
  private static final String ASSAY_ACCESSION = "12345";
  private static final String PROTEIN_ACCESSION = "P12345";
  private static final String REPORTED_ID = "12345";
  private static final String PEP_SEQUENCE = "ABCDEF";


  private static final String ID =
      PROJECT_ACCESSION + SEPARATOR +
          ASSAY_ACCESSION + SEPARATOR +
          REPORTED_ID + SEPARATOR +
          PROTEIN_ACCESSION + SEPARATOR +
          PEP_SEQUENCE;


  @Test
  public void testGetId() throws Exception {
    String newID = getId(PROJECT_ACCESSION, ASSAY_ACCESSION, REPORTED_ID, PROTEIN_ACCESSION, PEP_SEQUENCE);
    assertEquals(ID,newID);
  }

  @Test
  public void testGetProjectAccession() throws Exception {
    assertEquals(PROJECT_ACCESSION, getProjectAccession(ID));
  }

  @Test
  public void testGetAssayAccession() throws Exception {
    assertEquals(ASSAY_ACCESSION, getAssayAccession(ID));
  }

  @Test
  public void testGetReportedId() throws Exception {
    assertEquals(REPORTED_ID, getReportedId(ID));
  }

  @Test
  public void testGetProteinAccession() throws Exception {
    assertEquals(PROTEIN_ACCESSION, getProteinAccession(ID));
  }

  @Test
  public void testGetPeptideSequence() throws Exception {
    assertEquals(PEP_SEQUENCE, getPeptideSequence(ID));
  }
}
