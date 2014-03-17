package uk.ac.ebi.pride.psmindex.search.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 * NOTE: protein accessions can contain chars that produce problems in solr queries ([,],:). They are replaced by _ when
 * using the repository methods
 */
@Service
public class PsmSearchService {

    private SolrPsmRepository solrPsmRepository;

    public PsmSearchService(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void setSolrPsmRepository(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }


    // find by accession methods
    public List<Psm> findById(String id) {
        return solrPsmRepository.findById(id);
    }
    public List<Psm> findById(Collection<String> ids) {
        return solrPsmRepository.findByIdIn(ids);
    }


}
