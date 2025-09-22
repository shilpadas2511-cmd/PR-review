package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.c2l.RegionModel;


/**
 * TestDataGenerator for RegionModel
 */
public class RegionModelTestDataGenerator
{
	public static RegionModel createRegionModel(final String isocode)
	{
		final RegionModel region = new RegionModel();
		region.setIsocode(isocode);
		return region;
	}
}
