package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.model.Modification;
import uk.ac.ebi.pride.jmztab.model.PSM;
import uk.ac.ebi.pride.jmztab.model.SplitList;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.prider.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
public class MzTabDataProviderReader {

    private static Logger logger = LoggerFactory.getLogger(MzTabDataProviderReader.class.getName());

    private static ErrorLogOutputStream errorLogOutputStream = new ErrorLogOutputStream(logger);

    private static final String GENERATED_FOLDER_NAME = "generated";

    /**
     * mzTab files in the directory will have names such as PRIDE_Exp_Complete_Ac_28654.submissions. We are interested in the
     * assay accession, the last bit if we split by '_'.
     *
     * @return A map of assay accessions to PSMs
     * @throws java.io.IOException
     */
    public static Map<String, LinkedList<Psm>> readPsmsFromMzTabFilesDirectory(String projectAccession, File mzTabFilesDirectory) throws IOException, MZTabException {

        Map<String, LinkedList<Psm>> res = new HashMap<String, LinkedList<Psm>>();

        File[] mzTabFilesInDirectory = mzTabFilesDirectory.listFiles(new MzTabFileNameFilter());
        if (mzTabFilesInDirectory != null) {
            for (File tabFile : mzTabFilesInDirectory) {

                MZTabFileParser mzTabFileParser = new MZTabFileParser(tabFile, errorLogOutputStream);
                MZTabFile mzTabFile = mzTabFileParser.getMZTabFile();

                if (mzTabFile != null) {
                    // get assay accession
                    String assayAccession = tabFile.getName().split("[_\\.]")[4];

                    // get all psms from the file
                    LinkedList<Psm> assayPsms = convertFromMzTabPsmsToPrideArchivePsms(mzTabFile.getPSMs(), projectAccession, assayAccession);

                    // add assay psms to the result
                    res.put(assayAccession, assayPsms);
                    logger.debug("Found " + assayPsms.size() + " psms for Assay " + assayAccession + " in file " + tabFile.getAbsolutePath());
                } else {
                    mzTabFileParser.getErrorList().print(errorLogOutputStream);
                }

            }
        }

        return res;
    }

    private static LinkedList<Psm> convertFromMzTabPsmsToPrideArchivePsms(Collection<PSM> mzTabPsms, String projectAccession, String assayAccession) {

        LinkedList<Psm> res = new LinkedList<Psm>();

        for (PSM filePsm: mzTabPsms) {
            Psm newPsm = new Psm();
            // TODO: set PSM id
            newPsm.setPepSequence(filePsm.getSequence());
            newPsm.setProjectAccession(projectAccession);
            newPsm.setAssayAccession(assayAccession);
            newPsm.setProteinAccession(filePsm.getAccession());
            // TODO - more fields to come

            res.add(newPsm);
        }

        return res;
    }

    private static void updatePsm(Psm psm, PSM mzTabPsm) {

        // TODO
        // We update the psm if we have the same id reported in several rows in the mzTab file.
        // This can happen if the same Psm infer several proteins.
        // The rest of the information should be the same, except pre, post, start, stop and protein accession.


        //The save method will take into account the replacement of the ":" in the protein accessions
        //Protein accession
    }

    private static Map<String, List<String>> createModsFromMzTab(SplitList<Modification> modifications) {
        // TODO
        return null;
    }

    private static Map<String, List<String>> createSpeciesFromMzTabPsm(String accession) {
        // TODO
        return null;
    }

    public static String buildGeneratedDirectoryFilePath(String prefix, ProjectProvider project) {
        if (project.isPublicProject()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getPublicationDate());
            int month = calendar.get(Calendar.MONTH) + 1;

            return prefix
                    + File.separator + calendar.get(Calendar.YEAR)
                    + File.separator + (month < 10 ? "0" : "") + month
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME;
        } else {
            return prefix
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME;
        }

    }

}
