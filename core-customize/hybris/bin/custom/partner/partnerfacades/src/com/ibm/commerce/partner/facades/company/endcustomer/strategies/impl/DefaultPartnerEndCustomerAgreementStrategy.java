package com.ibm.commerce.partner.facades.company.endcustomer.strategies.impl;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.facades.company.endcustomer.strategies.PartnerEndCustomerAgreementStrategy;
import com.ibm.commerce.partner.facades.comparators.PartnerAgreementDetailComparator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Implementation for {@link PartnerEndCustomerAgreementStrategy}
 */
public class DefaultPartnerEndCustomerAgreementStrategy implements
    PartnerEndCustomerAgreementStrategy {

    private Converter<IbmPartnerAgreementDetailData, IbmPartnerAgreementDetailModel> agreementDetailReverseConverter;
    private Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDetailConverter;
    private PartnerAgreementDetailComparator agreementDetailComparator;
    private ModelService modelService;

    public DefaultPartnerEndCustomerAgreementStrategy(
        final Converter<IbmPartnerAgreementDetailData, IbmPartnerAgreementDetailModel> agreementDetailReverseConverter,
        final Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDetailConverter,
        final PartnerAgreementDetailComparator agreementDetailComparator,
        final ModelService modelService) {
        this.agreementDetailReverseConverter = agreementDetailReverseConverter;
        this.agreementDetailConverter = agreementDetailConverter;
        this.agreementDetailComparator = agreementDetailComparator;
        this.modelService = modelService;
    }

    @Override
    public IbmPartnerAgreementDetailModel getOrCreate(
        final IbmPartnerAgreementDetailData agreementDetailData,
        final IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnit) {
        if (endCustomerB2BUnit == null || agreementDetailData == null || StringUtils.isEmpty(
            agreementDetailData.getProgramType())) {
            return null;
        }
        IbmPartnerAgreementDetailModel existingAgreement = checkExistingAgreementAttachedWithB2BUnit(
            agreementDetailData, endCustomerB2BUnit);
        if (existingAgreement != null) {
            return existingAgreement;
        }
        return createAndSaveAgreementDetails(agreementDetailData, endCustomerB2BUnit);
    }

    /**
     * method to fetch existing agreement attached with b2bUnit.
     *
     * @param agreementDetailData
     * @param b2bUnitModel
     * @return
     */
    protected IbmPartnerAgreementDetailModel checkExistingAgreementAttachedWithB2BUnit(
        final IbmPartnerAgreementDetailData agreementDetailData,
        IbmPartnerEndCustomerB2BUnitModel b2bUnitModel) {
        if (CollectionUtils.isNotEmpty(b2bUnitModel.getAgreementDetails())) {
            for (IbmPartnerAgreementDetailModel partnerAgreementDetailModel : b2bUnitModel.getAgreementDetails()) {
                IbmPartnerAgreementDetailData partnerAgreementB2bUnitDetailData = getPartnerAgreementDataConverter().convert(
                    partnerAgreementDetailModel);
                if (partnerAgreementB2bUnitDetailData != null && (
                    getAgreementDetailComparator().compare(partnerAgreementB2bUnitDetailData,
                        agreementDetailData) == NumberUtils.INTEGER_ZERO)) {
                    return partnerAgreementDetailModel;
                }
            }
        }
        return null;
    }

    /**
     * Create and Save Partner Agreement Details
     *
     * @param agreementDetailData
     * @param b2bUnitModel
     * @return
     */
    protected IbmPartnerAgreementDetailModel createAndSaveAgreementDetails(
        final IbmPartnerAgreementDetailData agreementDetailData,
        final IbmPartnerEndCustomerB2BUnitModel b2bUnitModel) {
        if (b2bUnitModel != null && agreementDetailData != null && Objects.nonNull(
            agreementDetailData.getProgramType())) {
            final List<IbmPartnerAgreementDetailModel> agreementDetails = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(b2bUnitModel.getAgreementDetails())) {
                agreementDetails.addAll(b2bUnitModel.getAgreementDetails());
            }
            final IbmPartnerAgreementDetailModel partnerAgreementDetail = getAgreementDetailReverseConverter().convert(
                agreementDetailData);
            if (partnerAgreementDetail != null) {
                partnerAgreementDetail.setUnit(b2bUnitModel);
                agreementDetails.add(partnerAgreementDetail);
                b2bUnitModel.setAgreementDetails(agreementDetails);
                getModelService().saveAll(partnerAgreementDetail, b2bUnitModel);
            }
            return partnerAgreementDetail;
        }
        return null;
    }

    public Converter<IbmPartnerAgreementDetailData, IbmPartnerAgreementDetailModel> getAgreementDetailReverseConverter() {
        return agreementDetailReverseConverter;
    }


    public Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> getPartnerAgreementDataConverter() {
        return agreementDetailConverter;
    }

    public PartnerAgreementDetailComparator getAgreementDetailComparator() {
        return agreementDetailComparator;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
