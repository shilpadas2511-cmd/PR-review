/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import com.ibm.commerce.partner.facades.order.PartnerCartFacade;
import com.ibm.commerce.partner.facades.specialbidreason.PartnerSpecialBidReasonFacade;
import com.ibm.commerce.partner.occ.v2.helper.PartnerQuoteHelper;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsDataListData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonListData;
import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideEntryPriceWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideHeaderPriceWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.partnerquestions.PartnerQuestionsListWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.specialbidreason.PartnerSpecialBidReasonListWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "special bid controller")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
public class PartnerSpecialBidController extends PartnerBaseController {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerSpecialBidController.class);

    @Resource(name = "partnerSpecialBidFacade")
    private PartnerSpecialBidReasonFacade specialBidReasonFacade;

    @Resource(name = "partnerQuoteHelper")
    protected PartnerQuoteHelper partnerQuoteHelper;
    @Resource(name = "partnerCartFacade")
    private PartnerCartFacade partnerCartFacade;

    @Resource(name = "partnerOverrideHeaderValidator")
    private Validator partnerOverrideHeaderValidator;


    @Resource(name = "partnerOverrideEntryValidator")
    private Validator partnerOverrideEntryValidator;

    @Resource(name = "ibmCommonConfigurationService")
    private ConfigurationService ibmCommonConfigurationService;


    @GetMapping(value = "/carts/{cartId}/specialbid/specialbidreasons", produces = APPLICATION_JSON_VALUE)
    @Operation(operationId = "specialBidReason", summary = "Creates special bid reason list", description = "Creates special bid reason /{baseSiteId}/users/{userId}/carts/{cartId}/specialBid/specialBidReasons")
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PartnerSpecialBidReasonListWsDTO getAllSpecialBidReasonDetailsWithSelection(
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<PartnerSpecialBidReasonData> specialBidReasonDataList = specialBidReasonFacade.getAllSpecialBidReasonDetailsWithSelection();
        return getDataMapper().map(getSpecialBidReasonDataList(specialBidReasonDataList),
            PartnerSpecialBidReasonListWsDTO.class, fields);
    }

    @GetMapping(value = "/specialbid/specialbidreasons", produces = APPLICATION_JSON_VALUE)
    @Operation(operationId = "specialBidReason", summary = "Fetches all special bid reason list", description = "Creates special bid reason /{baseSiteId}/users/{userId}/carts/{cartId}/specialBid/specialBidReasons")
    @ResponseBody
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PartnerSpecialBidReasonListWsDTO getAllSpecialBidReasonDetails(
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<PartnerSpecialBidReasonData> specialBidReasonDataList = specialBidReasonFacade.getAllSpecialBidReasonDetails();
        return getDataMapper().map(getSpecialBidReasonDataList(specialBidReasonDataList),
            PartnerSpecialBidReasonListWsDTO.class, fields);
    }

    @PostMapping(value = "/carts/{cartId}/specialbid", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "save special bid information", hidden = true, summary = "save and validate the special bid information", description = "save and validate the special bid information to Cart")
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void saveSpecialBidDetails(
        @Parameter(description = "Base site identifier.", required = true) @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "Cart DTO", required = true) @RequestBody @Nonnull final CartWsDTO cartWsDTO)
        throws CommerceCartModificationException {

        final boolean isMultipleSpecialBidDisabled = getIbmCommonConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);

        if (isMultipleSpecialBidDisabled) {
            if (Objects.nonNull(cartWsDTO.getSpecialBidReason()) && StringUtils.isNotBlank(
                cartWsDTO.getSpecialBidReason().getCode()) && StringUtils.isNotBlank(
                cartWsDTO.getSpecialBidBusinessJustification())) {
                partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(
                    cartWsDTO.getSpecialBidReason().getCode(),
                    cartWsDTO.getSpecialBidBusinessJustification());
            }

        }else{
            partnerQuoteHelper.saveJustification(cartWsDTO.getSpecialBidBusinessJustification());
        }

    }

    @PostMapping(value = "/carts/{cartId}/specialbid/reasons", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "save special bid reasons and fetch related questions", hidden = true, summary = "save special bid reasons and fetch related questions", description = "save special bid reasons and fetch related questions")
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PartnerQuestionsListWsDTO saveAndFetch(
        @Parameter(description = "Base site identifier.", required = true) @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "Cart DTO", required = true) @RequestBody @Nonnull final CartWsDTO cartWsDTO)
        throws CommerceCartModificationException {

        final CartData cartData = getDataMapper().map(cartWsDTO, CartData.class);

        final PartnerQuestionsDataListData allPartnerQuestions = getAllPartnerQuestions(
            partnerQuoteHelper.saveAndFetchQuestionsForReasons(cartData.getSpecialBidReasons()));
        return getDataMapper().map(allPartnerQuestions, PartnerQuestionsListWsDTO.class, fields);
    }

    @PostMapping(value = "/carts/{cartId}/specialbid/justification/save", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "save special bid reasons and fetch related questions", hidden = true, summary = "save special bid reasons and fetch related questions", description = "save special bid reasons and fetch related questions")
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void saveJustification(
        @Parameter(description = "Base site identifier.", required = true) @PathVariable final String baseSiteId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "Cart DTO", required = true) @RequestBody @Nonnull final CartWsDTO cartWsDTO)
        throws CommerceCartModificationException {

        partnerQuoteHelper.saveJustification(cartWsDTO.getSpecialBidBusinessJustification());
    }

    @Operation(operationId = "updateCartHeaderPrice", hidden = true, summary = "override prices on cart header", description = "edit price or discount on cart header level")
    @PostMapping(value = "/carts/{cartId}/specialbid/overrideheaderpricing", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void overrideCartHeaderPrice(
        @Parameter(description = "object containing prices that need to be override") @RequestBody final PartnerOverrideHeaderPriceWsDTO partnerOverrideHeaderPriceWsDTO)
        throws CommerceCartModificationException {
        final PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData = convertToCartHeaderData(
            partnerOverrideHeaderPriceWsDTO);
        validate(partnerOverrideHeaderPriceWsDTO, "partnerOverrideHeaderPriceWsDTO",
            getPartnerOverrideHeaderValidator());
        partnerCartFacade.updateHeaderPriceDetails(partnerOverrideHeaderPriceData);
    }

    @Operation(operationId = "updateCartEntryPrice", hidden = true, summary = "override prices on cart header", description = "edit price or discount on cart entry level")
    @PostMapping(value = "/carts/{cartId}/specialbid/overrideentrypricing", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void overrideCartEntryPrice(
        @Parameter(description = "entry containing price and discounts.") @RequestBody final PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO)
        throws CommerceCartModificationException {
        final PartnerOverrideEntryPriceData partnerOverrideEntryPriceData = convertToCartEntryData(
            partnerOverrideEntryPriceWsDTO);
        validate(partnerOverrideEntryPriceWsDTO, "partnerOverrideEntryPriceWsDTO",
            getPartnerOverrideEntryValidator());
        partnerCartFacade.updateEntryPriceDetails(partnerOverrideEntryPriceData);
    }

    protected PartnerOverrideHeaderPriceData convertToCartHeaderData(
        final PartnerOverrideHeaderPriceWsDTO partnerOverrideHeaderPriceWsDTO) {
        final PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData = new PartnerOverrideHeaderPriceData();
        getDataMapper().map(partnerOverrideHeaderPriceWsDTO, partnerOverrideHeaderPriceData);
        return partnerOverrideHeaderPriceData;
    }

    protected PartnerOverrideEntryPriceData convertToCartEntryData(
        final PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO) {
        final PartnerOverrideEntryPriceData partnerOverrideEntryPriceData = new PartnerOverrideEntryPriceData();
        getDataMapper().map(partnerOverrideEntryPriceWsDTO, partnerOverrideEntryPriceData);
        return partnerOverrideEntryPriceData;
    }


    public Validator getPartnerOverrideHeaderValidator() {
        return partnerOverrideHeaderValidator;
    }

    public Validator getPartnerOverrideEntryValidator() {
        return partnerOverrideEntryValidator;
    }

    private PartnerSpecialBidReasonListData getSpecialBidReasonDataList(
        List<PartnerSpecialBidReasonData> specialBidReasonDataList) {
        final PartnerSpecialBidReasonListData partnerSpecialBidReasonListData = new PartnerSpecialBidReasonListData();
        partnerSpecialBidReasonListData.setSpecialBidReasons(specialBidReasonDataList);
        return partnerSpecialBidReasonListData;
    }

    public ConfigurationService getIbmCommonConfigurationService() {
        return ibmCommonConfigurationService;
    }

    private PartnerQuestionsDataListData getAllPartnerQuestions(
        List<PartnerQuestionsData> partnerQuestionsList) {
        final PartnerQuestionsDataListData qustionsList = new PartnerQuestionsDataListData();
        qustionsList.setPartnerQuestions(partnerQuestionsList);
        return qustionsList;
    }

}
