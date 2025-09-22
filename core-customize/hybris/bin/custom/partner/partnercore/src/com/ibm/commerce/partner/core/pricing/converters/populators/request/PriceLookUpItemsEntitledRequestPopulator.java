package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Map;

/**
 * Populator class responsible for populating CommonPriceLookUpItemsRequestData from an
 * AbstractOrderEntryModel for entitled items. Extends PriceLookUpItemsCommonRequestPopulator and
 * implements Populator interface with AbstractOrderEntryModel as source and
 * CommonPriceLookUpItemsRequestData as target.
 */
public class PriceLookUpItemsEntitledRequestPopulator extends
    PriceLookUpItemsCommonRequestPopulator implements
    Populator<AbstractOrderEntryModel, CommonPriceLookUpItemsRequestData> {

    public PriceLookUpItemsEntitledRequestPopulator(final IbmProductService productService,
        final PartnerPidAgreementService partnerPidAgreementService,
        Map<String, String> billingFrequencyMap) {
        super(productService, partnerPidAgreementService, billingFrequencyMap);
    }

    /**
     * Populates the target CommonPriceLookUpItemsRequestData with data from the source
     * AbstractOrderEntryModel.
     *
     * @param source the source AbstractOrderEntryModel object from which data is populated
     * @param target the target CommonPriceLookUpItemsRequestData object to which data is populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(AbstractOrderEntryModel source, CommonPriceLookUpItemsRequestData target)
        throws ConversionException {
        if (source != null) {
            target.setHasDealReg(PartnercoreConstants.FALSE);
            super.populate(source, target);
        }
    }

}
