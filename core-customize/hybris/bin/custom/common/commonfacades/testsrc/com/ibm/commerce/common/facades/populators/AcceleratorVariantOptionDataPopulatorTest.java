package com.ibm.commerce.common.facades.populators;

import com.ibm.commerce.common.core.utils.*;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class AcceleratorVariantOptionDataPopulatorTest {

    @Mock
    private TypeService typeService;

    @Mock
    private MediaService mediaService;

    @Mock
    private MediaContainerService mediaContainerService;

    @Mock
    private ImageFormatMapping imageFormatMapping;

    @Mock
    private Converter<MediaModel, ImageData> imageConverter;

    @InjectMocks
    private AcceleratorVariantOptionDataPopulator populator;
    private Map<String, String> variantAttributeMapping;
    private static final String MAP_KEY = "test.Qualifier";
    private static final String MAP_VALUE = "ImageFormat";
    private static final String MEDIA_CODE = "789";
    private static final String COMPOSED_ID = "test";
    private static final String MEDIA_QUAL = "MediaFormatQualifier";
    private static final String QUALIFER = "Qualifier";
    private static final String PRODUCT_CODE = "productCode";
    VariantProductModel source;
    VariantOptionData target;
    MediaContainerModel mediaContainer;
    MediaModel mediaModel;
    @Mock
    private Logger LOG;
    @Mock
    ProductModel productModel;
    @Mock
    private VariantsService variantsService;
    @Mock
    private UrlResolver<ProductModel> productModelUrlResolver;
    @Mock
    private Converter<ProductModel, StockData> stockConverter;
    @Mock
    private CommercePriceService commercePriceService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        imageFormatMapping = mock(ImageFormatMapping.class);
        variantAttributeMapping = new HashMap<>();
        variantAttributeMapping.put(MAP_KEY, MAP_VALUE);
        populator.setVariantAttributeMapping(variantAttributeMapping);
        populator.setImageFormatMapping(imageFormatMapping);
        populator.setMediaService(mediaService);
        populator.setMediaContainerService(mediaContainerService);
        populator.setImageConverter(imageConverter);
        populator.setVariantsService(variantsService);
        populator.setStockConverter(stockConverter);
        source = VariantProductModelTestDataGenerator.createVariantProduct();
        target = VariantOptionDataTestGenerator.createVariantOptionData();
        mediaContainer = MediaContainerModelTestDataGenerator.createMediaContainer();
        mediaModel = MediaModelTestDataGenerator.createMediaModel(MEDIA_CODE);
    }

    @Test
    public void testPopulate_WithMedia() {

        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(QUALIFER);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        ComposedTypeModel composedTypeModel = ComposedTypeModelTestDataGenerator.createComposeType(COMPOSED_ID);
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(composedTypeModel);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        ImageData imageData = ImageDataTestGenerator.createMediaContainer(MAP_VALUE);
        Mockito.when(imageConverter.convert(Mockito.any())).thenReturn(imageData);

        when(imageFormatMapping.getMediaFormatQualifierForImageFormat(MAP_VALUE)).thenReturn(MEDIA_QUAL);
        MediaFormatModel mediaFormat = new MediaFormatModel();
        when(mediaService.getFormat(MEDIA_QUAL)).thenReturn(mediaFormat);
        when(mediaContainerService.getMediaForFormat(mediaContainer, mediaFormat)).thenReturn(mediaModel);

        populator.populate(source, target);
        assertNotNull(mediaModel);
        assertEquals(MEDIA_CODE, mediaModel.getCode());
        assertNotNull(imageData);
        assertEquals(MAP_VALUE, imageData.getFormat());
        assertEquals(imageData, qualifierData.getImage());
    }

    @Test
    public void testPopulate_WithMediaWhenModelNotFoundException() {
        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(QUALIFER);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        ComposedTypeModel composedTypeModel = ComposedTypeModelTestDataGenerator.createComposeType(COMPOSED_ID);
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(composedTypeModel);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        when(imageFormatMapping.getMediaFormatQualifierForImageFormat(MAP_VALUE)).thenReturn(MEDIA_QUAL);
        MediaFormatModel mediaFormat = new MediaFormatModel();
        when(mediaService.getFormat(MEDIA_QUAL)).thenReturn(mediaFormat);
        when(mediaContainerService.getMediaForFormat(mediaContainer, mediaFormat)).thenThrow(ModelNotFoundException.class);
        populator.populate(source, target);
        Assert.assertNotNull(target.getVariantOptionQualifiers().iterator().next().getQualifier());
        Assert.assertNull(target.getCode());
    }

    @Test
    public void testPopulate_WithMediaWhenProductTypeNull() {

        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(QUALIFER);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(null);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        source.setCode(PRODUCT_CODE);
        source.setBaseProduct(productModel);
        populator.populate(source, target);
        Assert.assertEquals(PRODUCT_CODE,target.getCode());
    }

    @Test
    public void testPopulate_WithMediaWhenProductTypeNullVariantQualifierNull() {

        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(null);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(null);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        populator.populate(source, target);
        assertNull(target.getVariantOptionQualifiers().iterator().next().getQualifier());
    }

    @Test
    public void testPopulate_WithMediaWhenProductTypeVariantQualifierNull() {

        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(null);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        ComposedTypeModel composedTypeModel = ComposedTypeModelTestDataGenerator.createComposeType(COMPOSED_ID);
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(composedTypeModel);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        populator.populate(source, target);
        assertNull(target.getVariantOptionQualifiers().iterator().next().getQualifier());
    }

    @Test
    public void testPopulate_WithOutMediaContainer() {
        populator.populate(source, target);
        assertNull(target.getVariantOptionQualifiers());
    }

    @Test
    public void testPopulate_Empty_Media() {
        VariantOptionQualifierData qualifierData = VariantOptionQualifierDataTestGenerator.createVariantOption(QUALIFER);
        target.setVariantOptionQualifiers(Collections.singletonList(qualifierData));
        ComposedTypeModel composedTypeModel = ComposedTypeModelTestDataGenerator.createComposeType(COMPOSED_ID);
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(composedTypeModel);
        mediaModel.setMediaContainer(mediaContainer);
        source.setPicture(mediaModel);
        ImageData imageData = ImageDataTestGenerator.createMediaContainer(MAP_VALUE);
        when(typeService.getComposedTypeForClass(VariantProductModel.class)).thenReturn(composedTypeModel);
        when(imageFormatMapping.getMediaFormatQualifierForImageFormat(MAP_VALUE)).thenReturn(null);
        populator.populate(source, target);
        assertNull(qualifierData.getImage());
    }
}


