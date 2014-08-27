package uk.ac.ebi.pride.psmindex.search.indexer;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.indexutils.modifications.Modification;
import uk.ac.ebi.pride.indexutils.params.CvParam;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepositoryFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ntoro
 * @since 26/08/2014 13:36
 */
public class NeutralLossIndexerTest extends SolrTestCaseJ4 {

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

    private static final String MOD_1_SYNONYM = "phosphorylation";
    private static final String MOD_2_SYNONYM = "amidation";

    //Neutral Loss without mod
    private static final String NEUTRAL_LOSS_ONT = "MS";
    private static final String NEUTRAL_LOSS_ACC = "MS:1001524";
    private static final String NEUTRAL_LOSS_NAME = "fragment neutral loss";
    private static final String NEUTRAL_LOSS_VAL = "63.998283";
    private static final Integer NEUTRAL_LOSS_POS = 7;

    //Neutral Loss with mod
    private static final Integer NEUTRAL_LOSS_MOD_POS = 3;


    private SolrServer server;
    private SolrPsmRepositoryFactory solrPsmRepositoryFactory;

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

    }


    @Test
    public void addPsmWithNeutralLoss() {
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
        mod1.setName(MOD_1_NAME);

        Modification mod2 = new Modification();
        mod2.addPosition(MOD_2_POS, null);
        mod2.setAccession(MOD_2_ACCESSION);
        mod2.setName(MOD_2_NAME);

        Modification mod3 = new Modification();
        mod3.addPosition(NEUTRAL_LOSS_POS, null);
        mod3.setNeutralLoss(new CvParam(NEUTRAL_LOSS_ONT, NEUTRAL_LOSS_ACC, NEUTRAL_LOSS_NAME, NEUTRAL_LOSS_VAL));

        List<ModificationProvider> modifications = new LinkedList<ModificationProvider>();
        modifications.add(mod1);
        modifications.add(mod2);
        modifications.add(mod3);

        psm.setModifications(modifications);

        PsmIndexService psmIndexService = new PsmIndexService(server, this.solrPsmRepositoryFactory.create());
        psmIndexService.save(psm);
    }

    @Test
    public void addPsmWithNeutralLossAndModSamePos() {
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
        mod1.setName(MOD_1_NAME);

        Modification mod2 = new Modification();
        mod2.addPosition(NEUTRAL_LOSS_MOD_POS, null);
        mod2.setNeutralLoss(new CvParam(NEUTRAL_LOSS_ONT, NEUTRAL_LOSS_ACC, NEUTRAL_LOSS_NAME, NEUTRAL_LOSS_VAL));

        Modification mod3 = new Modification();
        mod3.addPosition(MOD_2_POS, null);
        mod3.setAccession(MOD_2_ACCESSION);
        mod3.setName(MOD_2_NAME);

        List<ModificationProvider> modifications = new LinkedList<ModificationProvider>();
        modifications.add(mod1);
        modifications.add(mod2);
        modifications.add(mod3);

        psm.setModifications(modifications);

        PsmIndexService psmIndexService = new PsmIndexService(server, this.solrPsmRepositoryFactory.create());
        psmIndexService.save(psm);
    }
}
