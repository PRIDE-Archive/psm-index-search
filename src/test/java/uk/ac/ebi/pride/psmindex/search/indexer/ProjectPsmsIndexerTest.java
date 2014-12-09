package uk.ac.ebi.pride.psmindex.search.indexer;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepositoryFactory;
import uk.ac.ebi.pride.psmindex.search.util.ErrorLogOutputStream;

import java.io.File;
import java.util.Collection;

public class ProjectPsmsIndexerTest extends SolrTestCaseJ4 {


    private static Logger logger = LoggerFactory.getLogger(ProjectPsmsIndexerTest.class);
    private static ErrorLogOutputStream errorLogOutputStream = new ErrorLogOutputStream(logger);

    private static final String PROJECT_1_ACCESSION = "PXD000581";
    private static final String PROJECT_2_ACCESSION = "TST000121";

    private static final String PROJECT_1_ASSAY_1 = "32411";
    private static final String PROJECT_1_ASSAY_2 = "32416";
    private static final String PROJECT_2_ASSAY_1 = "00001";

    private static final int NUM_PSMS_PROJECT_1 = 4;
    private static final int NUM_PSMS_P1A1 = 3;
    private static final int NUM_PSMS_P1A2 = 1;

    private static final int NUM_PSMS_PROJECT_2 = 7;
    private static final int NUM_PSMS_P2A1 = 7;

    private static final String PSM3_ID = "3";

    private static MZTabFile mzTabFileP1A1;
    private static MZTabFile mzTabFileP1A2;
    private static MZTabFile mzTabFileP2A1;

    private SolrServer server;
    private SolrPsmRepositoryFactory solrPsmRepositoryFactory;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
        solrPsmRepositoryFactory = new SolrPsmRepositoryFactory(new SolrTemplate(server));
    }

    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/test/resources/solr/collection1/conf/solrconfig.xml",
                "src/test/resources/solr/collection1/conf/schema.xml",
                "src/test/resources/solr");

        mzTabFileP1A1 = new MZTabFileParser(
                new File("src/test/resources/submissions/2014/01/PXD000581/generated/PRIDE_Exp_Complete_Ac_32411.mztab"),
                errorLogOutputStream).getMZTabFile();
        mzTabFileP1A2 = new MZTabFileParser(
                new File("src/test/resources/submissions/2014/01/PXD000581/generated/PRIDE_Exp_Complete_Ac_32416.mztab"),
                errorLogOutputStream).getMZTabFile();
        mzTabFileP2A1 = new MZTabFileParser(
                new File("src/test/resources/submissions/TST000121/generated/PRIDE_Exp_Complete_Ac_00001.mztab"),
                errorLogOutputStream).getMZTabFile();
    }

    @Test
    public void testIndexAllPsmsForProjectAndAssay() throws Exception {
        PsmSearchService PsmSearchService = new PsmSearchService(this.solrPsmRepositoryFactory.create());
        PsmIndexService PsmIndexService = new PsmIndexService(server, this.solrPsmRepositoryFactory.create());

        ProjectPsmsIndexer projectPsmsIndexer = new ProjectPsmsIndexer(PsmSearchService, PsmIndexService);

        projectPsmsIndexer.indexAllPsmsForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, mzTabFileP1A1);
        projectPsmsIndexer.indexAllPsmsForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2, mzTabFileP1A2);
        projectPsmsIndexer.indexAllPsmsForProjectAndAssay(PROJECT_2_ACCESSION, PROJECT_2_ASSAY_1, mzTabFileP2A1);

        //We force the commit for testing purposes (avoids wait one minute)
        server.commit();

        Collection<Psm> psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_1);
        assertEquals(NUM_PSMS_P1A1, psms.size());
        psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_2);
        assertEquals(NUM_PSMS_P1A2, psms.size());
        psms = PsmSearchService.findByAssayAccession(PROJECT_2_ASSAY_1);
        assertEquals(NUM_PSMS_P2A1, psms.size());

        psms = PsmSearchService.findByProjectAccession(PROJECT_1_ACCESSION);
        assertEquals(NUM_PSMS_PROJECT_1, psms.size());

        psms = PsmSearchService.findByProjectAccession(PROJECT_2_ACCESSION);
        assertEquals(NUM_PSMS_PROJECT_2, psms.size());

    }

    @Test
    public void testDeleteAllPsmsForProject() throws Exception {

        PsmSearchService PsmSearchService = new PsmSearchService(this.solrPsmRepositoryFactory.create());
        PsmIndexService PsmIndexService = new PsmIndexService(server, this.solrPsmRepositoryFactory.create());

        ProjectPsmsIndexer projectPsmsIndexer = new ProjectPsmsIndexer(PsmSearchService, PsmIndexService);

        projectPsmsIndexer.indexAllPsmsForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, mzTabFileP1A1);
        projectPsmsIndexer.indexAllPsmsForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2, mzTabFileP1A2);

        //We force the commit for testing purposes (avoids wait one minute)
        server.commit();

        Collection<Psm> psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_1);
        assertEquals(NUM_PSMS_P1A1, psms.size());

        psms = PsmSearchService.findByProjectAccession(PROJECT_1_ACCESSION);
        assertEquals(NUM_PSMS_PROJECT_1, psms.size());
        psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_2);
        assertEquals(NUM_PSMS_P1A2, psms.size());

        psms = PsmSearchService.findByReportedId(PSM3_ID);
        assertEquals(1, psms.size());

        // delete
        projectPsmsIndexer.deleteAllPsmsForProject(PROJECT_1_ACCESSION);

        psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_1);
        assertEquals(0, psms.size());
        psms = PsmSearchService.findByAssayAccession(PROJECT_1_ASSAY_2);
        assertEquals(0, psms.size());

        psms = PsmSearchService.findByProjectAccession(PROJECT_1_ACCESSION);
        assertEquals(0, psms.size());

        psms = PsmSearchService.findByReportedId(PSM3_ID);
        assertEquals(0, psms.size());

    }
}
