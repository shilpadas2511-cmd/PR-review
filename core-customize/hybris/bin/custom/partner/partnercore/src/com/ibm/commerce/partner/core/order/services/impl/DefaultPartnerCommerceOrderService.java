package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.order.dao.PartnerCommerceOrderDao;
import com.ibm.commerce.partner.core.order.services.PartnerCommerceOrderService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.List;

/*
 * DefaultPartnerCommerceOrderService is used to get the orders from back end.
 */

public class DefaultPartnerCommerceOrderService extends DefaultCommerceOrderService implements
    PartnerCommerceOrderService {


    private PartnerCommerceOrderDao commerceOrderDao;

    private SearchRestrictionService searchRestrictionService;


    public DefaultPartnerCommerceOrderService(PartnerCommerceOrderDao commerceOrderDao,
        SearchRestrictionService searchRestrictionService) {
        this.commerceOrderDao = commerceOrderDao;
        this.searchRestrictionService = searchRestrictionService;
    }


    /**
     * get the list of orders assocaited with quote
     *
     * @param quote
     * @return list of orders
     */
    @Override
    public List<OrderModel> findOrdersByQuote(QuoteModel quote) {

        getSearchRestrictionService().disableSearchRestrictions();
        List<OrderModel> orders=getCommerceOrderDao().findOrdersByQuote(quote);
        getSearchRestrictionService().enableSearchRestrictions();
        return orders;
    }

    public PartnerCommerceOrderDao getCommerceOrderDao() {
        return commerceOrderDao;
    }

    public SearchRestrictionService getSearchRestrictionService() {
        return searchRestrictionService;
    }

}
