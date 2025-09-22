/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.utils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;


/**
 * Utility class for b2b Partner
 */
public class PartnerB2BUnitUtils {


    /**
     * This method is used for filtering the Active Sites based on condition
     *
     * @param b2bCustomerModel customer model
     * @return return active sites
     */
    public static List<B2BUnitModel> filteredActiveSites(
        final B2BCustomerModel b2bCustomerModel) {
        return b2bCustomerModel.getGroups().stream()
            .filter(IbmPartnerB2BUnitModel.class::isInstance)
            .map(
                IbmPartnerB2BUnitModel.class::cast)
            .filter(site ->
                PartnerCountryUtils.isCountryActive(site.getCountry()) &&
                    (IbmPartnerB2BUnitType.RESELLER_TIER_2.equals(site.getType())
                        || PartnerCountryUtils.isCurrencyActive(site.getCurrency()))
            )
            .collect(Collectors.toList());
    }

    /**
     * This method is used extract the active sites on the customer
     *
     * @param customer list of B2BCustomerModel
     * @param defaultPartnerB2BUnitId partnerB2BUnit
     */
    public static boolean findAnyNotActiveSite(final B2BCustomerModel customer,
        final String defaultPartnerB2BUnitId) {

        return customer.getGroups().stream()
            .filter(IbmPartnerB2BUnitModel.class::isInstance)
            .map(IbmPartnerB2BUnitModel.class::cast)
            .filter(site -> !defaultPartnerB2BUnitId.equalsIgnoreCase(site.getUid()))
            .anyMatch(site ->
                !PartnerCountryUtils.isCountryActive(site.getCountry())
                    || (!IbmPartnerB2BUnitType.RESELLER_TIER_2.equals(site.getType())
                    && !PartnerCountryUtils.isCurrencyActive(site.getCurrency())));
    }


    /**
     * This method is used for checking sites which are not active
     *
     * @param countryIsoCode      The ISO code of the site's country.
     * @param currencyIsoCode     The ISO code of the site's currency.
     * @param type                The type of the site.
     * @param activeCountryCodes  A list of ISO codes representing active countries.
     * @param activeCurrencyCodes A list of ISO codes representing active currencies.
     * @return {@code true} if the site is considered inactive; {@code false} otherwise.
     */
    public static boolean notActiveSiteByType(final String countryIsoCode,
        final String currencyIsoCode,
        final String type,
        final List<String> activeCountryCodes, final List<String> activeCurrencyCodes) {
        if ((CollectionUtils.isEmpty(activeCountryCodes) || !activeCountryCodes.contains(
            countryIsoCode)) || (
            !IbmPartnerB2BUnitType.RESELLER_TIER_2.getCode().equalsIgnoreCase(type) && (
                CollectionUtils.isEmpty(
                    activeCurrencyCodes) || !activeCurrencyCodes.contains(currencyIsoCode)))) {
            return true;
        }
        return false;
    }

    /**
     * This method is used for combining ISO code and SAP code into a List
     *
     * @param countries list of CountryModel
     */
    public static List<String> getCountrySapAndIsoCode(final List<CountryModel> countries) {

        if (CollectionUtils.isNotEmpty(countries)) {
            return countries.stream().flatMap(
                    country -> List.of(country.getIsocode(), null != country.getSapCode() ? country
                        .getSapCode() : "").stream())
                .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * This method is used for combining ISO code and SAP code into a List
     *
     * @param currencies list of CurrencyModel
     */
    public static List<String> getCurrencySapAndIsoCode(final List<CurrencyModel> currencies) {
        if (CollectionUtils.isNotEmpty(currencies)) {
            return currencies.stream().flatMap(currency -> List.of(currency.getIsocode(),
                    null != currency.getSapCode() ? currency.getSapCode() : "").stream())

                .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * This method is used for combining ISO code and SAP code into a List
     *
     * @param b2bCustomerModel B2BCustomerModel
     */
    public static Set<PrincipalGroupModel> getGroups(final B2BCustomerModel b2bCustomerModel) {
        return b2bCustomerModel.getGroups().stream()
            .filter(group -> !(group instanceof B2BUnitModel))
            .collect(Collectors.toSet());
    }


    /**
     * Retrieves all B2B units associated with the given B2B customer from their assigned groups.
     * <p>
     * This method filters the customer's group memberships to extract only those that are instances
     * of {@link B2BUnitModel}.
     *
     * @param b2bCustomerModel the B2B customer whose B2B units are to be retrieved
     * @return a list of {@link B2BUnitModel} instances the customer belongs to
     */
    public static List<B2BUnitModel> getCustomerB2bUnits(
        final B2BCustomerModel b2bCustomerModel) {
        return b2bCustomerModel.getGroups().stream()
            .filter(group -> (group instanceof B2BUnitModel)).map(unit -> (B2BUnitModel) unit)
            .collect(Collectors.toList());
    }


    /**
     * Filters the provided list of B2B units to exclude units of type {@code RESELLER_TIER_1}.
     * <p>
     * This method operates only on instances of {@link IbmPartnerB2BUnitModel}. Any units not of
     * this type are ignored. Among the filtered units, those that have a type of
     * {@code RESELLER_TIER_1} are excluded from the result.
     *
     * @param b2bUnits the list of B2B units to filter; may include instances not of type
     *                 {@code IbmPartnerB2BUnitModel}
     * @return a list of {@link B2BUnitModel} instances that are non-tier-1 reseller partner units
     */
    public static List<B2BUnitModel> filteredNonTier1Sites(List<B2BUnitModel> b2bUnits) {
        return b2bUnits.stream()
            .filter(IbmPartnerB2BUnitModel.class::isInstance)
            .map(IbmPartnerB2BUnitModel.class::cast)
            .filter(
                unit -> !IbmPartnerB2BUnitType.RESELLER_TIER_1.equals(unit.getType()))
            .map(unit -> (B2BUnitModel) unit)
            .toList();
    }
}
