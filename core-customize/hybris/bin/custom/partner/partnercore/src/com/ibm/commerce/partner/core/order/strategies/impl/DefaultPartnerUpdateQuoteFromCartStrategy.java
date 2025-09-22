package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.ErrorDetailsModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * This class is used to update Quote From Cart
 */
public class DefaultPartnerUpdateQuoteFromCartStrategy extends
    GenericAbstractOrderCloningStrategy<IbmPartnerQuoteModel, IbmPartnerQuoteEntryModel, CartModel> implements
    UpdateQuoteFromCartStrategy {

    private final PriceLookUpService priceLookUpService;
    private final ConfigurationService configurationService;

    public DefaultPartnerUpdateQuoteFromCartStrategy(PriceLookUpService priceLookUpService,
        ConfigurationService configurationService,
        ModelService modelService,
        KeyGenerator pidQuoteKeyGenerator, KeyGenerator guidKeyGenerator,
        EventService eventService) {
        super(IbmPartnerQuoteModel.class, IbmPartnerQuoteEntryModel.class, CartModel.class);
        this.priceLookUpService = priceLookUpService;
        this.configurationService = configurationService;
        this.modelService = modelService;
        this.pidQuoteKeyGenerator = pidQuoteKeyGenerator;
        this.guidKeyGenerator = guidKeyGenerator;
        this.eventService = eventService;
    }

    private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
    private final ModelService modelService;
    private static final String PID_QUOTE = "PIDQUOTE";
    private final KeyGenerator pidQuoteKeyGenerator;
    private KeyGenerator guidKeyGenerator;
    private final EventService eventService;

    /**
     * method to update Quote from Cart.
     *
     * @param cart a {@link CartModel} associated with a {@link QuoteModel}.
     * @return
     */
    @Override
    public IbmPartnerQuoteModel updateQuoteFromCart(final CartModel cart) {
        validateParameterNotNullStandardMessage("cart", cart);
        final QuoteModel outdatedQuote = getQuoteForCart(cart);
        saveOutdatedQuoteActiveIndex(outdatedQuote);
        final IbmPartnerQuoteModel updatedQuote = clone(cart, Optional.of(outdatedQuote.getCode()));
        final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
        if (StringUtils.equalsIgnoreCase(cart.getQuoteReference().getState().getCode(),
            String.valueOf(QuoteState.BUYER_DRAFT)) || StringUtils.equalsIgnoreCase(
            cart.getQuoteReference().getState().getCode(),
            String.valueOf(QuoteState.CLONE_BUYER_DRAFT))) {
            updatedQuote.setSubmitter(userModel);
            updatedQuote.setSubmittedDate(
                ((IbmPartnerQuoteModel) outdatedQuote).getSubmittedDate());
            updatedQuote.setCreator(((IbmPartnerQuoteModel) outdatedQuote).getCreator());
        }
        updatedQuote.setCpqQuoteNumber(updatedQuote.getCode());
        updatedQuote.setVersion(outdatedQuote.getVersion() + 1);
        updatedQuote.setQuoteIndexActive(Boolean.TRUE);
        createErrorModel(updatedQuote, cart);
        createChildEntries(updatedQuote, cart);
        populatePricingDetailsFromCartToQuote((IbmPartnerCartModel) cart, updatedQuote);
        updatedQuote.setState(outdatedQuote.getState());
        updatedQuote.setPreviousEstimatedTotal(outdatedQuote.getPreviousEstimatedTotal());

        final Boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);

        if (BooleanUtils.isFalse(isMultipleSpecialBidDisabled)) {
            createSpecialBidReasons(updatedQuote, cart);
        }

        postProcess(cart, updatedQuote);
        return updatedQuote;
    }

    /**
     * save special bid reasons to quote.
     *
     * @param updatedQuote
     * @param cart
     */
    private void createSpecialBidReasons(QuoteModel updatedQuote, CartModel cart) {
        if (cart instanceof IbmPartnerCartModel cartModel && CollectionUtils.isNotEmpty(
            cartModel.getSpecialBidReasons())
            && updatedQuote instanceof IbmPartnerQuoteModel partnerQuoteModel) {
            partnerQuoteModel.setSpecialBidReasons(cartModel.getSpecialBidReasons());
        }
    }

    private void saveOutdatedQuoteActiveIndex(QuoteModel outdatedQuote) {
        if (outdatedQuote instanceof IbmPartnerQuoteModel outdatedQuoteIndex) {
            outdatedQuoteIndex.setQuoteIndexActive(false);
            getModelService().save(outdatedQuoteIndex);
        }
    }

    /**
     * Method to Created Error Model from Cart Model to QuoteModel
     */
    protected void createErrorModel(IbmPartnerQuoteModel updatedQuote, CartModel cart) {
        cart.getEntries().stream().filter(entry -> entry instanceof IbmPartnerCartEntryModel
            && ((IbmPartnerCartEntryModel) entry).getErrorDetails() != null).forEach(cartEntry -> {
            updatedQuote.getEntries().stream().filter(
                    quoteEntry -> quoteEntry.getEntryNumber().equals(cartEntry.getEntryNumber()))
                .findFirst().ifPresent(quoteEntry -> {
                    ErrorDetailsModel cartErrorDetails = ((IbmPartnerCartEntryModel) cartEntry).getErrorDetails();
                    ErrorDetailsModel clonedErrorDetails = cloneErrorDetails(cartErrorDetails);
                    ((IbmPartnerQuoteEntryModel) quoteEntry).setErrorDetails(clonedErrorDetails);
                    getModelService().save(clonedErrorDetails);
                    ((IbmPartnerCartEntryModel) cartEntry).setErrorDetails(null);
                    getModelService().save(cartEntry);
                    getModelService().remove(cartErrorDetails);
                });
        });
    }

    /**
     * Clones the given {@link ErrorDetailsModel} by creating a new instance and copying relevant
     * data from the original model.
     *
     * @param originalErrorDetails the original {@link ErrorDetailsModel} to be cloned
     * @return a new instance of {@link ErrorDetailsModel} with a unique code and the same
     * description as the original
     */
    protected ErrorDetailsModel cloneErrorDetails(ErrorDetailsModel originalErrorDetails) {
        ErrorDetailsModel clonedErrorDetails = getModelService().create(ErrorDetailsModel.class);
        clonedErrorDetails.setCode(getGuidKeyGenerator().generate().toString());
        clonedErrorDetails.setDescription(originalErrorDetails.getDescription());

        return clonedErrorDetails;
    }

    private void createChildEntries(IbmPartnerQuoteModel quote, CartModel cart) {
        quote.getEntries().forEach(quoteEntry -> {
            populateCloneEntries(quoteEntry);
            Optional<AbstractOrderEntryModel> originalCartEntry = cart.getEntries().stream()
                .filter(entry -> entry.getEntryNumber().equals(quoteEntry.getEntryNumber()))
                .findAny();
            //Remove the PID quote from the entries.
            if (CollectionUtils.isNotEmpty(quoteEntry.getChildEntries())) {
                getModelService().remove(quoteEntry.getChildEntries().iterator().next().getOrder());
                getModelService().refresh(quoteEntry);
            }

            if (originalCartEntry.isPresent()) {
                quoteEntry.setProductConfiguration(
                    originalCartEntry.get().getProductConfiguration());
                if (CollectionUtils.isNotEmpty(originalCartEntry.get().getChildEntries())) {
                    IbmPartnerPidCartModel originalPidCart = (IbmPartnerPidCartModel) originalCartEntry.get()
                        .getChildEntries().iterator().next().getOrder();

                    final String orderCode =
                        PID_QUOTE + PartnercoreConstants.UNDERSCORE + originalCartEntry.get()
                            .getProduct().getCode() + PartnercoreConstants.UNDERSCORE
                            + getPidQuoteKeyGenerator().generate().toString();

                    AbstractOrderModel clone = getCloneAbstractOrderStrategy().clone(null, null,
                        originalPidCart, orderCode, IbmPartnerPidQuoteModel.class,
                        IbmPartnerPidQuoteEntryModel.class);
                    getModelService().save(clone);
                    quoteEntry.setChildEntries(clone.getEntries());
                    getModelService().save(quoteEntry);
                }
            }
        });
    }

    private void populateCloneEntries(AbstractOrderEntryModel pidCart) {
        if (pidCart != null && pidCart.getCpqPricingDetails() != null) {
            pidCart.getCpqPricingDetails().stream().filter(
                    pricing -> CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType()))
                .findFirst().ifPresent((fullPrice -> pidCart.setTotalPrice(
                    ((PartnerCpqPricingDetailModel) fullPrice).getTotalExtendedPrice())));
        }
    }

    /*
    // Method to populate PartnerCpqHeaderPricingDetailModel from reference cart to quote model
     */
    protected void populatePricingDetailsFromCartToQuote(IbmPartnerCartModel cartModel,
        IbmPartnerQuoteModel ibmPartnerQuote) {
        cartModel.getPricingDetails().forEach(cartCpqPricing -> {
            PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getPriceLookUpService().populateCPQHeaderPricingDetail(
                cartCpqPricing);

            cpqPricingDetail.setIbmPartnerQuote(ibmPartnerQuote);
            cpqPricingDetail.setOverrideTotalPrice(cartCpqPricing.getOverrideTotalPrice());
            cpqPricingDetail.setOverrideTotalDiscount(cartCpqPricing.getOverrideTotalDiscount());
            getModelService().save(cpqPricingDetail);

        });
    }


    /**
     * postProcess method for updatedQuote
     *
     * @param original
     * @param copy
     */
    @Override
    protected void postProcess(final CartModel original, final IbmPartnerQuoteModel copy) {
        super.postProcess(original, copy);
        copy.setCartReference(original);
        original.setQuoteReference(copy);
    }

    protected QuoteModel getQuoteForCart(final CartModel cart) {
        if (cart.getQuoteReference() == null) {
            throw new IllegalStateException(
                "Unable to update quote since cart is not created from a quote. Cart code: "
                    + cart.getCode());
        }
        return cart.getQuoteReference();
    }

    public QuoteUserIdentificationStrategy getQuoteUserIdentificationStrategy() {
        return quoteUserIdentificationStrategy;
    }

    public void setQuoteUserIdentificationStrategy(
        QuoteUserIdentificationStrategy quoteUserIdentificationStrategy) {
        this.quoteUserIdentificationStrategy = quoteUserIdentificationStrategy;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public KeyGenerator getPidQuoteKeyGenerator() {
        return pidQuoteKeyGenerator;
    }

    public KeyGenerator getGuidKeyGenerator() {
        return guidKeyGenerator;
    }

    public EventService getEventService() {
        return eventService;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}
