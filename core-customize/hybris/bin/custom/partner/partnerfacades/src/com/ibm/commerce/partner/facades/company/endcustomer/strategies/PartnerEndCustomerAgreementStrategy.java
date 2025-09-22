package com.ibm.commerce.partner.facades.company.endcustomer.strategies;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;

/**
 * Strategy to fetch or create
 * {@link com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel} for end Customer
 */
public interface PartnerEndCustomerAgreementStrategy {

    /**
     * Fetch/Create {@link IbmPartnerAgreementDetailModel} linked to
     * {@link IbmPartnerEndCustomerB2BUnitModel}
     *
     * @param agreementDetailData
     * @param endCustomerB2BUnit
     * @return
     */
    IbmPartnerAgreementDetailModel getOrCreate(IbmPartnerAgreementDetailData agreementDetailData,
        IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnit);

}
