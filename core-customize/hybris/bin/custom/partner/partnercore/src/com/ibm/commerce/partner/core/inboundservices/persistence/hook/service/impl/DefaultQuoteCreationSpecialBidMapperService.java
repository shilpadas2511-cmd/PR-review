package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Default Partner DefaultQuoteCreationSpecialBidMapperService MapperService class is used to
 * populate or map the special Bid details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationSpecialBidMapperService implements
    PartnerQuoteCreationMapperService {

    private ModelService modelService;
    private PartnerB2BUnitService partnerB2BUnitService;

    private PartnerSpecialBidReasonService partnerSpecialBidReasonService;

    public DefaultQuoteCreationSpecialBidMapperService(ModelService modelService,
        PartnerSpecialBidReasonService partnerSpecialBidReasonService,
        PartnerB2BUnitService partnerB2BUnitService) {
        this.modelService = modelService;
        this.partnerSpecialBidReasonService = partnerSpecialBidReasonService;
        this.partnerB2BUnitService = partnerB2BUnitService;
    }

    /**
     * Maps the Special Bid Reason and its associated details from the {@link
     * CpqIbmPartnerQuoteModel} to the {@link IbmPartnerQuoteModel}. .
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model containing the Special
     *                                Bid Reason and Business Justification. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model where the Special Bid
     *                                Reason and Business Justification will be mapped. Must not be
     *                                null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is
     *                                  null.
     */
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {

        Set<PartnerSpecialBidReasonModel> existingSpecialBidReasons =
            Optional.ofNullable(cpqIbmPartnerQuoteModel.getSpecialBidReasons())
                .orElse(Collections.emptySet())
                .stream()
                .filter(Objects::nonNull)
                .map(sbr -> getPartnerSpecialBidReasonService().getSpecialBidReasonById(
                    sbr.getCode()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(existingSpecialBidReasons)) {
            quoteModel.setSpecialBidReasons(existingSpecialBidReasons);
        }

        if (cpqIbmPartnerQuoteModel.getSpecialBidBusinessJustification() != null) {
            quoteModel.setSpecialBidBusinessJustification(
                cpqIbmPartnerQuoteModel.getSpecialBidBusinessJustification());
        }
    }

    public PartnerSpecialBidReasonService getPartnerSpecialBidReasonService() {
        return partnerSpecialBidReasonService;
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

}
