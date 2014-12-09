package uk.ac.ebi.pride.psmindex.search.service;

import com.google.common.collect.Iterables;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.UncategorizedSolrException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.psmindex.search.model.Psm;
import uk.ac.ebi.pride.psmindex.search.service.repository.SolrPsmRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jose A. Dianes, Noemi del Toro
 * @version $Id$
 *          <p/>
 *          NOTE: protein accessions can contain chars that produce problems in solr queries ([,],:). They are replaced by _ when
 *          using the repository methods
 */
@Service
public class PsmIndexService {

    private static Logger logger = LoggerFactory.getLogger(PsmIndexService.class.getName());

    private static final int NUM_TRIES = 10;
    private static final int SECONDS_TO_WAIT = 30;
    private static final long MAX_ELAPSED_TIME_PING_QUERY = 10000;
    private static final int MAX_DOCS = 100000;

    private SolrServer solrPsmServer;

    private SolrPsmRepository solrPsmRepository;

    public PsmIndexService() {
    }

    public PsmIndexService(SolrServer solrPsmServer, SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
        this.solrPsmServer = solrPsmServer;
    }

    public void setSolrPsmRepository(SolrPsmRepository solrPsmRepository) {
        this.solrPsmRepository = solrPsmRepository;
    }

    public void setSolrPsmServer(SolrServer solrPsmServer) {
        this.solrPsmServer = solrPsmServer;
    }

    @Transactional
    public boolean save(Psm psm) {
        Collection<Psm> psmCollection = new LinkedList<Psm>();
        psmCollection.add(psm);
        return save(psmCollection);
    }

    @Transactional
    public boolean save(Iterable<Psm> psms) {
        if (psms != null && psms.iterator().hasNext()) {

            //Maybe the partitioner can be move to the projectPsmsIndexer, but for know we keep here for simplicity
            Iterable<List<Psm>> batches = Iterables.partition(psms, MAX_DOCS);

            int numTries;
            boolean succeed = false;
            for (List<Psm> batch : batches) {
                numTries = 0;
                succeed = false;

                while (numTries < NUM_TRIES && !succeed) {
                    try {
                        SolrPingResponse pingResponse = this.solrPsmServer.ping();
                        if ((pingResponse.getStatus() == 0) && pingResponse.getElapsedTime() < MAX_ELAPSED_TIME_PING_QUERY) {

                            // One min commit
                            this.solrPsmServer.addBeans(batch, 60000);
                            succeed = true;
                        } else {
                            logger.error("[TRY " + numTries + " Solr server too busy!");
                            logger.error("PING response status: " + pingResponse.getStatus());
                            logger.error("PING elapsed time: " + pingResponse.getElapsedTime());
                            logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                            waitSecs();
                        }
                    } catch (SolrServerException e) {
                        logger.error("[TRY " + numTries + "] There are server problems: " + e.getCause());
                        logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                        waitSecs();
                    } catch (UncategorizedSolrException e) {
                        logger.error("[TRY " + numTries + "] There are server problems: " + e.getCause());
                        logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                        waitSecs();
                    } catch (Exception e) {
                        logger.error("[TRY " + numTries + "] There are UNKNOWN problems: " + e.getCause());
                        e.printStackTrace();
                        logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                        waitSecs();
                    }
                    numTries++;
                }
            }

            return succeed;

        } else {
            logger.error("Psm Index Service [reliable-save]: Trying to save an empty psm list!");

            return false;
        }
    }

    @Transactional
    public void delete(Psm psm) {
        solrPsmRepository.delete(psm);
    }

    @Transactional
    public void delete(Iterable<Psm> psms) {
        if (psms == null || !psms.iterator().hasNext())
            logger.info("No PSMS to delete");
        else {
            solrPsmRepository.delete(psms);
        }
    }

    @Transactional
    public void deleteAll() {
        solrPsmRepository.deleteAll();
    }

    private void waitSecs() {
        try {
            Thread.sleep(SECONDS_TO_WAIT * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
