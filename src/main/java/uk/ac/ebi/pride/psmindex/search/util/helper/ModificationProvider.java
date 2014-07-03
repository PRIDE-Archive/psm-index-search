package uk.ac.ebi.pride.psmindex.search.util.helper;

import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;

import java.util.Map;

/**
 * User: ntoro
 * Date: 02/07/2014
 * Time: 14:59
 */
public interface ModificationProvider {

    public String getAccession();

    public Integer getMainPosition();

    public CvParamProvider getNeutralLoss();

    public Map<Integer, CvParamProvider> getPositionMap();

}
