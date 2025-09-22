package com.ibm.commerce.partner.core.pricing.services;


import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import java.util.List;

/**
 * Service Interface to fetch Pricing
 */
public interface PartnerPricingOutboundService {


    /**
     * Fetch Price for orderModel
     *
     * @param orderModel
     * @return
     */
    PriceLookUpResponseData getEntitledPrice(AbstractOrderModel orderModel);

    /**
     * Fetch Price for orderModel
     *
     * @param orderModel
     * @return
     */
    PriceLookUpResponseData getFullPrice(AbstractOrderModel orderModel);

    /**
     * Fetch Deal Reg Information for orderModel
     *
     * @param orderModel
     * @return
     */
    List<DealRegResponseData> getDealRegDetail(AbstractOrderModel orderModel);

    /**
     * Fetch CpqCharacteristicAssignedValues from AbstractOrderEntryModel
     *
     * @param AbstractOrderEntryModel
     * @param String type
     * @return
     */
    String getProductInfo(AbstractOrderEntryModel source, String type);
}
