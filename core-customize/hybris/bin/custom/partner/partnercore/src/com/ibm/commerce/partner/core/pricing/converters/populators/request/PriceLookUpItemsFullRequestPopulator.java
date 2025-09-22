package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpItemRequestData;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;


/**
 * Populator class to populate common price lookup items request data with additional full price
 * lookup information.
 */
public class PriceLookUpItemsFullRequestPopulator extends
    PriceLookUpItemsCommonRequestPopulator implements
    Populator<AbstractOrderEntryModel, CommonPriceLookUpItemsRequestData> {


    private final SessionService sessionService;

    private String overrideObsoletePrice;

    private Map<String, String> dealRegMap;

    public PriceLookUpItemsFullRequestPopulator(final IbmProductService productService,
        SessionService sessionService, final PartnerPidAgreementService partnerPidAgreementService,final Map<String, String> billingFrequencyMap,
        Map<String, String> dealRegMap) {
        super(productService, partnerPidAgreementService,billingFrequencyMap);
        this.sessionService = sessionService;
        this.dealRegMap = dealRegMap;
    }


    /**
     * Populates the target CommonPriceLookUpItemsRequestData with information from the source
     * AbstractOrderEntryModel, including additional full price lookup data such as deal
     * registration flag.
     *
     * @param source The source AbstractOrderEntryModel from which to extract data.
     * @param target The target CommonPriceLookUpItemsRequestData to populate with extracted data.
     * @throws ConversionException if an error occurs during the conversion process.
     */
    @Override
    public void populate(AbstractOrderEntryModel source, CommonPriceLookUpItemsRequestData target)
        throws ConversionException {
        if (source != null) {
            super.populate(source, target);

            target.setHasDealReg(dealRegMap.get(PartnerOrderUtils.getProductInfo(source,
                PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG)));
            if (CollectionUtils.isNotEmpty(source.getCpqPricingDetails())) {
                source.getCpqPricingDetails().stream()
                    .filter(price -> CpqPricingTypeEnum.FULL.getCode()
                        .equalsIgnoreCase(price.getPricingType()))
                    .findAny().ifPresent(
                        pricingDetails -> updateTargetWithPricingDetails(pricingDetails,
                            (FullPriceLookUpItemRequestData) target));
            }
        }
    }

    protected void updateTargetWithPricingDetails(CpqPricingDetailModel pricingDetails,
        FullPriceLookUpItemRequestData target) {
        PartnerCpqPricingDetailModel partnerPricingDetails = (PartnerCpqPricingDetailModel) pricingDetails;
        if (partnerPricingDetails.getOverrideDiscount() != null) {
            target.setOverrideDiscount(
                String.valueOf(partnerPricingDetails.getOverrideDiscount()));
        }
        if (partnerPricingDetails.getOverrideBidUnitPrice() != null) {
            target.setOverridePrice(
                String.valueOf(partnerPricingDetails.getOverrideBidUnitPrice()));
        }
        if (partnerPricingDetails.getOverrideYearToYearGrowth() != null) {
            target.setOverrideYearToYearGrowth(
                String.valueOf(partnerPricingDetails.getOverrideYearToYearGrowth()));
        }
        if (partnerPricingDetails.getOverrideObsoletePrice() != null) {
            overrideObsoletePrice = String.valueOf(
                partnerPricingDetails.getOverrideObsoletePrice());
            target.setOverrideObsoletePrice(overrideObsoletePrice);
            target.setOverridePrice(
                partnerPricingDetails.getOverrideBidUnitPrice() != null
                    ? String.valueOf(partnerPricingDetails.getOverrideBidUnitPrice())
                    : overrideObsoletePrice);
        }
    }
}
