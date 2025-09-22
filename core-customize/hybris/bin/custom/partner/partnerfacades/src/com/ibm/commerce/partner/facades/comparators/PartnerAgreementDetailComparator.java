package com.ibm.commerce.partner.facades.comparators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Comparator class to check b2bUnitModelAgreement and partnerAgreementData
 */
public class PartnerAgreementDetailComparator implements Comparator<IbmPartnerAgreementDetailData> {

    /**
     * Compare method to check programtype and agreementNumber
     *
     * @param b2bUnitAgreementDetail     the first object to be compared.
     * @param partnerAgreementDetailData the second object to be compared.
     * @return
     */
    @Override
    public int compare(IbmPartnerAgreementDetailData b2bUnitAgreementDetail,
        IbmPartnerAgreementDetailData partnerAgreementDetailData) {
        if (StringUtils.equals(b2bUnitAgreementDetail.getProgramType(),
            partnerAgreementDetailData.getProgramType()) && StringUtils.equals(
            b2bUnitAgreementDetail.getAgreementNumber(),
            partnerAgreementDetailData.getAgreementNumber()) && StringUtils.equals(
            b2bUnitAgreementDetail.getAgreementOption(),
            partnerAgreementDetailData.getAgreementOption()) && StringUtils.equals(
            b2bUnitAgreementDetail.getAgreementLevel(),
            partnerAgreementDetailData.getAgreementLevel())
            && b2bUnitAgreementDetail.isDefaultProgramType()
            == partnerAgreementDetailData.isDefaultProgramType()) {
            return NumberUtils.INTEGER_ZERO;
        }
        return NumberUtils.INTEGER_ONE;
    }
}
