package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.lang3.StringUtils;

/**
 * This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class IbmB2BUnitCurrencyPopulator implements Populator<IbmB2BUnitModel, IbmB2BUnitData> {

    private Converter<CurrencyModel, CurrencyData> currencyDataConverter;

    public IbmB2BUnitCurrencyPopulator(
        final Converter<CurrencyModel, CurrencyData> currencyDataConverter) {
        this.currencyDataConverter = currencyDataConverter;
    }


    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the PartnerB2BUnitData object
     * @param target the PartnerB2BUnitModel to fill
     */
    @Override
    public void populate(final IbmB2BUnitModel source, final IbmB2BUnitData target)
        throws ConversionException {
        if (source.getCurrency() != null) {
            target.setCurrency(getCurrencyDataConverter().convert(source.getCurrency()));
        }
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyDataConverter() {
        return currencyDataConverter;
    }
}
