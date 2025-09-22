package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.util.data.FacetValueTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerFacetValuePopulatorTest {

    private static final String QUERY = "";
    private static final String STATE = "";
    private static final String CODE = "123";

    @InjectMocks
    PartnerFacetValuePopulator populator;

    FacetValueData source;
    FacetValueData target;
    @Mock
    Converter<FacetValueData<Object>, FacetValueData<Object>> childFacetValueConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new PartnerFacetValuePopulator<>(childFacetValueConverter);
        List<FacetValueData> childvalues = new ArrayList<>();
        FacetValueData valueData = FacetValueTestDataGenerator.createFacetValueData(CODE);
        childvalues.add(valueData);
        source = FacetValueTestDataGenerator.createFacetValueData(CODE);
        source.setChildValues(childvalues);
        target = new FacetValueData();
    }

    @Test
    public void testPopulate() {
        populator.populate(source, target);
        Assert.assertNotNull(target.getChildValues());
    }
}
