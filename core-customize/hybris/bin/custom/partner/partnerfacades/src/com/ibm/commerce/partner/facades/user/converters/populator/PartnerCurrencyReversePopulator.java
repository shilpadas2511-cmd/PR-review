/**
 * This package contains converters and populators related to user data for IBM Commerce Partner
 * integrations.
 * <p>
 * It provides utility classes to transform and map data between Hybris B2B models and IBM B2B
 * models.
 */
package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.facades.company.converter.populators.IbmB2BUnitCurrencyReversePopulator;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * The {@code PartnerCurrencyReversePopulator} is responsible for populating currency information
 * from a {@link B2BUnitData} source object to a {@link B2BUnitModel} target object.
 * <p>
 * Specifically, it processes instances of {@link IbmB2BUnitData} and {@link IbmB2BUnitModel} to
 * ensure that currency details are properly mapped.
 */
public class PartnerCurrencyReversePopulator implements Populator<B2BUnitData, B2BUnitModel> {

    private final IbmB2BUnitCurrencyReversePopulator b2bUnitCurrencyReversePopulator;

    public PartnerCurrencyReversePopulator(
        final IbmB2BUnitCurrencyReversePopulator b2bUnitCurrencyReversePopulator) {
        this.b2bUnitCurrencyReversePopulator = b2bUnitCurrencyReversePopulator;
    }

    /**
     * Populates the target {@link B2BUnitModel} with currency information from the source
     * {@link B2BUnitData}.
     * <p>
     * If the source object is an instance of {@link IbmB2BUnitData} and the target is an instance
     * of {@link IbmB2BUnitModel}, it extracts the currency data and sets the corresponding currency
     * model.
     *
     * @param source the source object containing currency data
     * @param target the target model to be populated
     * @throws ConversionException if an error occurs during the conversion process
     */
    @Override
    public void populate(B2BUnitData source, B2BUnitModel target) throws ConversionException {
        if (source instanceof IbmB2BUnitData b2BUnitData
            && target instanceof IbmB2BUnitModel b2bUnitModel) {
            getB2bUnitCurrencyReversePopulator().populate(b2BUnitData, b2bUnitModel);
        }
    }

    public IbmB2BUnitCurrencyReversePopulator getB2bUnitCurrencyReversePopulator() {
        return b2bUnitCurrencyReversePopulator;
    }
}
