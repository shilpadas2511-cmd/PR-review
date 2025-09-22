package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.utils.PartnerUtils;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import de.hybris.platform.converters.Populator;

/**
 * populator class is used to populate PartnerAgreementDetailData from PartnerAgreementDetailModel.
 */
public class PartnerAgreementDetailPopulator implements
    Populator<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> {

    /**
     * populate method to populate PartnerAgreementDetailData attributes
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(final IbmPartnerAgreementDetailModel source,
        final IbmPartnerAgreementDetailData target) {
        validateParameterNotNullStandardMessage("target", target);
        validateParameterNotNullStandardMessage("source", source);
        target.setAgreementNumber(source.getAgreementNumber());
        target.setProgramType(source.getProgramType());
        target.setAgreementOption(source.getAgreementOption());
        target.setAgreementLevel(source.getAgreementLevel());
        target.setAnniversaryMonth(
            PartnerUtils.getAnniversaryMonth(source.getAnniversaryMonth()));
        target.setDefaultProgramType(source.isDefaultProgramType());
    }


}
