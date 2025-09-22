package com.ibm.commerce.partner.core.provisionform.Dao.impl;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.provisionform.Dao.PartnerProvisionFormDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

/**
 * This class is used to  fetch all the model related provision form
 */
public class DefaultPartnerProvisionFormDao implements PartnerProvisionFormDao {

    private final FlexibleSearchService flexibleSearchService;
    private static final String FETCH_PARTNER_PRODUCTSET_MODEL_BY_CODE =
        "SELECT {" + ItemModel.PK + "} FROM {"
            + PartnerProductSetModel._TYPECODE + " } WHERE {" + PartnerProductSetModel.CODE
            + "}= ?code";

    public DefaultPartnerProvisionFormDao(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * This method is used to fetch PartnerProductSetModel by searching product code
     *
     * @param code The request data contains info needed to be sent for the productCode
     * @return PartnerProductSetModel
     */
    public PartnerProductSetModel getProductSet(final String code) {
        Assert.notNull(code, "code code must not be null");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
            FETCH_PARTNER_PRODUCTSET_MODEL_BY_CODE);
        query.addQueryParameter("code", code);
        final SearchResult<PartnerProductSetModel> result = getFlexibleSearchService().search(
            query);
        if (null != result && CollectionUtils.isNotEmpty(result.getResult())) {
            return result.getResult().get(0);
        }
        return null;

    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

}
