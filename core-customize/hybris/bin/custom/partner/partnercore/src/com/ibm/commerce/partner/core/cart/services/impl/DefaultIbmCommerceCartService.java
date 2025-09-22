/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.cart.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.cart.dao.impl.DefaultIbmCommerceCartDao;
import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Service have methods related to the Cart
 */
public class DefaultIbmCommerceCartService extends DefaultCommerceCartService implements
    PartnerCommerceCartService {

    private DefaultIbmCommerceCartDao commerceCartDao;

    private PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;

    private final Converter<ProvisionFormResponseData, AbstractOrderModel> provisionFormResponseReverseConverter;


    private final Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestDataConverter;


    private final Converter<AbstractOrderModel, ProvisionFormItemsRequestData> provisionFormItemPatchRequestDataConverter;

    private final UserService userService;

    private final String provisionformErrorMessage;

    private ConfigurationService configurationService;

    private final CustomerEmailResolutionService customerEmailResolutionService;

    private final PartnerCartUpdateStrategy cartUpdateQuestionSelectionStrategy;

    private final Double cart1MilPriceValue;

    public DefaultIbmCommerceCartService(
        DefaultIbmCommerceCartDao commerceCartDao,
        Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestDataConverter,
        PartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService,
        Converter<ProvisionFormResponseData, AbstractOrderModel> provisionFormResponseReverseConverter,
        UserService userService, String provisionformErrorMessage,
        ConfigurationService configurationService,
        Converter<AbstractOrderModel, ProvisionFormItemsRequestData> provisionFormItemPatchRequestDataConverter,
        CustomerEmailResolutionService customerEmailResolutionService,
        final PartnerCartUpdateStrategy cartUpdateQuestionSelectionStrategy,
        final Double cart1MilPriceValue) {
        this.commerceCartDao = commerceCartDao;
        this.provisionFormRequestDataConverter = provisionFormRequestDataConverter;
        this.partnerProvisionFormOutboundIntegrationService=partnerProvisionFormOutboundIntegrationService;
        this.provisionFormResponseReverseConverter =provisionFormResponseReverseConverter;
        this.userService = userService;
        this.provisionformErrorMessage = provisionformErrorMessage;
        this.provisionFormItemPatchRequestDataConverter = provisionFormItemPatchRequestDataConverter;
        this.configurationService = configurationService;
        this.customerEmailResolutionService = customerEmailResolutionService;
        this.cartUpdateQuestionSelectionStrategy = cartUpdateQuestionSelectionStrategy;
        this.cart1MilPriceValue = cart1MilPriceValue;
    }

    /**
     * Retrieves a {@link CartModel} for the given cart code, current user, and site. This method
     * ensures that the cart is associated with one of the B2B unit groups to which the current user
     * belongs and is associated with the specified site.
     *
     * @param code            The cart code, must not be null.
     * @param currentUser     The current user for whom the cart is being retrieved. Must not be
     *                        null.
     * @param currentBaseSite The current base site for which the cart is searched. Must not be
     *                        null.
     */
    public CartModel getCartModelForCodeAndSiteIds(String code, UserModel currentUser, BaseSiteModel currentBaseSite) {
        List<IbmPartnerB2BUnitModel> siteIds = new ArrayList<>();
        validateParameterNotNullStandardMessage("quoteUserModel", currentUser);
        validateParameterNotNullStandardMessage("cartCode", code);
        validateParameterNotNullStandardMessage("site", currentBaseSite);
        getSiteIds((CustomerModel) currentUser, siteIds);
        String cartCode = determineCartCode(code, currentBaseSite);
        return getCommerceCartDao().getCartByCodeAndSiteIdsAndStore(cartCode, siteIds, currentBaseSite);
    }

    /**
     * create provision form using the call the conveters to convert request object and populate the
     * request object
     *
     * @param order
     */
    @Override
    public void createProvisionForm(AbstractOrderModel order) {
        ProvisionFormRequestData provisionFormRequestData = provisionFormRequestDataConverter.convert(
            order);
        try {
            ProvisionFormResponseData responseData = getPartnerProvisionFormOutboundIntegrationService().create(
                provisionFormRequestData);
            getProvisionFormResponseReverseConverter().convert(responseData, order);
        } catch (IbmWebServiceFailureException e) {
            createErrorProvisionForm(order);
        }
    }

    /**
     * Verifying the cart model's provision forms to determine whether it contains errors, in order
     * to decide whether to create or update via the API.
     *
     * @param order
     */
    @Override
    public void updateProvisionForm(AbstractOrderModel order) {
        if (order instanceof IbmPartnerCartModel cartModel) {
            if ((cartModel.getProvisionForms() == null) || (cartModel.getProvisionForms() != null
                && StringUtils.isNotEmpty(
                cartModel.getProvisionForms().getErrors()))) {
                createProvisionForm(cartModel);
                getModelService().save(cartModel);
            } else {
                try {
                    String cartId = cartModel.getProvisionForms().getCode();
                    ProvisionFormItemsRequestData provisionFormRequestData = getProvisionFormItemPatchRequestDataConverter().convert(
                        order);
                    ProvisionFormResponseData responseData = getPartnerProvisionFormOutboundIntegrationService().patch(
                        provisionFormRequestData, cartId);
                    getProvisionFormResponseReverseConverter().convert(responseData, order);
                } catch (IbmWebServiceFailureException e) {
                    createErrorProvisionForm(order);
                }
            }
        }
    }

    /**
     * These methods validate and update the details of the provision form.
     *
     * @param cart
     */
    @Override
    public void validateProvisionForms(CartModel cart) {
        if (getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)
            && cart.getQuoteReference() != null) {
            if (PartnerOrderUtils.checkSaasProduct(cart)) {
                updateProvisionForm((IbmPartnerCartModel) cart);
            } else if (cart instanceof IbmPartnerCartModel) {
                ((IbmPartnerCartModel) cart).setProvisionForms(null);
                getModelService().save(cart);
            }
        }
    }

    @Override
    public void updateQuestionSelections(final IbmPartnerCartModel cart) {
        getCartUpdateQuestionSelectionStrategy().update(cart);
    }

    @Override
    public boolean isCartValueAtLeast1M(final IbmPartnerCartModel cart) {
        if (CollectionUtils.isNotEmpty(cart.getPricingDetails())) {
            return cart.getPricingDetails().stream().anyMatch(
                price -> CpqPricingTypeEnum.FULL.getCode().equals(price.getPricingType())
                    && price.getTotalUSDExtendedPrice() >= getCart1MilPriceValue());
        }
        return false;
    }

    /**
     * Determines the appropriate cart code based on the given code and base site.
     * @param code the code of the cart. Must not be null.
     * @param currentBaseSite the base site associated with the cart. Must not be null.
     * @return the main cart code if the original code starts with the PID_CART prefix
     *         and the main cart code is available; otherwise, returns the original code.
     */
    protected String determineCartCode(String code, BaseSiteModel currentBaseSite) {
        if (code.startsWith(PartnercoreConstants.PID_CART)) {
            String mainCartCode = getMainCartCode(code, currentBaseSite);
            return mainCartCode != null ? mainCartCode : code;
        }
        return code;
    }

    /**
     * Retrieves the main cart code for a given cart based on the provided code and base site.
     *
     * @param code the code of the cart to look up. Must not be null.
     * @param currentBaseSite the base site associated with the cart. Must not be null.
     * @return the code of the main cart if found, or {@code null} if the cart does not exist,
     *         has no entries, or the associated order has no code.
     */
    protected String getMainCartCode(String code, BaseSiteModel currentBaseSite) {
        IbmPartnerPidCartModel pidCart = getCommerceCartDao().getPidCartByCodeAndStore(
            code, currentBaseSite);

        return Optional.ofNullable(pidCart)
            .map(cart -> cart.getEntries())
            .filter(entries -> !entries.isEmpty())
            .map(entries -> entries.get(0))
            .map(entry -> entry.getMasterEntry())
            .map(mainEntry -> mainEntry.getOrder())
            .map(order -> order.getCode())
            .orElse(null);
    }

    /**
     * Retrieving all site IDs associated with the customer
     *
     * @param customer
     * @param siteIds
     */
    protected void getSiteIds(final CustomerModel customer,
        final List<IbmPartnerB2BUnitModel> siteIds) {
        if (customer != null && CollectionUtils.isNotEmpty(customer.getGroups())) {
            customer.getGroups().stream().filter(IbmPartnerB2BUnitModel.class::isInstance)
                .map(IbmPartnerB2BUnitModel.class::cast).forEach(siteIds::add);
        }
    }

    @Override
    public DefaultIbmCommerceCartDao getCommerceCartDao() {
        return commerceCartDao;
    }

    /**
     * create provision forms with creation provision error form.
     *
     * @param order
     */

    protected void createErrorProvisionForm(AbstractOrderModel order) {
        if (order instanceof IbmPartnerCartModel cartModel) {
            PartnerProvisionFormsModel provisionForms = cartModel.getProvisionForms();
            // Check if provisionForms is null or its errors are null
            if (provisionForms == null || StringUtils.isEmpty(provisionForms.getErrors())) {
                provisionForms = createProvisionErrorForms(cartModel);
                cartModel.setProvisionForms(provisionForms);
                getModelService().save(cartModel);
            }
        }
    }

    /**
     * create provision form with error information.
     *
     * @param cartModel
     * @return PartnerProvisionFormsModel
     */
    protected PartnerProvisionFormsModel createProvisionErrorForms(IbmPartnerCartModel cartModel) {
        PartnerProvisionFormsModel provisionForms = getModelService().create(
            PartnerProvisionFormsModel.class);
        provisionForms.setErrors(getProvisionformErrorMessage());
        provisionForms.setAllowedEditUsers(getCustomerEmailResolutionService().getEmailForCustomer(
            (CustomerModel) getUserService().getCurrentUser()));
        provisionForms.setCode(cartModel.getCode());
        return provisionForms;
    }


    public Converter<AbstractOrderModel, ProvisionFormRequestData> getProvisionFormRequestDataConverter() {
        return provisionFormRequestDataConverter;
    }

    public PartnerProvisionFormOutboundIntegrationService getPartnerProvisionFormOutboundIntegrationService() {
        return partnerProvisionFormOutboundIntegrationService;
    }


    public Converter<ProvisionFormResponseData, AbstractOrderModel> getProvisionFormResponseReverseConverter() {
        return provisionFormResponseReverseConverter;
    }

    public String getProvisionformErrorMessage() {
        return provisionformErrorMessage;
    }

    public UserService getUserService() {
        return userService;
    }

    public Converter<AbstractOrderModel, ProvisionFormItemsRequestData> getProvisionFormItemPatchRequestDataConverter() {
        return provisionFormItemPatchRequestDataConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    public PartnerCartUpdateStrategy getCartUpdateQuestionSelectionStrategy() {
        return cartUpdateQuestionSelectionStrategy;
    }

    public Double getCart1MilPriceValue() {
        return cart1MilPriceValue;
    }
}
