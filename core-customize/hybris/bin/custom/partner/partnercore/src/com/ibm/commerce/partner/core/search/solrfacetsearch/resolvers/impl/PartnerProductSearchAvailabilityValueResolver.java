package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import com.ibm.commerce.partner.core.services.PartnerProductSearchAvailabilityService;
import com.ibm.commerce.partner.core.utils.PartnerProductDeploymentTypeUtils;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;


/**
 * This resolver class extends AbstractValueResolver for adding searchAvailability in Indexing.
 */
public class PartnerProductSearchAvailabilityValueResolver extends
    AbstractValueResolver<IbmVariantProductModel, Object, Object> {


    private PartnerProductSearchAvailabilityService partnerProductSearchAvailabilityService;
    private final IbmProductService productService;
    private final IbmDeploymentTypeService deploymentTypeService;

    public PartnerProductSearchAvailabilityValueResolver(
        PartnerProductSearchAvailabilityService partnerProductSearchAvailabilityService,
        IbmProductService productService, final IbmDeploymentTypeService deploymentTypeService) {
        this.partnerProductSearchAvailabilityService = partnerProductSearchAvailabilityService;
        this.productService = productService;
        this.deploymentTypeService = deploymentTypeService;
    }


    /**
     * method for updating searchAvailability value as per partProduct and product approval status.
     *
     * @param inputDocument
     * @param indexerBatchContext
     * @param indexedProperty
     * @param ibmProductModel
     * @param valueResolverContext
     * @throws FieldValueProviderException
     */
    @Override
    protected void addFieldValues(final InputDocument inputDocument,
        final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
        final IbmVariantProductModel ibmProductModel,
        final ValueResolverContext<Object, Object> valueResolverContext)
        throws FieldValueProviderException {

        final IbmDeploymentTypeModel deploymentType = getProductService().getDeploymentType(
            ibmProductModel);

        if (!ArticleApprovalStatus.APPROVED.equals(ibmProductModel.getApprovalStatus())
            || deploymentType == null || !getDeploymentTypeService().isDeploymentTypeSupported(
            deploymentType.getCode())) {
            inputDocument.addField(indexedProperty, Boolean.FALSE);
            return;
        }

        if (PartnerProductDeploymentTypeUtils.isBeespokeMultiPid(ibmProductModel)
            && CollectionUtils.isEmpty(ibmProductModel.getProductReferences())) {
            inputDocument.addField(indexedProperty, Boolean.FALSE);
            return;
        }

        List<IbmPartProductModel> partProducts = getProductService().getPartProducts(
            ibmProductModel);

        partProducts = getPartnerProductSearchAvailabilityService().filterSoftwareQuotingProducts(
            partProducts);

        boolean searchAvailability = CollectionUtils.isNotEmpty(partProducts);

        if (searchAvailability) {
            if (PartnerProductDeploymentTypeUtils.isSaasProduct(ibmProductModel)
                && getDeploymentTypeService().isDeploymentTypeSupported(
                PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE)) {
                searchAvailability = getPartnerProductSearchAvailabilityService().isSearchAvailabilityForProduct(
                    ibmProductModel, PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE);
                if (searchAvailability && getDeploymentTypeService().isDeploymentTypeSupported(
                    PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID)) {
                    searchAvailability = getPartnerProductSearchAvailabilityService().isSearchAvailabilityForProduct(
                        ibmProductModel, PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID);
                }
            } else if (PartnerProductDeploymentTypeUtils.isBespokeProduct(ibmProductModel)
                && getDeploymentTypeService().isDeploymentTypeSupported(
                PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID)) {
                searchAvailability = getPartnerProductSearchAvailabilityService().isSearchAvailabilityForProduct(
                    ibmProductModel, PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID);
            }
        }
        inputDocument.addField(indexedProperty, searchAvailability);
    }

    /**
     * this method call product service
     *
     * @return productService
     */
    public IbmProductService getProductService() {
        return productService;
    }

    /**
     * this method call product search availability
     *
     * @return partnerProductSearchAvailabilityService
     */
    public PartnerProductSearchAvailabilityService getPartnerProductSearchAvailabilityService() {
        return partnerProductSearchAvailabilityService;
    }


    public IbmDeploymentTypeService getDeploymentTypeService() {
        return deploymentTypeService;
    }
}
