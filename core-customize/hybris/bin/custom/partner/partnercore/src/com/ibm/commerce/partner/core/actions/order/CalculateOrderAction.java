package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.task.RetryLaterException;
import java.text.MessageFormat;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculation Action for Order
 */
public class CalculateOrderAction extends
    PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CalculateOrderAction.class);


    private final CalculationService calculationService;
    private final PartnerCommerceCartService commerceCartService;
    private final PartnerCartUpdateStrategy reasonUpdateStrategy;


    protected CalculateOrderAction(final Integer maxRetryAllowed, final Integer retryDelay,
        final CalculationService calculationService,
        final PartnerCommerceCartService commerceCartService,
        final PartnerCartUpdateStrategy reasonUpdateStrategy) {
        super(maxRetryAllowed, retryDelay);
        this.calculationService = calculationService;
        this.commerceCartService = commerceCartService;
        this.reasonUpdateStrategy = reasonUpdateStrategy;
    }

    @Override
    public Transition executeAction(final PriceLookUpProcessModel processModel)
        throws RetryLaterException, Exception {
        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), processModel.getCode());
        LOG.debug(msg);
        try {
            if (processModel.getOrder() instanceof IbmPartnerCartModel cartModel) {
                setIsPriceOverridden(cartModel);
                getCalculationService().calculate(processModel.getOrder());
                setFullPriceReceivedFlag(cartModel);
                getReasonUpdateStrategy().update(cartModel);
                getCommerceCartService().updateQuestionSelections(cartModel);
                return Transition.OK;
            }
            return Transition.NOK;
        } catch (final Exception ex) {
            return retryOrFailAction(processModel, msg);
        }
    }

    /*
    // Method to set IsPriceOverridden flag to false for cart Model as we are having price call so overridden value will not be used
     */
    protected void setIsPriceOverridden(IbmPartnerCartModel cart) {
        cart.setIsPriceOverridden(Boolean.FALSE);
        cart.getEntries().stream().forEach(entry -> {
            setIsPriceOverridden(entry);
            getModelService().save(cart);
        });
    }

    /*
    // Method to set IsPriceOverridden flag to false for entry Model as we are having price call so overridden value will not be used
     */
    protected void setIsPriceOverridden(AbstractOrderEntryModel entryModel) {
        if (null != entryModel) {
            if (CollectionUtils.isNotEmpty(entryModel.getChildEntries())) {
                entryModel.getChildEntries()
                    .forEach(childEntry -> setIsPriceOverridden(childEntry));
            } else {
                IbmPartnerCartEntryModel entry = (IbmPartnerCartEntryModel) entryModel;
                entry.setIsPriceOverridden(Boolean.FALSE);
                getModelService().save(entry);
            }
        }
    }

    /*
    // Method to make fullPriceReceived flag as true when cart is empty
     */
    protected void setFullPriceReceivedFlag(IbmPartnerCartModel cart) {
        if (Objects.nonNull(cart.getQuoteReference()) && CollectionUtils.isEmpty(
            cart.getEntries())) {
            cart.setFullPriceReceived(Boolean.TRUE);
            getModelService().save(cart);
        }
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public PartnerCommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    public PartnerCartUpdateStrategy getReasonUpdateStrategy() {
        return reasonUpdateStrategy;
    }
}
