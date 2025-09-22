package com.ibm.commerce.partner.facades.company.impl;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitListData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.company.endcustomer.strategies.PartnerEndCustomerAgreementStrategy;
import com.ibm.commerce.partner.facades.company.strategies.PartnerB2BUnitStrategy;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default implementation of {@link PartnerB2BUnitFacade}
 */
public class DefaultPartnerB2BUnitFacade extends DefaultB2BUnitFacade implements
    PartnerB2BUnitFacade {

    private static final Logger LOG = Logger.getLogger(DefaultPartnerB2BUnitFacade.class);

    private final PartnerB2BUnitStrategy b2bUnitStrategy;

    private final PartnerEndCustomerAgreementStrategy endCustomerAgreementStrategy;

    private final Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter;

    private final PartnerB2BUnitService partnerB2BUnitService;


    public DefaultPartnerB2BUnitFacade(final PartnerB2BUnitStrategy b2bUnitStrategy,
        final PartnerEndCustomerAgreementStrategy endCustomerAgreementStrategy,
        final Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter,
        PartnerB2BUnitService partnerB2BUnitService) {
        this.endCustomerAgreementStrategy = endCustomerAgreementStrategy;
        this.b2bUnitStrategy = b2bUnitStrategy;
        this.partnerB2BUnitDataConverter = partnerB2BUnitDataConverter;
        this.partnerB2BUnitService = partnerB2BUnitService;
    }

    /**
     * Creating PartnerB2BUnit if it is not exist, or get the PartnerB2BUnit if exists
     *
     * @param b2bUnitData object of PartnerB2BUnitData
     * @return PartnerB2BUnitModel
     */
    @Override
    public B2BUnitModel getOrCreate(final IbmB2BUnitData b2bUnitData) {
        try {
            final B2BUnitModel b2bUnitModel = getB2bUnitStrategy().getOrCreateUnit(b2bUnitData);
            return b2bUnitModel;
        } catch (RuntimeException e) {
            LOG.error("Ignoring unit due to the error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isActive(final B2BUnitModel b2bUnit) {
        return getPartnerB2BUnitService().isActive(b2bUnit);
    }

    /**
     * Fetch B2BUnitModel by uid
     *
     * @param uid
     * @param isSearchRestrictionDisabled
     * @return B2BUnitModel
     */
    public B2BUnitModel getUnitByUid(String uid, boolean isSearchRestrictionDisabled) {
        return (B2BUnitModel) getPartnerB2BUnitService().getUnitForUid(uid,
            isSearchRestrictionDisabled);
    }

    /**
     * Fetch eligible IbmPartnerB2BUnitListData
     *
     * @param b2BUnitListData
     */
    public void fetchEligibleB2BUnitDetails(IbmPartnerB2BUnitListData b2BUnitListData) {

        List<IbmPartnerB2BUnitData> sites = b2BUnitListData.getSites();
        List<IbmPartnerB2BUnitData> eligibleSites = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sites)) {
            eligibleSites.addAll(sites.stream().filter(b2bUnitData -> StringUtils.isNotBlank(
                    b2bUnitData.getUid()))  // Check if UID is not blank
                .filter(b2bUnitData -> getUnitByUid(b2bUnitData.getUid(), Boolean.TRUE)
                    != null)  // Check if unit exists for the given UID
                .collect(Collectors.toList())  // Collect the eligible sites into a list
            );
        }

        b2BUnitListData.setSites(eligibleSites);
    }

    /**
     * Creating IBMPartnerB2BUnit if it is not exist, or get the IBMPartnerB2BUnit if exists
     *
     * @param partnerResellerSiteIdResponseData object of PartnerResellerSiteIdResponseData
     */
    public void createB2BSite(PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData) {
        if (null != partnerResellerSiteIdResponseData) {
            IbmPartnerB2BUnitData partnerB2BUnitData = getPartnerB2BUnitDataConverter().convert(
                partnerResellerSiteIdResponseData);
            B2BUnitModel b2BUnitModel = getOrCreate(partnerB2BUnitData);
            if (b2BUnitModel != null) {
                getModelService().save(b2BUnitModel);
            }
        }
    }


    /**
     * get or create {@link IbmPartnerAgreementDetailModel}.
     *
     * @param agreementDetailData
     * @return
     */
    @Override
    public IbmPartnerAgreementDetailModel getOrCreatePartnerAgreementDetail(
        final IbmPartnerAgreementDetailData agreementDetailData,
        IbmPartnerEndCustomerB2BUnitModel b2bUnitModel) {
        return getEndCustomerAgreementStrategy().getOrCreate(agreementDetailData, b2bUnitModel);
    }


    public PartnerB2BUnitStrategy getB2bUnitStrategy() {
        return b2bUnitStrategy;
    }

    public PartnerEndCustomerAgreementStrategy getEndCustomerAgreementStrategy() {
        return endCustomerAgreementStrategy;
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> getPartnerB2BUnitDataConverter() {
        return partnerB2BUnitDataConverter;
    }
}