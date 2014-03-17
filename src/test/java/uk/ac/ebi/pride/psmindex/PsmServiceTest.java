package uk.ac.ebi.pride.psmindex;

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
import org.junit.runner.RunWith;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepositoryFactory;

import java.util.List;

public class PsmServiceTest extends SolrTestCaseJ4 {

    /**
     * PSM 1 test data
     */
    private static final String PSM_1_ID = "TEST-PSM-ID1";

    /**
     * PSM 2 test data
     */
    private static final String PSM_2_ID = "TEST-PSM-ID2";

    /**
     * PSM 3 test data
     */
    private static final String PSM_3_ID = "TEST-PSM-ID3";

    private static final int NUM_TEST_PSMS = 3;
    private static final String PSM_ID_PREFIX = "TEST-PSM-ID";

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
        PsmIndexService psmIndexService = new PsmIndexService(solrPsmRepositoryFactory.create());

        Psm psm = new Psm();
        for (int i=0;i<NUM_TEST_PSMS;i++) {
            psm.setId(PSM_ID_PREFIX + i);
            psmIndexService.save(psm);
        }

    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(ZERO_DOCS, response.getResults().getNumFound());
    }

    @Test
    public void testSearchById() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(PSM_1_ID);

        assertNotNull(psms);
        assertEquals(1, psms.size());

        Psm psm1 = psms.get(0);
        assertEquals(PSM_1_ID,psm1.getId());
    }

    @Test
    public void testSearchByIdWildcard() throws SolrServerException {
        PsmSearchService psmSearchService = new PsmSearchService(solrPsmRepositoryFactory.create());

        List<Psm> psms = psmSearchService.findById(PSM_ID_PREFIX+"*");

        assertNotNull(psms);
        assertEquals(NUM_TEST_PSMS, psms.size());

    }

}