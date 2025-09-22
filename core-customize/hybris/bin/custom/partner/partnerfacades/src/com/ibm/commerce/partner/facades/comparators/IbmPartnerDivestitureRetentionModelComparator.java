package com.ibm.commerce.partner.facades.comparators;

import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Comparing {@link IbmPartnerDivestitureRetentionModel}
 */
public class IbmPartnerDivestitureRetentionModelComparator implements
    Comparator<IbmPartnerDivestitureRetentionModel> {

    @Override
    public int compare(final IbmPartnerDivestitureRetentionModel o1,
        final IbmPartnerDivestitureRetentionModel o2) {
        return StringUtils.equalsIgnoreCase(o1.getEntmtType(), o2.getEntmtType())
            && StringUtils.equalsIgnoreCase(o1.getEntmtTypeDesc(), o2.getEntmtTypeDesc())
            && StringUtils.equalsIgnoreCase(o1.getSapDivsttrCode(), o2.getSapDivsttrCode())
            && DateUtils.isSameDay(o1.getRetainedEndDate(), o2.getRetainedEndDate())
            ? NumberUtils.INTEGER_ZERO : NumberUtils.INTEGER_ONE;
    }
}
