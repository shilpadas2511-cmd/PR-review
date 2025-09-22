package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.model.CpqIbmPartnerOrderModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * create new order for quote
 */
public class PartnerOrderCreationPostPersistHook implements PostPersistHook {

    private static final Logger LOG = Logger.getLogger(PartnerOrderCreationPostPersistHook.class);
    private final ModelService modelService;
    private final QuoteService quoteService;
    private final CommonI18NService commonI18NService;
    private final CustomerAccountDao CustomerAccountDao;

    public PartnerOrderCreationPostPersistHook(final ModelService modelService,
        QuoteService quoteService, CommonI18NService commonI18NService,
        de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao customerAccountDao) {
        this.modelService = modelService;
        this.quoteService = quoteService;
        this.commonI18NService = commonI18NService;
        CustomerAccountDao = customerAccountDao;
    }

    /**
     * create new order for each request for quote
     * @param item item model
     */

    public void execute(ItemModel item, PersistenceContext context) {
        if (item instanceof final CpqIbmPartnerOrderModel orderModel) {
            createOrder(orderModel);
        }
    }

    /**
     * create new order
     * @param  orderModel
     */

    protected void createOrder(CpqIbmPartnerOrderModel orderModel) {
        if (isOrderModelValid(orderModel)) {
            IbmPartnerQuoteModel quoteModel = (IbmPartnerQuoteModel) getQuoteService().getCurrentQuoteForCode(orderModel.getCpqQuoteNumber());
            if (isNewOrder(orderModel.getOrderId(), quoteModel)) {
                createNewOrder(orderModel, quoteModel);
            }
        }
    }

    protected boolean isOrderModelValid(CpqIbmPartnerOrderModel orderModel) {
        return StringUtils.isNotEmpty(orderModel.getOrderId()) &&
            StringUtils.isNotEmpty(orderModel.getCpqQuoteId()) &&
            StringUtils.isNotEmpty(orderModel.getCpqQuoteNumber());
    }



    protected boolean isNewOrder(String orderId, IbmPartnerQuoteModel quoteModel) {

        try {
            OrderModel orderModel = getCustomerAccountDao().findOrderByCodeAndStore(orderId,
                quoteModel.getStore());
            if (orderModel != null) {
                return false;
            }
        } catch (ModelNotFoundException e) {
            LOG.warn("Order not found, new order will create");
        }
        return true;
    }

    protected void createNewOrder(CpqIbmPartnerOrderModel orderModel, IbmPartnerQuoteModel quoteModel) {
        OrderModel order = getModelService().create(OrderModel.class);
        order.setUser(quoteModel.getUser());
        order.setCreationtime(new Date());
        order.setDate(new Date());
        order.setCurrency(getCommonI18NService().getBaseCurrency());
        order.setCode(orderModel.getOrderId());
        order.setStore(quoteModel.getStore());
        order.setQuoteReference(quoteModel);
        getModelService().save(order);
        getModelService().refresh(order);
    }


    public ModelService getModelService() {
        return modelService;
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public CustomerAccountDao getCustomerAccountDao() {
        return CustomerAccountDao;
    }


}
