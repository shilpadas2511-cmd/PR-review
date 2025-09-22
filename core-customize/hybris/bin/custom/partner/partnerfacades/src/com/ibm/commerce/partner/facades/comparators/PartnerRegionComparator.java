package com.ibm.commerce.partner.facades.comparators;

import de.hybris.platform.core.model.c2l.RegionModel;
import java.util.Comparator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Comparator to compare Region Models
 */
public class PartnerRegionComparator implements Comparator<RegionModel> {

    @Override
    public int compare(final RegionModel o1, final RegionModel o2) {
        return (ObjectUtils.allNull(o1, o2) || (ObjectUtils.allNotNull(o1, o2) && o1.getIsocode()
            .equalsIgnoreCase(o2.getIsocode()))) ? NumberUtils.INTEGER_ZERO
            : NumberUtils.INTEGER_ONE;
    }
}
