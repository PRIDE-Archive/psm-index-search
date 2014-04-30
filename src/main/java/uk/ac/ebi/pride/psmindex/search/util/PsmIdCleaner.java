package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;

/**
 * Created by jdianes on 01/04/2014.
 */
public class PsmIdCleaner {

    private static Logger logger = LoggerFactory.getLogger(PsmIndexService.class.getName());

    // The amino acids characters come from PRIDE Inspector
    public static final String NO_PEPTIDE_REGEX = "[^ABCDEFGHIJKLMNPQRSTUVWXYZ]";

    public static String cleanId(String id) {
        logger.info("Request to clean ID " + id);
        String res = id.replace(':','-');
        res = res.replace('[','-');
        res = res.replace(']','-');
        res = res.replace('|','-');
        res = res.replace(',','-');
        return res;
    }

    public static String cleanPeptideSequence(String peptideSequence) {
        String res = null;

        logger.debug("Request to clean peptide sequence " + peptideSequence);

        if(peptideSequence!= null){
            res = peptideSequence.toUpperCase();
            res = res.replaceAll(NO_PEPTIDE_REGEX,"");
        }

        logger.debug("Peptide sequence after cleaning it " + res);

        return res;
    }
}
