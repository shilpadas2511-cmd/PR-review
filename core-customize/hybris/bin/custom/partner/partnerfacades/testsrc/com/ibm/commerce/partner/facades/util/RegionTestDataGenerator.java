package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.user.data.RegionData;

public class RegionTestDataGenerator {

    public static RegionData createRegionData(final String isoCode, final String name) {
        RegionData regionData = new RegionData();
        regionData.setIsocode(isoCode);
        regionData.setName(name);
        return  regionData;
    }

}
