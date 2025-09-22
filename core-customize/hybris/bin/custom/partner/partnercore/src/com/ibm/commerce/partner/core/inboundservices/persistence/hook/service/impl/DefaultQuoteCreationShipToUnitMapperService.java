package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerAddressModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerEndCustomerUnitModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
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
 * Default Partner DefaultQuoteCreationShipToUnitMapperService MapperService class is used to
 * populate or map the ship to Unit details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationShipToUnitMapperService implements
    PartnerQuoteCreationMapperService {

    private static final Logger LOG = Logger.getLogger(DefaultQuoteCreationShipToUnitMapperService.class);

    private ModelService modelService;
    private PartnerCountryService countryService;
    private PartnerB2BUnitService partnerB2BUnitService;
    private CommonI18NService commonI18NService;

    public DefaultQuoteCreationShipToUnitMapperService(ModelService modelService,
        PartnerCountryService countryService,
        PartnerB2BUnitService partnerB2BUnitService, CommonI18NService commonI18NService) {
        this.modelService = modelService;
        this.countryService = countryService;
        this.partnerB2BUnitService = partnerB2BUnitService;
        this.commonI18NService = commonI18NService;
    }


    /**
     * Maps the EndCustomer details from the {@link CpqIbmPartnerQuoteModel} to the {@link
     * IbmPartnerQuoteModel}. The EndCustomer (Ship-To Unit) details from the source model are
     * copied to the target model if they are not null.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which EndCustomer
     *                                details will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is
     *                                  null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {

        if (ObjectUtils.isNotEmpty(cpqIbmPartnerQuoteModel.getEndCustomer())) {
            CpqIbmPartnerEndCustomerUnitModel cpqShipToUnit = cpqIbmPartnerQuoteModel.getEndCustomer();
            IbmPartnerEndCustomerB2BUnitModel existingShipToUnit = (IbmPartnerEndCustomerB2BUnitModel) getPartnerB2BUnitService().getUnitForUid(
                cpqIbmPartnerQuoteModel.getEndCustomer().getUid(), true);

            if (Objects.nonNull(existingShipToUnit)) {
                quoteModel.setUnit(existingShipToUnit);
            } else {
                validateMandatoryAttributes(cpqShipToUnit, cpqIbmPartnerQuoteModel);
                IbmPartnerEndCustomerB2BUnitModel shipToUnit = getModelService().create(
                    IbmPartnerEndCustomerB2BUnitModel.class);
                setUnit(shipToUnit,cpqShipToUnit,quoteModel);
            }
        }
    }

    /**
     * Sets the parent group for the given {@code shipToUnit} based on the parent of {@code cpqShipToUnit}.
     * @param shipToUnit   the {@link IbmPartnerEndCustomerB2BUnitModel} to which the parent group will be assigned.
     * @param cpqShipToUnit the {@link CpqIbmPartnerUnitModel} from which the parent information is derived.
     * @throws IllegalArgumentException if {@code cpqShipToUnit.getParent()} is null.
     */
    protected void setParent(IbmPartnerEndCustomerB2BUnitModel shipToUnit,
        CpqIbmPartnerUnitModel cpqShipToUnit) {
            Set<PrincipalGroupModel> userGroups = new HashSet<>();
            UserGroupModel userGroupModel = getPartnerB2BUnitService().getUnitForUid(
                cpqShipToUnit.getParent().getUid());
            if (userGroupModel != null) {
                userGroups.add(userGroupModel);
            }
            else {
                UserGroupModel defaultUserGroupModel = getModelService().create(
                    UserGroupModel.class);
                defaultUserGroupModel.setUid(cpqShipToUnit.getParent().getUid());
                defaultUserGroupModel.setName(cpqShipToUnit.getParent().getName());
                getModelService().save(defaultUserGroupModel);
                userGroups.add(defaultUserGroupModel);
            }
            shipToUnit.setGroups(userGroups);

    }
    /**
     * Converts and maps the address from the {@link CpqIbmPartnerUnitModel} to the {@link
     * IbmPartnerEndCustomerB2BUnitModel}. The address information is copied from the source model
     * to the target model if not null.
     *
     * @param cpqShipToUnit the source CPQ IBM Partner Unit model containing address details. Must
     *                      not be null.
     * @param shipToUnit    the target EndCustomer B2B Unit model to which address details will be
     *                      mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqShipToUnit` or `shipToUnit` is null.
     */
    protected void convertAddress(CpqIbmPartnerUnitModel cpqShipToUnit,
        IbmPartnerEndCustomerB2BUnitModel shipToUnit) {

        CpqIbmPartnerAddressModel cpqAddress = cpqShipToUnit.getAddress();
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
        if (shipToUnit.getAddresses() != null) {
            for (AddressModel existingAddress : shipToUnit.getAddresses()) {
                if (PartnerAddressUtils.areAddressesEqual(existingAddress, newAddressModel)) {
                    addressExists = true;
                    break;
                }
            }
        }
        if (!addressExists) {
            Collection<AddressModel> updatedAddresses = new ArrayList<>(shipToUnit.getAddresses());
            updatedAddresses.add(newAddressModel);
            shipToUnit.setAddresses(updatedAddresses);
        }
    }

    protected void setUnit(IbmPartnerEndCustomerB2BUnitModel shipToUnit,CpqIbmPartnerEndCustomerUnitModel cpqShipToUnit,IbmPartnerQuoteModel quoteModel) {
        if (StringUtils.isNotEmpty(cpqShipToUnit.getUid())) {
            shipToUnit.setUid(cpqShipToUnit.getUid());
        }
        if (StringUtils.isNotEmpty(cpqShipToUnit.getCurrency())) {
            shipToUnit.setCurrency(getCommonI18NService().getCurrentCurrency());
        }
        if (StringUtils.isNotEmpty(cpqShipToUnit.getName())) {
            shipToUnit.setName(cpqShipToUnit.getName());
        }
        shipToUnit.setGoe(cpqShipToUnit.getGoe());
        if (StringUtils.isNotEmpty(cpqShipToUnit.getCountry())) {
            shipToUnit.setCountry(
                getCountryService().getByCodeOrSapCode(cpqShipToUnit.getCountry()));
        }
        if (Objects.nonNull(shipToUnit.getType())
            && IbmPartnerB2BUnitType.ENDCUSTOMER.equals(cpqShipToUnit.getType())) {
            shipToUnit.setType(IbmPartnerB2BUnitType.ENDCUSTOMER);
        }
        if (cpqShipToUnit.getParent() != null) {
            setParent(shipToUnit, cpqShipToUnit);
        }
        if (cpqShipToUnit.getAddress() != null) {
            convertAddress(cpqShipToUnit, shipToUnit);
        }
        quoteModel.setUnit(shipToUnit);
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
    }

    private void logAndThrowError(String attribute, CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        String errorMessage = String.format("Mandatory attribute [%s] for end customer is missing.", attribute);
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


    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerCountryService getCountryService() {
        return countryService;
    }
}
