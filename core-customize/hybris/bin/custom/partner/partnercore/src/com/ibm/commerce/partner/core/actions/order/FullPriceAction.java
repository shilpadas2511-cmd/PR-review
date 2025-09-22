package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to consume CPQ Entitled Price API. This action retrieves price data from a CPQ Entitled
 * Price API and updates the order accordingly.
 */
public class FullPriceAction extends PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {


    private static final Logger LOG = LoggerFactory.getLogger(FullPriceAction.class);

    private final PartnerPricingOutboundService pricingOutboundService;
    private final PriceLookUpService priceLookUpService;

    private final Converter<PriceLookUpResponseData, AbstractOrderModel> responseReverseDataConverter;

    public FullPriceAction(final Integer maxRetryAllowed, final Integer retryDelay,
        final PartnerPricingOutboundService pricingOutboundService,
        PriceLookUpService priceLookUpService,
        final Converter<PriceLookUpResponseData, AbstractOrderModel> responseReverseDataConverter) {
        super(maxRetryAllowed, retryDelay);
        this.pricingOutboundService = pricingOutboundService;
        this.priceLookUpService = priceLookUpService;
        this.responseReverseDataConverter = responseReverseDataConverter;
    }


    /**
     * Executes the action to consume CPQ Entitled Price API.
     *
     * @param priceLookUpProcessModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(PriceLookUpProcessModel processModel) throws IOException {

        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), processModel.getCode());
        LOG.debug(msg);

        try {
            updateYtyForChildEntries((IbmPartnerCartModel) processModel.getOrder());
            PriceLookUpResponseData responseData = getPricingOutboundService().getFullPrice(
                processModel.getOrder());
            if (responseData != null) {
                responseData.setType(CpqPricingTypeEnum.FULL);
                AbstractOrderModel cart = getResponseReverseDataConverter().convert(responseData,
                    processModel.getOrder());
                getModelService().saveAll(cart);
                return Transition.OK;
            } else {
                throw new IbmWebServiceFailureException("NO RESPONSE FOUND");
            }
        } catch (final IbmWebServiceFailureException ex) {
            return retryOrFailAction(processModel, msg);
        }
    }

    protected void updateYtyForChildEntries(IbmPartnerCartModel cart) {
        PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getPriceLookUpService().getHeaderPricingDetail(
            cart);
        if (cpqPricingDetail != null && MapUtils.isNotEmpty(cpqPricingDetail.getYtyYears())) {
            cpqPricingDetail.getYtyYears().forEach((key, value) -> {
                if (value != null && value > NumberUtils.INTEGER_ZERO) {
                    cart.getEntries().stream()
                        .filter(pidEntry -> CollectionUtils.isNotEmpty(pidEntry.getChildEntries()))
                        .forEach(pidEntry -> {
                            final Optional<AbstractOrderEntryModel> anyChildEntry = pidEntry.getChildEntries()
                                .stream().findAny();
                            if (anyChildEntry.isPresent()) {
                                final Integer childYtyEntryGroupNum = getEntryGroupNumber(
                                    anyChildEntry.get().getOrder(), key);
                                final List<AbstractOrderEntryModel> ytyRelatedEntries = getEntries(
                                    pidEntry.getChildEntries(), childYtyEntryGroupNum);
                                ytyRelatedEntries.forEach(ytyEntry -> {
                                    PartnerCpqPricingDetailModel fullPricingDetail = getPriceLookUpService().getEntryCpqPricingDetail(
                                        ytyEntry, CpqPricingTypeEnum.FULL);
                                    fullPricingDetail.setOverrideYearToYearGrowth(value);
                                    populateExistingKeyValue(key, cpqPricingDetail);
                                    getModelService().save(fullPricingDetail);
                                    getModelService().save(ytyEntry);
                                });
                            }
                        });
                }
            });
            getModelService().save(cpqPricingDetail);
        }
    }

    protected void populateExistingKeyValue(String key,
        PartnerCpqHeaderPricingDetailModel cpqPricingDetail) {
        Map<String, Double> yearsMap = cpqPricingDetail.getYtyYears();
        if (Collections.unmodifiableMap(yearsMap).equals(yearsMap)) {
            yearsMap = new HashMap<>(yearsMap);
            yearsMap.put(key, null);
            cpqPricingDetail.setYtyYears(yearsMap);
            getModelService().save(cpqPricingDetail);
        }
    }

    protected Integer getEntryGroupNumber(AbstractOrderModel order, String entryGroupLabel) {

        if (order == null || StringUtils.isBlank(entryGroupLabel)) {
            return null;
        }
        return order.getEntryGroups().stream().filter(
            entryGroup -> GroupType.YTY.equals(entryGroup.getGroupType()) && entryGroupLabel.equals(
                entryGroup.getLabel())).map(EntryGroup::getGroupNumber).findAny().orElse(null);
    }

    protected List<AbstractOrderEntryModel> getEntries(Collection<AbstractOrderEntryModel> entries,
        Integer entryGroupNum) {

        if (entryGroupNum == null || CollectionUtils.isEmpty(entries)) {
            return Collections.emptyList();
        }

        return entries.stream()
            .filter(entry -> entry.getEntryGroupNumbers().contains(entryGroupNum)).toList();
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public PartnerPricingOutboundService getPricingOutboundService() {
        return pricingOutboundService;
    }

    public Converter<PriceLookUpResponseData, AbstractOrderModel> getResponseReverseDataConverter() {
        return responseReverseDataConverter;
    }
}
