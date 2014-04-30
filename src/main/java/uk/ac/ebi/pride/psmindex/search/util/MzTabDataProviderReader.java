package uk.ac.ebi.pride.psmindex.search.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.prider.utils.spectrum.SpectrumIDGenerator;
import uk.ac.ebi.pride.prider.utils.spectrum.SpectrumIdGeneratorPride3;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
public class MzTabDataProviderReader {

    private static Logger logger = LoggerFactory.getLogger(MzTabDataProviderReader.class.getName());

    private static ErrorLogOutputStream errorLogOutputStream = new ErrorLogOutputStream(logger);

    /**
     * mzTab files in the directory will have names such as PRIDE_Exp_Complete_Ac_28654.submissions. We are interested in the
     * assay accession, the last bit if we split by '_'.
     *
     * @return A map of assay accessions to PSMs
     * @throws java.io.IOException
     */
    @Deprecated
    public static Map<String, LinkedList<Psm>> readPsmsFromMzTabFilesDirectory(String projectAccession, File mzTabFilesDirectory) throws IOException, MZTabException {

        Map<String, LinkedList<Psm>> res = new HashMap<String, LinkedList<Psm>>();

        File[] mzTabFilesInDirectory = mzTabFilesDirectory.listFiles(new MzTabFileNameFilter());
        if (mzTabFilesInDirectory != null) {
            for (File tabFile : mzTabFilesInDirectory) {

                MZTabFileParser mzTabFileParser = new MZTabFileParser(tabFile, errorLogOutputStream);
                MZTabFile mzTabFile = mzTabFileParser.getMZTabFile();

                if (mzTabFile != null) {
                    // get assay accession
                    String assayAccession = tabFile.getName().split("[_\\.]")[4];

                    // get all psms from the file
                    LinkedList<Psm> assayPsms = convertFromMzTabPsmsToPrideArchivePsms(mzTabFile.getPSMs(), projectAccession, assayAccession);

                    // add assay psms to the result
                    res.put(assayAccession, assayPsms);
                    logger.debug("Found " + assayPsms.size() + " psms for Assay " + assayAccession + " in file " + tabFile.getAbsolutePath());
                } else {
                    mzTabFileParser.getErrorList().print(errorLogOutputStream);
                }

            }
        }

        return res;
    }

    /**
     * The map between the assay accession and the file need to be provided externally from the database
     *
     * @return A map of assay accessions to PSMs
     * @throws java.io.IOException
     */
    public static LinkedList<Psm> readPsmsFromMzTabFile(String projectAccession, String assayAccession, MZTabFile mzTabFile) throws IOException, MZTabException {

        LinkedList<Psm> res = new LinkedList<Psm>();

        if (mzTabFile != null) {

                // get all psms from the file
                res = convertFromMzTabPsmsToPrideArchivePsms(mzTabFile.getPSMs(), projectAccession, assayAccession);
                logger.debug("Found " + res.size() + " psms for Assay " + assayAccession + " in file " + mzTabFile);

        }

        return res;
    }

    private static LinkedList<Psm> convertFromMzTabPsmsToPrideArchivePsms(Collection<PSM> mzTabPsms, String projectAccession, String assayAccession) {

        LinkedList<Psm> res = new LinkedList<Psm>();

        for (PSM filePsm : mzTabPsms) {
            String cleanPepSequence = PsmIdCleaner.cleanPeptideSequence(filePsm.getSequence());

            Psm newPsm = new Psm();
            newPsm.setId(
                    projectAccession + "_"
                            + assayAccession + "_"
                            + filePsm.getPSM_ID() + "_"
                            + filePsm.getAccession() + "_"
                            + cleanPepSequence
            );
            newPsm.setReportedId(filePsm.getPSM_ID());
            newPsm.setSpectrumId(createSpectrumId(filePsm, projectAccession));
            newPsm.setPepSequence(cleanPepSequence);
            newPsm.setProjectAccession(projectAccession);
            newPsm.setAssayAccession(assayAccession);
            newPsm.setProteinAccession(filePsm.getAccession());
            newPsm.setModifications(new LinkedList<String>());
            for (Modification mod : filePsm.getModifications()) {
                String modificationLine = new String();

                for (Map.Entry<Integer, CVParam> modPosition : mod.getPositionMap().entrySet()) {
                    modificationLine = modificationLine + modPosition.getKey();
                    if (modPosition.getValue() != null) {
                        CVParam posCvParam = modPosition.getValue();

                        modificationLine = modificationLine + "["
                                + posCvParam.getCvLabel() + ","
                                + posCvParam.getAccession() + ","
                                + posCvParam.getName() + ","
                                + posCvParam.getValue()
                                + "]";
                    }
                    modificationLine = modificationLine + "|";
                }
                if (modificationLine.length() > 0)
                    modificationLine = modificationLine.substring(0, modificationLine.length() - 1); // remove the last |

                modificationLine = modificationLine + "-";

                String modAccession = mod.getAccession();
                if (!mod.getAccession().startsWith("MOD")) {
                    modAccession = "MOD:" + modAccession;
                }
                modificationLine = modificationLine + modAccession;

                newPsm.getModifications().add(modificationLine);

                // as an alternative we can call the mzTab method to write it back
                // newPsm.getModifications().add(mod.toString());
            }

            //Unique
            MZBoolean unique = filePsm.getUnique();
            if (unique != null) {
                newPsm.setUnique(MZBoolean.True.equals(unique));
            } else {
                newPsm.setUnique(null);
            }

            //Search Engine
            //For now we keep the same representation as it is in the mzTab line
            newPsm.setSearchEngine(new LinkedList<String>());
            //If the mzTab search engine can not be converted SplitList can be null
            if (filePsm.getSearchEngine() != null) {
                for (Param searchEngine : filePsm.getSearchEngine()) {
                    //The toString method is overridden in mzTab library to write the Param mzTab representation
                    newPsm.getSearchEngine().add(searchEngine.toString());
                }
            }

            //Search Engine Score
            newPsm.setSearchEngineScore(new LinkedList<String>());
            //If the mzTab search engine can not be converted SplitList can be null
            if (filePsm.getSearchEngineScore() != null) {
                for (Param searchEngineScore : filePsm.getSearchEngineScore()) {
                    //The toString method is overridden in mzTab library to write the Param mzTab representation
                    newPsm.getSearchEngineScore().add(searchEngineScore.toString());
                }
            }

            // Retention Time
            // NOTE: If the psm was discovered as a combination of several spectra, we will
            // simplify the case choosing only the first spectrum and the first retention time
            SplitList<Double> retentionTimes = filePsm.getRetentionTime();
            if (retentionTimes != null && !retentionTimes.isEmpty()) {
                newPsm.setRetentionTime(retentionTimes.get(0));
            }

            newPsm.setCharge(filePsm.getCharge());
            newPsm.setExpMassToCharge(filePsm.getExpMassToCharge());
            newPsm.setCalculatedMassToCharge(filePsm.getCalcMassToCharge());
            newPsm.setPreAminoAcid(filePsm.getPre());
            newPsm.setPostAminoAcid(filePsm.getPost());

            if(filePsm.getStart()!=null){
                try{
                    newPsm.setStartPosition(Integer.parseInt(filePsm.getStart()));
                } catch (NumberFormatException e){
                    logger.warn("The start position of the peptide can not be parsed as a Number", e);
                }
            }

            if(filePsm.getEnd()!=null){
                try{
                    newPsm.setEndPosition(Integer.parseInt(filePsm.getEnd()));
                } catch (NumberFormatException e){
                    logger.warn("The end position of the peptide can not be parsed as a Number", e);
                }
            }

            res.add(newPsm);
        }

        return res;
    }

    /**
     * Creates a spectrum Id compatible with PRIDE 3
     *
     * @param psm              original mzTab psm
     * @param projectAccession PRIDE Archive accession
     * @return the spectrum Id or "unknown_id" if the conversion fails
     */
    private static String createSpectrumId(PSM psm, String projectAccession) {

        String msRunFileName;
        String identifierInMsRunFile;
        String spectrumId = "unknown_id";

        SpectraRef spectrum;

        SplitList<SpectraRef> spectra = psm.getSpectraRef();
        if (spectra != null && !spectra.isEmpty()) {

            // NOTE: If the psm was discovered as a combination of several spectra, we will
            // simplify the case choosing only the first spectrum
            if (spectra.size() != 1) {
                logger.debug("Found " + spectra.size() + " spectra for PSM " +
                        psm.getPSM_ID() + " only the first one will be use for generating the spectrum id");
            }
            spectrum = spectra.get(0);

            if (spectrum != null) {

                msRunFileName = extractFileName(spectrum.getMsRun().getLocation().getFile());
                identifierInMsRunFile = spectrum.getReference();

                if (msRunFileName != null && !msRunFileName.isEmpty() && identifierInMsRunFile != null && !identifierInMsRunFile.isEmpty()) {
                    SpectrumIDGenerator spectrumIDGenerator = new SpectrumIdGeneratorPride3();
                    spectrumId = spectrumIDGenerator.generate(projectAccession, msRunFileName, identifierInMsRunFile);
                } else {
                    logger.warn("The spectrum id for PSM " + psm.getPSM_ID() +
                            " can not be generated because the file location is not valid");
                }
            } else {
                logger.warn("The spectrum id for PSM " + psm.getPSM_ID() +
                        " can not be generated because the spectrum is null");
            }
        } else {
            logger.warn("The spectrum id for PSM " + psm.getPSM_ID() +
                    " can not be generated because the spectraRef is null or empty");
        }

        return spectrumId;
    }

    private static String extractFileName(String filePath) {
        return FilenameUtils.getName(filePath);
    }

}
