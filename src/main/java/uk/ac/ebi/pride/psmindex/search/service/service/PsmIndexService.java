package uk.ac.ebi.pride.psmindex.search.service.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Service
public class PsmIndexService {

    private SolrPsmRepository solrPsmRepository;

    public PsmIndexService(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void setSolrPsmRepository(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void save(Psm psm) {
        solrPsmRepository.save(psm);
    }

    public void save(Iterable<Psm> psms) {
        solrPsmRepository.save(psms);
    }

    public void deleteAll() {
        solrPsmRepository.deleteAll();
    }
}
