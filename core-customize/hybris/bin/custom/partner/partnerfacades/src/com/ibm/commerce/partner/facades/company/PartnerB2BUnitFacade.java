package com.ibm.commerce.partner.facades.company;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitListData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;

/**
 * Interface to define b2bUnit related operations.
 */
public interface PartnerB2BUnitFacade extends B2BUnitFacade {

    /**
     * Fetch the B2BUnit based on partnerB2BUnit.uid if it exist, otherwise create the B2bUnit. It
     * also creates new address for B2BUnit if it doesnot exist for B2BUnit.
     *
     * @param partnerB2BUnit
     * @return
     */
    B2BUnitModel getOrCreate(IbmB2BUnitData partnerB2BUnit);

    /**
     * fetch the PartnerAgreementDetailModel based on partnerAgreementDetailData if it exists with
     * b2bUnit, otherwise we will set new PartnerAgreementDetail to cart and End Customer b2b unit.
     *
     * @param partnerAgreementDetailData
     * @return
     */
    IbmPartnerAgreementDetailModel getOrCreatePartnerAgreementDetail(
        IbmPartnerAgreementDetailData partnerAgreementDetailData,
        IbmPartnerEndCustomerB2BUnitModel b2BUnitModel);

    /**
     * Validates if the B2BUnitModel is active or not for the Current User
     *
     * @param b2BUnitModel
     * @return
     */
    boolean isActive(B2BUnitModel b2BUnitModel);

    /**
     * Fetch B2BUnitModel by uid
     *
     * @param uid
     * @param isSearchRestrictionDisabled
     * @return B2BUnitModel
     */
    B2BUnitModel getUnitByUid(String uid, boolean isSearchRestrictionDisabled);

    /**
     * Fetch eligible IbmPartnerB2BUnitListData
     *
     * @param b2BUnitListData
     */
    void fetchEligibleB2BUnitDetails(IbmPartnerB2BUnitListData b2BUnitListData);

    /**
     * Creating IBMPartnerB2BUnit if it is not exist, or get the IBMPartnerB2BUnit if exists
     *
     * @param partnerResellerSiteIdResponseData object of PartnerResellerSiteIdResponseData
     */
    void createB2BSite(PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData);

}