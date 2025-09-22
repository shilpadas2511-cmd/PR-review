package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import org.springframework.lang.NonNull;

/**
 * Populates Basic Details in B2bUnitData
 */
public class B2BUnitActiveFlagDetailsPopulator implements Populator<B2BUnitModel, B2BUnitData> {

    private final PartnerB2BUnitFacade b2BUnitFacade;

    public B2BUnitActiveFlagDetailsPopulator(final PartnerB2BUnitFacade b2BUnitFacade) {
        this.b2BUnitFacade = b2BUnitFacade;
    }


    /**
     * In this populate, transferring data from source to target
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(@NonNull final B2BUnitModel source, @NonNull final B2BUnitData target) {

        target.setActive(source.getActive());
    }

    public PartnerB2BUnitFacade getB2BUnitFacade() {
        return b2BUnitFacade;
    }
}
