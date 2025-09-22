package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationPriceMapperService;
import com.ibm.commerce.partner.core.model.CPQIbmPartnerOrderEntryProductInfoModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerEntryPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.core.utils.PartnerUtils;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default Partner DefaultQuoteCreationEntryPricesMapperService MapperService class is used to
 * populate or map the entry price details from CpqIbmPartnerQuoteModel field values to
 * IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationEntryPricesMapperService implements
    PartnerQuoteCreationPriceMapperService<CpqIbmPartnerEntryPricingDetailsModel, IbmPartnerQuoteEntryModel> {

    private ModelService modelService;

    public DefaultQuoteCreationEntryPricesMapperService(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Maps the pricing details from a CPQ entry to a Partner quote entry.
     *
     * @param partnerCpqPricingDetails the pricing details for the partner's CPQ entry.
     * @param quoteEntry               the quote entry that will be associated with the pricing
     *                                 details.
     */
    public void mapPricing(CpqIbmPartnerEntryPricingDetailsModel partnerCpqPricingDetails,
        IbmPartnerQuoteEntryModel quoteEntry) {
        PartnerCpqPricingDetailModel cpqPricingDetail = modelService.create(
            PartnerCpqPricingDetailModel.class);

        cpqPricingDetail.setOrderEntry(quoteEntry);
        cpqPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        cpqPricingDetail.setTotalExtendedPrice(partnerCpqPricingDetails.getTotalExtendedPrice());
        quoteEntry.setTotalPrice(partnerCpqPricingDetails.getTotalExtendedPrice());
        setOptionalPricingDetails(partnerCpqPricingDetails, cpqPricingDetail);
        getModelService().save(cpqPricingDetail);
        getModelService().save(quoteEntry);
    }

    /**
     * Creates a {@link PartnerCpqPricingDetailModel} based on the provided {@link
     * CpqIbmPartnerEntryPricingDetailsModel}.
     *
     * @param partnerCpqPricingDetails the pricing details for the partner's CPQ entry. Must not be
     *                                 null.
     * @throws IllegalArgumentException if either {@code partnerCpqPricingDetails} or {@code
     *                                  quoteEntry} is null.
     */
    protected void setOptionalPricingDetails(
        CpqIbmPartnerEntryPricingDetailsModel partnerCpqPricingDetails,
        PartnerCpqPricingDetailModel cpqPricingDetail) {
        cpqPricingDetail.setRolledUpBidExtendedPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getRolledUpBidExtendedPrice()));
        cpqPricingDetail.setNetPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getTotalExtendedPrice()));
        cpqPricingDetail.setExtendedListPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getExtendedListPrice()));
        cpqPricingDetail.setListPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getListPrice()));
        cpqPricingDetail.setDiscountAmount(
            PartnerUtils.getValue(partnerCpqPricingDetails.getDiscountAmount()));
        cpqPricingDetail.setDiscountPercent(
            PartnerUtils.getValue(partnerCpqPricingDetails.getDiscountPercent()));
        cpqPricingDetail.setRolledUpListPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getRolledUpListPrice()));
        cpqPricingDetail.setRolledUpExtendedListPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getRolledUpExtendedListPrice()));
        cpqPricingDetail.setRolledUpNetPrice(
            PartnerUtils.getValue(partnerCpqPricingDetails.getRolledUpNetPrice()));

        cpqPricingDetail.setPricingStrategy(partnerCpqPricingDetails.getPricingStrategy());
        cpqPricingDetail.setEccRequest(partnerCpqPricingDetails.getEccRequest());
        cpqPricingDetail.setEccOverrideFields(partnerCpqPricingDetails.getEccOverrideFields());
        cpqPricingDetail.setExtendedUnitPrice(partnerCpqPricingDetails.getExtendedUnitPrice());
        cpqPricingDetail.setChannelMargin(partnerCpqPricingDetails.getChannelMargin());
        cpqPricingDetail.setYtyPercentage(partnerCpqPricingDetails.getYtyPercentage());
        getModelService().save(cpqPricingDetail);
    }

    public ModelService getModelService() {
        return modelService;
    }

}