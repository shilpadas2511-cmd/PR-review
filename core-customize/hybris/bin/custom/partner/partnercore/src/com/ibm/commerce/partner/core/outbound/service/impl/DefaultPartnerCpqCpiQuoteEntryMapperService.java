package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteEntryMapperService;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;


/**
 * Default Partner PartnerCpqCpiQuoteEntry MapperService class is used to map the populate or map
 * the quote model field values to SAPCPQOutboundQuoteItem data object
 */
public class DefaultPartnerCpqCpiQuoteEntryMapperService implements
    PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel> {

    private final String defaultTransactionType;
    private final IbmProductService productService;
    private final PartnerPricingOutboundService partnerPricingOutboundService;
    private final PartnerQuoteService partnerQuoteService;
    private final CatalogVersionService catalogVersionService;

    private static final String EXTERNAL_ITEM_ID = "1";

    public DefaultPartnerCpqCpiQuoteEntryMapperService(String defaultTransactionType,
        IbmProductService productService,
        PartnerPricingOutboundService partnerPricingOutboundService,
        PartnerQuoteService partnerQuoteService,
        CatalogVersionService catalogVersionService) {
        this.defaultTransactionType = defaultTransactionType;
        this.productService = productService;
        this.partnerPricingOutboundService = partnerPricingOutboundService;
        this.partnerQuoteService = partnerQuoteService;
        this.catalogVersionService = catalogVersionService;
    }


    @Override
    public void map(AbstractOrderEntryModel quoteEntryModel,
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel, QuoteModel quoteModel) {
        AbstractOrderEntryModel pidEntry = quoteEntryModel.getMasterEntry();
        if (Objects.nonNull(quoteEntryModel.getProduct())) {
            sapcpqOutboundQuoteItemModel.setPartNumber(
                getProductService().getProductCode(quoteEntryModel.getProduct()));

            sapcpqOutboundQuoteItemModel.setProductCode(
                getProductService().getProductCode(quoteEntryModel.getProduct()));

            if (pidEntry.getProduct() instanceof IbmVariantProductModel pidProduct) {
                if (PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID.equalsIgnoreCase(
                    pidProduct.getDeploymentType().getCode())) {
                    getCatalogVersionService().addSessionCatalogVersion(
                        getCatalogVersionService().getCatalogVersion(
                            PartnercoreConstants.PRODUCT_CATALOG, PartnercoreConstants.ONLINE));
                    final ProductConfigurationModel productConfiguration = pidEntry.getProductConfiguration();
                    final Collection<ProductModel> products = productConfiguration.getProduct();
                    sapcpqOutboundQuoteItemModel.setProductSystemId(
                        ((IbmVariantProductModel) products.iterator()
                            .next()).getConfiguratorCode());
                    getCatalogVersionService().setSessionCatalogVersions(Collections.emptyList());
                } else {
                    sapcpqOutboundQuoteItemModel.setProductSystemId(
                        pidProduct.getConfiguratorCode());
                }
            }
        }
        sapcpqOutboundQuoteItemModel.setExternalItemID(EXTERNAL_ITEM_ID);
        sapcpqOutboundQuoteItemModel.setQuantity(String.valueOf(quoteEntryModel.getQuantity()));
        sapcpqOutboundQuoteItemModel.setConfigurationId(getConfigId(pidEntry));
        sapcpqOutboundQuoteItemModel.setTransactionType(getDefaultTransactionType());
        sapcpqOutboundQuoteItemModel.setStartDate(
            getPartnerQuoteService().getProductInfoFormatted(quoteEntryModel,
                PartnercoreConstants.STARTDATE));
        sapcpqOutboundQuoteItemModel.setEndDate(
            getPartnerQuoteService().getProductInfoFormatted(quoteEntryModel,
                PartnercoreConstants.ENDDATE));
        sapcpqOutboundQuoteItemModel.setStartDateChange(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.STARTDATECHANGED));
        sapcpqOutboundQuoteItemModel.setEndDateChange(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.ENDDATECHANGED));
        sapcpqOutboundQuoteItemModel.setBackDateChange(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.BACKDATEDSTARTDATE));
        String isStandAloneFlag = getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
            PartnercoreConstants.ORDER_ENTRY_STANDALONE);
        if(StringUtils.isNotEmpty(isStandAloneFlag)) {
            sapcpqOutboundQuoteItemModel.setIsStandAlone(isStandAloneFlag);
        }
    }

    protected String getConfigId(AbstractOrderEntryModel entry) {
        if (entry != null && entry.getProductConfiguration() != null) {
            return StringUtils.defaultIfBlank(entry.getProductConfiguration().getConfigurationId(),
                StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    public PartnerPricingOutboundService getPartnerPricingOutboundService() {
        return partnerPricingOutboundService;
    }

    public String getDefaultTransactionType() {
        return defaultTransactionType;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public PartnerQuoteService getPartnerQuoteService() {
        return partnerQuoteService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }
}
