package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * * Populator class responsible for populating CommonPriceLookUpItemsRequestData * from an
 * AbstractOrderEntryModel.
 */
public class PriceLookUpItemsRequestPopulator implements
    Populator<AbstractOrderEntryModel, CommonPriceLookUpItemsRequestData> {

    private final SessionService sessionService;
    private final String ePartLicenceTypeCode;
    private ConfigurationService configurationService;

    public PriceLookUpItemsRequestPopulator(final SessionService sessionService,
        String ePartLicenceTypeCode, ConfigurationService configurationService) {
        this.sessionService = sessionService;
        this.ePartLicenceTypeCode = ePartLicenceTypeCode;
        this.configurationService = configurationService;
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

            final String licenceTypeCode = PartnerOrderUtils.getProductInfo(source,
                PartnercoreConstants.ORDER_ENTRY_LICENCE_TYPE_CODE);
            final String standAloneFlag = PartnerOrderUtils.getProductInfo(source,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE);
            if (BooleanUtils.isTrue(
                getEPartLicenceTypeCode().equalsIgnoreCase(licenceTypeCode))) {
                populateRelatedItemNumber(target, source);
            } else {
                if (!getConfigurationService().getConfiguration()
                    .getBoolean(PartnercoreConstants.RELATED_ITEM_NUMBER_FEATURE_FLAG, true)
                    && standAloneFlag.equalsIgnoreCase(PartnercoreConstants.IS_STANDALONE_N)) {
                    source.getMasterEntry()
                        .getChildEntries()
                        .stream().filter(
                            entry -> source.getProduct().getCode()
                                .equalsIgnoreCase(entry.getProduct().getCode())
                                && PartnerOrderUtils.getProductInfo(entry,
                                    PartnercoreConstants.ORDER_ENTRY_STANDALONE)
                                .equalsIgnoreCase(PartnercoreConstants.IS_STANDALONE_Y))
                        .findFirst()
                        .ifPresent(abstractOrderEntryModel -> target.setRelatedItemNumber(
                            String.valueOf(
                                PartnerOrderUtils.getItemNumber(abstractOrderEntryModel))));
                }
            }

            target.setIsStandalone(standAloneFlag);

        }
    }

    /**
     * @param target CommonPriceLookUpItemsRequestData
     * @param source AbstractOrderEntryModel populated RelatedItemNumber based on the subId
     */
    protected void populateRelatedItemNumber(CommonPriceLookUpItemsRequestData target,
        AbstractOrderEntryModel source) {
        String subId = PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.ORDER_ENTRY_SUB_ID);
        if (StringUtils.isNotEmpty(subId)) {
            Map<String, AbstractOrderEntryModel> subIdToItemNumberMap = sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP);
            if (!subIdToItemNumberMap.containsKey(subId)) {
                target.setRelatedItemNumber(String.valueOf(target.getItemNumber()));
            } else {
                target.setRelatedItemNumber(String.valueOf(
                    PartnerOrderUtils.getItemNumber(subIdToItemNumberMap.get(subId))));
            }
        }
    }

    public String getEPartLicenceTypeCode() {
        return ePartLicenceTypeCode;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
