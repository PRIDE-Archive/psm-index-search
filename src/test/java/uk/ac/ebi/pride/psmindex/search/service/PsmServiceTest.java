package uk.ac.ebi.pride.psmindex.search.service;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.FacetPivotFieldEntry;
import uk.ac.ebi.pride.psmindex.search.model.Modification;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.model.PsmFields;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepositoryFactory;
import uk.ac.ebi.pride.psmindex.search.util.helper.ModificationProvider;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PsmServiceTest extends SolrTestCaseJ4 {


    // PSM 1 test data
    private static final String PSM_1_ID = "TEST-PSM-ID1";
    private static final String PSM_1_REPORTED_ID = "TEST-PSM-REPORTED-ID1";
    private static final String PSM_1_SEQUENCE = "MLVEYTQNQKEVLSEKEKKLEEYK";
    private static final String PSM_1_SPECTRUM = "SPECTRUM-ID1";

    // PSM 2 test data
    private static final String PSM_2_ID = "TEST-PSM-ID2";
    private static final String PSM_2_REPORTED_ID = "TEST-PSM-REPORTED-ID2";
    private static final String PSM_2_SEQUENCE = "YSQPEDSLIPFFEITVPESQLTVSQFTLPK";
    private static final String PSM_2_SPECTRUM = "SPECTRUM-ID1";


    // PSM 3 test data
    private static final String PSM_3_ID = "TEST-PSM-ID3";
    private static final String PSM_3_REPORTED_ID = "TEST-PSM-REPORTED-ID3";
    private static final String PSM_3_SEQUENCE = "YSQPEDSLIPFFEITVPE";
    private static final String PSM_3_SPECTRUM = "SPECTRUM-ID3";


    private static final int NUM_TEST_PSMS = 3;
    private static final String PSM_ID_PREFIX = "TEST-PSM-ID";

    //Proteins
    private static final String PROTEIN_1_ACCESSION = "PROTEIN-1-ACCESSION";
    private static final String PROTEIN_2_ACCESSION = "PROTEIN-2-ACCESSION";

    private static final String PARTIAL_ACCESSION_WILDCARD = "PROTEIN-*";
    private static final String PARTIAL_ACCESSION_WILDCARD_END_1 = "*1-ACCESSION";
    private static final String PARTIAL_ACCESSION_WILDCARD_END_2 = "*2-ACCESSION";

    //Projects and assays
    private static final String PROJECT_1_ACCESSION = "PROJECT-1-ACCESSION";
    private static final String PROJECT_2_ACCESSION = "PROJECT-2-ACCESSION";
    private static final String ASSAY_1_ACCESSION = "ASSAY-1-ACCESSION";
    private static final String ASSAY_2_ACCESSION = "ASSAY-2-ACCESSION";

    //Modifications
    private static final Integer MOD_1_POS = 3;
    private static final Integer MOD_2_POS = 5;
    private static final String MOD_1_ACCESSION = "MOD:00696";
    private static final String MOD_2_ACCESSION = "MOD:00674";

    private static final String MOD_1_NAME = "phosphorylated residue";
    private static final String MOD_2_NAME = "amidated residue";

    //Sequences
    private static final String SEQUENCE_SUB = "IPFFEITVPE";
    private static final String SEQUENCE_LT_6 = "ITVPE";
    private static final String SEQUENCE_BT_100 =
            "MKLNPQQAPLYGDCVVTVLLAEEDKAEDDVVFYLVFLGSTLRHCTSTRKVSSDTLETIAP" +
                    "GHDCCETVKVQLCASKEGLPVFVVAEEDFHFVQDEAYDAAQFLATSAGNQQALNFTRFLD";


    private SolrServer server;
    private SolrPsmRepositoryFactory solrPsmRepositoryFactory;

    public static final long ZERO_DOCS = 0L;
    public static final long SINGLE_DOC = 1L;

    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/test/resources/solr/collection1/conf/solrconfig.xml",
                "src/test/resources/solr/collection1/conf/schema.xml",
                "src/test/resources/solr");
    }


    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());

        solrPsmRepositoryFactory = new SolrPsmRepositoryFactory(new SolrTemplate(server));

        // delete all data
        deleteAllData();
        // insert test data
        insertTestData();
    }

    private void deleteAllData() {
        PsmIndexService psmIndexService = new PsmIndexService(solrPsmRepositoryFactory.create());
        psmIndexService.deleteAll();
    }

    private void insertTestData() {
        addPsm_1();
        addPsm_2();
        addPsm_3();
    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(ZERO_DOCS, response.getResults().getNumFound());
    }

    @Test
    public void testSearchByAccessionUsingQuery() throws Exception {

        SolrParams params = new SolrQuery(PsmFields.ID + ":" + PSM_1_ID);
        QueryResponse response = server.query(params);
        assertEquals(SINGLE_DOC, response.getResults().getNumFound());
        assertEquals(PSM_1_ID, response.getResults().get(0).get(PsmFields.ID));

    }

    @Test
    public void testSearchById() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(PSM_1_ID);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());
    }

    @Test
    public void testSearchByIds() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(Arrays.asList(PSM_1_ID, PSM_2_ID, PSM_3_ID));

        assertNotNull(psms);
        assertEquals(3, psms.size());
    }

    @Test
    public void testSearchByIdWildcard() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(PSM_ID_PREFIX + "*");

        assertNotNull(psms);
        assertEquals(NUM_TEST_PSMS, psms.size());

    }


    @Test
    public void testSearchByPeptideSequence() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSubSequence(PSM_3_SEQUENCE);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByPeptideSequenceWithPaging() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        // first check that there are more than one result for the generic query
        List<Psm> psms = psmSearchService.findByPeptideSubSequence(PSM_3_SEQUENCE);
        assertNotNull(psms);
        assertEquals(2, psms.size());

        // then check that the page restriction to one result works
        psms = psmSearchService.findByPeptideSubSequence(PSM_3_SEQUENCE, new PageRequest(1,1)).getContent();
        assertNotNull(psms);
        assertEquals(1, psms.size()); // expect only one result instead of two
    }

    @Test
    public void testSearchByPeptideSequenceMiddleSubstring() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSubSequence(SEQUENCE_SUB);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByPepSequenceStandardCase() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSequence(PSM_2_SEQUENCE);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm2 = psms.get(0);
        assertEquals(PSM_2_ID, psm2.getId());

    }

    @Test
    public void testSearchByPepSequenceBiggerThanOneHundred() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSequence(SEQUENCE_BT_100);

        assertNotNull(psms);
        assertEquals(0, psms.size());

    }

    @Test
    public void testSearchByPepSequenceLessThanSix() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSequence(SEQUENCE_LT_6);

        assertNotNull(psms);
        assertEquals(0, psms.size());

    }

    @Test
    public void testSearchByPepSequenceWildcard() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSequence(PSM_3_SEQUENCE + "*");

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByPepSequenceStrict() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSequence(PSM_3_SEQUENCE);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm3 = psms.get(0);
        assertEquals(PSM_3_ID, psm3.getId());

    }

    @Test
    public void testSearchByPeptideSequenceAndProjectAccessions() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSubSequenceAndProjectAccession(PSM_3_SEQUENCE, PROJECT_2_ACCESSION);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByPeptideSequenceAndAssaysAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByPeptideSubSequenceAndAssayAccession(PSM_3_SEQUENCE, ASSAY_2_ACCESSION);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue( PSM_3_ID.equals(psm.getId()) || PSM_2_ID.equals(psm.getId()) );
        }

    }

    @Test
    public void testSearchByProjectAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByProjectAccession(PROJECT_2_ACCESSION);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByProjectAccessions() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByProjectAccession(Arrays.asList(PROJECT_1_ACCESSION, PROJECT_2_ACCESSION));

        assertNotNull(psms);
        assertEquals(3, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) ||
                    psm.getId().contentEquals(PSM_2_ID) ||
                    psm.getId().contentEquals(PSM_1_ID));
        }

    }

    @Test
    public void testSearchByAssaysAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByAssayAccession(ASSAY_2_ACCESSION);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchByAssaysAccessions() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByAssayAccession(Arrays.asList(ASSAY_1_ACCESSION, ASSAY_2_ACCESSION));

        assertNotNull(psms);
        assertEquals(3, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) ||
                    psm.getId().contentEquals(PSM_2_ID) ||
                    psm.getId().contentEquals(PSM_1_ID));
        }

    }

    @Test
    public void testSearchBySpectrumId() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findBySpectrumId(PSM_1_SPECTRUM);

        assertNotNull(psms);
        assertEquals(2, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_1_ID) || psm.getId().contentEquals(PSM_2_ID));
        }

    }

    @Test
    public void testSearchBySpectrumIds() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findBySpectrumId(Arrays.asList(PSM_1_SPECTRUM, PSM_2_SPECTRUM, PSM_3_SPECTRUM));

        assertNotNull(psms);
        assertEquals(3, psms.size());

        for (Psm psm : psms) {
            assertTrue(psm.getId().contentEquals(PSM_3_ID) ||
                    psm.getId().contentEquals(PSM_2_ID) ||
                    psm.getId().contentEquals(PSM_1_ID));
        }

    }

    @Test
    public void testSearchByReportedId() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByReportedId(PSM_1_REPORTED_ID);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());

    }

    @Test
    public void testSearchByReportedIdAndProjectAccessions() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByReportedIdAndProjectAccession(PSM_1_REPORTED_ID, PROJECT_1_ACCESSION);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());
    }

    @Test
    public void testSearchByReportedIdAndAssaysAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByReportedIdAndAssayAccession(PSM_1_REPORTED_ID, ASSAY_1_ACCESSION);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());

    }

    @Test
    public void testSearchByReportedIdAndIncorrectAssaysAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        //There is not psm with these reported id and the assay accession
        List<Psm> psms = psmSearchService.findByReportedIdAndAssayAccession(PSM_1_REPORTED_ID, ASSAY_2_ACCESSION);
        assertNotNull(psms);
        assertEquals(0, psms.size());

    }

    @Test
    public void testSearchByProteinAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByProteinAccession(PROTEIN_1_ACCESSION);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());

    }

    @Test
    public void testSearchByProteinAccessionAndProjectAccessions() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByProteinAccessionAndProjectAccession(PROTEIN_1_ACCESSION, PROJECT_1_ACCESSION);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());
    }

    @Test
    public void testSearchByProteinAccessionAndAssaysAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findByProteinAccessionAndAssayAccession(PROTEIN_1_ACCESSION, ASSAY_1_ACCESSION);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID, psm1.getId());

    }

    @Test
    public void testPeptideSequencesProjectionByProjectAccession() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<String> peptideSequences = psmSearchService.findPeptideSequencesByProjectAccession(PROJECT_1_ACCESSION);

        assertNotNull(peptideSequences);
        assertEquals(1, peptideSequences.size());

        String sequence1 = peptideSequences.get(0);
        assertEquals(PSM_1_SEQUENCE, sequence1);

    }

    @Test
    public void testFindPeptides() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        FacetPage<Psm> peptides = psmSearchService.findPeptidesByProteinAccessionAndAssayAccession(PROTEIN_2_ACCESSION, ASSAY_2_ACCESSION, new PageRequest(0, 10));
        assertNotNull(peptides);

        List<FacetPivotFieldEntry> pivot = peptides.getPivot("peptide_sequence,modifications");
        assertNotNull(pivot);
        assertEquals(2,pivot.toArray().length);

        peptides = psmSearchService.findPeptidesByProteinAccessionAndProjectAccession(PROTEIN_2_ACCESSION, PROJECT_2_ACCESSION, new PageRequest(0, 10));
        pivot = peptides.getPivot("peptide_sequence,modifications");
        assertNotNull(pivot);
        assertEquals(2,pivot.toArray().length);

    }

    @Test
    public void testDeletePsm() throws SolrServerException {
        PsmIndexService psmIndexService = new PsmIndexService(this.solrPsmRepositoryFactory.create());
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(PSM_1_ID);
        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);

        psmIndexService.delete(psm1);
        psms = psmSearchService.findById(PSM_1_ID);

        assertNotNull(psms);
        assertEquals(0, psms.size());
    }

    @Test
    public void testDeletePsms() throws SolrServerException {
        PsmIndexService psmIndexService = new PsmIndexService(this.solrPsmRepositoryFactory.create());
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(Arrays.asList(PSM_1_ID,PSM_2_ID,PSM_3_ID));
        assertNotNull(psms);
        assertEquals(3, psms.size());

        psmIndexService.delete(psms);
        psms = psmSearchService.findById(Arrays.asList(PSM_1_ID,PSM_2_ID,PSM_3_ID));

        assertNotNull(psms);
        assertEquals(0, psms.size());
    }

    private void addPsm_1() {
        Psm psm = new Psm();

        psm.setId(PSM_1_ID);
        psm.setReportedId(PSM_1_REPORTED_ID);
        psm.setPeptideSequence(PSM_1_SEQUENCE);
        psm.setSpectrumId(PSM_1_SPECTRUM);
        psm.setProteinAccession(PROTEIN_1_ACCESSION);
        psm.setProjectAccession(PROJECT_1_ACCESSION);
        psm.setAssayAccession(ASSAY_1_ACCESSION);

//        List<String> modifications = new LinkedList<String>();

        Modification mod1 = new Modification();
        mod1.addPosition(MOD_1_POS, null);
        mod1.setAccession(MOD_1_ACCESSION);

        Modification mod2 = new Modification();
        mod2.addPosition(MOD_2_POS, null);
        mod2.setAccession(MOD_2_ACCESSION);

        List<ModificationProvider> modifications = new LinkedList<ModificationProvider>();
        modifications.add(mod1);
        modifications.add(mod2);

        psm.setModifications(modifications);


        PsmIndexService psmIndexService = new PsmIndexService(this.solrPsmRepositoryFactory.create());
        psmIndexService.save(psm);

    }

    private void addPsm_2() {
        Psm psm = new Psm();

        psm.setId(PSM_2_ID);
        psm.setReportedId(PSM_2_REPORTED_ID);
        psm.setPeptideSequence(PSM_2_SEQUENCE);
        psm.setSpectrumId(PSM_2_SPECTRUM);
        psm.setProteinAccession(PROTEIN_2_ACCESSION);
        psm.setProjectAccession(PROJECT_2_ACCESSION);
        psm.setAssayAccession(ASSAY_2_ACCESSION);

        Modification mod1 = new Modification();
        mod1.addPosition(MOD_1_POS, null);
        mod1.setAccession(MOD_1_ACCESSION);

        Modification mod2 = new Modification();
        mod2.addPosition(MOD_2_POS, null);
        mod2.setAccession(MOD_2_ACCESSION);

        List<ModificationProvider> modifications = new LinkedList<ModificationProvider>();
        modifications.add(mod1);
        modifications.add(mod2);

        psm.setModifications(modifications);

        PsmIndexService psmIndexService = new PsmIndexService(this.solrPsmRepositoryFactory.create());
        psmIndexService.save(psm);
    }

    private void addPsm_3() {
        Psm psm = new Psm();

        psm.setId(PSM_3_ID);
        psm.setReportedId(PSM_3_REPORTED_ID);
        psm.setPeptideSequence(PSM_3_SEQUENCE);
        psm.setSpectrumId(PSM_3_SPECTRUM);
        psm.setProteinAccession(PROTEIN_2_ACCESSION);
        psm.setProjectAccession(PROJECT_2_ACCESSION);
        psm.setAssayAccession(ASSAY_2_ACCESSION);

        Modification mod1 = new Modification();
        mod1.addPosition(MOD_1_POS, null);
        mod1.setAccession(MOD_1_ACCESSION);

        Modification mod2 = new Modification();
        mod2.addPosition(MOD_2_POS, null);
        mod2.setAccession(MOD_2_ACCESSION);

        List<ModificationProvider> modifications = new LinkedList<ModificationProvider>();
        modifications.add(mod1);
        modifications.add(mod2);

        psm.setModifications(modifications);

        PsmIndexService psmIndexService = new PsmIndexService(this.solrPsmRepositoryFactory.create());
        psmIndexService.save(psm);
    }

}
