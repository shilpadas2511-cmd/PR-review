package com.ibm.commerce.partner.facades.search.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IbmSearchResultVariantOptionsProductPopulatorTest {

    public static final String VARIANT_CODE = "variantCode";
    public static final String VARIANT_VALUE = "VAR123";
    public static final String PRICE_VALUE = "priceValue";
    public static final String URL = "url";
    public static final String VARIANT_URL = "/variant-url";
    public static final String ROLLUP_PROPERTY = "rollupProp";
    @Mock
    private Converter<SearchResultValueData, VariantOptionData> variantOptionDataConverter;

    @Mock
    private PriceDataFactory priceDataFactory;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private PriceData priceData;
    @Mock
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private UrlResolver<ProductData> productDataUrlResolver;


    @InjectMocks
    private IbmSearchResultVariantOptionsProductPopulator populator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new IbmSearchResultVariantOptionsProductPopulator(variantOptionDataConverter) {
            @Override
            protected List getVariantOptionQualifiers(final SearchResultValueData variant,
                final Set variantTypeAttributes, final String rollupProperty) {
                return Collections.emptyList();
            }
        };
        populator.setPriceDataFactory(priceDataFactory);
        populator.setCommonI18NService(commonI18NService);
        populator.setProductFeatureListPopulator(productFeatureListPopulator);
        populator.setImageFormatMapping(imageFormatMapping);
		populator.setProductDataUrlResolver(productDataUrlResolver);
    }

    @Test
    public void testGetVariantOptions_withValidData_shouldReturnVariantOptions() {
        final SearchResultValueData variant = mock(SearchResultValueData.class);
        final List<SearchResultValueData> variants = Collections.singletonList(variant);

        final Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(VARIANT_CODE, VARIANT_VALUE);
        valueMap.put(PRICE_VALUE, 100.0);
        valueMap.put(URL, VARIANT_URL);

        when(variant.getValues()).thenReturn(valueMap);

        doAnswer(invocation -> {
            final VariantOptionData data = invocation.getArgument(1);
            data.setCode(VARIANT_VALUE);
            return null;
        }).when(variantOptionDataConverter).convert(eq(variant), any(VariantOptionData.class));

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("USD");
        when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
        when(priceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), eq(currency)))
            .thenReturn(priceData);

        final List<VariantOptionData> result = populator.getVariantOptions(variants, new HashSet<>(),
            ROLLUP_PROPERTY);

        assertNotNull(result);
        assertEquals(1, result.size());
        final VariantOptionData variantOptionData = result.get(0);
        assertEquals("VAR123", variantOptionData.getCode());
        assertEquals(priceData, variantOptionData.getPriceData());
    }



	 @Test
	 public void testPopulate_shouldCallSuperPopulate()
	 {
		 final SearchResultValueData source = mock(SearchResultValueData.class);
		 final ProductData target = new ProductData();

		 populator.populate(source, target);
	 }
}
