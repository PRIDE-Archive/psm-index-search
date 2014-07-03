package uk.ac.ebi.pride.psmindex.search.model;

import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;

/**
 * User: ntoro
 * Date: 01/07/2014
 * Time: 16:45
 */
public class CvParam implements CvParamProvider {

    private String cvLabel;
    private String accession;
    private String name;
    private String value;


    public CvParam(String cvLabel, String accession, String name, String value) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
    }

    public CvParam() {

    }

    @Override
    public String getCvLabel() {
        return cvLabel;
    }

    public void setCvLabel(String cvLabel) {
        this.cvLabel = cvLabel;
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Long getId() {
        return null;
    }
}
