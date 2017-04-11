package uk.ac.ebi.pride.psmindex.search.service.repository;

import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;

public class SolrPsmRepositoryFactory {

  private SolrOperations solrOperations;

  public SolrPsmRepositoryFactory(SolrOperations solrOperations) {
    this.solrOperations = solrOperations;
  }

  public SolrPsmRepository create() {
    return new SolrRepositoryFactory(this.solrOperations).getRepository(SolrPsmRepository.class);
  }

}