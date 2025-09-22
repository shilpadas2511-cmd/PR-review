package com.ibm.commerce.partner.core.order.services;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.List;

/*
* PartnerCommerceOrderService interface to get the orders
* */
public interface PartnerCommerceOrderService {

    /*
    * get the order list from dao call
    * @parm quote
    *
    * @return list of orders
    * */
    List<OrderModel> findOrdersByQuote(QuoteModel quote);
}
