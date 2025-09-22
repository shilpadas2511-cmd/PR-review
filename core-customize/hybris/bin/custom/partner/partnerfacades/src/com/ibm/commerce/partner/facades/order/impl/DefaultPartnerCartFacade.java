package com.ibm.commerce.partner.facades.order.impl;

import static com.ibm.commerce.partner.core.constants.PartnercoreConstants.COLLAB_ADD_FAILURE;
import static com.ibm.commerce.partner.core.constants.PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import com.ibm.commerce.data.order.QuoteCollaboratorsData;
import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.order.services.PidCartFactory;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import com.ibm.commerce.partner.facades.order.PartnerCartFacade;
import com.ibm.commerce.partner.facades.validator.PartnerOverridePriceValidator;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.b2bacceleratorfacades.exception.DomainException;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCartFacade;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.LockedException;

/**
 * This class is to define Cart and orderEntry related methods.
 */
public class DefaultPartnerCartFacade extends DefaultB2BCartFacade implements PartnerCartFacade {


    private static final String BASKET_QUANTITY_ERROR_KEY = "basket.error.quantity.invalid";

    private static final String CART_MODIFICATION_ERROR = "basket.error.occurred";

    private final Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
    private final CommerceCartService commerceCartService;
    private final Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
    private final Converter<PartnerOverrideHeaderPriceData, PartnerCpqHeaderPricingDetailModel> partnerCartHeaderPricingDetailsReverseConverter;
    private final Converter<PartnerOverrideEntryPriceData, PartnerCpqPricingDetailModel> partnerCartEntryPricingDetailsReverseConverter;

    private PartnerCommerceCartService partnerCommerceCartService;
    private final PidCartFactory pidCartFactory;

    private final SessionService sessionService;

    private final ModelService modelService;
    private EventService eventService;

    private PartnerProcessService partnerProcessService;

    private final PartnerSapCpqQuoteService partnerSapCpqQuoteService;

    private final ConfigurationService configurationService;

    private final PriceLookUpService priceLookUpService;

    private final PartnerOverridePriceValidator partnerOverridePriceValidator;

    private final int collaboratorEmailsLimit;

    private final CustomerEmailResolutionService customerEmailResolutionService;


    public DefaultPartnerCartFacade(
        final Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter,
        final CommerceCartService commerceCartService,
        final Converter<CommerceCartModification, CartModificationData> cartModificationConverter,
        Converter<PartnerOverrideHeaderPriceData, PartnerCpqHeaderPricingDetailModel> partnerCartHeaderPricingDetailsReverseConverter,
        Converter<PartnerOverrideEntryPriceData, PartnerCpqPricingDetailModel> partnerCartEntryPricingDetailsReverseConverter,
        final PidCartFactory pidCartFactory, final SessionService sessionService,
        final ModelService modelService,
        final EventService eventService,
        final PartnerProcessService partnerProcessService, PriceLookUpService priceLookUpService,
        PartnerOverridePriceValidator partnerOverridePriceValidator,
        int collaboratorEmailsLimit, final PartnerCommerceCartService partnerCommerceCartService,
        PartnerSapCpqQuoteService partnerSapCpqQuoteService,
        ConfigurationService configurationService, CustomerEmailResolutionService customerEmailResolutionService) {

        this.commerceCartParameterConverter = commerceCartParameterConverter;
        this.commerceCartService = commerceCartService;
        this.cartModificationConverter = cartModificationConverter;
        this.partnerCartHeaderPricingDetailsReverseConverter = partnerCartHeaderPricingDetailsReverseConverter;
        this.partnerCartEntryPricingDetailsReverseConverter = partnerCartEntryPricingDetailsReverseConverter;
        this.pidCartFactory = pidCartFactory;
        this.sessionService = sessionService;
        this.modelService = modelService;
        this.eventService = eventService;
        this.partnerProcessService = partnerProcessService;
        this.priceLookUpService = priceLookUpService;
        this.partnerOverridePriceValidator = partnerOverridePriceValidator;
        this.collaboratorEmailsLimit = collaboratorEmailsLimit;
        this.partnerCommerceCartService = partnerCommerceCartService;
        this.partnerSapCpqQuoteService = partnerSapCpqQuoteService;
        this.configurationService = configurationService;
        this.customerEmailResolutionService = customerEmailResolutionService;
    }


    /**
     * @param cartEntry the cart entry with the new product to add..
     * @return CartModificationData modified data for the cart
     * @throws EntityValidationException throw exception during entity validation
     */
    @Override
    public CartModificationData addOrderEntry(final OrderEntryData cartEntry)
        throws EntityValidationException {

        if (!isValidEntry(cartEntry)) {
            throw new EntityValidationException(getLocalizedString(BASKET_QUANTITY_ERROR_KEY));
        }
        CartModificationData cartModification = null;
        try {
            cartModification = addToCart(cartEntry);
        } catch (final CommerceCartModificationException e) {
            throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
        }
        setAddStatusMessage(cartEntry, cartModification);
        return cartModification;
    }

    /**
     * @param cartEntry cart entry data
     * @return CartModificationData modified data for the cart
     * @throws CommerceCartModificationException throw exception during cart modification
     */
    public CartModificationData addToCart(final OrderEntryData cartEntry)
        throws CommerceCartModificationException {
        final AddToCartParams params = populateAddToCartParameter(cartEntry);
        return getCartFacade().addToCart(params);
    }

    private AddToCartParams populateAddToCartParameter(final OrderEntryData cartEntry) {
        final AddToCartParams params = new AddToCartParams();
        params.setProductCode(cartEntry.getProduct().getCode());
        params.setQuantity(cartEntry.getQuantity());
        params.setConfigurationInfos(cartEntry.getConfigurationInfos());
        params.setPartProduct(cartEntry.isPartProduct());
        params.setPidId(cartEntry.getPidId());
        params.setConfiguratorPidId(cartEntry.getConfiguratorPidId());
        params.setConfigId(cartEntry.getConfigId());
        params.setCreateNewEntry(cartEntry.isCreateNewEntry());
        if (Objects.nonNull(cartEntry.getEntryStatus())) {
            params.setEntryStatus(cartEntry.getEntryStatus());
        }
        if (Objects.nonNull (cartEntry.getErrorDetails()) && StringUtils.isNotEmpty(cartEntry.getErrorDetails().getDescription())) {
            params.setErrorDetails(cartEntry.getErrorDetails());
        }
        if (ObjectUtils.isNotEmpty(cartEntry.getCommerceRampUpData())) {
             params.setCommerceRampUpData(cartEntry.getCommerceRampUpData());
        }
        return params;
    }

    /**
     * @param orderEntry the cart entry with the price data
     * @return CartModificationData modified data for the cart
     */
    @Override
    public CartModificationData updateOrderEntry(final OrderEntryData orderEntry) {
        validateIfCartCanBeUpdated();

        CartModificationData cartModification = null;
        if (!orderEntry.isCreateNewEntry()) {
            orderEntry.setEntryNumber(getOrderEntryNumber(orderEntry));
        }

        try {
            if (orderEntry.getEntryNumber() != null) {
                // grouped items
                if (CollectionUtils.isNotEmpty(orderEntry.getEntries())) {
                    if (orderEntry.getQuantity().intValue() == 0) {
                        cartModification = deleteGroupedOrderEntries(orderEntry);
                    }
                } else {
                    final AddToCartParams dto = populateAddToCartParameter(orderEntry);
                    final CommerceCartParameter parameter = getCommerceCartParameterConverter().convert(
                        dto);
                    if (parameter != null) {
                        parameter.setEnableHooks(true);
                        parameter.setEntryNumber(orderEntry.getEntryNumber());
                        final CommerceCartModification modification = getCommerceCartService()
                            .updateQuantityForCartEntry(parameter);
                        cartModification = getCartModificationConverter().convert(modification);
                    }
                }
            } else {
                cartModification = addOrderEntry(orderEntry);
            }

            setUpdateStatusMessage(orderEntry, cartModification);
        } catch (final CommerceCartModificationException e) {
            throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
        }

        return cartModification;

    }

    @Override
    public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries) {
        validateIfCartCanBeUpdated();
        final List<CartModificationData> modificationDataList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(cartEntries)) {
            final List<OrderEntryData> partList = new ArrayList<>();
            final List<OrderEntryData> pidList = new ArrayList<>();

            for (OrderEntryData cartEntry : cartEntries) {
                if (cartEntry.isPartProduct()) {
                    partList.add(cartEntry);
                } else {
                    pidList.add(cartEntry);
                }
            }
            for (final OrderEntryData orderEntryData : pidList) {
                final CartModificationData cartModificationData = addOrderEntry(
                    modificationDataList,
                    orderEntryData);
                createChildEntries(modificationDataList, cartModificationData, partList, true);
            }
            CartModel cart= getCartService().getSessionCart();
            getPartnerCommerceCartService().validateProvisionForms(cart);
            getPriceLookUpService().populateYtyDiscount((IbmPartnerCartModel) cart);
            getEventService().publishEvent(new CartPriceLookUpEvent(cart));
        }

        if (modificationDataList.isEmpty()) {
            throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR));
        }
        return getGroupCartModificationListConverter().convert(null, modificationDataList);
    }

    protected CartModel getPidCart(final AbstractOrderEntryModel pidEntry, final String pidCode,
        final boolean forceCreate) {
        if (pidEntry != null) {
            if (CollectionUtils.isNotEmpty(pidEntry.getChildEntries())) {
                final CartModel oldPidOrder = (CartModel) pidEntry.getChildEntries().iterator()
                    .next()
                    .getOrder();
                if (!forceCreate) {
                    return oldPidOrder;
                }
                getModelService().remove(oldPidOrder);
            }
            return getPidCartFactory().createCart(pidCode);
        }
        return null;
    }

    protected void createChildEntries(final List<CartModificationData> modificationDataList,
        final CartModificationData cartModificationData, final List<OrderEntryData> partList,
        final boolean forceChildCartCreation) {
        if (cartModificationData != null && CommerceCartModificationStatus.SUCCESS.equals(
            cartModificationData.getStatusCode())) {
            final CartModel sessionCart = getCartService().getSessionCart();
            getModelService().refresh(sessionCart);
            final String pidCode = cartModificationData.getEntry().getProduct().getCode();
            final AbstractOrderEntryModel pidEntry = getPidEntry(sessionCart,
                cartModificationData.getEntry().getEntryNumber());

            final List<OrderEntryData> pidParts = partList.stream()
                .filter(partEntry -> pidCode.equals(partEntry.getPidId())).toList();
            final CartModel partCart = getPidCart(pidEntry, pidCode, forceChildCartCreation);
            setSessionCartForPart(partCart);

            for (final OrderEntryData pidPart : pidParts) {
                if(forceChildCartCreation)
                {
                    pidPart.setCreateNewEntry(Boolean.TRUE);
                }
                addOrderEntry(modificationDataList, pidPart);
            }
            pidEntry.setChildEntries(partCart.getEntries());
            partCart.getEntries().forEach(childEntry -> childEntry.setMasterEntry(pidEntry));
            getModelService().save(pidEntry);
            getSessionService().removeAttribute("partCart");
        }
    }

    protected AbstractOrderEntryModel getPidEntry(final AbstractOrderModel orderModel,
        final int entryNumber) {
        if (orderModel != null) {
            final Optional<AbstractOrderEntryModel> optionalPidWithChildEntry = orderModel.getEntries()
                .stream().filter(entryModel -> entryModel.getEntryNumber() == entryNumber)
                .findAny();
            return optionalPidWithChildEntry.isPresent() ? optionalPidWithChildEntry.get() : null;
        }
        return null;
    }

    protected CartModificationData addOrderEntry(
        final List<CartModificationData> modificationDataList,
        final OrderEntryData orderEntry) {
            try {
                final CartModificationData cartModificationData = addOrderEntry(orderEntry);
                if (cartModificationData != null) {
                    modificationDataList.add(cartModificationData);
                    return cartModificationData;
                }
            } catch (final DomainException d) {
                LOG.error("Error processing entry", d);
            }
        return null;
    }

    private void setSessionCartForPart(final CartModel cartModel) {
        sessionService.setAttribute("partCart", cartModel);
    }

    @Override
    public void updatePrices() {
        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
        validateCartEditable(cartModel);
        if (cartModel != null && cartModel.getQuoteReference() != null) {
            cartModel.setCalculated(Boolean.FALSE);
            cartModel.setFullPriceReceived(Boolean.FALSE);
            cartModel.setErrorMesaage(null);
            getModelService().save(cartModel);
            getPartnerProcessService().removeCartFromOldProcess(cartModel);
            CartPriceLookUpEvent event = new CartPriceLookUpEvent(cartModel);
            getEventService().publishEvent(event);
        }

    }

    @Override
    public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries) {
        validateIfCartCanBeUpdated();
        final List<CartModificationData> modificationDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cartEntries)) {
            final List<OrderEntryData> pidList = cartEntries.stream()
                .filter(orderEntryData -> BooleanUtils.isFalse(orderEntryData.isPartProduct()))
                .sorted(
                    Comparator.comparing(OrderEntryData::isCreateNewEntry))
                .toList();
            final List<OrderEntryData> partList = cartEntries.stream()
                .filter(OrderEntryData::isPartProduct).toList();

            for (final OrderEntryData orderEntryData : pidList) {
                final CartModificationData cartModificationData = updateOrderEntry(orderEntryData);
                if (orderEntryData.getQuantity() > 0 && CollectionUtils.isNotEmpty(partList)) {
                    createChildEntries(modificationDataList, cartModificationData, partList, true);
                }
            }
        }
        CartModel cart= getCartService().getSessionCart();
        getPartnerCommerceCartService().validateProvisionForms(cart);
        getPriceLookUpService().populateYtyDiscount((IbmPartnerCartModel) cart);
        getEventService().publishEvent(new CartPriceLookUpEvent(cart));
        return getGroupCartModificationListConverter().convert(null, modificationDataList);
    }

    /**
     * @param partnerOverrideHeaderPriceData update the edit prices on cart
     */

    public void updateHeaderPriceDetails(
        PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData)
        throws CommerceCartModificationException {
        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
        validateCartEditable(cartModel);
        getPartnerOverridePriceValidator().validateHeaderMandatoryField(cartModel,
            partnerOverrideHeaderPriceData);

        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = getPriceLookUpService().getHeaderCpqPricingDetail(
            cartModel, CpqPricingTypeEnum.FULL);
        getPartnerCartHeaderPricingDetailsReverseConverter().convert(
            partnerOverrideHeaderPriceData, partnerCpqHeaderPricingDetail);
        if (null != partnerCpqHeaderPricingDetail.getOverrideTotalPrice()
            || null != partnerCpqHeaderPricingDetail.getOverrideTotalDiscount()
            || PartnerOrderUtils.validateYTYOverridden(partnerCpqHeaderPricingDetail)) {
            cartModel.setCalculated(false);
            cartModel.setIsPriceOverridden(true);
            removeOverriddenEntryPrices(cartModel);
            getModelService().save(cartModel);
            getModelService().refresh(cartModel);
        }

        getModelService().save(partnerCpqHeaderPricingDetail);
    }

    /**
     * @param partnerOverrideEntryPriceData update the edit prices on child entries
     */
    @Override
    public void updateEntryPriceDetails(
        PartnerOverrideEntryPriceData partnerOverrideEntryPriceData)
        throws CommerceCartModificationException {
        final IbmPartnerCartModel cartModel = (IbmPartnerCartModel) getCartService().getSessionCart();
        validateCartEditable(cartModel);
        IbmPartnerCartEntryModel entry = getEntry(cartModel,
            partnerOverrideEntryPriceData.getEntryNumber());
        if (ObjectUtils.isNotEmpty(entry)) {
            Optional<PartnerCpqPricingDetailModel> partnerCpqEntryPricingDetail = getPriceLookUpService().getCpqPricingDetail(
                entry, CpqPricingTypeEnum.FULL);
            if (partnerCpqEntryPricingDetail.isPresent()) {
                getPartnerOverridePriceValidator().validateEntryMandatoryField(entry,
                    partnerOverrideEntryPriceData, partnerCpqEntryPricingDetail);

                getPartnerCartEntryPricingDetailsReverseConverter().convert(
                    partnerOverrideEntryPriceData,
                    partnerCpqEntryPricingDetail.get());
                getModelService().save(partnerCpqEntryPricingDetail.get());
                entry.setCalculated(false);
                entry.setIsPriceOverridden(true);
                cartModel.setCalculated(false);
                getModelService().saveAll(entry, cartModel);
                getCart(cartModel);
            }
        } else {
            throw new CommerceCartModificationException("entry number provided is not valid");
        }
    }

    /**
     * @param cartModel get main cart and setting calculated to false.
     */
    protected void getCart(IbmPartnerCartModel cartModel) throws CommerceCartModificationException {
        IbmPartnerCartModel cart = cartModel.getEntries().stream()
            .filter(e -> e.getMasterEntry() != null && e.getMasterEntry().getOrder() != null)
            .map(e -> (IbmPartnerCartModel) e.getMasterEntry().getOrder())
            .findAny()
            .orElseThrow(() -> new CommerceCartModificationException("cart is not valid"));
        cart.setCalculated(false);
        removeOverriddenPrices(cart);
    }

    /**
     * @param cart remove overridden header price data
     */

    protected void removeOverriddenPrices(IbmPartnerCartModel cart) {

        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = getPriceLookUpService().getHeaderCpqPricingDetail(
            cart, CpqPricingTypeEnum.FULL);

        if (partnerCpqHeaderPricingDetail != null && (
            partnerCpqHeaderPricingDetail.getOverrideTotalPrice() != null
                || partnerCpqHeaderPricingDetail.getOverrideTotalDiscount() != null)) {
            partnerCpqHeaderPricingDetail.setOverrideTotalPrice(null);
            partnerCpqHeaderPricingDetail.setOverrideTotalDiscount(null);
            getModelService().save(partnerCpqHeaderPricingDetail);
        }
        getModelService().save(cart);
        getModelService().refresh(cart);
    }

    /**
     * @param cart
     * @param entryNumber fetch entry from cart through entryNumber
     */
    protected IbmPartnerCartEntryModel getEntry(@Nonnull final CartModel cart,
        final long entryNumber) {
        if (CollectionUtils.isEmpty(cart.getEntries())) {
            throw new IllegalArgumentException("Cart " + cart.getCode() + " has no entries");
        }
        return (IbmPartnerCartEntryModel)cart.getEntries().stream().filter(e -> e.getEntryNumber() != null)
            .filter(e -> entryNumber == e.getEntryNumber().longValue()).findAny().orElse(null);
    }

    /*
    Remove cpqentrylevelPricingDetails when user updates overriden prices on quote cart
     */
    protected void removeOverriddenEntryPrices(@Nonnull IbmPartnerCartModel cart) {
        if (CollectionUtils.isNotEmpty(cart.getEntries())) {
            for (AbstractOrderEntryModel entry : cart.getEntries()) {
                if (CollectionUtils.isNotEmpty(entry.getChildEntries())) {
                    for (AbstractOrderEntryModel childEntry : entry.getChildEntries()) {
                        Optional<PartnerCpqPricingDetailModel> partnerCpqEntryPriceDetail = getPriceLookUpService().getCpqPricingDetail(
                            childEntry, CpqPricingTypeEnum.FULL);
                        if (partnerCpqEntryPriceDetail.isPresent()) {
                            partnerCpqEntryPriceDetail.get().setOverrideDiscount(null);
                            partnerCpqEntryPriceDetail.get().setOverrideBidUnitPrice(null);
                            partnerCpqEntryPriceDetail.get().setOverrideYearToYearGrowth(null);
                            getModelService().save(partnerCpqEntryPriceDetail.get());
                        }
                    }
                }
            }
        }

    }

    /**
     * Validates if the current user is allowed to edit the given cart.
     *
     * @param cartModel The cart to validate for editability.
     * @throws LockedException if the cart is associated with a different user.
     */
    protected void validateCartEditable(CartModel cartModel) {
        validateParameterNotNull(cartModel,"CartModel cannot be null");

        final UserModel currentUser = getUserService().getCurrentUser();
        final UserModel cartUser = cartModel.getUser();

        if (cartModel.getQuoteReference() != null && !currentUser.equals(cartUser)) {
            throw new LockedException(String.format(
                PartnercoreConstants.QUOTE_LOCK_EXCEPTION,
                getCustomerEmailResolutionService().getEmailForCustomer(
                    (CustomerModel) cartUser)));
        }
    }

    /**
     * Adds collaborators to the quote cart if it meets the criteria.
     *
     * @param data the QuoteCollaboratorsData containing the emails to be added
     * @return true if collaborators were successfully added, false otherwise
     */
    @Override
    public boolean addCollaborator(QuoteCollaboratorsData data) {
        CartModel cart = getCartService().getSessionCart();
        if (null != cart && null != cart.getQuoteReference()) {
            List<String> pendingCollaborators = data.getCollaboratorEmails();
            Set<String> existingCollaborators = ((IbmPartnerCartModel) cart).getCollaboratorEmails();
            if (Math.addExact(CollectionUtils.size(pendingCollaborators),
                CollectionUtils.size(existingCollaborators)) <= collaboratorEmailsLimit) {
                Set<String> updatedCollaboratorEmails = new HashSet<>();
                Stream.of(existingCollaborators, pendingCollaborators)
                    .filter(CollectionUtils::isNotEmpty)
                    .forEach(updatedCollaboratorEmails::addAll);
                if (isQuoteCommonApiFeatureDisabled()) {
                    ((IbmPartnerCartModel) cart).setCollaboratorEmails(updatedCollaboratorEmails);
                    getModelService().save(cart);
                    return true;
                } else {
                    try {
                        Transaction tx = Transaction.current();
                        tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
                        return (boolean) tx.execute(new TransactionBody() {
                            @Override
                            public Boolean execute() throws Exception {
                                ((IbmPartnerCartModel) cart).setCollaboratorEmails(
                                    updatedCollaboratorEmails);
                                getModelService().save(cart);
                                getPartnerSapCpqQuoteService().addCollaboratorsToCpq(
                                    (IbmPartnerCartModel) cart);
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        LOG.error(
                            String.format(COLLAB_ADD_FAILURE, cart.getQuoteReference().getCode(),
                                e.getMessage()), e);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Removes specified collaborators from the current session cart's CPQ quote.
     *
     * <p>This method updates the list of collaborators associated with the quote stored in the
     * session cart.
     * If the quote collaboration feature is disabled, it only updates the local model. If enabled,
     * it also makes an outbound call to the CPQ system to synchronize the changes.</p>
     *
     * <p>The method executes within a transaction (with {@code TRANSACTION_READ_COMMITTED}
     * isolation)
     * when the CPQ collaboration API is enabled. If any exception occurs during the remote call or
     * update, it logs the error and returns {@code false}.</p>
     *
     * @param data the {@link QuoteCollaboratorsData} containing the list of collaborators to
     *             remove
     * @return {@code true} if the collaborators were successfully removed; {@code false} otherwise
     */
    @Override
    public boolean removeCollaborator(QuoteCollaboratorsData data) {
        IbmPartnerCartModel cart = (IbmPartnerCartModel) getCartService().getSessionCart();
        if (Objects.isNull(cart) || Objects.isNull(cart.getQuoteReference())
            || CollectionUtils.isEmpty(cart.getCollaboratorEmails())) {
            return Boolean.FALSE;
        }
        Set<String> existingCollaborators = new HashSet<>(cart.getCollaboratorEmails());
        existingCollaborators.removeAll(data.getCollaboratorEmails());
        if (isQuoteCommonApiFeatureDisabled()) {
            cart.setCollaboratorEmails(existingCollaborators);
            getModelService().save(cart);
            return Boolean.TRUE;
        } else {
            try {
                Transaction tx = Transaction.current();
                tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
                return (boolean) tx.execute(new TransactionBody() {
                    @Override
                    public Boolean execute() throws Exception {
                        cart.setCollaboratorEmails(
                            existingCollaborators);
                        getModelService().save(cart);
                        getPartnerSapCpqQuoteService().removeCollaboratorsToCpq(
                            cart, data.getCollaboratorEmails());
                        return true;
                    }
                });
            } catch (Exception e) {
                LOG.error(
                    String.format(COLLAB_ADD_FAILURE, cart.getQuoteReference().getCode(),
                        e.getMessage()), e);
            }
        }

        return Boolean.FALSE;

    }


    /**
     * Checks whether the Common Quote API feature is disabled based on the configuration setting.
     *
     * <p>This method retrieves the value of the configuration property
     * {@code CPQ_COMMON_QUOTE_API_FEATURE_DISABLED}. If the property is not explicitly set, it
     * defaults to {@code true}, meaning the feature is considered disabled by default.</p>
     *
     * @return {@code true} if the Common Quote API feature is disabled or not configured;
     * {@code false} if it is explicitly enabled in the configuration
     */
    public boolean isQuoteCommonApiFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(CPQ_QUOTE_COMMON_API_FEATURE_DISABLED, false);
    }

    public Converter<PartnerOverrideEntryPriceData, PartnerCpqPricingDetailModel> getPartnerCartEntryPricingDetailsReverseConverter() {
        return partnerCartEntryPricingDetailsReverseConverter;
    }

    public Converter<PartnerOverrideHeaderPriceData, PartnerCpqHeaderPricingDetailModel> getPartnerCartHeaderPricingDetailsReverseConverter() {
        return partnerCartHeaderPricingDetailsReverseConverter;
    }

    protected CommerceCartService getCommerceCartService() {
        return commerceCartService;
    }

    protected Converter<AddToCartParams, CommerceCartParameter> getCommerceCartParameterConverter() {
        return commerceCartParameterConverter;
    }

    protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter() {
        return cartModificationConverter;
    }

    public PidCartFactory getPidCartFactory() {
        return pidCartFactory;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public PartnerProcessService getPartnerProcessService() {
        return partnerProcessService;
    }


    public PartnerOverridePriceValidator getPartnerOverridePriceValidator() {
        return partnerOverridePriceValidator;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }
    public PartnerCommerceCartService getPartnerCommerceCartService() {
        return partnerCommerceCartService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }    public PartnerSapCpqQuoteService getPartnerSapCpqQuoteService() {
        return partnerSapCpqQuoteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}
