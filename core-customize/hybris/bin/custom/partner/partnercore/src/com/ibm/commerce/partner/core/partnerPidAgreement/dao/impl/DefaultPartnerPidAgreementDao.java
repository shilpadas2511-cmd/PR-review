package com.ibm.commerce.partner.core.partnerPidAgreement.dao.impl;

import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;
import com.ibm.commerce.partner.core.partnerPidAgreement.dao.PartnerPidAgreementDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * Default implementation of {@link PartnerPidAgreementDao} that extends {@link DefaultGenericDao}
 * to perform database operations for {@link PartnerPIDAgreementModel}.
 * <p>
 * This DAO provides the logic to retrieve Partner PID Agreement records based on specific
 * attributes, such as the partner PID.
 * </p>
 */
public class DefaultPartnerPidAgreementDao extends
    DefaultGenericDao<PartnerPIDAgreementModel> implements PartnerPidAgreementDao {

    private static final String SELECT_PQ = "SELECT {pq:";
    private static final String FROM = "} FROM {";
    private static final String GET_ALL_PARTNER_PID_AGREEMENT_BY_PID =
        SELECT_PQ + ItemModel.PK + FROM + PartnerPIDAgreementModel._TYPECODE
            + " as pq } where{pq.pid}=?" + PartnerPIDAgreementModel.PID;


    public DefaultPartnerPidAgreementDao() {
        super(PartnerPIDAgreementModel._TYPECODE);
    }

    /**
     * Retrieves a {@link PartnerPIDAgreementModel} by the provided Partner PID using a
     * FlexibleSearch query.
     *
     * @param pid the Partner PID to search for; must not be {@code null}.
     * @return the matching {@link PartnerPIDAgreementModel}, or {@code null} if no match is found.
     * @throws IllegalArgumentException if the provided PID is {@code null}.
     */
    @Override
    public PartnerPIDAgreementModel getPIDAgreementByPid(String pid) {
        FlexibleSearchQuery query;
        final Map<String, Object> params = new HashMap<>();
        query = new FlexibleSearchQuery(GET_ALL_PARTNER_PID_AGREEMENT_BY_PID);
        params.put(PartnerPIDAgreementModel.PID, pid);
        query.addQueryParameters(params);
        final SearchResult<PartnerPIDAgreementModel> result = getFlexibleSearchService().search(
            query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }
}
