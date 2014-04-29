package uk.ac.ebi.pride.psmindex.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 * NOTE: protein accessions can contain chars that produce problems in solr queries ([,],:). They are replaced by _ when
 * using the repository methods
 */
@Service
public class PsmIndexService {

    private static Logger logger = LoggerFactory.getLogger(PsmIndexService.class.getName());

    private SolrPsmRepository solrPsmRepository;

    public PsmIndexService(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void setSolrPsmRepository(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void save(Psm psm) {
        // fix the accession of needed
        //TODO
//        logger.info("Saving PSM with accession " + psm.getId());
//        psm.setId(PsmIdCleaner.cleanId(psm.getId()));
        solrPsmRepository.save(psm);
    }

    public void save(Iterable<Psm> psms) {
        if (psms==null || !psms.iterator().hasNext())
            logger.info("No PSMS to save");
        else {
            // fix the accession if needed
//        for (Psm psm: psms) {
////            logger.info("Saving PSM with accession " + psm.getId());
//            psm.setId(PsmIdCleaner.cleanId(psm.getId()));
//        }
            solrPsmRepository.save(psms);
        }
    }

    public void delete(Psm psm){
        solrPsmRepository.delete(psm);
    }

    public void delete(Iterable<Psm> psms){
        if (psms==null || !psms.iterator().hasNext())
            logger.info("No PSMS to delete");
        else {
            solrPsmRepository.delete(psms);
        }
    }

    public void deleteAll() {
        solrPsmRepository.deleteAll();
    }
}
