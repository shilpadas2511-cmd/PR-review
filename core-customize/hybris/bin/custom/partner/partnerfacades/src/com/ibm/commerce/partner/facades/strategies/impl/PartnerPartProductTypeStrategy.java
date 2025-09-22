package com.ibm.commerce.partner.facades.strategies.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.partner.core.enums.PartProductType;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * Strategy class for determining the {@link PartProductType} of a {@link IbmPartProductModel} based
 * on configured revenue stream codes.
 * <p>
 * This strategy compares the revenue stream code of the {@link IbmPartProductModel} with the
 * configured SLA and overage revenue stream code lists to classify the part product.
 * </p>
 */
public class PartnerPartProductTypeStrategy {

    private final String slaRevenueStreamCodes;
    private final String overageRevenueStreamCodes;

    public PartnerPartProductTypeStrategy(String slaRevenueStreamCodes,
        String overageRevenueStreamCodes) {
        this.slaRevenueStreamCodes = slaRevenueStreamCodes;
        this.overageRevenueStreamCodes = overageRevenueStreamCodes;
    }

    /**
     * Determines the {@link PartProductType} of the given {@link IbmPartProductModel}.
     * <p>
     * If the part product has a revenue stream and its code matches any of the configured SLA or
     * overage revenue stream codes, the corresponding {@link PartProductType} is returned.
     * </p>
     *
     * @param partProduct the {@link IbmPartProductModel} to evaluate
     * @return {@link PartProductType#SLA} if it matches SLA codes, {@link PartProductType#OVERAGE}
     * if it matches overage codes, or {@code null} if no match is found or revenue stream is not
     * set
     */
    public PartProductType getProductType(IbmPartProductModel partProduct) {
        if (Objects.nonNull(partProduct.getRevenueStream())) {
            if (StringUtils.isNotBlank(getSlaRevenueStreamCodes())
                && getSlaRevenueStreamCodes().contains(
                partProduct.getRevenueStream().getCode())) {
                return PartProductType.SLA;
            } else if (StringUtils.isNotBlank(getOverageRevenueStreamCodes())
                && getOverageRevenueStreamCodes().contains(
                partProduct.getRevenueStream().getCode())) {
                return PartProductType.OVERAGE;
            }
        }
        return null;
    }

    /**
     * Gets the configured SLA revenue stream codes.
     *
     * @return the SLA revenue stream codes as a string
     */
    public String getSlaRevenueStreamCodes() {
        return slaRevenueStreamCodes;
    }

    /**
     * Gets the configured overage revenue stream codes.
     *
     * @return the overage revenue stream codes as a string
     */
    public String getOverageRevenueStreamCodes() {
        return overageRevenueStreamCodes;
    }

}
