package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;

public class PsmSequenceCleaner {

  private static Logger logger = LoggerFactory.getLogger(PsmIndexService.class.getName());

  // The amino acids characters come from PRIDE Inspector
  public static final String NO_PEPTIDE_REGEX = "[^ABCDEFGHIJKLMNPQRSTUVWXYZ]";

  public static String cleanPeptideSequence(String peptideSequence) {
    String result = null;
    logger.debug("Request to clean peptide sequence " + peptideSequence);
    if(peptideSequence!= null){
      result = peptideSequence.toUpperCase();
      result = result.replaceAll(NO_PEPTIDE_REGEX,"");
    }
    logger.debug("Peptide sequence after cleaning it " + result);
    return result;
  }
}
