package com.ibm.commerce.common.facades.storefinder.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

@UnitTest
public class SearchPagePointOfServiceDistancePopulatorTest {
    private static final double SOURCE_LATITUDE = 10.0;
    private static final double SOURCE_LONGITUDE = 15.0;
    private static final double BOUND_EAST_LONGITUDE = 10.0;
    private static final double BOUND_WEST_LONGITUDE = 10.0;
    private static final double BOUND_NORTH_LATITUDE = 10.0;
    private static final double BOUND_SOUTH_LATITUDE = 10.0;
    private static final String LOCATION_TEXT = "test Text";

    @InjectMocks
    SearchPagePointOfServiceDistancePopulator<StoreFinderSearchPageData<PointOfServiceDistanceData>,
        StoreFinderSearchPageData<PointOfServiceData>> populator;

    StoreFinderSearchPageData<PointOfServiceDistanceData> source;
    StoreFinderSearchPageData<PointOfServiceData> target;

    @Before
    public void setUp() {
        populator = new SearchPagePointOfServiceDistancePopulator<>();
        source = new StoreFinderSearchPageData<>();
        target = new StoreFinderSearchPageData<>();
        List<PointOfServiceDistanceData> sourceResults = new ArrayList<>();
        PointOfServiceDistanceData distanceData1 = new PointOfServiceDistanceData();
        PointOfServiceDistanceData distanceData2 = new PointOfServiceDistanceData();
        sourceResults.add(distanceData1);
        sourceResults.add(distanceData2);
        source.setResults(sourceResults);
        source.setSourceLatitude(SOURCE_LATITUDE);
        source.setSourceLongitude(SOURCE_LONGITUDE);
        source.setBoundEastLongitude(BOUND_EAST_LONGITUDE);
        source.setBoundNorthLatitude(BOUND_NORTH_LATITUDE);
        source.setBoundSouthLatitude(BOUND_SOUTH_LATITUDE);
        source.setBoundWestLongitude(BOUND_WEST_LONGITUDE);

        Converter<PointOfServiceDistanceData, PointOfServiceData> converter = mock(AbstractConverter.class);
        when(converter.convert(distanceData1)).thenReturn(new PointOfServiceData());
        when(converter.convert(distanceData2)).thenReturn(new PointOfServiceData());
        populator.setPointOfServiceDistanceConverter(converter);
    }

    @Test
    public void testPopulate() {
        source.setLocationText(LOCATION_TEXT);
        populator.populate(source, target);

        assertEquals(source.getPagination(), target.getPagination());
        assertEquals(source.getSorts(), target.getSorts());
        assertEquals(source.getResults().size(), target.getResults().size());
        assertEquals(source.getLocationText(), target.getLocationText());
        Assert.assertEquals(source.getSourceLatitude(), target.getSourceLatitude(), SOURCE_LATITUDE);
        Assert.assertEquals(source.getSourceLongitude(), target.getSourceLongitude(), SOURCE_LONGITUDE);
        Assert.assertEquals(source.getBoundNorthLatitude(), target.getBoundNorthLatitude(),BOUND_NORTH_LATITUDE);
        Assert.assertEquals(source.getBoundSouthLatitude(), target.getBoundSouthLatitude(), BOUND_SOUTH_LATITUDE);
        Assert.assertEquals(source.getBoundWestLongitude(), target.getBoundWestLongitude(), BOUND_WEST_LONGITUDE);
        Assert.assertEquals(source.getBoundEastLongitude(), target.getBoundEastLongitude(), BOUND_EAST_LONGITUDE);
    }

    @Test
    public void testPopulate_LocationTextAsNull() {
        populator.populate(source, target);

        assertEquals(source.getPagination(), target.getPagination());
        assertEquals(source.getSorts(), target.getSorts());
        assertEquals(source.getResults().size(), target.getResults().size());
        assertEquals(source.getLocationText(), target.getLocationText());
        Assert.assertEquals(source.getSourceLatitude(), target.getSourceLatitude(), SOURCE_LATITUDE);
        Assert.assertEquals(source.getSourceLongitude(), target.getSourceLongitude(), SOURCE_LONGITUDE);
        Assert.assertEquals(source.getBoundNorthLatitude(), target.getBoundNorthLatitude(),BOUND_NORTH_LATITUDE);
        Assert.assertEquals(source.getBoundSouthLatitude(), target.getBoundSouthLatitude(), BOUND_SOUTH_LATITUDE);
        Assert.assertEquals(source.getBoundWestLongitude(), target.getBoundWestLongitude(), BOUND_WEST_LONGITUDE);
        Assert.assertEquals(source.getBoundEastLongitude(), target.getBoundEastLongitude(), BOUND_EAST_LONGITUDE);
    }
}