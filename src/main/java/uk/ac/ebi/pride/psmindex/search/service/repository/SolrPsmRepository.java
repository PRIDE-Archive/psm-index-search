package uk.ac.ebi.pride.psmindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.solr.core.query.Query.Operator.*;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface SolrPsmRepository extends SolrCrudRepository<Psm, String> {

    public static final String HIGHLIGHT_PRE_FRAGMENT = "<span class='search-hit'>";
    public static final String HIGHLIGHT_POST_FRAGMENT = "</span>";

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
    Long countByPeptideSequenceAndProjectAccession(String peptideSequence, String projectAccession);
    @Query("peptide_sequence:?0 AND assay_accession:?1")
    List<Psm> findByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession);
    Long countByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession);

    // Project accession query methods
    @Query("project_accession:?0")
    List<Psm> findByProjectAccession(String projectAccession);
    Long countByProjectAccession(String projectAccession);
    @Query("project_accession:(?0)")
    List<Psm> findByProjectAccessionIn(Collection<String> projectAccessions);
    @Query("project_accession:?0")
    Page<Psm> findByProjectAccession(String projectAccession, Pageable pageable);
    @Query("project_accession:(?0)")
    Page<Psm> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

    //Filter by mods
    @Query(value = "project_accession:?0", filters = "mod_names:(?1)", defaultOperator = AND)
    Page<Psm> findByProjectAccessionAndFilterModNames(String projectAccession, List<String> modNames, Pageable pageable);
    @Query(value = "project_accession:?0", filters = "mod_synonyms:(?1)", defaultOperator = AND)
    Page<Psm>findByProjectAccessionAndFilterModSynonyms(String projectAccession, List<String> modSynonyms, Pageable pageable);

    //Highlight only makes sense if we try to search by peptide_sequence or protein_accession as an extra term
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    HighlightPage<Psm>findByProjectAccessionHighlights(String projectAccession, String term, Pageable pageable);

    //Filter by mods
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_names:(?2)", defaultOperator = AND)
    HighlightPage<Psm> findByProjectAccessionHighlightsAndFilterModNames(String projectAccession, String term, List<String> modNames, Pageable pageable);
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_synonyms:(?2)", defaultOperator = AND)
    HighlightPage<Psm>findByProjectAccessionHighlightsAndFilterModSynonyms(String projectAccession, String term, List<String> modSynonyms, Pageable pageable);


    //Faceting
    //Mod names
    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_names:(?2)", defaultOperator = AND)
    FacetPage<Psm> findByProjectAccessionFacetAndFilterModNames(String projectAccession, String term, List<String> modNames, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "project_accession:?0" , filters = "mod_names:(?1)", defaultOperator = AND)
    FacetPage<Psm>findByProjectAccessionFacetAndFilterModNames(String projectAccession, List<String> modNames, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    FacetPage<Psm> findByProjectAccessionFacetModNames(String projectAccession, String term, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "project_accession:?0")
    FacetPage<Psm> findByProjectAccessionFacetModNames(String projectAccession, Pageable pageable);

    //Mod synonyms
    @Facet(fields = {"mod_synonyms"}, limit = 100) //default is 10 it can be reached  with modifications and labels
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_synonyms:(?2)", defaultOperator = AND)
    FacetPage<Psm>findByProjectAccessionFacetAndFilterModSynonyms(String projectAccession, String term, List<String> modSynonyms, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "project_accession:?0" , filters = "mod_synonyms:(?1)", defaultOperator = AND)
    FacetPage<Psm>findByProjectAccessionFacetAndFilterModSynonyms(String projectAccession, List<String> modSynonyms, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "project_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    FacetPage<Psm> findByProjectAccessionFacetModSynonyms(String projectAccession, String term, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "project_accession:?0")
    FacetPage<Psm> findByProjectAccessionFacetModSynonyms(String projectAccession, Pageable pageable);

    // Assay accession query methods
    @Query("assay_accession:?0")
    List<Psm> findByAssayAccession(String assayAccession);
    Long countByAssayAccession(String assayAccession);
    @Query("assay_accession:(?0)")
    List<Psm> findByAssayAccessionIn(Collection<String> assayAccessions);
    @Query("assay_accession:?0")
    Page<Psm> findByAssayAccession(String assayAccession, Pageable pageable);
    @Query("assay_accession:(?0)")
    Page<Psm> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);

    //Filter by mods
    @Query(value = "assay_accession:?0", filters = "mod_names:(?1)", defaultOperator = AND)
    Page<Psm> findByAssayAccessionAndFilterModNames(String assayAccession, List<String> modNames, Pageable pageable);
    @Query(value = "assay_accession:?0", filters = "mod_synonyms:(?1)", defaultOperator = AND)
    Page<Psm>findByAssayAccessionAndFilterModSynonyms(String assayAccession, List<String> modSynonyms, Pageable pageable);

    //Highlight only makes sense if we try to search by peptide_sequence or protein_accession as an extra term
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    HighlightPage<Psm>findByAssayAccessionHighlights(String assayAccession, String term, Pageable pageable);

    //Filter by mods
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_names:(?2)", defaultOperator = AND)
    HighlightPage<Psm> findByAssayAccessionHighlightsAndFilterModNames(String assayAccession, String term, List<String> modNames, Pageable pageable);
    @Highlight(prefix = HIGHLIGHT_PRE_FRAGMENT, postfix = HIGHLIGHT_POST_FRAGMENT, fields = {"peptide_sequence, protein_accession"})
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_synonyms:(?2)", defaultOperator = AND)
    HighlightPage<Psm>findByAssayAccessionHighlightsAndFilterModSynonyms(String assayAccession, String term, List<String> modSynonyms, Pageable pageable);

    //Faceting
    //Mod names
    @Facet(fields = {"mod_names"}, limit = 100)  //default is 10 it can be reached  with modifications and labels
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_names:(?2)", defaultOperator = AND)
    FacetPage<Psm> findByAssayAccessionFacetAndFilterModNames(String assayAccession, String term, List<String> modNames, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "assay_accession:?0" , filters = "mod_names:(?1)", defaultOperator = AND)
    FacetPage<Psm>findByAssayAccessionFacetAndFilterModNames(String assayAccession, List<String> modNames, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    FacetPage<Psm> findByAssayAccessionFacetModNames(String assayAccession, String term, Pageable pageable);

    @Facet(fields = {"mod_names"}, limit = 100)
    @Query(value = "assay_accession:?0")
    FacetPage<Psm> findByAssayAccessionFacetModNames(String assayAccession, Pageable pageable);

    //Mod synonyms
    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)", filters = "mod_synonyms:(?2)", defaultOperator = AND)
    FacetPage<Psm>findByAssayAccessionFacetAndFilterModSynonyms(String assayAccession, String term, List<String> modSynonyms, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "assay_accession:?0" , filters = "mod_synonyms:(?1)", defaultOperator = AND)
    FacetPage<Psm>findByAssayAccessionFacetAndFilterModSynonyms(String assayAccession, List<String> modSynonyms, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "assay_accession:?0 AND (peptide_sequence:?1 OR protein_accession:?1)")
    FacetPage<Psm> findByAssayAccessionFacetModSynonyms(String assayAccession, String term, Pageable pageable);

    @Facet(fields = {"mod_synonyms"}, limit = 100)
    @Query(value = "assay_accession:?0")
    FacetPage<Psm> findByAssayAccessionFacetModSynonyms(String assayAccession, Pageable pageable);

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
    @Query("reported_id:?0 AND assay_accession:?1 AND protein_accession:?2 AND peptide_sequence:?3")
    List<Psm> findByReportedIdAndAssayAccessionAndProteinAccessionAndPeptideSequence(String reportedId, String assayAccession, String proteinAccession, String peptideSequence);

    // Protein accession query methods
    @Query("protein_accession:?0")
    List<Psm> findByProteinAccession(String proteinAccession);
    @Query("protein_accession:?0 AND project_accession:?1")
    List<Psm> findByProteinAccessionAndProjectAccession(String proteinAccession, String projectAccession);
    @Query("protein_accession:?0 AND assay_accession:?1")
    List<Psm> findByProteinAccessionAndAssayAccession(String proteinAccession, String assayAccession);

    //Projection for peptide sequence
    @Query(fields = {"peptide_sequence"})
    List<Psm> findPeptideSequencesByProjectAccession(String projectAccession);

    //Pivot queries (for the future)
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
