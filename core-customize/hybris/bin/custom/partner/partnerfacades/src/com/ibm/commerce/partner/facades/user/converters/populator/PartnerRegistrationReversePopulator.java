package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.services.LanguageService;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.HashSet;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

/**
 * This Reverser Populator is for transferring the data from B2BRegistrationData to
 * PartnerB2BCustomerModel
 */
public class PartnerRegistrationReversePopulator implements
    Populator<PartnerB2BRegistrationData, PartnerB2BCustomerModel> {

    private final String defaultB2BUnit;

    private final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    private final CommonI18NService commonI18NService;

    private final LanguageService languageService;

    private final CustomerNameStrategy customerNameStrategy;
    private final UserService userService;

    public PartnerRegistrationReversePopulator(final String defaultB2BUnit,
        final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService,
        final CommonI18NService commonI18NService, final LanguageService languageService,
        final CustomerNameStrategy customerNameStrategy, final UserService userService) {
        this.defaultB2BUnit = defaultB2BUnit;
        this.b2bUnitService = b2bUnitService;
        this.commonI18NService = commonI18NService;
        this.languageService = languageService;
        this.customerNameStrategy = customerNameStrategy;
        this.userService = userService;
    }

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(@NonNull final PartnerB2BRegistrationData source,
        @NonNull final PartnerB2BCustomerModel target) {
        if (StringUtils.isNotBlank(source.getUid())) {
            target.setUid(source.getUid());
        }
        if (StringUtils.isNotBlank(source.getCustomerUid())) {
            target.setCustomerID(source.getCustomerUid());
        }
        target.setName(
            getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));

        updateLanguage(source, target);
        updateCountry(source, target);
        updateGroup(source, target);
        updateActive(source, target);
    }

    protected void updateActive(final PartnerB2BRegistrationData source,
        final PartnerB2BCustomerModel target) {
        target.setActive(Boolean.TRUE);
        target.setLoginDisabled(Boolean.FALSE);
    }

    protected void updateLanguage(final PartnerB2BRegistrationData source,
        final PartnerB2BCustomerModel target) {
        final String isoCode =
            source.getDefaultLanguage() != null ? source.getDefaultLanguage().getIsocode()
                : StringUtils.EMPTY;
        target.setSessionLanguage(getLanguageService().getOrDefaultLanguage(isoCode));
    }

    protected void updateCountry(final PartnerB2BRegistrationData source,
        final PartnerB2BCustomerModel target) {
        if (Objects.nonNull(source.getDefaultCountry()) && StringUtils.isNotBlank(
            source.getDefaultCountry().getIsocode())) {
            target.setDefaultCountry(
                getCommonI18NService().getCountry(source.getDefaultCountry().getIsocode()));
        }
    }

    protected void updateGroup(final PartnerB2BRegistrationData source,
        final PartnerB2BCustomerModel target) {
        if (CollectionUtils.isEmpty(target.getGroups())) {
            target.setGroups(new HashSet<>());
        }

        if (target.getGroups().stream().noneMatch(B2BUnitModel.class::isInstance)) {
            B2BUnitModel defaultB2BUnitModel;
            if (source.getSiteId() != null && StringUtils.isNotBlank(source.getSiteId().getUid())) {
                defaultB2BUnitModel = getB2bUnitService().getUnitForUid(source.getSiteId().getUid(),
                    Boolean.TRUE);
                target.setDefaultB2BUnit(defaultB2BUnitModel);
            } else {
                defaultB2BUnitModel = getB2bUnitService().getUnitForUid(getDefaultB2BUnit(),
                    Boolean.TRUE);
            }
            target.getGroups().add(defaultB2BUnitModel);
        }

        if (CollectionUtils.isNotEmpty(source.getRoles())) {
            source.getRoles().stream().filter(
                    role -> target.getGroups().stream().noneMatch(group -> group.getUid().equals(role)))
                .map(role -> getUserService().getUserGroupForUID(role)).filter(Objects::nonNull)
                .forEach(role -> target.getGroups().add(role));

        }
    }

    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public LanguageService getLanguageService() {
        return languageService;
    }

    public CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }

    public String getDefaultB2BUnit() {
        return defaultB2BUnit;
    }

    public UserService getUserService() {
        return userService;
    }
}