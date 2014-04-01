package uk.ac.ebi.pride.psmindex.search.service.repository;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface SolrPsmRepository extends SolrCrudRepository<Psm, String> {

    // Accession query methods
    @Query("id:?0")
    List<Psm> findById(String id);
    @Query("id:?0")
    List<Psm> findByIdIn(Collection<String> id);

    //Sequence query methods
    @Query("peptide_sequence:?0")
    List<Psm> findByPeptideSequence(String peptideSequence);
    @Query("peptide_sequence:?0 AND project_accession:?1")
    List<Psm> findByPeptideSequenceAndProjectAccessions(String peptideSequence, String projectAccession);
    @Query("peptide_sequence:?0 AND assay_accession:?1")
    List<Psm> findByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession);

    // Project accession query methods
    @Query("project_accession:?0")
    List<Psm> findByProjectAccession(String projectAccession);
    @Query("project_accession:(?0)")
    List<Psm> findByProjectAccessionIn(Collection<String> projectAccessions);


    // Assay accession query methods
    @Query("assay_accession:?0")
    List<Psm> findByAssayAccession(String assayAccession);
    @Query("assay_accession:(?0)")
    List<Psm> findByAssayAccessionIn(Collection<String> assayAccessions);
}
