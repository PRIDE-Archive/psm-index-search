package uk.ac.ebi.pride.psmindex.search.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
public class Psm {

    @Field(PsmFields.ID)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
