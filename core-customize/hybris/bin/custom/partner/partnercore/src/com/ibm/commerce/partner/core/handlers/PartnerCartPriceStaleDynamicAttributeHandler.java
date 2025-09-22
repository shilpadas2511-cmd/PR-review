package com.ibm.commerce.partner.core.handlers;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;


/**
 * Handler to check if the price is stale or not for the cart.
 */
public class PartnerCartPriceStaleDynamicAttributeHandler extends
    AbstractDynamicAttributeHandler<Boolean, IbmPartnerCartModel> {

    @Override
    public Boolean get(final IbmPartnerCartModel ruleSet) {

        if (CollectionUtils.isEmpty(ruleSet.getEntries()) || ObjectUtils.isEmpty(
            ruleSet.getQuoteReference())) {
            return false;
        }

        AbstractOrderEntryModel pidEntry = ruleSet.getEntries().get(0);
        return evaluatePricingDetails(pidEntry);
    }

    @Override
    public void set(final IbmPartnerCartModel paramMODEL, final Boolean paramVALUE) {
       // Ignore
    }

    /**
     * to check the price stale through Full Price CPQ pricing detail at the PID level
     *
     * @param entry
     * @return
     */
    private Boolean evaluatePricingDetails(AbstractOrderEntryModel entry) {
        Optional<CpqPricingDetailModel> fullPricingDetailOpt = findFullPricingDetail(entry);
        return fullPricingDetailOpt.map(this::isModifiedBeforeToday).orElse(Boolean.FALSE);
    }

    /**
     * fetch the information of the CPQ pricing detail at the PID level
     *
     * @param entry
     * @return
     */
    private Optional<CpqPricingDetailModel> findFullPricingDetail(AbstractOrderEntryModel entry) {
        return entry.getCpqPricingDetails()
            .stream()
            .filter(detail -> CpqPricingTypeEnum.FULL.getCode().equals(detail.getPricingType()))
            .findFirst();
    }

    /**
     * checking if the modified date is older then today's date
     *
     * @param pricingDetail
     * @return
     */
    private Boolean isModifiedBeforeToday(CpqPricingDetailModel pricingDetail) {
        LocalDate modifiedDate = convertToLocalDate(pricingDetail.getModifiedtime());
        LocalDate currentDate = LocalDate.now();
        return modifiedDate.isBefore(currentDate);
    }

    /**
     * converting date to LocalDate
     *
     * @param date
     * @return
     */
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}