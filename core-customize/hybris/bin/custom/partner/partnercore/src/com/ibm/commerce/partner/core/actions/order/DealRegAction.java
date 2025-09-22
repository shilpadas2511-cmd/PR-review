package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to consume CPQ Entitled Price API. This action retrieves price data from a CPQ Entitled
 * Price API and updates the order accordingly.
 */
public class DealRegAction extends PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {


    private static final Logger LOG = LoggerFactory.getLogger(DealRegAction.class);

    private final PartnerPricingOutboundService pricingOutboundService;


    private final Converter<List<DealRegResponseData>, AbstractOrderModel> responseReverseDataConverter;

    public DealRegAction(final Integer maxRetryAllowed, final Integer retryDelay,
        final PartnerPricingOutboundService pricingOutboundService,
        final Converter<List<DealRegResponseData>, AbstractOrderModel> responseReverseDataConverter) {
        super(maxRetryAllowed, retryDelay);
        this.pricingOutboundService = pricingOutboundService;
        this.responseReverseDataConverter = responseReverseDataConverter;
    }


    /**
     * Executes the action to consume CPQ Entitled Price API.
     *
     * @param priceLookUpProcessModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(PriceLookUpProcessModel processModel) throws IOException {

        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), processModel.getCode());
        LOG.debug(msg);

        try {
            List<DealRegResponseData> responseData = getPricingOutboundService().getDealRegDetail(
                processModel.getOrder());
            if (responseData != null) {
                AbstractOrderModel cart = getResponseReverseDataConverter().convert(responseData,
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

    public Converter<List<DealRegResponseData>, AbstractOrderModel> getResponseReverseDataConverter() {
        return responseReverseDataConverter;
    }
}
