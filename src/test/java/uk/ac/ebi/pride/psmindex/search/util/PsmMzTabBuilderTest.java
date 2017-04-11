package uk.ac.ebi.pride.psmindex.search.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.*;
import static uk.ac.ebi.pride.psmindex.search.util.PsmMzTabBuilder.readPsmsFromMzTabFile;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class PsmMzTabBuilderTest {


    private static Logger logger = LoggerFactory.getLogger(PsmMzTabBuilderTest.class);
    private static ErrorLogOutputStream errorLogOutputStream = new ErrorLogOutputStream(logger);

    private static final String PROJECT_1_ACCESSION = "PXD000581";
    private static final String PROJECT_2_ACCESSION = "TST000121";

    private static final String PROJECT_1_ASSAY_1 = "32411";
    private static final String PROJECT_1_ASSAY_2 = "32416";
    private static final String PROJECT_2_ASSAY_1 = "00001";

    private static final String MOD_ACCESSION = "MOD:01214";
    private static final String MOD_NAME = "iodoacetamide - site C";

    private static final String FILE_PRE = ";PRIDE_Exp_Complete_Ac_";
    private static final String FILE_POST = ".xml;spectrum=";

    private static MZTabFile mzTabFileP1A1;
    private static MZTabFile mzTabFileP1A2;
    private static MZTabFile mzTabFileP2A1;


    public static final int NUM_ASSAYS = 2;


    @BeforeClass
    public static void initialise() throws Exception {
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
    public void testReadPsmsFromMzTabFilesDirectory() throws Exception {
        Map<String, List<Psm>> psms = new HashMap<String, List<Psm>>();

        psms.put(PROJECT_1_ASSAY_1, readPsmsFromMzTabFile(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, mzTabFileP1A1));
        psms.put(PROJECT_1_ASSAY_2, readPsmsFromMzTabFile(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2, mzTabFileP1A2));

        assertTrue(psms.size() == NUM_ASSAYS);

        for (Map.Entry<String, List<Psm>> stringLinkedListEntry : psms.entrySet()) {
            for (Psm psm : stringLinkedListEntry.getValue()) {
                assertTrue(psm.getId().startsWith(PROJECT_1_ACCESSION + "_"));
            }
        }
    }

    @Test
    public void testReadPsmFromMzTabFileAndCompare() throws Exception {
        Map<String, List<Psm>> psms = new HashMap<String, List<Psm>>();
        psms.put(PROJECT_2_ASSAY_1, readPsmsFromMzTabFile(PROJECT_2_ACCESSION, PROJECT_2_ASSAY_1, mzTabFileP2A1));

        assertTrue(psms.size() == 1);

        Psm firstPsm = psms.entrySet().iterator().next().getValue().get(0);

        assertEquals("TST000121_00001_175_orf19/5636_QSTSSTPCPYWDTGCLCVMPQFAGAVGNCVAK", firstPsm.getId());
        assertEquals("orf19/5636", firstPsm.getProteinAccession());
    }

    private void checkModifications(Iterable<ModificationProvider> modifications) {
        Iterator<ModificationProvider> iterator = modifications.iterator();

        ModificationProvider mod = iterator.next();
        assertEquals(MOD_ACCESSION, mod.getAccession());
        assertEquals(MOD_NAME, mod.getName());
        assertEquals((Integer) 8, mod.getMainPosition());

        mod = iterator.next();
        assertEquals(MOD_ACCESSION, mod.getAccession());
        assertEquals(MOD_NAME, mod.getName());
        assertEquals((Integer) 15, mod.getMainPosition());

        mod = iterator.next();
        assertEquals(MOD_ACCESSION, mod.getAccession());
        assertEquals(MOD_NAME, mod.getName());
        assertEquals((Integer) 17, mod.getMainPosition());

        mod = iterator.next();
        assertEquals(MOD_ACCESSION, mod.getAccession());
        assertEquals(MOD_NAME, mod.getName());
        assertEquals((Integer) 29, mod.getMainPosition());
    }

    private void checkSearchEngine(Iterable<CvParamProvider> searchEngines) {
        CvParamProvider cvParamProvider = searchEngines.iterator().next();
        assertEquals("MS", cvParamProvider.getCvLabel());
        assertEquals("MS:1001207", cvParamProvider.getAccession());
        assertEquals("Mascot", cvParamProvider.getName());
        assertEquals(null, cvParamProvider.getValue());
    }

    private void checkSearchEnginesScores(Iterable<CvParamProvider> searchEngineScores) {
        CvParamProvider cvParamProvider = searchEngineScores.iterator().next();

        assertEquals("PRIDE", cvParamProvider.getCvLabel());
        assertEquals("PRIDE:0000069", cvParamProvider.getAccession());
        assertEquals("Mascot score", cvParamProvider.getName());
        assertEquals("91.24", cvParamProvider.getValue());
    }
}
