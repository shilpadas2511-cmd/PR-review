package com.ibm.commerce.partner.facades.company.converter.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import org.springframework.lang.NonNull;

/**
 * Populates Basic Details in B2bUnitData
 */
public class IbmPartnerEndCustomerActiveFlagDetailsPopulator implements
    Populator<B2BUnitModel, B2BUnitData> {


    /**
     * In this populate, transferring data from source to target
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(@NonNull final B2BUnitModel source, @NonNull final B2BUnitData target) {

        target.setActive(Boolean.TRUE);
    }

}
