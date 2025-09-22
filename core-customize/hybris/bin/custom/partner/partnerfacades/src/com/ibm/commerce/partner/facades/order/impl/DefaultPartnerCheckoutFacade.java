/**
 *
 */
package com.ibm.commerce.partner.facades.order.impl;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partner.core.order.services.PartnerCheckoutService;
import com.ibm.commerce.partner.facades.order.PartnerCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link PartnerCheckoutFacade}
 */
public class DefaultPartnerCheckoutFacade extends DefaultCheckoutFacade implements
    PartnerCheckoutFacade {

    private final Converter<IbmAddToCartParamsData, CommerceCheckoutParameter> partnerCommerceCheckoutParameterConverter;
    private final PartnerOpportunityService opportunityService;

    public DefaultPartnerCheckoutFacade(
        Converter<IbmAddToCartParamsData, CommerceCheckoutParameter> partnerCommerceCheckoutParameterConverter,
        final PartnerOpportunityService opportunityService) {
        this.partnerCommerceCheckoutParameterConverter = partnerCommerceCheckoutParameterConverter;
        this.opportunityService = opportunityService;
    }


    @Override
    public void updateIbmCartDetails(final IbmAddToCartParamsData ibmAddToCartParamsData) {
        if (!getCartService().hasSessionCart()) {
            return;
        }
        final IbmPartnerCartModel ibmCartModel = (IbmPartnerCartModel) getCart();
        final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(ibmCartModel,
            true);
        getCartParamsToCommerceCheckoutParamConverter().convert(ibmAddToCartParamsData, parameter);

        getPartnerCheckoutService().updateCart(parameter);
    }

    @Override
    public CartData getCheckoutCart() {
        return getCartFacade().getSessionCart();
    }

    /**
     * @return the partnerCheckoutService
     */
    public PartnerCheckoutService getPartnerCheckoutService() {
        return (PartnerCheckoutService) getCommerceCheckoutService();
    }

    public Converter<IbmAddToCartParamsData, CommerceCheckoutParameter> getCartParamsToCommerceCheckoutParamConverter() {
        return partnerCommerceCheckoutParameterConverter;
    }

    public PartnerOpportunityService getOpportunityService() {
        return opportunityService;
    }

}
