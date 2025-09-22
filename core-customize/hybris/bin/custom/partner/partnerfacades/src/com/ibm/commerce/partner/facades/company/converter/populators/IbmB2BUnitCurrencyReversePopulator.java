package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ø This Reverser Populator is for transferring the data from PartnerB2BUnitData to
 * PartnerB2BUnitModel
 */
public class IbmB2BUnitCurrencyReversePopulator implements
    Populator<IbmB2BUnitData, IbmB2BUnitModel> {

    private static final Logger LOG = LoggerFactory.getLogger(
        IbmB2BUnitCurrencyReversePopulator.class);

    private final PartnerCurrencyService currencyService;
    private final ConfigurationService configurationService;

    public IbmB2BUnitCurrencyReversePopulator(final PartnerCurrencyService currencyService,
        final ConfigurationService configurationService) {
        this.currencyService = currencyService;
        this.configurationService = configurationService;
    }

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the PartnerB2BUnitData object
     * @param target the PartnerB2BUnitModel to fill
     */
    @Override
    public void populate(final IbmB2BUnitData source, final IbmB2BUnitModel target)
        throws ConversionException {
        boolean isResellerTier2CurrencyDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_DISABLE_CURRENCY_FOR_RESELLER_TIER_2, Boolean.TRUE);
        if (isResellerTier2CurrencyDisabled && source.getType() != null
            && IbmPartnerB2BUnitType.RESELLER_TIER_2.getCode()
            .equalsIgnoreCase(source.getType().getCode())) {
            target.setCurrency(null);
            return;
        }
        if (source.getCurrency() != null && StringUtils.isNotBlank(
            source.getCurrency().getIsocode())) {
            if (target.getCurrency() == null) {
                try {
                    List<CurrencyModel> activeCurrencies = getCurrencyService().getActiveCurrencies(
                        Collections.singletonList(source.getCurrency().getIsocode()));
                    if (CollectionUtils.isNotEmpty(activeCurrencies)) {
                        target.setCurrency(activeCurrencies.get(0));
                    }
                } catch (UnknownIdentifierException e) {
                    LOG.error(PartnercoreConstants.ACTIVE_CURR_UNKNOWN_ID_EXCEPTION, source.getCurrency().getIsocode());
                }
            } else if (!target.getCurrency().getIsocode()
                .equals(source.getCurrency().getIsocode())) {
                {
                    LOG.error(PartnercoreConstants.CURR_MISMATCH_ID_EXCEPTION, source.getCurrency().getIsocode(),
                        target.getCurrency().getIsocode());
                }
            }
        }
    }

    public PartnerCurrencyService getCurrencyService() {
        return currencyService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
