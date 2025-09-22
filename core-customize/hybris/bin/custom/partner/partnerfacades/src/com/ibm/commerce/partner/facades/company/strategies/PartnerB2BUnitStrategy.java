package com.ibm.commerce.partner.facades.company.strategies;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import de.hybris.platform.b2b.model.B2BUnitModel;

/**
 * Strategy to define the
 */
public interface PartnerB2BUnitStrategy {

    /**
     * Fetches or Creates B2BUnit
     *
     * @param b2BUnitData
     * @return
     */
    B2BUnitModel getOrCreateUnit(IbmB2BUnitData b2BUnitData);

}
