package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.utils.PartnerCountryUtils;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.order.price.data.request.DealRegPartRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.DealRegRequestData;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates the target DealRegRequestData object with information extracted from the source
 * AbstractOrderModel.
 */
public class DealRegRequestPopulator implements Populator<AbstractOrderModel, DealRegRequestData> {

    private final PriceLookUpService priceLookUpService;

    private final PartnerB2BUnitService b2BUnitService;

    private final IbmProductService productService;

    private final String defaultDistributionChannel;
    private final String defaultFulfillmentSource;

    public DealRegRequestPopulator(PriceLookUpService priceLookUpService,
        PartnerB2BUnitService b2BUnitService, IbmProductService productService,
        final String defaultDistributionChannel, final String defaultFulfillmentSource) {
        this.priceLookUpService = priceLookUpService;
        this.b2BUnitService = b2BUnitService;
        this.productService = productService;
        this.defaultDistributionChannel = defaultDistributionChannel;
        this.defaultFulfillmentSource = defaultFulfillmentSource;
    }


    /**
     * Populates the target DealRegRequestData object with information extracted from the source
     * AbstractOrderModel.
     *
     * @param source the AbstractOrderModel containing the source data
     * @param target the DealRegRequestData object to be populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(AbstractOrderModel source, DealRegRequestData target)
        throws ConversionException {
        if (source instanceof IbmPartnerCartModel cart) {
            IbmPartnerEndCustomerB2BUnitModel endCustomer = (IbmPartnerEndCustomerB2BUnitModel) cart.getUnit();
            B2BUnitModel reseller = cart.getSoldThroughUnit();
            target.setIsCreditRebillQuote(Boolean.FALSE);
            target.setCustomerICN(endCustomer.getId());
            target.setCustomerDCID(endCustomer.getIbmCustomerDCID());
            target.setQuoteCountry(PartnerCountryUtils.getCountryCode(endCustomer.getCountry()));
            List<AbstractOrderEntryModel> childEntriesList = getPriceLookUpService().getChildEntriesList(
                source);
            if (CollectionUtils.isNotEmpty(childEntriesList)) {
                target.setParts(
                    childEntriesList.stream().map(this::createDealRegPartRequestData).toList());
            }
            target.setCpqQuoteNum(StringUtils.isNotEmpty(cart.getPriceUid())? cart.getPriceUid(): StringUtils.EMPTY);
            target.setSapDistributionChannel(StringUtils.defaultIfBlank(
                ((IbmPartnerCartModel) source).getCpqDistributionChannel(),
                PartnerQuoteChannelEnum.J.getCode()));
            target.setFulfillmentSrc(getDefaultFulfillmentSource());
            if (getB2BUnitService().getParent(reseller) instanceof B2BUnitModel parentB2Bunit) {
                target.setResellerCEID(parentB2Bunit.getUid());
            }
        }
    }

    protected DealRegPartRequestData createDealRegPartRequestData(
        AbstractOrderEntryModel entryModel) {
        DealRegPartRequestData dealRegPartRequestData = new DealRegPartRequestData();
        dealRegPartRequestData.setPartNumber(
            getProductService().getProductCode(entryModel.getProduct()));
        dealRegPartRequestData.setIsSaasRenewalPart(false);
        dealRegPartRequestData.setIsSlRenewalPart(false);
        return dealRegPartRequestData;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public PartnerB2BUnitService getB2BUnitService() {
        return b2BUnitService;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public String getDefaultDistributionChannel() {
        return defaultDistributionChannel;
    }

    public String getDefaultFulfillmentSource() {
        return defaultFulfillmentSource;
    }
}
