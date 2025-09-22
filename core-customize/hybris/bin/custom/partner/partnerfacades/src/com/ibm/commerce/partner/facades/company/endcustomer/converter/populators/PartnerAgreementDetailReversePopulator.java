package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * This class is used to populate PartnerAgreementDetailModel from PartnerAgreementDetailData.
 */

public class PartnerAgreementDetailReversePopulator implements
    Populator<IbmPartnerAgreementDetailData, IbmPartnerAgreementDetailModel> {

    /**
     * method to populate the PartnerAgreementDetailModel from PartnerAgreementDetailData.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */

    @Override
    public void populate(@NonNull IbmPartnerAgreementDetailData source,
        @NonNull IbmPartnerAgreementDetailModel target) throws ConversionException {
        validateParameterNotNullStandardMessage("target", target);
        validateParameterNotNullStandardMessage("source", source);
        target.setProgramType(source.getProgramType());
        target.setAgreementNumber(source.getAgreementNumber());
        target.setAgreementOption(source.getAgreementOption());
        target.setAgreementLevel(source.getAgreementLevel());
        target.setAnniversaryMonth(source.getAnniversaryMonth());
        target.setDefaultProgramType(source.isDefaultProgramType());
    }
}

