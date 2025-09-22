package com.ibm.commerce.partner.occ.controllers;

import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.SaveCartResultWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.AccessDeniedException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservices.core.order.data.CartDataList;
import de.hybris.platform.commercewebservicescommons.dto.order.CartListWsDTO;
import java.util.ArrayList;
import java.util.List;


import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import de.hybris.platform.commercefacades.user.UserFacade;

/**
 * Controller for Saved Carts
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@Tag(name = "Save Cart")
public class PartnerSavedCartsController extends PartnerBaseController {

    @Resource(name = "saveCartFacade")
    private SaveCartFacade saveCartFacade;
    @Resource(name = "userFacade")
    private UserFacade userFacade;
    @Resource(name = "commerceWebServicesCartFacade2")
    private CartFacade cartFacade;

    @PostMapping(value = "/{cartId}/clonesavedcart")
    @ResponseBody
    @RequestMappingOverride
    @Operation(operationId = "doCartClone", summary = "Creates a clone of a saved cart.", description = "Creates a clone of a saved cart. Customers can provide a name and a description for the cloned cart even though they aren't mandatory parameters.")
    @ApiBaseSiteIdAndUserIdParam
    public SaveCartResultWsDTO doCartClone(
        @Parameter(description = "Cart identifier: cart code for logged-in user, cart GUID for anonymous user, or 'current' for the last modified cart.", required = true) @PathVariable final String cartId,
        @Parameter(description = "Name of the cloned cart.") @RequestParam(value = "name", required = false) final String name,
        @Parameter(description = "Description of the cloned cart.") @RequestParam(value = "description", required = false) final String description,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
        throws CommerceSaveCartException {
        final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
        parameters.setCartId(cartId);
        parameters.setName(name);
        parameters.setEnableHooks(Boolean.TRUE);
        parameters.setDescription(description);

        final CommerceSaveCartResultData result = saveCartFacade.cloneSavedCart(parameters);
        return getDataMapper().map(result, SaveCartResultWsDTO.class, fields);
    }

    /**
     * Overriding the OOTB API, to use Default PageSize more than 20  payload because not able to add
     * custom attributes in the OOTB OrgUserRegistrationDataWsDTO
     *
     * @param savedCartsOnly
     */

    @GetMapping
    @RequestMappingOverride(priorityProperty = "b2b.PartnerSavedCartsController.getCarts.priority")
    @ResponseBody
    @Operation(operationId = "getCarts", summary = "Retrieves the carts of a customer.", description = "Retrieves a list of all the carts associated with a customer.")
    @ApiBaseSiteIdAndUserIdParam
    public CartListWsDTO getCarts(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                  @Parameter(description = "If the value is true, only saved carts are returned.") @RequestParam(defaultValue = "false") final boolean savedCartsOnly,
                                  @Parameter(description = "Pagination for savedCartsOnly. Default value is 0.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                  @Parameter(description = "Number of results returned per page if the savedCartsOnly parameter is set to true. Default value: 20.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                  @Parameter(description = "Sorting method applied to the return results if the savedCartsOnly parameter is set to true.") @RequestParam(required = false) final String sort)
    {
        if (getUserFacade().isAnonymousUser())
        {
            throw new AccessDeniedException("Access is denied");
        }

        final CartDataList cartDataList = new CartDataList();

        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(currentPage);
        pageableData.setPageSize(pageSize);
        pageableData.setSort(sort);
        SearchPageData<CartData> result = saveCartFacade.getSavedCartsForCurrentUser(pageableData, null);
        final List<CartData> allCarts = new ArrayList<>(
                result.getResults());
        if (!savedCartsOnly)
        {
            allCarts.addAll(getCartFacade().getCartsForCurrentUser());
        }
        cartDataList.setCarts(allCarts);
        cartDataList.setPagination(result.getPagination());

        return getDataMapper().map(cartDataList, CartListWsDTO.class, fields);
    }


    public CartFacade getCartFacade() {
        return cartFacade;
    }
    public UserFacade getUserFacade() {
        return userFacade;
    }

}
