package uk.ac.ebi.pride.psmindex.search.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *          <p/>
 *          NOTE: protein accessions can contain chars that produce problems in solr queries ([,],:). They are replaced by _ when
 *          using the repository methods
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

    //Sequence query methods
    public List<Psm> findByPepSequence(String pepSequence) {
        return solrPsmRepository.findByPepSequence(pepSequence);
    }

    public List<Psm> findByPepSequenceAndProjectAccessions(String pepSequence, String projectAccession) {
        return solrPsmRepository.findByPepSequenceAndProjectAccessions(pepSequence,projectAccession);
    }

    public List<Psm> findByPepSequenceAndAssayAccessions(String pepSequence, String assayAccession) {
        return solrPsmRepository.findByPepSequenceAndAssayAccessions(pepSequence,assayAccession);
    }

    // Project accession query methods
    public List<Psm> findByProjectAccessions(String projectAccession) {
        return solrPsmRepository.findByProjectAccessions(projectAccession);
    }

    public List<Psm> findByProjectAccessions(Collection<String> projectAccessions) {
        return solrPsmRepository.findByProjectAccessionsIn(projectAccessions);
    }

    // Assay accession query methods
    public List<Psm> findByAssayAccessions(String assayAccession) {
        return solrPsmRepository.findByAssayAccessions(assayAccession);
    }

    public List<Psm> findByAssayAccessions(Collection<String> assayAccessions) {
        return solrPsmRepository.findByAssayAccessionsIn(assayAccessions);
    }

}
