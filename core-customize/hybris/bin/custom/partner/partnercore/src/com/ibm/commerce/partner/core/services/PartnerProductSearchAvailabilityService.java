package com.ibm.commerce.partner.core.services;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import java.util.Collection;
import java.util.List;

/*
 *  PartnerProductSearchAvailabilityStrategy interface to check the search availability for product.
 */
public interface PartnerProductSearchAvailabilityService {

    /**
     * interface to check the search availability for product
     *
     * @param ibmVariantProductModel
     * @param deploymentType
     * @return
     */
    boolean isSearchAvailabilityForProduct(IbmVariantProductModel ibmVariantProductModel,
        String deploymentType);

    /**
     * this method use to get the variants based on the deployment types SaaS and Bespoke
     * @param productModel
     * @return Collection<VariantProductModel>
     */
    Collection<VariantProductModel> getVariants(final ProductModel productModel);

    /**
     * Filters list of Part Products based on Approval Status and Seller Audience Mask.
     * @param partProducts
     * @return
     */
    List<IbmPartProductModel> filterSoftwareQuotingProducts( List<IbmPartProductModel> partProducts);

}
