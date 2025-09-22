/**
 *
 */
package com.ibm.commerce.partner.occ.controllers;

import static de.hybris.platform.b2bocc.constants.B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.data.order.QuoteCollaboratorsData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.facades.order.PartnerCartFacade;
import com.ibm.commerce.partner.facades.order.PartnerCheckoutFacade;
import com.ibm.commerce.partner.facades.order.PartnerProvisionFormFacade;
import com.ibm.dto.order.IbmAddToCartParamsWsDTO;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bocc.constants.B2boccConstants;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commerceservicescommons.dto.order.QuoteCollaboratorsWsDTO;
import de.hybris.platform.commercewebservices.core.skipfield.SkipCartFieldValueSetter;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * PartnerB2BCartsController.To expose new b2b API's related to cart and to override OOTB
 * B2BCartsController API's.
 */
@RestController
@ApiVersion("v2")
@Tag(name = "B2B Partner Carts")
public class PartnerB2BCartsController extends PartnerBaseController {

    protected static final String API_COMPATIBILITY_B2B_CHANNELS = "api.compatibility.b2b.channels";

    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;

    @Resource(name = "partnerCartFacade")
    private PartnerCartFacade partnerCartFacade;

    @Resource(name = "checkoutFacade")
    private CheckoutFacade checkoutFacade;

    @Resource(name = "partnerAgreementValidator")
    private Validator partnerAgreementValidator;

    @Resource(name = "opportunityCodeValidator")
    private Validator opportunityCodeValidator;

    @Resource(name = "skipCartFieldValueSetter")
    private SkipCartFieldValueSetter skipCartFieldValueSetter;

    @Resource(name = "partnerProvisionFormFacade")
    private PartnerProvisionFormFacade partnerProvisionFormFacade;

    @Operation(operationId = "addOrgCartEntry", hidden = true, summary = "Adds Partner Details to Cart", description = "Adds Partner Details to Cart")
    @RequestMappingOverride
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PostMapping(value = B2boccConstants.OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH
        + "/carts/{cartId}/details", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO addCartDetails(
        @Parameter(description = "Base site identifier.", required = true) @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "Cart DTO") @RequestBody final IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO) {
        if (Objects.nonNull(ibmAddToCartParamsWsDTO.getAgreementDetail()) && StringUtils.isEmpty(
            ibmAddToCartParamsWsDTO.getQuoteExpirationDate())) {
            validate(ibmAddToCartParamsWsDTO.getAgreementDetail(), "ibmAddToCartParamsWsDTO",
                partnerAgreementValidator);
        }
        if (Objects.nonNull(
            ibmAddToCartParamsWsDTO.getOpportunity()) && Objects.nonNull(
            ibmAddToCartParamsWsDTO.getSoldThroughUnit())) {
            validate(ibmAddToCartParamsWsDTO, "ibmAddToCartParamsWsDTO",
                opportunityCodeValidator);
        }

        final IbmAddToCartParamsData ibmAddToCartParamsData = getDataMapper().map(
            ibmAddToCartParamsWsDTO, IbmAddToCartParamsData.class);

        getCheckoutFacade().updateIbmCartDetails(ibmAddToCartParamsData);

        return getDataMapper().map(getCartFacade().getCurrentCart(), CartWsDTO.class, fields);
    }

    @Operation(operationId = "doAddOrgCartEntries", summary = "Creates additional quantity of a product in the cart.", description = "Creates additional quantity of a product in the cart. Use this endpoint to add the quantities of the existing products. To update the products’ quantities or add new products to the cart, use PUT /{baseSiteId}/orgUsers/{userId}/carts/{cartId}/entries/")
    @RequestMappingOverride(priorityProperty = "partner.CartResource.addCartEntries.priority")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH
        + "/carts/{cartId}/entries/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartModificationListWsDTO addCartEntries(
        @Parameter(description = "Base site identifier.") @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "List of entries containing the amount to add and the product code or the entryNumber.") @RequestBody final OrderEntryListWsDTO entries) {

        final List<OrderEntryData> cartEntriesData = convertToData(entries);
        final List<CartModificationData> resultList = cartFacade.addOrderEntryList(cartEntriesData);

        return getDataMapper().map(getCartModificationDataList(resultList),
            CartModificationListWsDTO.class, fields);

    }

    @Operation(operationId = "replaceOrgCartEntries", summary = "Updates the quantity of the specified products in a cart.", description = "Adds specific products or overwrites the details of existing products in the cart, based either on the product code or the entry number. For existing products, attributes not provided in the request body will be defined again (set to null or default).")
    @RequestMappingOverride(priorityProperty = "partner.CartResource.updateCartEntries.priority")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PutMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH
        + "/carts/{cartId}/entries/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartModificationListWsDTO updateCartEntries(
        @Parameter(description = "Base site identifier.") @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "List of entries containing the amount to add and the product code or the entryNumber.") @RequestBody final OrderEntryListWsDTO entries) {
        final List<OrderEntryData> cartEntriesData = convertToData(entries);
        final List<CartModificationData> resultList = cartFacade.updateOrderEntryList(
            cartEntriesData);

        return getDataMapper().map(getCartModificationDataList(resultList),
            CartModificationListWsDTO.class, fields);
    }

    @Operation(summary = "Update Prices for Quote Cart.", description = "Updates the product Prices of the quote.", operationId = "updatePrices")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH  + "/carts/{cartId}/updatePrices", consumes =  {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void updatePrices(
        @Parameter(description = "Identifying code of the cart", required = true) @PathVariable @Nonnull @Valid final String cartId) {
        partnerCartFacade.updatePrices();

    }

    @GetMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}")
    @RequestMappingOverride(priorityProperty = "partner.CartResource.getCart.priority")
    @ResponseBody
    @Operation(operationId = "getCart", summary = "Retrieves a cart.", description = "Retrieves a cart using the cart identifier. To get entryGroup information, set fields value as follows: fields=entryGroups(BASIC), fields=entryGroups(DEFAULT), or fields=entryGroups(FULL).")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO getCart(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
    {
        skipCartFieldValueSetter.setValue(fields);
        partnerProvisionFormFacade.updateProvisionFormEditors();
        // CartMatchingFilter sets current cart based on cartId, so we can return cart from the session
        return getDataMapper().map(getCartFacade().getCurrentCart(), CartWsDTO.class, fields);
    }

    protected CartModificationDataList getCartModificationDataList(
        final List<CartModificationData> result) {
        final CartModificationDataList cartModificationDataList = new CartModificationDataList();
        cartModificationDataList.setCartModificationList(result);
        return cartModificationDataList;
    }

    protected List<OrderEntryData> convertToData(final OrderEntryListWsDTO entriesWS) {
        final List<OrderEntryData> entriesData = new ArrayList<>();

        for (final OrderEntryWsDTO entryDto : entriesWS.getOrderEntries()) {
            final OrderEntryData entryData = new OrderEntryData();
            getDataMapper().map(entryDto, entryData);
            entriesData.add(entryData);
        }
        return entriesData;
    }

    @Operation(summary = "Adds seller collaborators to quote cart.", description = "Adds seller collaborators to quote cart.", operationId = "addSellerCollaborators")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @PostMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH
        + "/carts/{cartId}/sellercollaborators", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void addCollaborator(
        @Parameter(description = "List of collaborator email ids to be added to quote cart.", required = true) @RequestBody final QuoteCollaboratorsWsDTO requestDTO) {
        final QuoteCollaboratorsData data = getDataMapper().map(requestDTO,
            QuoteCollaboratorsData.class);
        if (null == data || CollectionUtils.isEmpty(data.getCollaboratorEmails())
            || data.getCollaboratorEmails().stream()
            .anyMatch(email -> !EmailValidator.getInstance().isValid(email))
            || !partnerCartFacade.addCollaborator(data)) {
            throw new RequestParameterException(PartnercoreConstants.INVALID_REQUEST);
        }
    }

    /**
     *
     * This method removes seller collaborators to quote cart.
     * @param requestDTO
     */
    @Operation(summary = "removes seller collaborators to quote cart.", description = "Removes seller collaborators to quote cart.", operationId = "deleteSellerCollaborators")
    @SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2B_CHANNELS)
    @DeleteMapping(value = OCC_REWRITE_OVERLAPPING_BASE_SITE_USER_PATH
        + "/carts/{cartId}/sellercollaborators" ,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void removeCollaborator(
        @Parameter(description = "List of collaborator email ids to be added to quote cart.", required = true) @RequestBody @Nonnull final QuoteCollaboratorsWsDTO requestDTO) {
        final QuoteCollaboratorsData data = getDataMapper().map(requestDTO,
            QuoteCollaboratorsData.class);
        if (CollectionUtils.isEmpty(data.getCollaboratorEmails())||Boolean.FALSE.equals(partnerCartFacade.removeCollaborator(data))) {
            throw new RequestParameterException(PartnercoreConstants.INVALID_REQUEST);
        }
    }

    protected CartFacade getCartFacade() {
        return cartFacade;
    }


    /**
     * @return the partnerCheckoutFacade
     */
    public PartnerCheckoutFacade getCheckoutFacade() {
        return (PartnerCheckoutFacade) checkoutFacade;
    }

}
