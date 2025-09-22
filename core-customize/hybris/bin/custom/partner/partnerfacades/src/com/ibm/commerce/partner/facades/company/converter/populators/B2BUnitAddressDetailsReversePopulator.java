package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.facades.comparators.PartnerAddressComparator;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Populates Addresses into B2bUnitModel
 */
public class B2BUnitAddressDetailsReversePopulator implements Populator<B2BUnitData, B2BUnitModel> {

    private final Converter<AddressData, AddressModel> addressReverseConverter;

    private final PartnerAddressComparator addressComparator;

    private final B2BCommerceUnitService b2BCommerceUnitService;

    private final ModelService modelService;

    public B2BUnitAddressDetailsReversePopulator(
        final Converter<AddressData, AddressModel> addressReverseConverter,
        final PartnerAddressComparator addressComparator,
        final B2BCommerceUnitService b2BCommerceUnitService, final ModelService modelService) {
        this.addressReverseConverter = addressReverseConverter;
        this.addressComparator = addressComparator;
        this.b2BCommerceUnitService = b2BCommerceUnitService;
        this.modelService = modelService;
    }

    @Override
    public void populate(final B2BUnitData source, final B2BUnitModel target)
        throws ConversionException {

        if (CollectionUtils.isEmpty(source.getAddresses())) {
            return;
        }
        final List<AddressModel> addressModels = getAddressReverseConverter().convertAll(
            source.getAddresses());
        if (CollectionUtils.isEmpty(target.getAddresses())) {
            target.setAddresses(new ArrayList<>());
        }
        final List<AddressModel> newAddresses = addressModels.stream().filter(
            addressModel -> target.getAddresses().stream().noneMatch(
                b2bUnitAddress -> getAddressComparator().compare(b2bUnitAddress, addressModel)
                    == NumberUtils.INTEGER_ZERO)).collect(Collectors.toList());
        attachAddresses(target, newAddresses);
    }

    /**
     * Attaches New Address Modes to B2bUnitModel
     *
     * @param b2bUnitModel
     * @param addressModels
     */
    protected void attachAddresses(final B2BUnitModel b2bUnitModel,
        final List<AddressModel> addressModels) {

        addressModels.forEach(
            addressModel -> getB2BCommerceUnitService().saveAddressEntry(b2bUnitModel,
                addressModel));
    }

    public Converter<AddressData, AddressModel> getAddressReverseConverter() {
        return addressReverseConverter;
    }

    public PartnerAddressComparator getAddressComparator() {
        return addressComparator;
    }

    public B2BCommerceUnitService getB2BCommerceUnitService() {
        return b2BCommerceUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
