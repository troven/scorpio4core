package com.scorpio4.vendor.sesame.crud;

import com.scorpio4.oops.FactException;
import com.scorpio4.vendor.sesame.util.QueryTools;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * cuebic (c) 2013
 * Module: com.cuebic.sparql
 * User  : lee
 * Date  : 12/12/2013
 * Time  : 2:20 AM
 */
public class SesameRead {
    private static final Logger log = LoggerFactory.getLogger(SesameRead.class);
    private final SesameCRUD sesameCRUD;
    String sparql = null;

    public SesameRead(SesameCRUD sesameCRUD, String sparql) {
        this.sesameCRUD=sesameCRUD;
        this.sparql = sparql;
    }

    public Collection<Map> execute() throws FactException {
        try {
            return QueryTools.toCollection(sesameCRUD.getConnection(), sparql);
        } catch (IOException e) {
            throw new FactException(e.getMessage(),e);
        } catch (RepositoryException e) {
            throw new FactException(e.getMessage(),e);
        } catch (MalformedQueryException e) {
            throw new FactException(e.getMessage(),e);
        } catch (QueryEvaluationException e) {
            throw new FactException(e.getMessage(),e);
        }
    }


}
