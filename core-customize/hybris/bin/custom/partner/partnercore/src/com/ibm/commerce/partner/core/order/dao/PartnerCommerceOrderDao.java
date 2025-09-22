package com.ibm.commerce.partner.core.order.dao;

import de.hybris.platform.commerceservices.order.dao.CommerceOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import java.util.List;
import java.util.Map;


/*
*  PartnerCommerceOrderDao interface use to get the all orders.
* */
public interface PartnerCommerceOrderDao extends CommerceOrderDao {

    /*
     * get the order list from db call
     * @parm quote
     *
     * @return list of orders
     * */
    List<OrderModel> findOrdersByQuote(QuoteModel quote);

    @Override
    OrderModel findOrderByQuote(QuoteModel quote);

    @Override
    List<OrderModel> find();

    @Override
    List<OrderModel> find(Map<String, ?> map);

    @Override
    List<OrderModel> find(SortParameters sortParameters);

    @Override
    List<OrderModel> find(Map<String, ?> map, SortParameters sortParameters);

    @Override
    List<OrderModel> find(Map<String, ?> map, SortParameters sortParameters, int i);

}
