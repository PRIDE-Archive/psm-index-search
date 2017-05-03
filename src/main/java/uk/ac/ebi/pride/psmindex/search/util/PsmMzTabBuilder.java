package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static uk.ac.ebi.pride.indexutils.helpers.ModificationHelper.convertToModificationProvider;
import static uk.ac.ebi.pride.psmindex.search.util.PsmIdBuilder.getId;

public class PsmMzTabBuilder {

  private static Logger logger = LoggerFactory.getLogger(PsmMzTabBuilder.class.getName());

  /**
   * The map between the assay accession and the file need to be provided externally from the database
   *
   * @return A map of assay accessions to PSMs
   * @throws java.io.IOException
   */
  public static List<Psm> readPsmsFromMzTabFile(String projectAccession, String assayAccession, MZTabFile mzTabFile) throws IOException, MZTabException {
    List<Psm> res = new LinkedList<>();
    if (mzTabFile != null) { // get all psms from the file
      res = convertFromMzTabPsmsToPrideArchivePsms(mzTabFile.getPSMs(), mzTabFile.getMetadata(), projectAccession, assayAccession);
      logger.debug("Found " + res.size() + " psms for Assay " + assayAccession + " in file " + mzTabFile);
    }
    return res;
  }

  private static LinkedList<Psm> convertFromMzTabPsmsToPrideArchivePsms(Collection<PSM> mzTabPsms, Metadata metadata, String projectAccession, String assayAccession) {
    LinkedList<Psm> result = new LinkedList<>();
    for (PSM mzTabPsm : mzTabPsms) {
      String cleanPepSequence = PsmSequenceCleaner.cleanPeptideSequence(mzTabPsm.getSequence());
      Psm newPsm = new Psm();
      newPsm.setId(getId(projectAccession, assayAccession, mzTabPsm.getPSM_ID(), mzTabPsm.getAccession(), cleanPepSequence));
      newPsm.setReportedId(mzTabPsm.getPSM_ID());
      newPsm.setPeptideSequence(cleanPepSequence);
      newPsm.setProjectAccession(projectAccession);
      newPsm.setAssayAccession(assayAccession);
      newPsm.setProteinAccession(mzTabPsm.getAccession());
      newPsm.setModificationNames(new LinkedList<ModificationProvider>());
      if (mzTabPsm.getModifications() != null && mzTabPsm.getModifications().size()>0) {
        for (Modification mod : mzTabPsm.getModifications()) { // Using the writer for the library
          newPsm.addModificationNames(convertToModificationProvider(mod));
        }
      }
      result.add(newPsm);
    }
    return result;
  }
}