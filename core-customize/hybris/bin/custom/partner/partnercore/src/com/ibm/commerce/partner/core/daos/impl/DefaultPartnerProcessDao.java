package com.ibm.commerce.partner.core.daos.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.daos.PartnerProcessDao;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Doa have methods related to the business process
 */
public class DefaultPartnerProcessDao implements PartnerProcessDao {

    private final FlexibleSearchService flexibleSearchService;

    private static final String NOT_BLANK = " must not be blank";

    protected static final String GET_PROCESS_BY_CODE =
        "SELECT {d:pk} " +
            "FROM {BusinessProcess AS d} " +
            "WHERE {d:code} LIKE ?" + BusinessProcessModel.CODE;


    public DefaultPartnerProcessDao(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * Fetches the businessProcess created for that order code
     *
     * @return BusinessProcessList
     */
    @Override
    public List<BusinessProcessModel> getProcessListByCode(
        final String orderCode) {
        final Map<String, Object> params = new HashMap<>();
        params.put(BusinessProcessModel.CODE, orderCode);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_PROCESS_BY_CODE, params);
        final SearchResult<BusinessProcessModel> result = getFlexibleSearchService().search(
            query);
        if (null != result && CollectionUtils.isNotEmpty(result.getResult())) {
            return result.getResult();
        }
        return null;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

}
