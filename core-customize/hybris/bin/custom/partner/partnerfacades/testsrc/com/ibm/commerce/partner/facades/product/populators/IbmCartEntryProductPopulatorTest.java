package com.ibm.commerce.partner.facades.product.populators;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.strategies.impl.PartnerPartProductTypeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class IbmCartEntryProductPopulatorTest {

    private IbmCartEntryProductPopulator populator;

    @Mock
    private CommerceCommonI18NService commerceCommonI18NService;

    private static final String PART_DESC = "Part Product description";
    private static final String PART_PROD_CODE = "123456";
    private static final String PART_DEPLOYMENT_CODE = "pertputal";
    private static final String PART_DEPLOYMENT_NAME = "pertputal Name";

    private static final String VAR_DESC = "Varaint Product description";
    private static final String VAR_DEPLOYMENT_CODE = "pertputaltest";
    private static final String VAR_DEPLOYMENT_NAME = "pertputaltest Name";

    @Mock
    private PartnerPartProductTypeStrategy partnerPartProductTypeStrategy;

    @Mock
    private Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new IbmCartEntryProductPopulator(partnerPartProductTypeStrategy,
            displayTypeDataConverter,
            commerceCommonI18NService);
    }

    @Test
    public void testPopulatePartProduct() throws ConversionException {
        ProductModel productModel = mock(IbmPartProductModel.class);
        ProductData productData = new ProductData();

        IbmDeploymentTypeModel deploymentTypeModel = mock(IbmDeploymentTypeModel.class);
        when(commerceCommonI18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(productModel.getDescription(Locale.ENGLISH)).thenReturn(PART_DESC);
        when(((IbmPartProductModel) productModel).getSapMaterialCode()).thenReturn(PART_PROD_CODE);
        when(((IbmPartProductModel) productModel).getDeploymentType()).thenReturn(deploymentTypeModel);
        when(deploymentTypeModel.getCode()).thenReturn(PART_DEPLOYMENT_CODE);
        when(deploymentTypeModel.getName()).thenReturn(PART_DEPLOYMENT_NAME);

        populator.populate(productModel, productData);

        assertEquals(PART_DESC, productData.getDescription());
        assertEquals(PART_PROD_CODE, productData.getSapMaterialCode());
        assertEquals(PART_DEPLOYMENT_CODE, productData.getDeploymentType().getCode());
        assertEquals(PART_DEPLOYMENT_NAME, productData.getDeploymentType().getName());
    }

    @Test
    public void testPopulateVariantProduct() throws ConversionException {
        ProductModel productModel = mock(IbmVariantProductModel.class);
        ProductData productData = new ProductData();

        IbmDeploymentTypeModel deploymentTypeModel = mock(IbmDeploymentTypeModel.class);

        when(commerceCommonI18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(productModel.getDescription(Locale.ENGLISH)).thenReturn(VAR_DESC);
        when(((IbmVariantProductModel) productModel).getDeploymentType()).thenReturn(deploymentTypeModel);
        when(deploymentTypeModel.getCode()).thenReturn(VAR_DEPLOYMENT_CODE);
        when(deploymentTypeModel.getName()).thenReturn(VAR_DEPLOYMENT_NAME);

        populator.populate(productModel, productData);

        assertEquals(VAR_DESC, productData.getDescription());
        assertEquals(VAR_DEPLOYMENT_CODE, productData.getDeploymentType().getCode());
        assertEquals(VAR_DEPLOYMENT_NAME, productData.getDeploymentType().getName());
    }
}
