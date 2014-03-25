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
    List<Psm> findByPepSequence(String pepSequence);
    @Query("peptide_sequence:?0 AND project_accessions:?1")
    List<Psm> findByPepSequenceAndProjectAccessions(String pepSequence, String projectAccession);
    @Query("peptide_sequence:?0 AND assay_accessions:?1")
    List<Psm> findByPepSequenceAndAssayAccessions(String pepSequence, String assayAccession);

    // Project accession query methods
    @Query("project_accessions:?0")
    List<Psm> findByProjectAccessions(String projectAccession);
    @Query("project_accessions:(?0)")
    List<Psm> findByProjectAccessionsIn(Collection<String> projectAccessions);


    // Assay accession query methods
    @Query("assay_accessions:?0")
    List<Psm> findByAssayAccessions(String assayAccession);
    @Query("assay_accessions:(?0)")
    List<Psm> findByAssayAccessionsIn(Collection<String> assayAccessions);
}
