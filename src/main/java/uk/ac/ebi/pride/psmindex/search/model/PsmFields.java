package uk.ac.ebi.pride.psmindex.search.model;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 */
public class PsmFields {
    public static final String ID = "id";
    public static final String PEP_SEQUENCE = "peptide_sequence";
    public static final String SPECTRUM_ID = "spectrum_id";
    public static final String PROTEIN_ACCESSION = "protein_accession";
    public static final String PROJECT_ACCESSION = "project_accession";
    public static final String ASSAY_ACCESSION = "assay_accession";
    public static final String MODS_WITH_LOCATIONS = "*_pos";
    public static final String SPECIES = "*_species";

    //For the future
    public static final String UNIQUE = "unique";
    public static final String START_POSITION = "start_position";
    public static final String PEP_LENGTH = "peptide_length";
    public static final String RETENTION_TIME = "retention_time";
    public static final String MS_SCORE = "ms_score";
    public static final String EXP_MASS_TO_CHARGE = "exp_mass_to_charge";

}
