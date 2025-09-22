/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.v2.validator.impl;



import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Required;

import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.occ.v2.validator.PartnerQuoteValidator;


public class PartnerQuoteMandateValidator implements PartnerQuoteValidator {

    private TimeService timeService;
    private final EventService eventService;
    private ConfigurationService configurationService;

    private static final String END_CUSTOMER_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String RESELLER_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String DISTRIBUTOR_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String CART_NOT_CALCULATED = "Pricing is still being calculated. Please wait 10 minutes and try again.";
    private static final String MISSING_PRODUCT = "You do not have any products configured. Select 'Add more' to configure a product.";
    private static final String CHILD_ENTRY_MISSING = "[PID#]Configuration error. Select 'Edit' on [PID#] to update the configuration.";
    private static final String QUOTE_EXPIRED = "Quote is expired. Please update my quote expiration date. ";
    private static final String FULL_PRICE_MISSING = "Quote is not having the correct prices. Please wait 10 minutes and try again.";
    private static final String OPPORTUNITY_INVALID = "Opportunity id is invalid. ";
    private static final String OPPORTUNITY_ID = "opportunityId";
    private static final String FULL_PRICE_STALE = "Your pricing is stale and needs to be updated, please click the 'Update Pricing' button to refresh your pricing.";

    private static final String QUOTE_REF_MISSING = "cart have no quote refernce";
    private static final String SPECIAL_BID_JUSTIFICATION_MISSING = "You've initiated a special bid, please ensure a special bid exception code and business justification have been entered on the Special bid tab";
    private static final String SPECIAL_BID_JUSTIFICATION_TEXT_INVALID = "Business Justification is Invalid and exceeds 2000 character";
    private static final String SPECIAL_BID_PRICE_INVALID = "Special Bid Price is empty";

    public PartnerQuoteMandateValidator(final EventService eventService,
        final ConfigurationService configurationService) {
        this.eventService = eventService;
        this.configurationService = configurationService;
    }

    @Override
    public void validateMandatoryField(final CartModel cart) throws CommerceCartModificationException {

        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) cart;
        {
            validateUnits(cartModel);

            if (!cartModel.getCalculated()) {
                throw new CommerceCartModificationException(CART_NOT_CALCULATED);
            }
            if (CollectionUtils.isEmpty(cartModel.getEntries()) || cartModel.getEntries().stream()
                .anyMatch(e -> e.getProduct() == null)) {
                throw new CommerceCartModificationException(MISSING_PRODUCT);
            }
            if (cartModel.getEntries().stream()
                .anyMatch(e -> e.getChildEntries().isEmpty())) {
                throw new CommerceCartModificationException(CHILD_ENTRY_MISSING);
            }
            if (Objects.isNull(cartModel.getQuoteExpirationDate())
                || getTimeService().getCurrentTime().compareTo(cartModel.getQuoteExpirationDate())
                > 0) {
                throw new CommerceCartModificationException(QUOTE_EXPIRED);
            }

            if (Boolean.TRUE.equals(cartModel.getPriceStale())) {
                throw new CommerceCartModificationException(FULL_PRICE_STALE);
            }

        }
            if (!Boolean.TRUE.equals(cartModel.getFullPriceReceived())) {
            getEventService().publishEvent(new CartPriceLookUpEvent(cart));
            throw new CommerceCartModificationException(FULL_PRICE_MISSING);
            }

            if (cartModel.isSpecialBid()) {
            validateSpecialBidAttributes(cartModel);
        }
    }

    /**
     * validate the cart information
     *
     * @param cartModel
     * @param businessJustification
     * @throws CommerceCartModificationException
     */
    @Override
    public void validateSpecialBidReasonDetails(final IbmPartnerCartModel cartModel,
        final String businessJustification)
        throws CommerceCartModificationException {

        if (cartModel.getQuoteReference() == null) {
            throw new CommerceCartModificationException(QUOTE_REF_MISSING);
        }
        if (businessJustification.length() > 2000) {
            throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_TEXT_INVALID);
        }
        if (!Boolean.TRUE.equals(validateHeaderOverridePricing(cartModel))) {
            throw new CommerceCartModificationException(SPECIAL_BID_PRICE_INVALID);
        }

    }

      public boolean validateHeaderOverridePricing(final IbmPartnerCartModel cartModel)
      {
          if(CollectionUtils.isNotEmpty(cartModel.getPricingDetails())){
              return true;
          }
          return validateEntryLevelOverridePricing(cartModel);
      }
    protected boolean validateEntryLevelOverridePricing(final IbmPartnerCartModel cartModel) {

        return cartModel.getEntries().stream()
            .flatMap(orderEntryModel -> orderEntryModel.getChildEntries().stream())
            .flatMap(
                abstractOrderEntryModel -> abstractOrderEntryModel.getCpqPricingDetails().stream())
            .map(cpqPricingDetailModel -> (PartnerCpqPricingDetailModel) cpqPricingDetailModel)
            .anyMatch(partnerCpqPricingDetailModel ->
                Objects.nonNull(partnerCpqPricingDetailModel.getOverrideBidUnitPrice()) ||
                    Objects.nonNull(partnerCpqPricingDetailModel.getOverrideDiscount())
                    || Objects.nonNull(partnerCpqPricingDetailModel.getOverrideYearToYearGrowth())
            );
    }

	 /**
	  * @param cartModel
	  * @throws CommerceCartModificationException
	  */
	 private void validateSpecialBidAttributes(final IbmPartnerCartModel cartModel) throws CommerceCartModificationException
	 {
         final boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
             .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                 Boolean.TRUE);

         if (isMultipleSpecialBidDisabled) {
             if (Objects.isNull(cartModel.getSpecialBidReason())) {
                 throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_MISSING);
             }
             if (Objects.isNull(cartModel.getSpecialBidReason().getCode())) {
                 throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_MISSING);
             }
         } else {
             if (CollectionUtils.isEmpty(cartModel.getSpecialBidReasons())) {
                 throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_MISSING);
             }
         }

		 if (Objects.isNull(cartModel.getSpecialBidBusinessJustification()))
		 {
			 throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_MISSING);
		 }
		 else if (Objects.nonNull(cartModel.getSpecialBidBusinessJustification()))
		 {
			 final String businessJustification = cartModel.getSpecialBidBusinessJustification();
			 final int sizeOfbusinessJustification = businessJustification.length();
			 if (sizeOfbusinessJustification > 2000)
			 {
				 throw new CommerceCartModificationException(SPECIAL_BID_JUSTIFICATION_TEXT_INVALID);
			 }
		 }
	 }

	 /**
	  * @param cartModel
	  * @throws CommerceCartModificationException
	  */
	 private void validateUnits(final IbmPartnerCartModel cartModel) throws CommerceCartModificationException
	 {
		 if (cartModel.getUnit() == null)
		 {

			 throw new CommerceCartModificationException(END_CUSTOMER_MISSING);
		 }
		 if (cartModel.getSoldThroughUnit() == null)
		 {

			 throw new CommerceCartModificationException(RESELLER_MISSING);
		 }
		 if (cartModel.getBillToUnit() == null)
		 {

			 throw new CommerceCartModificationException(DISTRIBUTOR_MISSING);
		 }
	 }

    protected TimeService getTimeService() {
        return timeService;
    }

    @Required
    public void setTimeService(final TimeService timeService) {
        this.timeService = timeService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}




