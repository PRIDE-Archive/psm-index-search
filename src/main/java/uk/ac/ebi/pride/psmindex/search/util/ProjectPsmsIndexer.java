package uk.ac.ebi.pride.psmindex.search.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;

import java.io.File;
import java.util.*;

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

    public void indexAllPsms(String projectAccession, String pathToMzTabFiles) {

        Map<String, LinkedList<Psm>> psms = new HashMap<String, LinkedList<Psm>>();

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        // build protein identifications from mzTabFiles
        try {
            if (pathToMzTabFiles != null) {
                File generatedDirectory = new File(pathToMzTabFiles);
                psms = MzTabDataProviderReader.readPsmsFromMzTabFilesDirectory(generatedDirectory);
                logger.debug("Found " + getTotalPsmsCount(psms) + " psms in directory " + pathToMzTabFiles);
            }
        } catch (Exception e) { // we need to recover from any exception when reading the mzTab file so the whole process can continue
            logger.error("Cannot get psms from project " + projectAccession + " in folder" + pathToMzTabFiles);
            logger.error("Reason: ");
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        logger.info("DONE getting psms from file for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

        //add all proteins
        logger.info("Adding psms to index for project " + projectAccession);
        startTime = System.currentTimeMillis();

        for (Map.Entry<? extends String, ? extends Collection<? extends Psm>> assayPsms : psms.entrySet()) {
            Map<String, Psm> psmsToIndex = new HashMap<String, Psm>();

            //TODO -->
            for (Psm psm : assayPsms.getValue()) {
                try {

                    // check for existing protein - WE NEED TO REPLACE ':' characters IF ANY
                    List<Psm> psmsFromIndex =  psmSearchService.findById(psm.getId());
                    if (psmsFromIndex != null && psmsFromIndex.size() > 0) {
                        // get the existing protein
                        Psm psmFromIndex = psmsFromIndex.get(0);
                        // add new project accession
                        psmFromIndex.getProjectAccessions().add(projectAccession);
                        // add new assay accession
                        psmFromIndex.getAssayAccessions().add(assayPsms.getKey());
                        // add to save
                        psmsToIndex.put(psmFromIndex.getId(), psmFromIndex);

                        logger.debug(
                                "UPDATED protein " + psm.getId() +
                                        " from PROJECT:" + projectAccession +
                                        " ASSAY:" + assayPsms.getKey()
                        );
                    } else {
                        // set the project accessions
                        psm.setProjectAccessions(
                                new TreeSet<String>(Arrays.asList(projectAccession))
                        );
                        // set assay accessions
                        psm.setAssayAccessions(
                                new TreeSet<String>(Arrays.asList(assayPsms.getKey()))
                        );
                        // add to save
                        psmsToIndex.put(psm.getId(), psm);

                        logger.debug(
                                "ADDED PSM " + psm.getId() +
                                        " from PROJECT:" + projectAccession +
                                        " ASSAY:" + assayPsms.getKey()
                        );
                    }
                } catch (Exception e) {
                    logger.error("PSM " + psm.getId() + " caused an error");
                    logger.error("ASSAY " + assayPsms.getKey());
                    logger.error("PROJECT " + projectAccession);
                    e.printStackTrace();
                }

                //TODO <--
            }


            // get all the synonyms for the proteins to index
            long startTime2 = System.currentTimeMillis();
            long endTime2 = System.currentTimeMillis();
            logger.info("DONE getting all synonyms for assay " + assayPsms.getKey() + " in project " + projectAccession + " in " + (double) (endTime2 - startTime2) / 1000.0 + " seconds");

            // save all assay identifications
            startTime2 = System.currentTimeMillis();
            psmIndexService.save(psmsToIndex.values());
            endTime2 = System.currentTimeMillis();
            logger.debug("COMMITTED " + psmsToIndex.size() +
                    " psms from PROJECT:" + projectAccession +
                    " ASSAY:" + assayPsms.getKey() +
                    " in " + (double) (endTime2 - startTime2) / 1000.0 + " seconds");
        }

        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all psms for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }

    private static long getTotalPsmsCount(Map<? extends String, ? extends Collection<? extends Psm>> psms) {
        long res = 0;

        for (Map.Entry<? extends String, ? extends Collection<? extends Psm>> psmEntry : psms.entrySet()) {
            res = res + psmEntry.getValue().size();
        }

        return res;
    }
}
