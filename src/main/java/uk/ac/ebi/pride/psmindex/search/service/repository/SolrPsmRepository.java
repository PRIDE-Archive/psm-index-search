package uk.ac.ebi.pride.psmindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
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

    // ToDo: add count methods

    // Accession query methods
    @Query("id:?0")
    List<Psm> findById(String id);
    @Query("id:?0")
    List<Psm> findByIdIn(Collection<String> id);

    //Sequence query methods
    @Query("peptide_sequence:?0")
    List<Psm> findByPeptideSequence(String peptideSequence);
    @Query("peptide_sequence:?0")
    Page<Psm> findByPeptideSequence(String peptideSequence, Pageable pageable);
    @Query("peptide_sequence:?0 AND project_accession:?1")
    List<Psm> findByPeptideSequenceAndProjectAccessions(String peptideSequence, String projectAccession);
    @Query("peptide_sequence:?0 AND assay_accession:?1")
    List<Psm> findByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession);

    // Project accession query methods
    @Query("project_accession:?0")
    List<Psm> findByProjectAccession(String projectAccession);
    @Query("project_accession:(?0)")
    List<Psm> findByProjectAccessionIn(Collection<String> projectAccessions);
    @Query("project_accession:?0")
    Page<Psm> findByProjectAccession(String projectAccession, Pageable pageable);
    @Query("project_accession:(?0)")
    Page<Psm> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

    // Assay accession query methods
    @Query("assay_accession:?0")
    List<Psm> findByAssayAccession(String assayAccession);
    @Query("assay_accession:(?0)")
    List<Psm> findByAssayAccessionIn(Collection<String> assayAccessions);
    @Query("assay_accession:?0")
    Page<Psm> findByAssayAccession(String assayAccession, Pageable pageable);
    @Query("assay_accession:(?0)")
    Page<Psm> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);

    // Spectrum id query methods
    @Query("spectrum_id:?0")
    List<Psm> findBySpectrumId(String spectrumId);
    @Query("spectrum_id:(?0)")
    List<Psm> findBySpectrumIdIn(Collection<String> spectrumIds);

    // Reported id query methods
    @Query("reported_id:?0")
    List<Psm> findByReportedId(String reportedId);
    @Query("reported_id:?0 AND project_accession:?1")
    List<Psm> findByReportedIdAndProjectAccession(String reportedId, String projectAccession);
    @Query("reported_id:?0 AND assay_accession:?1")
    List<Psm> findByReportedIdAndAssayAccession(String reportedId, String assayAccession);

    // Protein accession query methods
    @Query("protein_accession:?0")
    List<Psm> findByProteinAccession(String proteinAccession);
    @Query("protein_accession:?0 AND project_accession:?1")
    List<Psm> findByProteinAccessionAndProjectAccession(String proteinAccession, String projectAccession);
    @Query("protein_accession:?0 AND assay_accession:?1")
    List<Psm> findByProteinAccessionAndAssayAccession(String proteinAccession, String assayAccession);

    //Projection for peptide sequence
    @Query(fields = { "peptide_sequence"})
    List<Psm> findPeptideSequencesByProjectAccession(String projectAccession);

    @Query("project_accession:?0 AND assay_accession:?1")
    @Facet(pivotFields = ("peptide_sequence,modifications"))
    FacetPage<Psm> findByProjectAccessionAndAssayAccessionPivotOnPeptideSequenceVsModifications(String projectAccession, String assayAccession, Pageable pageable);

    @Query("protein_accession:?0 AND project_accession:?1")
    @Facet(pivotFields = ("peptide_sequence,modifications"))
    FacetPage<Psm> findByProteinAccessionAndProjectAccessionPivotOnPeptideSequenceVsModifications(String proteinAccession, String projectAccession, Pageable pageable);

    @Query("protein_accession:?0 AND assay_accession:?1")
    @Facet(pivotFields = ("peptide_sequence,modifications"))
    FacetPage<Psm> findByProteinAccessionAndAssayAccessionPivotOnPeptideSequenceVsModifications(String proteinAccession, String assayAccession, Pageable pageable);

}
