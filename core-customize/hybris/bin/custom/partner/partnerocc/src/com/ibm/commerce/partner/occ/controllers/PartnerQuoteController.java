/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.ibm.commerce.partner.occ.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.common.base.Preconditions;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.facades.quote.PartnerQuoteFacade;
import com.ibm.commerce.partner.occ.v2.helper.PartnerQuoteHelper;
import com.ibm.commerce.partnerwebservicescommons.dto.approvalComments.PartnerCommentsListWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestWsDTO;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bocc.exceptions.QuoteException;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;
import de.hybris.platform.commercefacades.util.CommerceCartMetadataUtils;
import de.hybris.platform.commercefacades.util.builder.CommerceCartMetadataBuilder;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceQuoteExpirationTimeException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.comments.CommentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.comments.CreateCommentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteActionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteDiscountWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteMetadataWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteStarterWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotes")
@Tag(name = "Quotes")
public class PartnerQuoteController extends PartnerBaseController {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerQuoteController.class);

    @Resource
    private Validator quoteNameValidator;
    @Resource
    private Validator quoteDescriptionValidator;
    @Resource
    private Validator discountTypeValidator;
    @Resource
    private Validator quoteCommentValidator;

    @Autowired
    protected PartnerQuoteHelper partnerQuoteHelper;
    @Resource
    private PartnerQuoteFacade defaultPartnerQuoteFacade;

    @Resource(name = "partnerImportQuoteFileValidator")
    private Validator partnerImportQuoteFileValidator;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @RequestMappingOverride
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @Operation(summary = "Creates a quote.", description = "Creates a quote by linking a cart using the cart identifier (cartId) to the quote. To trigger a requote, provide a value to the quoteCode parameter, instead of the cartId parameter inside the request body. The response body will contain the new data for the quote.", operationId = "createQuote")
    @ApiBaseSiteIdAndUserIdParam
    public QuoteWsDTO createQuote(
        @Parameter(description = "Object representing ways of creating new quote - by cartId for creating a new quote from the cart, by quoteCode for the requote action ", required = true) @RequestBody @Nonnull @Valid final QuoteStarterWsDTO quoteStarter,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
        throws VoucherOperationException, CommerceCartModificationException {
        final boolean quoteCodePresent = StringUtils.isNotEmpty(quoteStarter.getQuoteCode());
        final boolean cartIdPresent = StringUtils.isNotEmpty(quoteStarter.getCartId());

        if (cartIdPresent && !quoteCodePresent) {
            return getQuoteHelper().initiateQuote(quoteStarter.getCartId(), fields);
        }
        if (!cartIdPresent && quoteCodePresent) {
            return getQuoteHelper().requote(quoteStarter.getQuoteCode(), fields);
        }
        throw new IllegalArgumentException("Either cartId or quoteCode must be provided");
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @RequestMappingOverride
    @ResponseStatus(HttpStatus.OK)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @Operation(summary = "Retrieves all quotes for a customer.", description = "Retrieves all quote details associated with a customer. The response may display the results across multiple pages, when applicable.", operationId = "getQuotes")
    @ApiBaseSiteIdAndUserIdParam
    public QuoteListWsDTO getQuotes(
        @Parameter(description = "Current result page. Default value is 0.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
        @Parameter(description = "Number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
        @Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
        @ApiFieldsParam(defaultValue = FieldSetLevelHelper.BASIC_LEVEL) @RequestParam(required = false, defaultValue = FieldSetLevelHelper.BASIC_LEVEL) final String fields) {
        final PageableData pageableData = new PageableData();
        pageableData.setPageSize(pageSize);
        pageableData.setCurrentPage(currentPage);
        pageableData.setSort(sort);
        return getQuoteHelper().getQuotes(pageableData, fields);
    }

    @RequestMappingOverride
    @GetMapping(value = "/{quoteCode}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the quote.", description = "Retrieves the details of a quote. To get entryGroup information, set fields value as follows: fields=entryGroups(BASIC), fields=entryGroups(DEFAULT), or fields=entryGroups(FULL).", operationId = "getQuote")
    @ApiBaseSiteIdAndUserIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public QuoteWsDTO getQuote(
        @Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        return getQuoteHelper().getQuote(quoteCode, fields);
    }

    @RequestMappingOverride
    @PatchMapping(value = "/{quoteCode}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Updates the quote.", description = "Updates the name, description, or expiration date of the quote.", operationId = "updateQuote")
    @ApiBaseSiteIdAndUserIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void updateQuote(
        @Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "Updated name, description or expiry date of the quote", required = true) @RequestBody @Nonnull @Valid final QuoteMetadataWsDTO metadata) {
        final QuoteUserType userType = getQuoteHelper().getCurrentQuoteUserType().orElse(null);
        if (QuoteUserType.BUYER.equals(userType)) {
            updateNameAndDescription(metadata, quoteCode);
        } else if (QuoteUserType.SELLER.equals(userType)) {
            updateExpirationTime(metadata, quoteCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @RequestMappingOverride
    @PutMapping(value = "/{quoteCode}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Updates the quote.", description = "Updates the name, description, and expiration date of the quote.", operationId = "replaceQuote")
    @ApiBaseSiteIdAndUserIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void replaceQuote(
        @Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "Updated name, description or expiry date of the quote", required = true) @RequestBody @Nonnull @Valid final QuoteMetadataWsDTO metadata) {
        final QuoteUserType userType = getQuoteHelper().getCurrentQuoteUserType().orElse(null);
        if (QuoteUserType.BUYER.equals(userType)) {
            replaceNameAndDescription(metadata, quoteCode);
        } else if (QuoteUserType.SELLER.equals(userType)) {
            replaceExpirationTime(metadata, quoteCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @RequestMappingOverride
    @PostMapping(value = "/{quoteCode}/comments", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a comment for a quote.", description = "Creates a comment for a quote. Text is added in the request body.", operationId = "createCommentForQuote")
    @ApiBaseSiteIdAndUserIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public void createCommentForQuote(
        @Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "Text of the comment", required = true) @RequestBody @Nonnull @Valid final CreateCommentWsDTO comment) {
        validate(comment, "text", getQuoteCommentValidator());
        getQuoteHelper().addCommentToQuote(quoteCode, comment.getText());
    }

    @RequestMappingOverride
    @PostMapping(value = "/{quoteCode}/action", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Creates workflow actions for the quote.", description = "Creates workflow action during the quote editing process. Possible values are: CANCEL, SUBMIT, EDIT, CHECKOUT, APPROVE, or REJECT.", operationId = "performQuoteAction")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public void performQuoteAction(
        @Parameter(description = "Code of the quote.", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "The action with the quote. The quote action field is mandatory.", required = true) @RequestBody @Nonnull @Valid final QuoteActionWsDTO quoteAction)
        throws VoucherOperationException, CommerceCartModificationException {
        if (quoteAction.getAction() == null) {
            throw new IllegalArgumentException("Provided action cannot be null");
        }

        final String action = quoteAction.getAction().toUpperCase(Locale.ENGLISH);
        switch (action) {
            case "SUBMIT":
                getQuoteHelper().submitQuote(quoteCode);
                break;

            case "CANCEL":
                getQuoteHelper().cancelQuote(quoteCode);
                break;

            case "APPROVE":
                getQuoteHelper().approveQuote(quoteCode);
                break;

            case "REJECT":
                getQuoteHelper().rejectQuote(quoteCode);
                break;

            case "CHECKOUT":
                getQuoteHelper().acceptAndPrepareCheckout(quoteCode);
                break;

            case "EDIT":
                getQuoteHelper().enableQuoteEdit(quoteCode);
                break;

            default:
                throw new IllegalArgumentException("Provided action not supported");
        }
    }

    @RequestMappingOverride
    @PostMapping(value = "/{quoteCode}/entries/{entryNumber}/comments", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a comment for a quote entry.", description = "Creates a comment for a quote entry. Text is added in the request body.", operationId = "createQuoteEntryComment")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public void createQuoteEntryComment(
        @Parameter(description = "Code of the quote.", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero.", required = true) @PathVariable @Nonnull @Valid final long entryNumber,
        @Parameter(description = "Text of the comment", required = true) @RequestBody @Nonnull @Valid final CreateCommentWsDTO comment) {
        validate(comment, "text", quoteCommentValidator);
        getQuoteHelper().addCommentToQuoteEntry(quoteCode, entryNumber, comment.getText());
    }

    @RequestMappingOverride
    @PostMapping(value = "/{quoteCode}/discounts", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @Operation(summary = "Creates a discount for an existing quote.", description = "Creates a discount for an open quote. Only sellers are allowed to apply a discount to a quote. The types of discount are: PERCENT, for discount by percentage; ABSOLUTE, for discount by amount; and TARGET, for discount by adjustment of the total value.", operationId = "createQuoteDiscount")
    @ApiBaseSiteIdAndUserIdParam
    public void createQuoteDiscount(
        @Parameter(description = "Code of the quote.", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @Parameter(description = "Discount applied to the quote - discountType for type of the discount, discountRate for value of the discount ", required = true) @RequestBody @Nonnull @Valid final QuoteDiscountWsDTO quoteDiscount) {
        validate(new String[]{quoteDiscount.getDiscountType()}, "discountType",
            getDiscountTypeValidator());
        getQuoteHelper().applyDiscount(quoteCode, quoteDiscount.getDiscountType(),
            quoteDiscount.getDiscountRate());
    }
    @RequestMappingOverride
    @GetMapping(value = "/{quoteCode}/approvalcomments", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieves the approval comments.", description = "Retrieves the approval comments of a quote. To get entryGroup information, set fields value as follows: fields=entryGroups(BASIC), fields=entryGroups(DEFAULT), or fields=entryGroups(FULL).", operationId = "fetchApprovalComments")
    @ApiBaseSiteIdAndUserIdParam
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    public PartnerCommentsListWsDTO fetchApprovalComments(
        @Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) throws IbmWebServiceFailureException, EntityValidationException {
        List<CommentData> commentsListData = defaultPartnerQuoteFacade.fetchApprovalComments(quoteCode);
        return convertWsDTO(commentsListData);
    }

    @PostMapping(value = "/{quoteCode}/clone", consumes = APPLICATION_JSON_VALUE)
    @RequestMappingOverride
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @Operation(summary = "Clone a quote.", description = "Duplicate a quote on the basis of draft and submitted status", operationId = "duplicateQuote")
    @ApiBaseSiteIdAndUserIdParam
    public QuoteWsDTO duplicateQuote(
        @Parameter(description = "Object representing ways of creating new quote - by cartId for creating a new quote from the cart, by quoteCode for the requote action ", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @RequestBody @Nonnull final QuoteWsDTO quoteWsDTO) {
        final boolean quoteCodePresent = StringUtils.isNotEmpty(quoteCode);
        final boolean quoteNamePresent = StringUtils.isNotEmpty(quoteWsDTO.getName());

        if (quoteCodePresent && quoteNamePresent) {
            return getQuoteHelper().cloneQuoteModel(quoteCode, quoteWsDTO.getName(), fields);
        } else {
            throw new IllegalArgumentException(" Either quoteCode or quoteName is missing");
        }
    }

    @Operation(summary = "updates status of quote.", description = "Updates Quote Status ", operationId = "quoteStatusUpdate")
    @PostMapping(value = "/{quoteCode}/quotestatusupdate", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public void quoteStatusUpdate(@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
        @RequestBody @Nonnull final QuoteWsDTO quoteWsDTO) {
        final QuoteState state = QuoteState.valueOf(quoteWsDTO.getState());

        if(StringUtils.isNotBlank(quoteCode) && Objects.nonNull(state)) {
             getQuoteHelper().updateQuoteStatus(quoteCode, state);
        } else {
            throw new IllegalArgumentException(" Mandatory parameter missing");
        }
    }

    @Operation(summary = "quote creation from imported file.", description = "quote creation from imported file", operationId = "importQuoteCreation")
    @PostMapping(value = "/createimportquote", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public QuoteWsDTO importQuoteCreation(
        @Parameter(description = "Requested import file details", required = true) @RequestBody @Nonnull @Valid final PartnerImportQuoteFileRequestWsDTO partnerImportQuoteFileRequestWsDTO,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
        throws VoucherOperationException, CommerceCartModificationException {
        validate(partnerImportQuoteFileRequestWsDTO, "partnerImportQuoteFileRequestWsDTO",
            partnerImportQuoteFileValidator);
        PartnerImportQuoteFileRequestData fileRequestData = getDataMapper().map(
            partnerImportQuoteFileRequestWsDTO, PartnerImportQuoteFileRequestData.class);
        return getDataMapper().map(defaultPartnerQuoteFacade.createImportedQuote(fileRequestData), QuoteWsDTO.class, fields);
    }

    protected void updateNameAndDescription(final QuoteMetadataWsDTO metadata,
        final String quoteCode) {
        final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
        Preconditions.checkArgument(metadata.getExpirationTime() == null,
            "User not allowed to change expiration date");
        if (metadata.getName() != null) // name is provided and quote needs to be updated
        {
            validate(metadata, "name", getQuoteNameValidator());
            builder.name(Optional.of(metadata.getName()));
        }
        if (metadata.getDescription()
            != null) // description is provided and quote needs to be updated
        {
            validate(metadata, "description", getQuoteDescriptionValidator());
            builder.description(Optional.of(metadata.getDescription()));
        }
        final CommerceCartMetadata cartMetadata = builder.build();
        getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
    }

    protected void updateExpirationTime(final QuoteMetadataWsDTO metadata, final String quoteCode) {
        final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
        Preconditions.checkArgument(metadata.getName() == null && metadata.getDescription() == null,
            "User not allowed to change name or description");
        if (metadata.getExpirationTime()
            != null) // expiration time is provided and quote needs to be updated
        {
            builder.expirationTime(Optional.of(metadata.getExpirationTime()));
        }
        final CommerceCartMetadata cartMetadata = builder.build();
        getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
    }

    protected void replaceNameAndDescription(final QuoteMetadataWsDTO metadata,
        final String quoteCode) {
        final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
        Preconditions.checkArgument(metadata.getExpirationTime() == null,
            "User not allowed to change expiration date");
        if (metadata.getName() != null) {
            validate(metadata, "name", getQuoteNameValidator());
            builder.name(Optional.of(metadata.getName()));
            if (metadata.getDescription()
                != null) // name and description are provided and need to be replaced
            {
                validate(metadata, "description", getQuoteDescriptionValidator());
                builder.description(Optional.of(metadata.getDescription()));
            } else {
                builder.description(
                    Optional.of("")); // description should be cleared when only providing name
            }
            final CommerceCartMetadata cartMetadata = builder.build();
            getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
        } else if (metadata.getDescription() != null) {
            throw new IllegalArgumentException(
                "Name is required."); // an error message should be produced when only providing description
        } else {
            throw new IllegalArgumentException("Please provide the fields you want to edit");
        }
    }

    protected void replaceExpirationTime(final QuoteMetadataWsDTO metadata,
        final String quoteCode) {
        final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
        Preconditions.checkArgument(metadata.getName() == null && metadata.getDescription() == null,
            "User not allowed to change name or description");
        if (metadata.getExpirationTime()
            == null) // expirationTime is provided and needs to be replaced
        {
            builder.removeExpirationTime(true); // expirationTime needs to be removed
        } else {
            builder.expirationTime(Optional.of(metadata.getExpirationTime()));
        }
        final CommerceCartMetadata cartMetadata = builder.build();
        getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
    }

     protected PartnerCommentsListWsDTO convertWsDTO(final List<CommentData> commentDataList){
         List<CommentWsDTO> commentWsDTOList = commentDataList.stream()
             .map(commentData -> {
                 CommentWsDTO commentWsDTO = new CommentWsDTO();
                 getDataMapper().map(commentData, commentWsDTO);
                 return commentWsDTO;
             }).collect(Collectors.toList());
         PartnerCommentsListWsDTO partnerCommentsListWsDTO = new PartnerCommentsListWsDTO();
         partnerCommentsListWsDTO.setComments(commentWsDTOList);
         return partnerCommentsListWsDTO;
    }

    public PartnerQuoteHelper getQuoteHelper() {
        return partnerQuoteHelper;
    }

    public Validator getQuoteNameValidator() {
        return quoteNameValidator;
    }

    protected void setQuoteNameValidator(final Validator quoteNameValidator) {
        this.quoteNameValidator = quoteNameValidator;
    }

    public Validator getQuoteDescriptionValidator() {
        return quoteDescriptionValidator;
    }

    protected void setQuoteDescriptionValidator(final Validator quoteDescriptionValidator) {
        this.quoteDescriptionValidator = quoteDescriptionValidator;
    }

    public Validator getQuoteCommentValidator() {
        return quoteCommentValidator;
    }

    protected void setQuoteCommentValidator(final Validator quoteCommentValidator) {
        this.quoteCommentValidator = quoteCommentValidator;
    }

    public Validator getDiscountTypeValidator() {
        return discountTypeValidator;
    }

    protected void setDiscountTypeValidator(final Validator discountTypeValidator) {
        this.discountTypeValidator = discountTypeValidator;
    }
}