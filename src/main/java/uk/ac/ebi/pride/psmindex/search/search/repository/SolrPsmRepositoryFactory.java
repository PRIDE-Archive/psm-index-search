package uk.ac.ebi.pride.psmindex.search.search.repository;

import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SolrPsmRepositoryFactory {

    private SolrOperations solrOperations;

    public SolrPsmRepositoryFactory(SolrOperations solrOperations) {
        this.solrOperations = solrOperations;
    }

    public SolrPsmRepository create() {
        return new SolrRepositoryFactory(this.solrOperations).getRepository(SolrPsmRepository.class);
    }

}