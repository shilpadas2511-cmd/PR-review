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
import de.hybris.platform.b2b.model.B2BUnitModel;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default Partner DefaultQuoteCreationBillToUnitMapperService MapperService class is used to
 * populate or map the bill to Unit details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationBillToUnitMapperService implements
    PartnerQuoteCreationMapperService {

    private static final Logger LOG = Logger.getLogger(DefaultQuoteCreationBillToUnitMapperService.class);


    private PartnerB2BUnitService partnerB2BUnitService;
    private CommonI18NService commonI18NService;
    private ModelService modelService;
    private PartnerCountryService countryService;

    public DefaultQuoteCreationBillToUnitMapperService(ModelService modelService,
        PartnerB2BUnitService partnerB2BUnitService, CommonI18NService commonI18NService,
        PartnerCountryService countryService) {
        this.modelService = modelService;
        this.partnerB2BUnitService = partnerB2BUnitService;
        this.commonI18NService = commonI18NService;
        this.countryService = countryService;
    }

    /**
     * Maps the distributor information from a {@link CpqIbmPartnerQuoteModel} to an {@link IbmPartnerQuoteModel}.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which data will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {

        if (cpqIbmPartnerQuoteModel.getDistributor() != null) {
            CpqIbmPartnerUnitModel cpqBillToUnit = cpqIbmPartnerQuoteModel.getDistributor();

            IbmPartnerB2BUnitModel existingBillToUnit = (IbmPartnerB2BUnitModel) getPartnerB2BUnitService()
                .getUnitForUid(cpqBillToUnit.getUid(), true);

            if (existingBillToUnit != null) {
                quoteModel.setBillToUnit(existingBillToUnit);
            } else {

                validateMandatoryAttributes(cpqBillToUnit, cpqIbmPartnerQuoteModel);
                IbmPartnerB2BUnitModel billToUnit = modelService.create(IbmPartnerB2BUnitModel.class);
                setUnitDetails(cpqBillToUnit,billToUnit,quoteModel);
            }
        }
    }

    /**
     * Assigns the parent group to the specified {@code billToUnit} based on the parent of the {@code cpqBillToUnit}.
     *
     * @param billToUnit   the {@link IbmPartnerB2BUnitModel} to which the parent group will be assigned.
     * @param cpqBillToUnit the {@link CpqIbmPartnerUnitModel} from which the parent group information is derived.
     *
     * @throws IllegalArgumentException if {@code cpqBillToUnit.getParent()} is null or the parent lacks required data.
     */
    protected void setParent(IbmPartnerB2BUnitModel billToUnit,
        CpqIbmPartnerUnitModel cpqBillToUnit) {

        Set<PrincipalGroupModel> userGroups = new HashSet<>();
        UserGroupModel userGroupModel = getPartnerB2BUnitService().getUnitForUid(
            cpqBillToUnit.getParent().getUid());
        if (userGroupModel != null) {
            userGroups.add(userGroupModel);
        } else {
            UserGroupModel defaultUserGroupModel = getModelService().create(
                UserGroupModel.class);
            defaultUserGroupModel.setUid(cpqBillToUnit.getParent().getUid());
            defaultUserGroupModel.setName(cpqBillToUnit.getParent().getName());
            getModelService().save(defaultUserGroupModel);
            userGroups.add(defaultUserGroupModel);
        }
        billToUnit.setGroups(userGroups);
    }

    /**
     * Converts the address details from a {@link CpqIbmPartnerUnitModel} to an {@link IbmPartnerB2BUnitModel}.
     *
     * @param cpqSoldThroughUnit the source CPQ Sold Through Unit containing address details. Must not be null.
     * @param billToUnit         the target BillToUnit to which the address details will be mapped. Must not be null.
     *                           If either input is null, the method will safely exit without performing any operations.
     */
    protected void convertAddress(CpqIbmPartnerUnitModel cpqSoldThroughUnit, IbmPartnerB2BUnitModel billToUnit) {
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
        if (billToUnit.getAddresses() != null) {
            for (AddressModel existingAddress : billToUnit.getAddresses()) {
                if (PartnerAddressUtils.areAddressesEqual(existingAddress, newAddressModel)) {
                    addressExists = true;
                    break;
                }
            }
        }
        if (!addressExists && Objects.nonNull(billToUnit.getAddresses())) {
            Collection<AddressModel> updatedAddresses = new ArrayList<>(billToUnit.getAddresses());
            updatedAddresses.add(newAddressModel);
            billToUnit.setAddresses(updatedAddresses);
        }
    }
    protected void setUnitDetails(CpqIbmPartnerUnitModel cpqBillToUnit,IbmPartnerB2BUnitModel billToUnit,IbmPartnerQuoteModel quoteModel){
        if (StringUtils.isNotEmpty(cpqBillToUnit.getUid())) {
            billToUnit.setUid(cpqBillToUnit.getUid());
        }
        if (StringUtils.isNotEmpty(cpqBillToUnit.getCurrency())) {
            billToUnit.setCurrency(getCommonI18NService().getCurrentCurrency());
        }
        if (StringUtils.isNotEmpty(cpqBillToUnit.getName())) {
            billToUnit.setName(cpqBillToUnit.getName());
        }

        if (cpqBillToUnit.getReportingOrganization() != null) {
            B2BUnitModel reportOrg = (B2BUnitModel) getPartnerB2BUnitService()
                .getUnitForUid(cpqBillToUnit.getReportingOrganization().getUid(), true);
            if (reportOrg != null) {
                billToUnit.setReportingOrganization(reportOrg);
            }
        }
        if (StringUtils.isNotEmpty(cpqBillToUnit.getCountry())) {
            billToUnit.setCountry(
                getCountryService().getByCodeOrSapCode(cpqBillToUnit.getCountry()));
        }
        if (cpqBillToUnit.getParent() != null) {
            setParent(billToUnit, cpqBillToUnit);
        }
        if (cpqBillToUnit.getAddress() != null) {
            convertAddress(cpqBillToUnit, billToUnit);
        }
        quoteModel.setBillToUnit(billToUnit);

    }

    private void validateMandatoryAttributes(CpqIbmPartnerUnitModel cpqBillToUnit,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        if (StringUtils.isEmpty(cpqBillToUnit.getUid())) {
            logAndThrowError("UID", cpqIbmPartnerQuoteModel);
        }
        if (StringUtils.isEmpty(cpqBillToUnit.getName())) {
            logAndThrowError("Name", cpqIbmPartnerQuoteModel);
        }
        if (StringUtils.isEmpty(cpqBillToUnit.getCountry())) {
            logAndThrowError("Country", cpqIbmPartnerQuoteModel);
        }
    }

    private void logAndThrowError(String attribute, CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        String errorMessage = String.format("Mandatory attribute [%s] for distributor is missing.", attribute);
        logError(attribute, new IllegalArgumentException(errorMessage), cpqIbmPartnerQuoteModel);
        throw new IllegalArgumentException(errorMessage);
    }
    /**
     * Logs an error message when an invalid value is encountered for a specific field in the
     * CPQ IBM Partner Quote model. The log entry includes details such as the field name,
     * quote ID, CPQ quote number, CPQ external ID, and the exception message.
     *
     * @param name the name of the field that encountered an invalid value
     * @param exception the exception thrown due to the invalid value
     * @param cpqIbmPartnerQuoteModel the CPQ IBM Partner Quote model associated with the error
     */
    protected void logError(String name, Exception exception, CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        LOG.info(String.format(" [%s]. QuoteId: [%s], CPQ QuoteNumber: [%s], CPQ External ID: [%s]. Error: [%s]",
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
