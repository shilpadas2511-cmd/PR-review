package com.ibm.commerce.partner.facades.search.converters.populator;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultVariantOptionsProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 *  IbmSearchResultVariantOptionsProductPopulator use override update the configurator code on variant opition data
 * */
public class IbmSearchResultVariantOptionsProductPopulator extends
    SearchResultVariantOptionsProductPopulator {

    private Converter<SearchResultValueData, VariantOptionData> variantOptionDataConverter;

    public IbmSearchResultVariantOptionsProductPopulator(
        Converter<SearchResultValueData, VariantOptionData> variantOptionDataConverter) {
        this.variantOptionDataConverter = variantOptionDataConverter;
    }


    @Override
    public void populate(SearchResultValueData source, ProductData target) {
        super.populate(source, target);

    }

    @Override
    protected List<VariantOptionData> getVariantOptions(final List<SearchResultValueData> variants,
        final Set<String> variantTypeAttributes, final String rollupProperty) {
        final List<VariantOptionData> variantOptions = new ArrayList<>();

        for (final SearchResultValueData variant : variants) {
            final VariantOptionData variantOption = new VariantOptionData();
            variantOption.setCode((String) getValue(variant, CODE_VARIANT_PROPERTY));
            getVariantOptionDataConverter().convert(variant, variantOption);
            final Double priceValue = resolvePrice(variant);
            if (priceValue != null) {
                final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
                    BigDecimal.valueOf(priceValue.doubleValue()),
                    getCommonI18NService().getCurrentCurrency());
                variantOption.setPriceData(priceData);
            }

            variantOption.setUrl((String) getValue(variant, URL_VARIANT_PROPERTY));
            variantOption.setVariantOptionQualifiers(
                getVariantOptionQualifiers(variant, variantTypeAttributes, rollupProperty));

            variantOptions.add(variantOption);
        }
        return variantOptions;
    }

    public Converter<SearchResultValueData, VariantOptionData> getVariantOptionDataConverter() {
        return variantOptionDataConverter;
    }


}
