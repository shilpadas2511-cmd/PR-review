package com.ibm.commerce.partner.core.utils;

import de.hybris.platform.core.model.user.AddressModel;
import org.apache.commons.lang3.StringUtils;

public class PartnerAddressUtils {

    public static boolean areAddressesEqual(AddressModel o1, AddressModel o2) {
        return o1 != null && o2 != null
            && o1.getCountry().equals(o2.getCountry())
            && o1.getRegion().equals(o2.getRegion())
            && StringUtils.equalsIgnoreCase(o1.getDistrict(), o2.getDistrict())
            && StringUtils.equalsIgnoreCase(o1.getTown(), o2.getTown())
            && StringUtils.equalsIgnoreCase(o1.getPostalcode(), o2.getPostalcode())
            && StringUtils.equalsIgnoreCase(o1.getEmail(), o2.getEmail())
            && StringUtils.equalsIgnoreCase(o1.getStreetname(), o2.getLine1())
            && StringUtils.equalsIgnoreCase(o1.getStreetnumber(), o2.getLine2())
            && StringUtils.equalsIgnoreCase(o1.getFirstname(), o2.getFirstname())
            && StringUtils.equalsIgnoreCase(o1.getLastname(), o2.getLastname());
    }
}
