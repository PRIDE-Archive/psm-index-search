package uk.ac.ebi.pride.psmindex.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.indexutils.results.PageWrapper;
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

    public Long countByPeptideSequenceAndProjectAccession(String peptideSequence, String projectAccession) {
        Page<Psm> result = solrPsmRepository.findByPeptideSequenceAndProjectAccession(peptideSequence, projectAccession, new PageRequest(0,1));
        return result.getTotalElements();
    }

    public Page<Psm> findByPeptideSequenceAndProjectAccession(String peptideSequence, String projectAccession, Pageable pageable) {
        return solrPsmRepository.findByPeptideSequenceAndProjectAccession(peptideSequence, projectAccession, pageable);
    }

    public Long countByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession) {
        Page<Psm> result = solrPsmRepository.findByPeptideSequenceAndAssayAccession(peptideSequence, assayAccession, new PageRequest(0,1));
        return result.getTotalElements();
    }

    public Page<Psm> findByPeptideSequenceAndAssayAccession(String peptideSequence, String assayAccession, Pageable pageable) {
        return solrPsmRepository.findByPeptideSequenceAndAssayAccession(peptideSequence, assayAccession, pageable);
    }

    public Long countByProjectAccession(String projectAccession) {
        return solrPsmRepository.countByProjectAccession(projectAccession);
    }

    public List<Psm> findByProjectAccession(String projectAccession) {
        return solrPsmRepository.findByProjectAccession(projectAccession);
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

    /**
     * Count the facets per modification name
     * @param projectAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @return a map with the mod_names and the number of hits per mod_synonym
     */
    public Map<String, Long> findByProjectAccessionFacetOnModificationNames(String projectAccession, String term, List<String> modNameFilters) {

        Map<String, Long> modificationsCount = new TreeMap<String, Long>();
        FacetPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByProjectAccessionFacetModNames(projectAccession, new PageRequest(0, 1));
        } else if ((term != null && !term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByProjectAccessionFacetModNames(projectAccession, term, new PageRequest(0, 1));
        } else if ((term == null || term.isEmpty()) && (modNameFilters != null && !modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByProjectAccessionFacetAndFilterModNames(projectAccession, modNameFilters, new PageRequest(0, 1));
        } else {
            psms = solrPsmRepository.findByProjectAccessionFacetAndFilterModNames(projectAccession, term, modNameFilters, new PageRequest(0, 1));
        }

        if (psms != null) {
            for (FacetFieldEntry facetFieldEntry : psms.getFacetResultPage(PsmFields.MOD_NAMES)) {
                modificationsCount.put(facetFieldEntry.getValue(), facetFieldEntry.getValueCount());
            }
        }
        return modificationsCount;
    }

    /**
     * Return filtered psms (or not) by modifications names with the highlights for peptide_sequence and protein_sequence
     * @param projectAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @param pageable requested page
     * @return A page with the psms and the highlights snippets
     */
    public PageWrapper<Psm> findByProjectAccessionHighlightsOnModificationNames(
        String projectAccession, String term, List<String> modNameFilters, Pageable pageable) {

        PageWrapper<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccession(projectAccession, pageable));
        } else if ((term != null && !term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionHighlights(projectAccession, term, pageable));
        } else if ((term == null || term.isEmpty()) && (modNameFilters != null && !modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionAndFilterModNames(projectAccession, modNameFilters, pageable));
        } else {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionHighlightsAndFilterModNames(projectAccession, term, modNameFilters, pageable));
        }

        return psms;
    }

    /**
     * Return filtered psms (or not) by modifications synonyms with the highlights for peptide_sequence and protein_sequence
     * @param projectAccession mandatory
     * @param term optional
     * @param modSynonymFilters optional
     * @param pageable requested page
     * @return A page with the psms and the highlights snippets
     */
    public PageWrapper<Psm> findByProjectAccessionHighlightsOnModificationSynonyms(
        String projectAccession, String term, List<String> modSynonymFilters, Pageable pageable) {

        PageWrapper<Psm> psms;

        if ((term == null || term.isEmpty()) && (modSynonymFilters == null || modSynonymFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccession(projectAccession, pageable));
        } else if ((term != null && !term.isEmpty()) && modSynonymFilters == null || modSynonymFilters.isEmpty()) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionHighlights(projectAccession, term, pageable));
        } else if ((term == null || term.isEmpty()) && (modSynonymFilters != null && !modSynonymFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionAndFilterModSynonyms(projectAccession, modSynonymFilters, pageable));
        } else {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByProjectAccessionHighlightsAndFilterModSynonyms(projectAccession, term, modSynonymFilters, pageable));
        }

        return psms;
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

    // Protein Accession query methods
    public List<Psm> findByProteinAccession(String proteinAccession) {
        return solrPsmRepository.findByProteinAccession(proteinAccession);
    }

    public Page<Psm> findByProteinAccessionAndProjectAccession(String proteinAccession, String projectAccession, Pageable pageable) {
        return solrPsmRepository.findByProteinAccessionAndProjectAccession(proteinAccession, projectAccession, pageable);
    }

    public Page<Psm> findByProteinAccessionAndAssayAccession(String proteinAccession, String assayAccession, Pageable pageable) {
        return solrPsmRepository.findByProteinAccessionAndAssayAccession(proteinAccession, assayAccession, pageable);
    }

    //Projection for peptide sequence
    public Page<Psm> findPeptideSequencesByProjectAccession(String projectAccession, Pageable pageable) {
        return solrPsmRepository.findPeptideSequencesByProjectAccession(projectAccession, pageable);
    }

    /**
     * Count the facets per modification name
     * @param assayAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @return a map with the mod_names and the number of hits per mod_synonym
     */
    public Map<String, Long> findByAssayAccessionFacetOnModificationNames(String assayAccession, String term, List<String> modNameFilters) {

        Map<String, Long> modificationsCount = new TreeMap<String, Long>();
        FacetPage<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionFacetModNames(assayAccession, new PageRequest(0, 1));
        } else if ((term != null && !term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = solrPsmRepository.findByAssayAccessionFacetModNames(assayAccession, term, new PageRequest(0, 1));
        } else if ((term == null || term.isEmpty()) && (modNameFilters != null && !modNameFilters.isEmpty())) {
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
     * Return filtered psms (or not) by modifications names with the highlights for peptide_sequence and protein_sequence
     * @param assayAccession mandatory
     * @param term optional
     * @param modNameFilters optional
     * @param pageable requested page
     * @return A page with the psms and the highlights snippets
     */
    public PageWrapper<Psm> findByAssayAccessionHighlightsOnModificationNames(
        String assayAccession, String term, List<String> modNameFilters, Pageable pageable) {

        PageWrapper<Psm> psms;

        if ((term == null || term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccession(assayAccession, pageable));
        } else if ((term != null && !term.isEmpty()) && (modNameFilters == null || modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccessionHighlights(assayAccession, term, pageable));
        } else if ((term == null || term.isEmpty()) && (modNameFilters != null && !modNameFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccessionAndFilterModNames(assayAccession, modNameFilters, pageable));
        } else {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccessionHighlightsAndFilterModNames(assayAccession, term, modNameFilters, pageable));
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
    public PageWrapper<Psm> findByAssayAccessionHighlightsOnModificationSynonyms(
        String assayAccession, String term, List<String> modSynonymFilters, Pageable pageable) {
        PageWrapper<Psm> psms;
        if ((term == null || term.isEmpty()) && (modSynonymFilters == null || modSynonymFilters.isEmpty())) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccession(assayAccession, pageable));
        } else if ((term != null && !term.isEmpty()) && modSynonymFilters == null || modSynonymFilters.isEmpty()) {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccessionHighlights(assayAccession, term, pageable));
        } else {
            psms = new PageWrapper<Psm>(solrPsmRepository.findByAssayAccessionHighlightsAndFilterModSynonyms(assayAccession, term, modSynonymFilters, pageable));
        }
        return psms;
    }
}
