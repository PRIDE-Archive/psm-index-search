package uk.ac.ebi.pride.psmindex.search.service;

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
        solrPsmRepository.save(psm);
    }

    public void save(Iterable<Psm> psms) {
        // fix the accession of needed
        //TODO
        solrPsmRepository.save(psms);
    }

    public void deleteAll() {
        solrPsmRepository.deleteAll();
    }
}
