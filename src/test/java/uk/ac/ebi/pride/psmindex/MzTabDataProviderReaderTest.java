package uk.ac.ebi.pride.psmindex;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.util.MzTabDataProviderReader;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class MzTabDataProviderReaderTest {

    public static final String PROJECT_ACCESSION = "PXD000581";
    public static final String PROJECT_ACCESSION_2 = "TST000121";

    public static final String FILE_PRE = ";PRIDE_Exp_Complete_Ac_";
    public static final String FILE_POST = ".xml;spectrum=";

    public static File mzTabFilesDirectory;
    public static File mzTabFilesDirectory_2;

    public static final int NUM_ASSAYS = 2;


    @BeforeClass
    public static void initialise() throws Exception {
        mzTabFilesDirectory = new File("src/test/resources/submissions/2014/01/PXD000581/generated");
        mzTabFilesDirectory_2 = new File("src/test/resources/submissions/TST000121/generated");

    }

    @Test
    public void testReadPsmsFromMzTabFilesDirectory() throws Exception {
        Map<String, LinkedList<Psm>> psms = MzTabDataProviderReader.readPsmsFromMzTabFilesDirectory(PROJECT_ACCESSION, mzTabFilesDirectory);

        assertTrue(psms.size() == NUM_ASSAYS);

        for (Map.Entry<String, LinkedList<Psm>> stringLinkedListEntry : psms.entrySet()) {
            for (Psm psm : stringLinkedListEntry.getValue()) {
                assertTrue(psm.getSpectrumId().startsWith(PROJECT_ACCESSION + FILE_PRE + stringLinkedListEntry.getKey() + FILE_POST));
            }
        }
    }

    @Test
    public void testReadPsmFromMzTabFileAndCompare() throws Exception {
        Map<String, LinkedList<Psm>> psms = MzTabDataProviderReader.readPsmsFromMzTabFilesDirectory(PROJECT_ACCESSION_2, mzTabFilesDirectory_2);

        assertTrue(psms.size() == 1);

        Psm firstPsm = psms.entrySet().iterator().next().getValue().getFirst();

        assertEquals(firstPsm.getId(), "TST000121_00001_175_orf19/5636_QSTSSTPCPYWDTGCLCVMPQFAGAVGNCVAK");
        assertEquals(firstPsm.getReportedId(), "175");
        assertEquals(firstPsm.getSpectrumId(), "TST000121;result_1_sample_1_dat.pride.xml;spectrum=175");
        assertEquals(firstPsm.getProteinAccession(), "orf19/5636");
        assertNull(firstPsm.isUnique());
        assertEquals(firstPsm.getSearchEngine(), Arrays.asList("[MS, MS:1001207, Mascot, ]"));
        assertEquals(firstPsm.getSearchEngineScore(), Arrays.asList("[PRIDE, PRIDE:0000069, Mascot score, 91.24]"));
        assertEquals(firstPsm.getModifications(), Arrays.asList("8|15|17|29-MOD:01214"));
        assertNull(firstPsm.getRetentionTime());
        assertNull(firstPsm.getCharge());
        assertTrue(firstPsm.getExpMassToCharge() == 1183.8615);
        assertNull(firstPsm.getCalculatedMassToCharge());
        assertNull(firstPsm.getPreAminoAcid());
        assertNull(firstPsm.getPostAminoAcid());
        assertTrue(firstPsm.getStartPosition() == 61);
        assertTrue(firstPsm.getEndPosition() == 92);
    }
}
