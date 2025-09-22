package com.ibm.commerce.partner.facades.comparators;

import de.hybris.platform.core.model.user.AddressModel;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Compares AddressModels
 */
public class PartnerAddressComparator implements Comparator<AddressModel> {

    private PartnerRegionComparator regionComparator;
    private PartnerCountryComparator countryComparator;

    public PartnerAddressComparator(final PartnerRegionComparator regionComparator,
        final PartnerCountryComparator countryComparator) {
        this.regionComparator = regionComparator;
        this.countryComparator = countryComparator;
    }

    @Override
    public int compare(final AddressModel o1, final AddressModel o2) {
        return getCountryComparator().compare(o1.getCountry(), o2.getCountry())
            == NumberUtils.INTEGER_ZERO
            && getRegionComparator().compare(o1.getRegion(), o2.getRegion())
            == NumberUtils.INTEGER_ZERO && StringUtils.equalsIgnoreCase(o1.getDistrict(),
            o2.getDistrict()) && StringUtils.equalsIgnoreCase(o1.getTown(), o2.getTown())
            && StringUtils.equalsIgnoreCase(o1.getPostalcode(), o2.getPostalcode())
            && StringUtils.equalsIgnoreCase(o1.getEmail(), o2.getEmail())
            && StringUtils.equalsIgnoreCase(o1.getLine1(), o2.getStreetname())
            && StringUtils.equalsIgnoreCase(o1.getLine2(), o2.getStreetnumber())
            && StringUtils.equalsIgnoreCase(o1.getFirstname(), o2.getFirstname())
            && StringUtils.equalsIgnoreCase(o1.getLastname(), o2.getLastname())
            ? NumberUtils.INTEGER_ZERO : NumberUtils.INTEGER_ONE;
    }

    public PartnerRegionComparator getRegionComparator() {
        return regionComparator;
    }

    public PartnerCountryComparator getCountryComparator() {
        return countryComparator;
    }
}
