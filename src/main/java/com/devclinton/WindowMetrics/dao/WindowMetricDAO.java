package com.devclinton.WindowMetrics.dao;

import com.devclinton.WindowMetrics.models.WindowMetric;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.ws.rs.core.MultivaluedMap;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Our DAO for doing all our dirty data dealings
 * <p>
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
public class WindowMetricDAO extends AbstractDAO<WindowMetric> {
    private static final Map<String, String> fieldMap = ImmutableMap.of(
            "executable", "executable",
            "window_title", "window_title"
    );

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public WindowMetricDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Map.Entry<Long, List<WindowMetric>> findAll(MultivaluedMap queryParameters) throws Exception {
        Iterator it = queryParameters.keySet().iterator();
        Criteria criteria = criteria();
        Criteria countCrit = criteria();

        int pageNumber = 0;
        int pageSize = 10;

        //Or queries
        Disjunction orGrouping = (queryParameters.containsKey("orQuery") && Boolean.valueOf(queryParameters.getFirst("orQuery").toString())) ? Restrictions.disjunction() : null;

        //alias entries
        boolean aliasedEntries = false;

        while (it.hasNext()) {
            String key = (String) it.next();
            String keyLower = key.toLowerCase();
            String id = null;
            switch (keyLower) {
                case "page":
                    pageNumber = Integer.decode(queryParameters.getFirst(key).toString());
                    break;
                case "pagesize":
                    pageSize = Integer.decode(queryParameters.getFirst(key).toString());
                    break;
                default:
                    //is the field one we have mapped for searching?
                    if (fieldMap.containsKey(key)) {
                        if (orGrouping != null) {
                            orGrouping.add(Restrictions.ilike(fieldMap.get(key), queryParameters.getFirst(key).toString(), MatchMode.ANYWHERE));
                        } else {
                            criteria.add(Restrictions.ilike(fieldMap.get(key), queryParameters.getFirst(key).toString(), MatchMode.ANYWHERE));
                            countCrit.add(Restrictions.ilike(fieldMap.get(key), queryParameters.getFirst(key).toString(), MatchMode.ANYWHERE));
                        }
                    }
            }
        }

        if (orGrouping != null) {
            criteria.add(orGrouping);
            countCrit.add(orGrouping);
        }

        criteria.setFirstResult((pageNumber - 1) * pageSize);
        criteria.setMaxResults(pageSize);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        Long uniqueResult = (Long) countCrit.setProjection(Projections.rowCount()).uniqueResult();

        return new AbstractMap.SimpleEntry<>(uniqueResult, criteria.list());

    }
}
