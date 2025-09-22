package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerAddressModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.utils.PartnerAddressUtils;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default Partner DefaultQuoteCreationSoldThroughUnitMapperService MapperService class is used to
 * populate or map the sold through unit details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationSoldThroughUnitMapperService implements
    PartnerQuoteCreationMapperService {

    private static final Logger LOG = Logger.getLogger(DefaultQuoteCreationSoldThroughUnitMapperService.class);
    private ModelService modelService;
    private PartnerB2BUnitService partnerB2BUnitService;
    private CommonI18NService commonI18NService;
    private PartnerCountryService countryService;

    public DefaultQuoteCreationSoldThroughUnitMapperService(ModelService modelService,
        PartnerB2BUnitService partnerB2BUnitService, CommonI18NService commonI18NService,
        PartnerCountryService countryService) {
        this.modelService = modelService;
        this.partnerB2BUnitService = partnerB2BUnitService;
        this.commonI18NService = commonI18NService;
        this.countryService = countryService;
    }


    /**
     * Maps the Reseller details (Sold-Through Unit) from the {@link CpqIbmPartnerQuoteModel} to the {@link IbmPartnerQuoteModel}.
     * The Reseller details are copied from the source model to the target model if they are not null. If the Reseller unit
     * already exists, it is reused; otherwise, a new unit is created and populated.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model containing Reseller (Sold-Through) details. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model where the Reseller unit will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel, IbmPartnerQuoteModel quoteModel) {

        if (ObjectUtils.isNotEmpty(cpqIbmPartnerQuoteModel.getReseller())) {
            CpqIbmPartnerUnitModel cpqSoldThroughUnit = cpqIbmPartnerQuoteModel.getReseller();

            IbmPartnerB2BUnitModel existingSoldThroughUnit = (IbmPartnerB2BUnitModel) getPartnerB2BUnitService().getUnitForUid(
                cpqIbmPartnerQuoteModel.getReseller().getUid(), true);

            if (Objects.nonNull(existingSoldThroughUnit)) {
                quoteModel.setSoldThroughUnit(existingSoldThroughUnit);
            } else {

                validateMandatoryAttributes(cpqSoldThroughUnit, cpqIbmPartnerQuoteModel);
                IbmPartnerB2BUnitModel soldThroughUnit = modelService.create(IbmPartnerB2BUnitModel.class);
                setUnit(soldThroughUnit,cpqSoldThroughUnit,quoteModel);
            }
        }
    }

    /**
     * Assigns the parent group to the given {@code soldThroughUnit} based on the parent of {@code cpqSoldThroughUnit}.
     *
     * @param soldThroughUnit   the {@link IbmPartnerB2BUnitModel} to which the parent group will be assigned.
     * @param cpqSoldThroughUnit the {@link CpqIbmPartnerUnitModel} from which the parent group information is derived.
     * @throws IllegalArgumentException if {@code cpqSoldThroughUnit.getParent()} is null or missing required fields.
     */
    protected void setParent(IbmPartnerB2BUnitModel soldThroughUnit,
        CpqIbmPartnerUnitModel cpqSoldThroughUnit) {
        Set<PrincipalGroupModel> userGroups = new HashSet<>();
        UserGroupModel userGroupModel = getPartnerB2BUnitService().getUnitForUid(
            cpqSoldThroughUnit.getParent().getUid());
        if (userGroupModel != null) {
            userGroups.add(userGroupModel);
        } else {
            UserGroupModel defaultUserGroupModel = getModelService().create(
                UserGroupModel.class);
            defaultUserGroupModel.setUid(cpqSoldThroughUnit.getParent().getUid());
            defaultUserGroupModel.setName(cpqSoldThroughUnit.getParent().getName());
            getModelService().save(defaultUserGroupModel);
            userGroups.add(defaultUserGroupModel);
        }
        soldThroughUnit.setGroups(userGroups);
    }

    /**
     * Converts and maps the address from the {@link CpqIbmPartnerUnitModel} to the {@link IbmPartnerB2BUnitModel}.
     * The address information is copied from the source model to the target model if not null.
     *
     * @param cpqSoldThroughUnit the source CPQ IBM Partner Unit model containing address details. Must not be null.
     * @param soldThroughUnit    the target B2B Unit model where the address will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqSoldThroughUnit` or `soldThroughUnit` is null.
     */
    protected void convertAddress(CpqIbmPartnerUnitModel cpqSoldThroughUnit, IbmPartnerB2BUnitModel soldThroughUnit) {

        CpqIbmPartnerAddressModel cpqAddress = cpqSoldThroughUnit.getAddress();
        if (cpqAddress == null) {
            return;
        }

        AddressModel newAddressModel = getModelService().create(AddressModel.class);
        newAddressModel.setTown(cpqAddress.getTown());
        newAddressModel.setPostalcode(cpqAddress.getPostalCode());
        newAddressModel.setFirstname(cpqAddress.getFirstName());
        newAddressModel.setLastname(cpqAddress.getLastName());
        newAddressModel.setStreetname(cpqAddress.getStreetName());
        newAddressModel.setStreetnumber(cpqAddress.getStreetNumber());

        CountryModel country = getCommonI18NService().getCountry(cpqAddress.getCountry());
        if (country != null) {
            newAddressModel.setCountry(country);

            if (StringUtils.isNotEmpty(cpqAddress.getRegion())) {
                RegionModel region = getCommonI18NService().getRegion(country, cpqAddress.getRegion());
                if (region != null) {
                    newAddressModel.setRegion(region);
                }
            }
        }
        boolean addressExists = false;
        if (soldThroughUnit.getAddresses() != null) {
            for (AddressModel existingAddress : soldThroughUnit.getAddresses()) {
                if (PartnerAddressUtils.areAddressesEqual(existingAddress, newAddressModel)) {
                    addressExists = true;
                    break;
                }
            }
        }
        if (!addressExists) {
            Collection<AddressModel> updatedAddresses = new ArrayList<>(soldThroughUnit.getAddresses());
            updatedAddresses.add(newAddressModel);
            soldThroughUnit.setAddresses(updatedAddresses);
        }
    }

    protected void setUnit( IbmPartnerB2BUnitModel soldThroughUnit,CpqIbmPartnerUnitModel cpqSoldThroughUnit,IbmPartnerQuoteModel quoteModel){
        if (StringUtils.isNotEmpty(cpqSoldThroughUnit.getUid())) {
            soldThroughUnit.setUid(cpqSoldThroughUnit.getUid());
        }
        if (StringUtils.isNotEmpty(cpqSoldThroughUnit.getCurrency())) {
            soldThroughUnit.setCurrency(getCommonI18NService().getCurrentCurrency());
        }
        if (StringUtils.isNotEmpty(cpqSoldThroughUnit.getName())) {
            soldThroughUnit.setName(cpqSoldThroughUnit.getName());
        }

        if (Objects.nonNull(cpqSoldThroughUnit.getReportingOrganization())) {
            IbmPartnerB2BUnitModel reportingOrg = (IbmPartnerB2BUnitModel) getPartnerB2BUnitService().getUnitForUid(
                cpqSoldThroughUnit.getReportingOrganization().getUid(), true);
            soldThroughUnit.setReportingOrganization(reportingOrg);
        }

        if (StringUtils.isNotEmpty(cpqSoldThroughUnit.getCountry())) {
            soldThroughUnit.setCountry(getCountryService().getByCodeOrSapCode(cpqSoldThroughUnit.getCountry()));
        }
        if (cpqSoldThroughUnit.getParent() != null) {
            setParent(soldThroughUnit, cpqSoldThroughUnit);
        }
        if (cpqSoldThroughUnit.getAddress() != null) {
            convertAddress(cpqSoldThroughUnit, soldThroughUnit);
        }
        quoteModel.setSoldThroughUnit(soldThroughUnit);
    }

    private void validateMandatoryAttributes(CpqIbmPartnerUnitModel cpqSoldThroughUnit,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        if (StringUtils.isEmpty(cpqSoldThroughUnit.getUid())) {
            logAndThrowError("UID", cpqIbmPartnerQuoteModel);
        }
        if (StringUtils.isEmpty(cpqSoldThroughUnit.getName())) {
            logAndThrowError("Name", cpqIbmPartnerQuoteModel);
        }
        if (StringUtils.isEmpty(cpqSoldThroughUnit.getCountry())) {
            logAndThrowError("Country", cpqIbmPartnerQuoteModel);
        }
        if (Objects.nonNull(cpqSoldThroughUnit.getReportingOrganization())) {
            logAndThrowError("reportingOrganization", cpqIbmPartnerQuoteModel);
        }
    }

    private void logAndThrowError(String attribute,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        String errorMessage = String.format("Mandatory attribute [%s] for reseller is missing.", attribute);
        logError(attribute, new IllegalArgumentException(errorMessage), cpqIbmPartnerQuoteModel);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Logs an error message when an invalid value is encountered for a specific field in the CPQ
     * IBM Partner Quote model. The log entry includes details such as the field name, quote ID, CPQ
     * quote number, CPQ external ID, and the exception message.
     *
     * @param name                    the name of the field that encountered an invalid value
     * @param exception               the exception thrown due to the invalid value
     * @param cpqIbmPartnerQuoteModel the CPQ IBM Partner Quote model associated with the error
     */
    protected void logError(String name, Exception exception,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        LOG.info(String.format(
            " [%s]. QuoteId: [%s], CPQ QuoteNumber: [%s], CPQ External ID: [%s]. Error: [%s]",
            name,
            cpqIbmPartnerQuoteModel.getCode(),
            cpqIbmPartnerQuoteModel.getCpqQuoteNumber(),
            cpqIbmPartnerQuoteModel.getCpqQuoteExternalId(),
            exception.getMessage()));
    }


    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }
    public PartnerCountryService getCountryService() {
        return countryService;
    }


}
