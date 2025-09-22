package com.ibm.commerce.partner.facades.company.strategies.impl;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.facades.company.strategies.PartnerB2BUnitStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation for
 * {@link com.ibm.commerce.partner.facades.company.strategies.PartnerB2BUnitStrategy}
 */
public class DefaultPartnerB2BUnitStrategy implements PartnerB2BUnitStrategy {

    private final Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> b2bUnitReverseConverterMap;
    private final Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> updateB2bUnitReverseConverterMap;

    private final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    private final EnumerationService enumerationService;

    private final ModelService modelService;

    private final Converter<B2BUnitData, IbmB2BUnitData> ibmB2BUnitDataConverter;

    public DefaultPartnerB2BUnitStrategy(
        final Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> b2bUnitReverseConverterMap,
        final Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> updateB2bUnitReverseConverterMap,
        final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService,
        final EnumerationService enumerationService, final ModelService modelService,
        final Converter<B2BUnitData, IbmB2BUnitData> ibmB2BUnitDataConverter) {
        this.b2bUnitReverseConverterMap = b2bUnitReverseConverterMap;
        this.updateB2bUnitReverseConverterMap = updateB2bUnitReverseConverterMap;
        this.b2BUnitService = b2BUnitService;
        this.enumerationService = enumerationService;
        this.modelService = modelService;
        this.ibmB2BUnitDataConverter = ibmB2BUnitDataConverter;
    }

    @Override
    public B2BUnitModel getOrCreateUnit(final IbmB2BUnitData b2BUnitData) {
        if (b2BUnitData == null || StringUtils.isBlank(b2BUnitData.getUid())) {
            return null;
        }

        final B2BUnitModel unitForUid = getB2BUnitService().getUnitForUid(b2BUnitData.getUid(),
            Boolean.TRUE);

        IbmPartnerB2BUnitType type = getType(b2BUnitData);
        if (unitForUid == null) {
            B2BUnitModel newB2BUnit = getB2bUnitReverseConverterMap().get(type)
                .convert(b2BUnitData);
            populateParent(b2BUnitData, newB2BUnit);
            // Add the distributor b2b unit in the reseller b2b unit with new reseller
            populateReportingOrganisation(b2BUnitData, newB2BUnit);
            return forceUpdate(newB2BUnit);
        }
        B2BUnitModel existingB2BUnit = getUpdateB2bUnitReverseConverterMap().get(type)
            .convert(b2BUnitData, unitForUid);
        populateParent(b2BUnitData, existingB2BUnit);
        // Add the distributor b2b unit in the reseller b2b unit with existing resller
        populateReportingOrganisation(b2BUnitData, existingB2BUnit);
        return forceUpdate(existingB2BUnit);

    }

    protected void populateParent(final B2BUnitData source, final B2BUnitModel target)
        throws ConversionException {

        if (source.getUnit() != null) {
            final IbmB2BUnitData ibmB2BUnitData = getIbmB2BUnitDataConverter().convert(
                source.getUnit());
            final B2BUnitModel parentUnit = getOrCreateUnit(ibmB2BUnitData);
            getB2BUnitService().updateParentB2BUnit(parentUnit, target);
        }
    }

    protected void populateReportingOrganisation(final B2BUnitData source,
        final B2BUnitModel target) throws ConversionException {

        if (source.getReportingOrganization() != null
            && source.getReportingOrganization() instanceof IbmB2BUnitData ibmB2BUnitData) {
            final B2BUnitModel parentUnit = getOrCreateUnit(ibmB2BUnitData);
            target.setReportingOrganization(parentUnit);
        }
    }

    protected B2BUnitModel forceUpdate(B2BUnitModel b2BUnitModel) {
        getModelService().save(b2BUnitModel);
        if (CollectionUtils.isNotEmpty(b2BUnitModel.getGroups())) {
            getModelService().saveAll(b2BUnitModel.getGroups());
        }
        return b2BUnitModel;
    }


    protected IbmPartnerB2BUnitType getType(IbmB2BUnitData b2BUnitData) {
        if (b2BUnitData.getType() == null || StringUtils.isBlank(b2BUnitData.getType().getCode())) {
            return IbmPartnerB2BUnitType.DEFAULT;
        }
        IbmPartnerB2BUnitType b2BUnitType = getEnumerationService().getEnumerationValue(
            IbmPartnerB2BUnitType.class, b2BUnitData.getType().getCode());
        if (b2BUnitType == null) {
            return IbmPartnerB2BUnitType.DEFAULT;
        }
        return b2BUnitType;
    }

    public Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> getB2bUnitReverseConverterMap() {
        return b2bUnitReverseConverterMap;
    }

    public Map<IbmPartnerB2BUnitType, Converter<B2BUnitData, B2BUnitModel>> getUpdateB2bUnitReverseConverterMap() {
        return updateB2bUnitReverseConverterMap;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public Converter<B2BUnitData, IbmB2BUnitData> getIbmB2BUnitDataConverter() {
        return ibmB2BUnitDataConverter;
    }
}
