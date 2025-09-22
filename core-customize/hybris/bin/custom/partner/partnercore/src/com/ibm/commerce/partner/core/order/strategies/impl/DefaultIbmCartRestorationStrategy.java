package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.user.UserModel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overriding the OOTB class {DefaultIbmCartRestorationStrategy}
 */
public class DefaultIbmCartRestorationStrategy extends DefaultCommerceCartRestorationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultIbmCartRestorationStrategy.class);


    @Resource(name = "partnerB2BUnitService")
    protected PartnerB2BUnitService<B2BUnitModel, UserModel> b2BUnitService;

    @Override
    public CommerceCartRestoration restoreCart(final CommerceCartParameter parameter)
        throws CommerceCartRestorationException {
        if (parameter.getCart() instanceof IbmPartnerCartModel cartModel
            && getBaseSiteService().getCurrentBaseSite().equals(cartModel.getSite())) {
            validateCart(cartModel);
        }
        return super.restoreCart(parameter);
    }

    protected void validateCart(IbmPartnerCartModel cartModel)
        throws CommerceCartRestorationException {

        if (!isUserAssociatedDistributor(cartModel.getBillToUnit()) && (
            isDisabled(cartModel.getSoldThroughUnit()) || isDisabled(cartModel.getBillToUnit()))) {
            getModelService().remove(cartModel);
            throw new CommerceCartRestorationException("B2b Unit is not Active");
        }
    }

    /**
     * This method checks whether the b2b unit is present in the user group
     *
     * @param b2BUnitModel - the b2b unit which need to be checked
     * @return boolean
     */
    protected boolean isUserAssociatedDistributor(B2BUnitModel b2BUnitModel) {
        return b2BUnitModel != null && getB2BUnitService().isUserAssociatedUnit(b2BUnitModel);
    }

    protected boolean isDisabled(B2BUnitModel b2BUnitModel) {
        return b2BUnitModel != null && !getB2BUnitService().isActive(b2BUnitModel);
    }

    public PartnerB2BUnitService<B2BUnitModel, UserModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public void setB2BUnitService(
        final PartnerB2BUnitService<B2BUnitModel, UserModel> b2BUnitService) {
        this.b2BUnitService = b2BUnitService;
    }
}
