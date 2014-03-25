package uk.ac.ebi.pride.psmindex.search.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.pride.prider.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.prider.repo.project.ProjectRepository;
import uk.ac.ebi.pride.psmindex.search.service.PsmIndexService;
import uk.ac.ebi.pride.psmindex.search.service.PsmSearchService;
import uk.ac.ebi.pride.psmindex.search.util.MzTabDataProviderReader;
import uk.ac.ebi.pride.psmindex.search.util.ProjectPsmsIndexer;

import java.io.File;


/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 */
@Component
public class PsmIndexBuilder {

    private static Logger logger = LoggerFactory.getLogger(PsmIndexBuilder.class.getName());

    @Autowired
    private File submissionsDirectory;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PsmSearchService psmSearchService;

    @Autowired
    private PsmIndexService psmIndexService;


    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/app-context.xml");
        PsmIndexBuilder psmIndexBuilder = context.getBean(PsmIndexBuilder.class);

        indexPsms(psmIndexBuilder);

    }

    public static void indexPsms(PsmIndexBuilder psmIndexBuilder) {

        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = psmIndexBuilder.projectRepository.findAll();

        // reset index
        psmIndexBuilder.psmIndexService.deleteAll();
        logger.info("All psms are now DELETED");

        // create the indexer
        ProjectPsmsIndexer projectPsmsIndexer = new ProjectPsmsIndexer(psmIndexBuilder.psmSearchService, psmIndexBuilder.psmIndexService);

        // iterate through project to index psms
        for (ProjectProvider project : projects) {
            if (project.isPublicProject()) { // we are going to index just public projects

                String generatedFolderPath = MzTabDataProviderReader.buildGeneratedDirectoryFilePath(
                        psmIndexBuilder.submissionsDirectory.getAbsolutePath(),
                        project
                );

                projectPsmsIndexer.indexAllPsms(
                        project.getAccession(), generatedFolderPath
                );
            }
        }
    }


}
