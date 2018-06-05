package uk.ac.ebi.pride.psmindex.search.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;
import uk.ac.ebi.pride.psmindex.search.util.PsmMzTabBuilder;

import java.util.LinkedList;
import java.util.List;

public class ProjectPsmsIndexer {

  private static Logger logger = LoggerFactory.getLogger(ProjectPsmsIndexer.class.getName());

  private PsmSearchService psmSearchService;
  private PsmIndexService psmIndexService;

  public ProjectPsmsIndexer(PsmSearchService psmSearchService, PsmIndexService psmIndexService) {
    this.psmSearchService = psmSearchService;
    this.psmIndexService = psmIndexService;
  }

  public void indexAllPsmsForProjectAndAssay(String projectAccession, String assayAccession, MZTabFile mzTabFile){
    List<Psm> psms = new LinkedList<Psm>();

    long startTime;
    long endTime;

    startTime = System.currentTimeMillis();

    // build PSMs from mzTabFiles
    try {
      if (mzTabFile != null) {
        psms = PsmMzTabBuilder.readPsmsFromMzTabFile(projectAccession, assayAccession, mzTabFile);
      }
    } catch (Exception e) { // we need to recover from any exception when reading the mzTab file so the whole process can continue
      logger.error("Cannot get psms from PROJECT:" + projectAccession + "and ASSAY:" + assayAccession );
      logger.error("Reason: ");
      e.printStackTrace();
    }

    endTime = System.currentTimeMillis();
    logger.info("Found " + psms.size() + " psms "
        + " for PROJECT:" + projectAccession
        + " and ASSAY:" + assayAccession
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

  public void deleteAllPsmsForProject(String projectAccession) {
    int MAX_PAGE_SIZE = 1000;
    long proteinCount = psmSearchService.countByProjectAccession(projectAccession);
    List<Psm> initialProteinsFound;
    while (0 < proteinCount) {
      for (int i = 0; i < (proteinCount / MAX_PAGE_SIZE) + 1; i++) {
        initialProteinsFound =
            psmSearchService
                .findByProjectAccession(projectAccession, new PageRequest(i, MAX_PAGE_SIZE))
                .getContent();
        psmIndexService.delete(initialProteinsFound);
      }
      proteinCount = psmSearchService.countByProjectAccession(projectAccession);
    }
  }

}
