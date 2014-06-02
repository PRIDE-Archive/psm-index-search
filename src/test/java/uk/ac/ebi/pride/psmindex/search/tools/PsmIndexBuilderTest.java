package uk.ac.ebi.pride.psmindex.search.tools;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.pride.archive.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.archive.repo.project.Project;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PsmIndexBuilderTest {

    @Test
    public void testBuildAbsoluteMzTabFilePathPublic() throws Exception {

        String prefix = "/nfs/pride/prod/archive";
        String gzpipMzTabFileName = "my_mzTab_file_compressed.pride.mztab.gz";
        Calendar calendar = new GregorianCalendar(2013, 1, 28, 13, 24, 56);

        ProjectProvider projectProvider = mock(Project.class);
        when(projectProvider.getAccession()).thenReturn("TST000121");
        when(projectProvider.getPublicationDate()).thenReturn((calendar.getTime()));
        when(projectProvider.isPublicProject()).thenReturn(true);

        String mztabFilePath =
                PsmIndexBuilder.buildAbsoluteMzTabFilePath(prefix, projectProvider, gzpipMzTabFileName);

        Assert.assertEquals("/nfs/pride/prod/archive/2013/02/TST000121/internal/my_mzTab_file_compressed.pride.mztab", mztabFilePath);

    }

    @Test
    public void testBuildAbsoluteMzTabFilePathPrivate() throws Exception {

        String prefix = "/nfs/pride/prod/archive";
        String gzpipMzTabFileName = "my_mzTab_file_compressed.pride.mztab.gz";
        Calendar calendar = new GregorianCalendar(2013, 1, 28, 13, 24, 56);

        ProjectProvider projectProvider = mock(Project.class);
        when(projectProvider.getAccession()).thenReturn("TST000121");
        when(projectProvider.getPublicationDate()).thenReturn((calendar.getTime()));
        when(projectProvider.isPublicProject()).thenReturn(false);

        String mztabFilePath =
                PsmIndexBuilder.buildAbsoluteMzTabFilePath(prefix, projectProvider, gzpipMzTabFileName);

        Assert.assertEquals("/nfs/pride/prod/archive/TST000121/internal/my_mzTab_file_compressed.pride.mztab", mztabFilePath);

    }
}
