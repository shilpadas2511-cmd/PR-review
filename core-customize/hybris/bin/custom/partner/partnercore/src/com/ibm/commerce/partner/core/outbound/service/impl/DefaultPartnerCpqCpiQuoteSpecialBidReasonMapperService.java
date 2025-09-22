package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Default Partner PartnerCpqCpiQuoteSpecialBidReason MapperService class is used to map the populate or map the quote
 * model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuoteSpecialBidReasonMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    private final ConfigurationService configurationService;

    public DefaultPartnerCpqCpiQuoteSpecialBidReasonMapperService(
        ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {
        if (quoteModel instanceof IbmPartnerQuoteModel partnerQuote
            && ((BooleanUtils.isTrue(isMultipleSpecialBidDisabled()) && Objects.nonNull(
            partnerQuote.getSpecialBidReason())) || (
            BooleanUtils.isFalse(isMultipleSpecialBidDisabled()) && Objects.nonNull(
                partnerQuote.getSpecialBidReasons())))) {
            sapcpqOutboundQuoteModel.setSpecialBidReason(
                createOutboundQuoteSpecialBidField(partnerQuote));
        }
    }

    protected PartnerCpqSpecialBidReasonModel createOutboundQuoteSpecialBidField(
        IbmPartnerQuoteModel quoteModel) {
        PartnerCpqSpecialBidReasonModel specialBidReasonModel = new PartnerCpqSpecialBidReasonModel();
        if (BooleanUtils.isTrue(isMultipleSpecialBidDisabled())) {
            specialBidReasonModel.setCode(quoteModel.getSpecialBidReason().getCode());
            specialBidReasonModel.setName(quoteModel.getSpecialBidReason().getName());
            return specialBidReasonModel;
        }

        Set<PartnerSpecialBidReasonModel> reasons = quoteModel.getSpecialBidReasons();
        if (CollectionUtils.isEmpty(reasons)) {
            return specialBidReasonModel;
        }
        specialBidReasonModel.setCode(
            prepareSpecialBidInformation(reasons, PartnerSpecialBidReasonModel::getCode));
        specialBidReasonModel.setName(
            prepareSpecialBidInformation(reasons, PartnerSpecialBidReasonModel::getName));
        return specialBidReasonModel;
    }

    private <T> String prepareSpecialBidInformation(Collection<T> items, Function<T, String> mapper) {
        return items.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.joining(","));
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public boolean isMultipleSpecialBidDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);
    }
}
