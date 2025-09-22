package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.io.IOException;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to consume CPQ Entitled Price API. This action retrieves price data from a CPQ Entitled
 * Price API and updates the order accordingly.
 */
public class EntitledPriceAction extends
    PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {


    private static final Logger LOG = LoggerFactory.getLogger(EntitledPriceAction.class);

    private final PartnerPricingOutboundService pricingOutboundService;


    private final Converter<PriceLookUpResponseData, AbstractOrderModel> responseReverseDataConverter;

    public EntitledPriceAction(final Integer maxRetryAllowed, final Integer retryDelay,
        final PartnerPricingOutboundService pricingOutboundService,
        final Converter<PriceLookUpResponseData, AbstractOrderModel> responseReverseDataConverter) {
        super(maxRetryAllowed, retryDelay);
        this.pricingOutboundService = pricingOutboundService;
        this.responseReverseDataConverter = responseReverseDataConverter;
    }


    /**
     * Executes the action to consume CPQ Entitled Price API.
     *
     * @param processModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(PriceLookUpProcessModel processModel) throws IOException {

        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), processModel.getCode());
        LOG.debug(msg);

        try {
            PriceLookUpResponseData entitledPrice = getPricingOutboundService().getEntitledPrice(
                processModel.getOrder());
            if (entitledPrice != null) {
                entitledPrice.setType(CpqPricingTypeEnum.ENTITLED);
                AbstractOrderModel cart = getResponseReverseDataConverter().convert(entitledPrice,
                    processModel.getOrder());
                getModelService().saveAll(cart);
                return Transition.OK;
            } else {
                throw new IbmWebServiceFailureException("NO RESPONSE FOUND");
            }
        } catch (final IbmWebServiceFailureException ex) {
            return retryOrFailAction(processModel, msg);
        }
    }

    public PartnerPricingOutboundService getPricingOutboundService() {
        return pricingOutboundService;
    }

    public Converter<PriceLookUpResponseData, AbstractOrderModel> getResponseReverseDataConverter() {
        return responseReverseDataConverter;
    }
}
