package uk.ac.ebi.pride.psmindex.search.util.helper;

import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;
import uk.ac.ebi.pride.jmztab.model.CVParam;
import uk.ac.ebi.pride.jmztab.model.MZTabUtils;
import uk.ac.ebi.pride.jmztab.model.Param;
import uk.ac.ebi.pride.psmindex.search.model.CvParam;

/**
 * User: ntoro
 * Date: 01/07/2014
 * Time: 18:10
 */

/**
 * Taken from the mzTab library to avoid the dependency in the psm-search library.
 */
public class CvParamHelper {


    public static CvParamProvider convertFromString(String param) {

        if (param == null)
            return null;

        Param mzTabParam = MZTabUtils.parseParam(param);
        return convertToCvParamProvider(mzTabParam);
    }

    public static String convertToString(CvParamProvider param) {

        if (param == null)
            return null;

        Param mzTabParam = convertFromCvParamProvider(param);
        return mzTabParam.toString();
    }

    public static String convertToString(String cvLabel, String accession, String name, String value) {
        Param mzTabParam = new CVParam(cvLabel, accession, name, value);
        return mzTabParam.toString();
    }

    public static CvParamProvider convertToCvParamProvider(Param mzTabParam) {
        if (mzTabParam == null)
            return null;

        return new CvParam(mzTabParam.getCvLabel(), mzTabParam.getAccession(), mzTabParam.getName(), mzTabParam.getValue());
    }

    public static CvParamProvider convertToCvParamProvider(String cvLabel, String accession, String name, String value) {
        return new CvParam(cvLabel, accession, name, value);
    }

    protected static Param convertFromCvParamProvider(CvParamProvider param) {

        if (param == null)
            return null;

        return new CVParam(param.getCvLabel(), param.getAccession(), param.getName(), param.getValue());
    }

}
