package com.ibm.commerce.partner.core.order.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.dao.PartnerCommerceOrderDao;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceOrderDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * DefaultPartnerCommerceOrderDao is used to get the orders from database
 */

public class DefaultPartnerCommerceOrderDao extends DefaultCommerceOrderDao implements
    PartnerCommerceOrderDao {


    private final FlexibleSearchService flexibleSearchService;

    public DefaultPartnerCommerceOrderDao(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    private static final String FIND_ORDERS_BY_QUOTE_QUERY =
        "SELECT {" + ItemModel.PK + "} FROM {" + OrderModel._TYPECODE
            + "} WHERE {" + OrderModel.QUOTEREFERENCE + "} = ?quoteReference AND {"
            + OrderModel.VERSIONID + "} IS NULL";

    /**
     * get the list of orders assocaited with quote
     *
     * @param quote
     * @return list of orders
     */
    @Override
    public List<OrderModel> findOrdersByQuote(QuoteModel quote) {
        validateParameterNotNullStandardMessage("QuoteModel", quote);
        OrderModel orderModel = null;
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("quoteReference", quote);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ORDERS_BY_QUOTE_QUERY, queryParams);
        final SearchResult<OrderModel> resultSearchList = getFlexibleSearchService().search(searchQuery);
        return resultSearchList.getResult();
    }


    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }


}
