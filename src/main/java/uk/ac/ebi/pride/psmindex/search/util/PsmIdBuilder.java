package uk.ac.ebi.pride.psmindex.search.util;

/**
 * @author ntoro
 * @since 18/09/2014 15:03
 */
public class PsmIdBuilder {

    public static final String SEPARATOR = "_";

    public static String getId(String projectAccession, String assayAccession, String reportedId, String proteinAccession, String peptideSequence) {
        return  projectAccession +  SEPARATOR
                + assayAccession +  SEPARATOR
                + reportedId +  SEPARATOR
                + proteinAccession +  SEPARATOR
                + peptideSequence;
    }

    public static String getProjectAccession(String id) {
        String [] tokens = id.split(SEPARATOR);
        return tokens[0];
    }

    public static String getAssayAccession(String id) {
        String [] tokens = id.split(SEPARATOR);
        return tokens[1];
    }

    public static String getReportedId(String id) {
        String [] tokens = id.split(SEPARATOR);
        return tokens[2];
    }

    public static String getProteinAccession(String id) {
        String [] tokens = id.split(SEPARATOR);
        return tokens[3];
    }

    public static String getPeptideSequence(String id) {
        String [] tokens = id.split(SEPARATOR);
        return tokens[4];
    }

}
