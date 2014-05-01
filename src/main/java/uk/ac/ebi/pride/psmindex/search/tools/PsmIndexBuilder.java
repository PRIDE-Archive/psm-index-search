package uk.ac.ebi.pride.psmindex.search.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.prider.dataprovider.file.ProjectFileProvider;
import uk.ac.ebi.pride.prider.dataprovider.file.ProjectFileSource;
import uk.ac.ebi.pride.prider.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.prider.repo.assay.AssayRepository;
import uk.ac.ebi.pride.prider.repo.file.ProjectFileRepository;
import uk.ac.ebi.pride.prider.repo.project.ProjectRepository;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;
import uk.ac.ebi.pride.psmindex.search.util.ErrorLogOutputStream;
import uk.ac.ebi.pride.psmindex.search.indexer.ProjectPsmsIndexer;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
@Component
public class PsmIndexBuilder {

    private static Logger logger = LoggerFactory.getLogger(PsmIndexBuilder.class.getName());
    private static ErrorLogOutputStream errorLogOutputStream = new ErrorLogOutputStream(logger);

    private static final String PRIDE_MZ_TAB_FILE_EXTENSION = ".pride.mztab";

    private static final String COMPRESS_EXTENSION = "gz";

    private static final String INTERNAL_FOLDER_NAME = ProjectFileSource.INTERNAL.getFolderName();
    private static final String GENERATED_FOLDER_NAME = ProjectFileSource.GENERATED.getFolderName();

    @Autowired
    private File submissionsDirectory;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AssayRepository assayRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Autowired
    private PsmSearchService psmSearchService;

    @Autowired
    private PsmIndexService psmIndexService;


    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/app-context.xml");
        PsmIndexBuilder psmIndexBuilder = context.getBean(PsmIndexBuilder.class);

        indexPsmsOld(psmIndexBuilder);

    }

    @Deprecated
    public static void indexPsmsOld(PsmIndexBuilder psmIndexBuilder) {

        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = psmIndexBuilder.projectRepository.findAll();

        // reset index
        psmIndexBuilder.psmIndexService.deleteAll();
        logger.info("All PSMs are now DELETED");

        // create the indexer
        ProjectPsmsIndexer projectPsmsIndexer = new ProjectPsmsIndexer(psmIndexBuilder.psmSearchService, psmIndexBuilder.psmIndexService);

        // iterate through project to index psms
        for (ProjectProvider project : projects) {
            String generatedFolderPath = buildGeneratedDirectoryFilePath(
                    psmIndexBuilder.submissionsDirectory.getAbsolutePath(),
                    project
            );

            projectPsmsIndexer.indexAllPsms(
                    project.getAccession(), generatedFolderPath
            );
        }
    }

    public static void indexPsms(PsmIndexBuilder psmIndexBuilder) {

        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = psmIndexBuilder.projectRepository.findAll();

        // reset index
        psmIndexBuilder.psmIndexService.deleteAll();
        logger.info("All PSMs are now DELETED");

        // create the indexer
        ProjectPsmsIndexer projectPsmsIndexer = new ProjectPsmsIndexer(psmIndexBuilder.psmSearchService, psmIndexBuilder.psmIndexService);

        // iterate through project to index psms
        for (ProjectProvider project : projects) {

            Iterable<? extends ProjectFileProvider> projectFiles = psmIndexBuilder.projectFileRepository.findAllByProjectId(project.getId());
            for (ProjectFileProvider projectFile : projectFiles) {
                //TODO: This will change when we have the internal file names in the database
                //To avoid using submitted mztab we need to filter by generated ones first
                if (ProjectFileSource.GENERATED.equals(projectFile.getFileSource())) {

                    String fileName = projectFile.getFileName();

                    if (fileName != null && fileName.contains(PRIDE_MZ_TAB_FILE_EXTENSION)) {

                        //We should have only one assay per mzTab file
                        String assayAccession = psmIndexBuilder.assayRepository.findOne(projectFile.getAssayId()).getAccession();

                        String pathToMzTabFile = buildAbsoluteMzTabFilePath(
                                psmIndexBuilder.submissionsDirectory.getAbsolutePath(),
                                project,
                                fileName
                        );

                        MZTabFileParser mzTabFileParser = null;
                        try {
                            mzTabFileParser = new MZTabFileParser(new File(pathToMzTabFile), errorLogOutputStream);
                            MZTabFile mzTabFile = mzTabFileParser.getMZTabFile();
                            projectPsmsIndexer.indexAllPsmsForProjectAndAssay(project.getAccession(), assayAccession, mzTabFile);
                        } catch (IOException e) {
                            logger.error("Could not open MzTab file: " + pathToMzTabFile);
                        }

                    }
                }
            }
        }
    }


    @Deprecated
    public static String buildGeneratedDirectoryFilePath(String prefix, ProjectProvider project) {
        if (project.isPublicProject()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getPublicationDate());
            int month = calendar.get(Calendar.MONTH) + 1;

            return prefix
                    + File.separator + calendar.get(Calendar.YEAR)
                    + File.separator + (month < 10 ? "0" : "") + month
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME;
        } else {
            return prefix
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME;
        }

    }

    //TODO: Move it to a pride-archive-utils
    public static String buildAbsoluteMzTabFilePath(String prefix, ProjectProvider project, String fileName) {
        if (project.isPublicProject()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getPublicationDate());
            int month = calendar.get(Calendar.MONTH) + 1;

            return prefix
                    + File.separator + calendar.get(Calendar.YEAR)
                    + File.separator + (month < 10 ? "0" : "") + month
                    + File.separator + project.getAccession()
                    + File.separator + INTERNAL_FOLDER_NAME
                    + File.separator + translateFromGeneratedToInternalFolderFileName(fileName);
        } else {
            return prefix
                    + File.separator + project.getAccession()
                    + File.separator + INTERNAL_FOLDER_NAME
                    + File.separator + translateFromGeneratedToInternalFolderFileName(fileName);
        }

    }

    //TODO: Move it to a pride-archive-utils
    /**
     * In the generated folder(the which one we are taking the file names) the files are gzip, so we need to remove
     * the extension to have the name in the internal folder (the one that we want)
     *
     * @param fileName mztab file name in generated folder
     * @return mztab file name in internal folder
     */
    private static String translateFromGeneratedToInternalFolderFileName(String fileName) {

        if (fileName != null) {
            if (fileName.endsWith(COMPRESS_EXTENSION)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
        }
        return fileName;
    }
}
