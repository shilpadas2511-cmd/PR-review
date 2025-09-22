package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Populator to populate Addresses
 */
public class B2BUnitAddressDetailsPopulator implements Populator<B2BUnitModel, B2BUnitData> {

    private Converter<AddressModel, AddressData> addressConverter;

    public B2BUnitAddressDetailsPopulator(
        final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public void populate(final B2BUnitModel source, final B2BUnitData target)
        throws ConversionException {
        if (CollectionUtils.isNotEmpty(source.getAddresses())) {
            List<AddressData> addressDataList = Converters.convertAll(source.getAddresses(),
                getAddressConverter());
            target.setAddresses(addressDataList);
        }
    }

    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }
}
