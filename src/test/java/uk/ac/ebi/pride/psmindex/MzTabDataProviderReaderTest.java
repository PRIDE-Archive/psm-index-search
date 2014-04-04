package uk.ac.ebi.pride.psmindex;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.util.MzTabDataProviderReader;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class MzTabDataProviderReaderTest {

    public static final String PROJECT_ACCESSION = "PXD000581";
    public static final String FILE_PRE = ";PRIDE_Exp_Complete_Ac_";
    public static final String FILE_POST = ".xml;spectrum=";

    public static File mzTabFilesDirectory;
    public static final int NUM_ASSAYS = 2;


    @BeforeClass
    public static void initialise() throws Exception {
        mzTabFilesDirectory = new File("src/test/resources/submissions/2014/01/PXD000581/generated");
    }

    @Test
    public void testReadPsmsFromMzTabFilesDirectory() throws Exception {
        Map<String,LinkedList<Psm>> psms = MzTabDataProviderReader.readPsmsFromMzTabFilesDirectory(PROJECT_ACCESSION,mzTabFilesDirectory);

        assertTrue(psms.size() == NUM_ASSAYS);

        for (Map.Entry<String, LinkedList<Psm>> stringLinkedListEntry : psms.entrySet()) {
            for (Psm psm : stringLinkedListEntry.getValue()) {
                assertTrue(psm.getSpectrumId().startsWith(PROJECT_ACCESSION+FILE_PRE+stringLinkedListEntry.getKey()+ FILE_POST));
            }
        }
    }

}
