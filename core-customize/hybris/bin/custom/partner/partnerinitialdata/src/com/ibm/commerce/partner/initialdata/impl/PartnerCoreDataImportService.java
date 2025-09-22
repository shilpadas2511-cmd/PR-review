package com.ibm.commerce.partner.initialdata.impl;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;

/**
 * service to add custom Core impexes that needs to load during init/update
 */
public class PartnerCoreDataImportService extends CoreDataImportService {

    /**
     * @param extensionName
     */
    @Override
    protected void importCommonData(final String extensionName) {
        super.importCommonData(extensionName);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/common/user-groups_backofficeRoles.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/common/sso-backoffice-samlusergroup.impex",
                        extensionName),
                false);
		getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/integration/partnerProduct.impex",
                extensionName),
            false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/common/revenueStream.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/common/sellerAudienceMask.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmParentChildInbound.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmPartnerQuoteStatus.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmPartProductInbound.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmPartProductInbound.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmPartToPidRelationshipInbound.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/ibmProductInbound.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/inboundCategoryRelation.impex",
                        extensionName),
                false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/inboundIbmVariantProduct.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/partnerInboundCategory.impex",
                        extensionName),
                false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/partnerOutboundQuote.impex",
                        extensionName),
                false);

        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/integration/partnerOutboundQuoteStatus.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
                String.format("/%s/import/coredata/common/products-ibmdeploymentType.impex",
                        extensionName),
                false);
        getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/common/accountService.impex",
                extensionName),
            false);
        getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/common/pricingService.impex",
                extensionName),
            false);
        getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/integration/cpqOutboundQuote.impex",
                extensionName),
            false);
        getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/common/approvalComments.impex",
                extensionName),
            false);
        getSetupImpexService().importImpexFile(
            String.format("/%s/import/coredata/integration/kafkaPartnerQuoteStatus.impex",
                extensionName),
            false);

    }

    /**
     * method to import content catalog impexes
     * @param extensionName
     * @param contentCatalogName
     */
    @Override
    protected void importContentCatalog(final String extensionName, final String contentCatalogName) {
        super.importContentCatalog(extensionName,contentCatalogName);

        getSetupImpexService()
                .importImpexFile(String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-content-homepage.impex",
                        extensionName, contentCatalogName), false);
    }
}
