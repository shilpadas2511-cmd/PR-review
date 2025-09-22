package com.ibm.commerce.partner.facades.order.populators;


import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;

import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.data.order.entry.pricing.CpqPricingDetailData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Objects;

/**
 * populate Channel Margin Information details
 */
public class PartnerDistributerPricingDetailsToEntryPopulator implements
    Populator<PartnerCpqPricingDetailModel, CpqPricingDetailData> {

    private PartnerUserService userService;

    public PartnerDistributerPricingDetailsToEntryPopulator(PartnerUserService userService) {
        this.userService = userService;
    }

    @Override
    public void populate(PartnerCpqPricingDetailModel source,
        CpqPricingDetailData target) throws ConversionException {
        AbstractOrderEntryModel masterEntry = source.getOrderEntry().getMasterEntry();
        if (Objects.nonNull(masterEntry) && Objects.nonNull(masterEntry.getOrder())) {
            if (getUserService().isVadView(masterEntry.getOrder(), userService.getCurrentUser())) {
                target.setChannelMargin(source.getChannelMargin());
            }
        }
    }

    public PartnerUserService getUserService() {
        return userService;
    }

}

