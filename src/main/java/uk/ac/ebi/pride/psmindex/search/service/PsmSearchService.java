package uk.ac.ebi.pride.psmindex.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.model.PsmFields;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

import java.util.*;

/**
 * @author Jose A. Dianes
 * @author ntoro
 * @version $Id$
 *
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

    // Sequence query methods

    // ToDo: document risks of using methods not using pagination
    //       There may be > 100.000 Psm objects for some queries
    //       It is highly recommended to use the paged version
    //       of a method if in doubt about the result.
    //       Example: PSMs for single Protein P02768: > 130.000
    public List<Psm> findByPeptideSequence(String peptideSequence) {
        return solrPsmRepository.findByPeptideSequence(peptideSequence);
    }

    public Page<Psm> findByPeptideSequence(String peptideSequence, Pageable pageable) {
        return solrPsmRepository.findByPeptideSequence(peptideSequence, pageable);
    }

    // ToDo: remove methods for sub sequence search and let client decide on wild card usage
    public List<Psm> findByPeptideSubSequence(String peptideSequence) {
        return solrPsmRepository.findByPeptideSequence("*" + peptideSequence + "*");
    }

    public Page<Psm> findByPeptideSubSequence(String peptideSequence, Pageable pageable) {
        return solrPsmRepository.findByPeptideSequence("*"+peptideSequence+"*", pageable);
    }

    public List<Psm> findByPeptideSequenceAndProjectAccession(String peptideSequence, String projectAccession) {
        return solrPsmRepository.findByPeptideSequenceAndProjectAccessions(peptideSequence,projectAccession);
    }
    public Long countByPeptideSequenceAndProjectAccession(String peptideSequence, String projectAccession) {
        return solrPsmRepository.countByPeptideSequenceAndProjectAccession(peptideSequence,projectAccession);
    }

    public List<Psm> findByPeptideSubSequenceAndProjectAccession(String peptideSequence, String projectAccession) {
        return solrPsmRepository.findByPeptideSequenceAndProjectAccessions("*"+peptideSequence+"*",projectAccession);
    }

    public List<Psm> findByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession) {
        return solrPsmRepository.findByPeptideSequenceAndAssayAccession(peptideSequence, assayAccession);
    }
    public Long countByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession) {
        return solrPsmRepository.countByPeptideSequenceAndAssayAccession(peptideSequence, assayAccession);
    }

    public List<Psm> findByPeptideSubSequenceAndAssayAccession(String peptideSequence, String assayAccession) {
        return solrPsmRepository.findByPeptideSequenceAndAssayAccession("*"+peptideSequence+"*", assayAccession);
    }

    // Project accession query methods
    public List<Psm> findByProjectAccession(String projectAccession) {
        return solrPsmRepository.findByProjectAccession(projectAccession);
    }
    public Long countByProjectAccession(String projectAccession) {
        return solrPsmRepository.countByProjectAccession(projectAccession);
    }

    public List<Psm> findByProjectAccession(Collection<String> projectAccessions) {
        return solrPsmRepository.findByProjectAccessionIn(projectAccessions);
    }

    public Page<Psm> findByProjectAccession(String projectAccession, Pageable pageable) {
        return solrPsmRepository.findByProjectAccession(projectAccession, pageable);
    }

    public Page<Psm> findByProjectAccession(Collection<String> projectAccessions, Pageable pageable) {
        return solrPsmRepository.findByProjectAccessionIn(projectAccessions, pageable);
    }

    // Assay accession query methods
    public List<Psm> findByAssayAccession(String assayAccession) {
        return solrPsmRepository.findByAssayAccession(assayAccession);
    }
    public Long countByAssayAccession(String assayAccession) {
        return solrPsmRepository.countByAssayAccession(assayAccession);
    }

    public List<Psm> findByAssayAccession(Collection<String> assayAccessions) {
        return solrPsmRepository.findByAssayAccessionIn(assayAccessions);
    }

    public Page<Psm> findByAssayAccession(String assayAccession, Pageable pageable) {
        return solrPsmRepository.findByAssayAccession(assayAccession, pageable);
    }

    public Page<Psm> findByAssayAccession(Collection<String> assayAccessions, Pageable pageable) {
        return solrPsmRepository.findByAssayAccessionIn(assayAccessions, pageable);
    }

    // Spectrum id query methods
    public List<Psm> findBySpectrumId(String spectrumId) {
        return solrPsmRepository.findBySpectrumId(spectrumId);
    }

    public List<Psm> findBySpectrumId(Collection<String> spectrumIds) {
        return solrPsmRepository.findBySpectrumIdIn(spectrumIds);
    }

    // Reported id query methods
    public List<Psm> findByReportedId(String reportedId) {
        return solrPsmRepository.findByReportedId(reportedId);
    }

    public List<Psm> findByReportedIdAndProjectAccession(String reportedId, String projectAccession) {
        return solrPsmRepository.findByReportedIdAndProjectAccession(reportedId, projectAccession);
    }

    public List<Psm> findByReportedIdAndAssayAccession(String reportedId, String assayAccession) {
        return solrPsmRepository.findByReportedIdAndAssayAccession(reportedId, assayAccession);
    }

    // Protein Accession query methods
    public List<Psm> findByProteinAccession(String proteinAccession) {
        return solrPsmRepository.findByProteinAccession(proteinAccession);
    }

    public List<Psm> findByProteinAccessionAndProjectAccession(String proteinAccession, String projectAccession) {
        return solrPsmRepository.findByProteinAccessionAndProjectAccession(proteinAccession, projectAccession);
    }

    public List<Psm> findByProteinAccessionAndAssayAccession(String proteinAccession, String assayAccession) {
        return solrPsmRepository.findByProteinAccessionAndAssayAccession(proteinAccession, assayAccession);
    }

    //Projection for peptide sequence
    public List<String> findPeptideSequencesByProjectAccession(String projectAccession) {

        List<String> peptideSequences = new ArrayList<String>();
        List<Psm> psms = solrPsmRepository.findPeptideSequencesByProjectAccession(projectAccession);

        if (psms != null) {
            for (Psm psm : psms) {
                peptideSequences.add(psm.getPeptideSequence());
            }
        }

        return peptideSequences;
    }

    /**
     * Count the facets per modification name
     * @param assayAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @return a map with the mod_names and the number of hits per mod_synonym
     */
    public Map<String, Long> findByAssayAccessionFacetOnModificationNames(String assayAccession, String term, List<String> modNameFilters) {

        Map<String, Long> modificationsCount = new HashMap<String, Long>();
        FacetPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionFacetModNames(assayAccession, new PageRequest(0, 1));
        } else if (term != null && !term.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionFacetModNames(assayAccession, term, new PageRequest(0, 1));
        } else if (modNameFilters != null && !modNameFilters.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionFacetAndFilterModNames(assayAccession, modNameFilters, new PageRequest(0, 1));
        } else {
            psms = solrPsmRepository.findByAssayAccessionFacetAndFilterModNames(assayAccession, term, modNameFilters, new PageRequest(0, 1));
        }

        if (psms != null) {
            for (FacetFieldEntry facetFieldEntry : psms.getFacetResultPage(PsmFields.MOD_NAMES)) {
                modificationsCount.put(facetFieldEntry.getValue(), facetFieldEntry.getValueCount());
            }
        }
        return modificationsCount;
    }

    /**
     * Count the facets per modification synonyms
     * @param assayAccession mandatory
     * @param term optional
     * @param modSynonymFilters optional
     * @return a map with the mod_synonyms and the number of hits per mod_synonym
     */
    public Map<String, Long> findByAssayAccessionFacetOnModificationSynonyms(
            String assayAccession, String term, List<String> modSynonymFilters) {

        Map<String, Long> modificationsCount = new HashMap<String, Long>();
        FacetPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modSynonymFilters == null || modSynonymFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionFacetModSynonyms(assayAccession, new PageRequest(0,1));
        } else if (term != null && !term.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionFacetModSynonyms(assayAccession, term, new PageRequest(0,1));
        } else if (modSynonymFilters != null && !modSynonymFilters.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionFacetAndFilterModSynonyms(assayAccession, modSynonymFilters, new PageRequest(0,1));
        } else {
            psms = solrPsmRepository.findByAssayAccessionFacetAndFilterModSynonyms(assayAccession, term, modSynonymFilters, new PageRequest(0,1));
        }

        if (psms != null) {
            for (FacetFieldEntry facetFieldEntry : psms.getFacetResultPage(PsmFields.MOD_SYNONYMS)) {
                modificationsCount.put(facetFieldEntry.getValue(), facetFieldEntry.getValueCount());
            }
        }
        return modificationsCount;
    }

    /**
     * Return filtered psms (or not) by modifications names with the highlights for peptide_sequence and protein_sequence
     * @param assayAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @param pageable requested page
     * @return A page with the psms and the highlights snippets
     */
    public HighlightPage<Psm> findByAssayAccessionHighlightsOnModificationNames(
            String assayAccession, String term, List<String> modNameFilters, Pageable pageable) {

        HighlightPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsModNames(assayAccession, pageable);
        } else if (term != null && !term.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsModNames(assayAccession, term, pageable);
        } else if (modNameFilters != null && !modNameFilters.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsAndFilterModNames(assayAccession, modNameFilters, pageable);
        } else {
            psms = solrPsmRepository.findByAssayAccessionHighlightsAndFilterModNames(assayAccession, term, modNameFilters, pageable);
        }

        return psms;
    }

    /**
     * Return filtered psms (or not) by modifications synonyms with the highlights for peptide_sequence and protein_sequence
     * @param assayAccession mandatory
     * @param term optional
     * @param modSynonymFilters optional
     * @param pageable requested page
     * @return A page with the psms and the highlights snippets
     */
    public HighlightPage<Psm> findByAssayAccessionHighlightsOnModificationSynonyms(
            String assayAccession, String term, List<String> modSynonymFilters, Pageable pageable) {

        HighlightPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modSynonymFilters == null || modSynonymFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsModSynonyms(assayAccession, pageable);
        } else if (term != null && !term.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsModSynonyms(assayAccession, term, pageable);
        } else if (modSynonymFilters != null && !modSynonymFilters.isEmpty()) {
            psms = solrPsmRepository.findByAssayAccessionHighlightsAndFilterModSynonyms(assayAccession, modSynonymFilters, pageable);
        } else {
            psms = solrPsmRepository.findByAssayAccessionHighlightsAndFilterModSynonyms(assayAccession, term, modSynonymFilters, pageable);
        }

        return psms;
    }

    //TODO: Change the return type
    public FacetPage<Psm> findPeptidesByProteinAccessionAndProjectAccession(String proteinAccession, String projectAccession, Pageable pageable){
        return solrPsmRepository.findByProteinAccessionAndProjectAccessionPivotOnPeptideSequenceVsModifications(proteinAccession, projectAccession, pageable);
    }

    //TODO: Change the return type
    public FacetPage<Psm> findPeptidesByProteinAccessionAndAssayAccession(String proteinAccession, String assayAccession, Pageable pageable){
        return solrPsmRepository.findByProteinAccessionAndAssayAccessionPivotOnPeptideSequenceVsModifications(proteinAccession, assayAccession, pageable);
    }

}
