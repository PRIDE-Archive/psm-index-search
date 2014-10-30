package uk.ac.ebi.pride.psmindex.search.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;
import uk.ac.ebi.pride.archive.utils.spectrum.SpectrumIDGenerator;
import uk.ac.ebi.pride.archive.utils.spectrum.SpectrumIdGeneratorPride3;
import uk.ac.ebi.pride.jmztab.model.*;
import uk.ac.ebi.pride.jmztab.utils.errors.MZTabException;
import uk.ac.ebi.pride.psmindex.search.model.Psm;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static uk.ac.ebi.pride.indexutils.helpers.CvParamHelper.convertToCvParamProvider;
import static uk.ac.ebi.pride.indexutils.helpers.ModificationHelper.convertToModificationProvider;
import static uk.ac.ebi.pride.psmindex.search.util.PsmIdBuilder.getId;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
public class PsmMzTabBuilder {

    private static Logger logger = LoggerFactory.getLogger(PsmMzTabBuilder.class.getName());

    /**
     * The map between the assay accession and the file need to be provided externally from the database
     *
     * @return A map of assay accessions to PSMs
     * @throws java.io.IOException
     */
    public static List<Psm> readPsmsFromMzTabFile(String projectAccession, String assayAccession, MZTabFile mzTabFile) throws IOException, MZTabException {

        List<Psm> res = new LinkedList<Psm>();

        if (mzTabFile != null) {

            // get all psms from the file
            res = convertFromMzTabPsmsToPrideArchivePsms(mzTabFile.getPSMs(), mzTabFile.getMetadata(), projectAccession, assayAccession);
            logger.debug("Found " + res.size() + " psms for Assay " + assayAccession + " in file " + mzTabFile);

        }

        return res;
    }

    private static LinkedList<Psm> convertFromMzTabPsmsToPrideArchivePsms(Collection<PSM> mzTabPsms, Metadata metadata, String projectAccession, String assayAccession) {

        LinkedList<Psm> res = new LinkedList<Psm>();

        for (PSM mzTabPsm : mzTabPsms) {
            String cleanPepSequence = PsmSequenceCleaner.cleanPeptideSequence(mzTabPsm.getSequence());

            Psm newPsm = new Psm();
            newPsm.setId(getId(projectAccession, assayAccession, mzTabPsm.getPSM_ID(), mzTabPsm.getAccession(), cleanPepSequence));
            newPsm.setReportedId(mzTabPsm.getPSM_ID());
            newPsm.setSpectrumId(createSpectrumId(mzTabPsm, projectAccession));
            newPsm.setPeptideSequence(cleanPepSequence);
            newPsm.setProjectAccession(projectAccession);
            newPsm.setAssayAccession(assayAccession);
            // To be compatible with the project index we don't clean the protein accession
            // String correctedAccession = getCorrectedAccession(mzTabPsm.getAccession(), mzTabPsm.getDatabase());
            // newPsm.setProteinAccession(correctedAccession);
            newPsm.setProteinAccession(mzTabPsm.getAccession());
            newPsm.setDatabase(mzTabPsm.getDatabase());
            newPsm.setDatabaseVersion(mzTabPsm.getDatabaseVersion());

            newPsm.setModifications(new LinkedList<ModificationProvider>());
            //Modifications
            if (mzTabPsm.getModifications() != null) {
                //Using the writer for the library
                for (Modification mod : mzTabPsm.getModifications()) {
                    newPsm.addModification(convertToModificationProvider(mod));
                }
            }

            //Unique
            MZBoolean unique = mzTabPsm.getUnique();
            if (unique != null) {
                newPsm.setUnique(MZBoolean.True.equals(unique));
            } else {
                newPsm.setUnique(null);
            }

            //Search Engine
            //For now we keep the same representation as it is in the mzTab line
            newPsm.setSearchEngines(new LinkedList<CvParamProvider>());
            //If the mzTab search engine can not be converted SplitList can be null
            if (mzTabPsm.getSearchEngine() != null && !mzTabPsm.getSearchEngine().isEmpty()) {
                for (Param searchEngine : mzTabPsm.getSearchEngine()) {
                    newPsm.addSearchEngine(convertToCvParamProvider(searchEngine));
                }
            }

            //Search Engine Score
            newPsm.setSearchEngineScores(new LinkedList<CvParamProvider>());
            //If the mzTab search engine can not be converted SplitList can be null

            if (metadata.getPsmSearchEngineScoreMap() != null && !metadata.getPsmSearchEngineScoreMap().isEmpty()) {
                for (PSMSearchEngineScore psmSearchEngineScore : metadata.getPsmSearchEngineScoreMap().values()) {
                    if (mzTabPsm.getSearchEngineScore(psmSearchEngineScore.getId()) != null) {
                        // We create a Param as the composition between the searchEngineScore stored in the metadata and
                        // the search engine score value stored in the psm
                        Param param = psmSearchEngineScore.getParam();
                        String value = mzTabPsm.getSearchEngineScore(psmSearchEngineScore.getId()).toString();
                        newPsm.addSearchEngineScore(convertToCvParamProvider(param.getCvLabel(), param.getAccession(), param.getName(), value));

                    }
                }
            }

            // Retention Time
            // NOTE: If the psm was discovered as a combination of several spectra, we will
            // simplify the case choosing only the first spectrum and the first retention time
            SplitList<Double> retentionTimes = mzTabPsm.getRetentionTime();
            if (retentionTimes != null && !retentionTimes.isEmpty()) {
                newPsm.setRetentionTime(retentionTimes.get(0));
            }

            newPsm.setCharge(mzTabPsm.getCharge());
            newPsm.setExpMassToCharge(mzTabPsm.getExpMassToCharge());
            newPsm.setCalculatedMassToCharge(mzTabPsm.getCalcMassToCharge());
            newPsm.setPreAminoAcid(mzTabPsm.getPre());
            newPsm.setPostAminoAcid(mzTabPsm.getPost());

            if (mzTabPsm.getStart() != null) {
                newPsm.setStartPosition(mzTabPsm.getStart());
            }

            if (mzTabPsm.getEnd() != null) {
                newPsm.setEndPosition(mzTabPsm.getEnd());
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
