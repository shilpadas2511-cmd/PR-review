package com.ibm.commerce.partner.facades.accountservice.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link IbmB2BUnitData} from {@link PartnerSiteIdResponseData}
 */
public class PartnerSiteIdResponsePopulator implements
    Populator<PartnerSiteIdResponseData, IbmB2BUnitData> {

    private final Converter<PartnerSiteCustomerAddressInfoResponseData, AddressData> partnerSiteIdAddressConverter;

    private final Converter<PartnerSiteIdResponseData, IbmB2BUnitData> ceidConverter;

    public PartnerSiteIdResponsePopulator(
        final Converter<PartnerSiteCustomerAddressInfoResponseData, AddressData> partnerSiteIdAddressConverter,
        final Converter<PartnerSiteIdResponseData, IbmB2BUnitData> ceidConverter) {
        this.partnerSiteIdAddressConverter = partnerSiteIdAddressConverter;
        this.ceidConverter = ceidConverter;
    }


    @Override
    public void populate(PartnerSiteIdResponseData source, IbmB2BUnitData target)
        throws ConversionException {
        if (StringUtils.isNotEmpty(source.getSapSiteNumber())) {
            target.setUid(source.getSapSiteNumber());
        }

        if (source.getCustomerInfo() != null && StringUtils.isNotEmpty(
            source.getCustomerInfo().getAccountName())) {
            target.setName(source.getCustomerInfo().getAccountName());
        }
        if (StringUtils.isNotEmpty(source.getIbmCustomerNumber())) {
            target.setIbmCustomerNumber(source.getIbmCustomerNumber());
        }

        if (StringUtils.isNotBlank(source.getCeid())) {
            target.setUnit(getCeidConverter().convert(source));
        }

        populateAddressData(source, target);
    }

    protected void populateAddressData(final PartnerSiteIdResponseData source,
        final IbmB2BUnitData target) {
        if (source.getCustomerInfo() == null || source.getCustomerInfo().getAddress() == null) {
            return;
        }
        target.setAddresses(new ArrayList<>(1));
        target.getAddresses()
            .add(getPartnerSiteIdAddressConverter().convert(source.getCustomerInfo().getAddress()));
    }

    public Converter<PartnerSiteCustomerAddressInfoResponseData, AddressData> getPartnerSiteIdAddressConverter() {
        return partnerSiteIdAddressConverter;
    }

    public Converter<PartnerSiteIdResponseData, IbmB2BUnitData> getCeidConverter() {
        return ceidConverter;
    }
}
