package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.partnerPidAgreement.dao.PartnerPidAgreementDao;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@UnitTest
public class PriceLookUpItemsEntitledRequestPopulatorTest {

    private PriceLookUpItemsEntitledRequestPopulator populator;
    private final String PRODUCT_CODE = "TestPartProduct123";
    private final String CONFIG_ID = "testConfigId";

    @Mock
    private IbmProductService productService;
    @Mock
    private PartnerPidAgreementService partnerPidAgreementService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> billingFrequency = Map.of(
            "Annually", "A",
            "Upfront", "U",
            "Quarterly", "Q",
            "Monthly", "M"
        );
        populator = new PriceLookUpItemsEntitledRequestPopulator(productService,
            partnerPidAgreementService,billingFrequency);
    }

    @Test
    public void testPopulate() {
        AbstractOrderEntryModel source = mock(AbstractOrderEntryModel.class);
        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();
        Mockito.when( source.getProduct()).thenReturn(IbmPartProductModelTestDataGenerator.createProductData(PRODUCT_CODE));
        AbstractOrderEntryModel masterEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
                0);
        masterEntry.setProductConfiguration(
                ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(
                        CONFIG_ID));
        Mockito.when(source.getMasterEntry()).thenReturn(masterEntry);
        populator.populate(source, target);

        assertNotNull(source);
    }

    @Test
    public void testPopulateSourceNull() {
        CommonPriceLookUpItemsRequestData target = new CommonPriceLookUpItemsRequestData();
        populator.populate(null, target);
            assertNull(target.getProductType());
        	assertNull(target.getConfigurationId());
    }
}