package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUpdateStateStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * Updates the Quote State
 */
public class DefaultPartnerQuoteUpdateStateStrategy extends DefaultQuoteUpdateStateStrategy {

    private final String quoteStatusOrderedList;
    private ConfigurationService configurationService;

    public DefaultPartnerQuoteUpdateStateStrategy(String quoteStatusOrderedList,
        ConfigurationService configurationService) {
        this.quoteStatusOrderedList = quoteStatusOrderedList;
        this.configurationService = configurationService;
    }

    /**
     * Updates the state of a given quote based on the specified quote action.
     *
     * @param quoteAction the action to be applied to the quote, must not be null.
     * @param quoteModel  the quote model to be updated, must not be null.
     * @param userModel   the user performing the action, must not be null.
     * @return the updated {@link QuoteModel} with the new state.
     * @throws IllegalArgumentException if any of the parameters are null.
     */
    @Override
    public QuoteModel updateQuoteState(final QuoteAction quoteAction, final QuoteModel quoteModel,
        final UserModel userModel) {
        validateParameterNotNullStandardMessage("Quote action", quoteAction);
        validateParameterNotNullStandardMessage("Quote", quoteModel);
        validateParameterNotNullStandardMessage("User", userModel);

        if (!QuoteState.CLONE_BUYER_CREATED.equals(quoteModel.getState())) {
            super.updateQuoteState(quoteAction, quoteModel, userModel);
        } else {
            quoteModel.setState(QuoteState.CLONE_BUYER_DRAFT);
        }
        updateQuoteActiveIndex(quoteModel);
        return quoteModel;
    }

    private void updateQuoteActiveIndex(QuoteModel updatedQuoteModel) {
        if (QuoteState.CANCELLED.equals(updatedQuoteModel.getState())
            && updatedQuoteModel instanceof IbmPartnerQuoteModel quoteModel) {
            quoteModel.setQuoteIndexActive(Boolean.FALSE);
        }
    }

    /**
     * Updating the Quote state based on the values of eccQuoteStatus and cpqQuoteStatus.
     *
     * @param quoteModel
     * @param cpqState
     * @param eccState
     */
    public void updatePartnerQuoteState(final IbmPartnerQuoteModel quoteModel,
        final String cpqState,
        final String eccState) {

        String[] orderedStatusArray = getQuoteStatusOrderedList().toLowerCase()
            .split(PartnercoreConstants.COMMA);

        quoteModel.setStrCpqQuoteStatus(cpqState);
        quoteModel.setStrEccQuoteStatus(eccState);

        if (cpqState.contains(PartnercoreConstants.AWAITING_INTERNAL_APPROVAL)) {
            quoteModel.setState(QuoteState.IN_REVIEW);
        }
        if (cpqState.contains(PartnercoreConstants.REJECTED)) {
            quoteModel.setState(QuoteState.NOT_APPROVED);
        }
        if (cpqState.contains(PartnercoreConstants.EXPIRED)) {
            quoteModel.setState(QuoteState.EXPIRED);
        }
        if (cpqState.contains(PartnercoreConstants.SUBMITTED)) {
            if (StringUtils.isBlank(eccState) || eccState.contains(PartnercoreConstants.NOT_APPLICABLE)) {
                quoteModel.setState(QuoteState.IN_REVIEW);
            } else if (eccState.contains(PartnercoreConstants.READY_TO_ORDER)) {
                quoteModel.setState(QuoteState.READY_TO_ORDER);
            } else if (!isQuoteStatusOrderedFlag() || (isQuoteStatusOrderedFlag() && Arrays.asList(
                    orderedStatusArray)
                .contains(eccState.toLowerCase()))) {
                quoteModel.setState(QuoteState.ORDERED);
            }
        }
    }

    public String getQuoteStatusOrderedList() {
        return quoteStatusOrderedList;
    }

    public boolean isQuoteStatusOrderedFlag() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
