package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
public class ProjectPsmsIndexer {

    private static Logger logger = LoggerFactory.getLogger(ProjectPsmsIndexer.class.getName());

    private PsmSearchService psmSearchService;
    private PsmIndexService psmIndexService;

    public ProjectPsmsIndexer(PsmSearchService psmSearchService, PsmIndexService psmIndexService) {
        this.psmSearchService = psmSearchService;
        this.psmIndexService = psmIndexService;
    }

    @Deprecated
    public void indexAllPsms(String projectAccession, String pathToMzTabFiles) {

        Map<String, LinkedList<Psm>> psms = new HashMap<String, LinkedList<Psm>>();

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        // build PSMs from mzTabFiles
        try {
            if (pathToMzTabFiles != null) {
                File generatedDirectory = new File(pathToMzTabFiles);
                psms = MzTabDataProviderReader.readPsmsFromMzTabFilesDirectory(projectAccession, generatedDirectory);
            }
        } catch (Exception e) { // we need to recover from any exception when reading the mzTab file so the whole process can continue
            logger.error("Cannot get psms from project " + projectAccession + " in folder" + pathToMzTabFiles);
            logger.error("Reason: ");
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        logger.info("Found " + getTotalPsmsCount(psms) + " psms "
                + " in directory " + pathToMzTabFiles
                + " for project " + projectAccession
                + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

        // add all PSMs to index
        startTime = System.currentTimeMillis();

        for (Map.Entry<? extends String, ? extends Collection<? extends Psm>> assayPsms : psms.entrySet()) {
            Map<String, Psm> psmsToIndex = new HashMap<String, Psm>();

            for (Psm psm : assayPsms.getValue()) {
                try {
                    // add to save
                    psmsToIndex.put(psm.getId(), psm);

                    logger.debug(
                            "ADDED PSM " + psm.getId() +
                                    " from PROJECT:" + projectAccession +
                                    " ASSAY:" + assayPsms.getKey()
                    );
                } catch (Exception e) {
                    logger.error("PSM " + psm.getId() + " caused an error");
                    logger.error("ASSAY " + assayPsms.getKey());
                    logger.error("PROJECT " + projectAccession);
                    e.printStackTrace();
                }

            }


            psmIndexService.save(psmsToIndex.values());
            logger.debug("COMMITTED " + psmsToIndex.size() +
                    " psms from PROJECT:" + projectAccession +
                    " ASSAY:" + assayPsms.getKey());
        }

        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }

    @Deprecated
    private static long getTotalPsmsCount(Map<? extends String, ? extends Collection<? extends Psm>> psms) {
        long res = 0;

        for (Map.Entry<? extends String, ? extends Collection<? extends Psm>> psmEntry : psms.entrySet()) {
            res = res + psmEntry.getValue().size();
        }

        return res;
    }


    public void indexAllPsmsForProjectAndAssay(String projectAccession, String assayAccession, MZTabFile mzTabFile){
        LinkedList<Psm> psms = new LinkedList<Psm>();

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        // build PSMs from mzTabFiles
        try {
            if (mzTabFile != null) {
                psms = MzTabDataProviderReader.readPsmsFromMzTabFile(projectAccession, assayAccession, mzTabFile);
            }
        } catch (Exception e) { // we need to recover from any exception when reading the mzTab file so the whole process can continue
            logger.error("Cannot get psms from project " + projectAccession + "and assay" + assayAccession + " in file" + mzTabFile);
            logger.error("Reason: ");
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        logger.info("Found " + psms.size() + " psms "
                + " in file " + mzTabFile
                + " for project " + projectAccession
                + " and assay" + assayAccession
                + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

        // add all PSMs to index
        startTime = System.currentTimeMillis();

        psmIndexService.save(psms);
        logger.debug("COMMITTED " + psms.size() +
                " psms from PROJECT:" + projectAccession +
                " ASSAY:" + assayAccession);


        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }


}
