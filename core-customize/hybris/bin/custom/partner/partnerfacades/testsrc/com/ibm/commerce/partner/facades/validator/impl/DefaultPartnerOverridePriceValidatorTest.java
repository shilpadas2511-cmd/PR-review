package com.ibm.commerce.partner.facades.validator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.facades.strategies.impl.PartnerPartProductTypeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerOverridePriceValidatorTest {
    private static final String DEAL_REG_FLAG = "dealRegFlag";

    @InjectMocks
    private DefaultPartnerOverridePriceValidator validator;

    private IbmPartnerCartModel cartModel;

    @Mock
    private PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData;

    @Mock
    private PartnerOverrideEntryPriceData partnerOverrideEntryPriceData;

    @Mock
    private AbstractOrderEntryModel entry;

    @Mock
    private PartnerCpqPricingDetailModel pricingDetailModel;

    @Mock
    private CPQOrderEntryProductInfoModel cpqOrderEntryProductInfoModel;

    @Mock
    private PartnerPartProductTypeStrategy partnerPartProductTypeStrategy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartModel = new IbmPartnerCartModel();
    }

    @Test
    public void testValidateHeaderMandatoryFieldCartNotEditable() {
        final AbstractOrderEntryModel orderEntry1 =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(0,
                        Collections.singletonList(new AbstractOrderEntryModel()));
        final AbstractOrderEntryModel orderEntry2 =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(1,
                        Collections.singletonList(new AbstractOrderEntryModel()));
        final AbstractOrderEntryModel childOrderEntry1 =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        childOrderEntry1.setProductInfos(new ArrayList<>());
        childOrderEntry1.getProductInfos().add(
                IbmPartnerCartModelTestDataGenerator.createcartProductInfo(DEAL_REG_FLAG, "N"));
        final AbstractOrderEntryModel childOrderEntry2 =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        childOrderEntry2.setProductInfos(new ArrayList<>());
        childOrderEntry2.getProductInfos().add(
                IbmPartnerCartModelTestDataGenerator.createcartProductInfo(DEAL_REG_FLAG, "Y"));
        orderEntry1.setChildEntries(Collections.singleton(childOrderEntry1));
        orderEntry2.setChildEntries(Collections.singleton(childOrderEntry2));

        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(orderEntry1);
        entries.add(orderEntry2);
        cartModel.setEntries(entries);

        assertThrows(CommerceCartModificationException.class, () -> {
            validator.validateHeaderMandatoryField(cartModel, partnerOverrideHeaderPriceData);
        });
    }

    @Test
    public void testValidateHeaderMandatoryFieldInvalidEntitledPrice() {
        cartModel.setTotalFullPrice(100.0);
        cartModel.setEntries(Collections.emptyList());
        when(partnerOverrideHeaderPriceData.getOverrideTotalPrice()).thenReturn(150.0);

        assertThrows(CommerceCartModificationException.class, () -> {
            validator.validateHeaderMandatoryField(cartModel, partnerOverrideHeaderPriceData);
        });
    }

    @Test
    public void testValidateHeaderMandatoryFieldOverrideTotalPriceNull() {
        cartModel.setTotalPrice(100.0);
        cartModel.setEntries(Collections.emptyList());
        when(partnerOverrideHeaderPriceData.getOverrideTotalPrice()).thenReturn(null);
        try {
            validator.validateHeaderMandatoryField(cartModel, partnerOverrideHeaderPriceData);
        } catch (final CommerceCartModificationException e) {
            fail("Expection is not expected");
        }

    }

    @Test
    public void testValidateHeaderMandatoryFieldOverrideTotalPriceLessThanTotalPrice() {
        cartModel.setTotalFullPrice(100.0);
        cartModel.setEntries(Collections.emptyList());
        when(partnerOverrideHeaderPriceData.getOverrideTotalPrice()).thenReturn(90.0);
        try {
            validator.validateHeaderMandatoryField(cartModel, partnerOverrideHeaderPriceData);
        } catch (final CommerceCartModificationException e) {
            fail("Expection is not expected");
        }
    }

    @Test
    public void testValidateEntryMandatoryFieldEntryNotEditable() {
        when(entry.getProductInfos())
                .thenReturn(Collections.singletonList(cpqOrderEntryProductInfoModel));
        when(cpqOrderEntryProductInfoModel.getCpqCharacteristicName()).thenReturn(DEAL_REG_FLAG);
        when(cpqOrderEntryProductInfoModel.getCpqCharacteristicAssignedValues()).thenReturn("N");

        assertThrows(CommerceCartModificationException.class, () -> {
            validator.validateEntryMandatoryField(entry, partnerOverrideEntryPriceData,
                    Optional.of(pricingDetailModel));
        });
    }

    @Test
    public void testValidateEntryMandatoryFieldInvalidEntitledUnitPrice() {
        when(entry.getProductInfos()).thenReturn(Collections.emptyList());
        when(pricingDetailModel.getNetPrice()).thenReturn("100.0");
        when(partnerOverrideEntryPriceData.getOverridePrice()).thenReturn(150.0);

        assertThrows(CommerceCartModificationException.class, () -> {
            validator.validateEntryMandatoryField(entry, partnerOverrideEntryPriceData,
                    Optional.of(pricingDetailModel));
        });
    }

    @Test
    public void testValidateEntryMandatoryFieldCpqPricingMissing() {
        when(entry.getProductInfos()).thenReturn(Collections.emptyList());

        assertThrows(CommerceCartModificationException.class, () -> {
            validator.validateEntryMandatoryField(entry, partnerOverrideEntryPriceData,
                    Optional.empty());
        });
    }

    @Test
    public void testValidateEntryMandatoryFieldOverridePriceNull() {
        when(entry.getProductInfos()).thenReturn(Collections.emptyList());
        when(partnerOverrideEntryPriceData.getOverridePrice()).thenReturn(null);

        try {
            validator.validateEntryMandatoryField(entry, partnerOverrideEntryPriceData,
                    Optional.of(pricingDetailModel));
        } catch (final CommerceCartModificationException e) {
            fail("Expection is not expected");
        }

    }

    @Test
    public void testValidateEntryMandatoryFieldOverridePriceLessThanNetPrice() {
        when(entry.getProductInfos()).thenReturn(Collections.emptyList());
        when(pricingDetailModel.getNetPrice()).thenReturn("100.0");
        when(partnerOverrideEntryPriceData.getOverridePrice()).thenReturn(90.0);

        try {
            validator.validateEntryMandatoryField(entry, partnerOverrideEntryPriceData,
                    Optional.of(pricingDetailModel));
        } catch (final CommerceCartModificationException e) {
            fail("Expection is not expected");
        }

    }
    @Test
    public void testIsEccPriceAvailableForObsoletePart_true() {
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);

        PartnerCpqPricingDetailModel pricing1 = mock(PartnerCpqPricingDetailModel.class);
        when(pricing1.getEccPriceAvailable()).thenReturn(false);

        PartnerCpqPricingDetailModel pricing2 = mock(PartnerCpqPricingDetailModel.class);
        when(pricing2.getEccPriceAvailable()).thenReturn(true);

        when(entry.getCpqPricingDetails()).thenReturn(Arrays.asList(pricing1, pricing2));

        DefaultPartnerOverridePriceValidator validator = new DefaultPartnerOverridePriceValidator(
            partnerPartProductTypeStrategy);
        assertTrue(validator.isEccPriceAvailableForObsoletePart(entry));
    }
    @Test
    public void testIsPartsEditable_Obsolete_NoECC_Allowed() {
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        CPQOrderEntryProductInfoModel obsoleteInfo = mock(CPQOrderEntryProductInfoModel.class);
        when(obsoleteInfo.getCpqCharacteristicName()).thenReturn(PartnercoreConstants.PRODUCT_SALE_STATE_CODE);
        when(obsoleteInfo.getCpqCharacteristicAssignedValues()).thenReturn(PartnercoreConstants.PRODUCT_SALE_STATE_CODE_VALUE);
        when(entry.getProductInfos()).thenReturn(List.of(obsoleteInfo));
        PartnerCpqPricingDetailModel pricingDetail = mock(PartnerCpqPricingDetailModel.class);
        when(pricingDetail.getEccPriceAvailable()).thenReturn(false);
        when(entry.getCpqPricingDetails()).thenReturn(List.of(pricingDetail));
        assertDoesNotThrow(() -> validator.isPartsEditable(entry));
    }
    @Test
    public void testIsPartsEditable_Obsolete_WithECC_Throws() {
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        CPQOrderEntryProductInfoModel obsoleteInfo = mock(CPQOrderEntryProductInfoModel.class);
        when(obsoleteInfo.getCpqCharacteristicName()).thenReturn(PartnercoreConstants.PRODUCT_SALE_STATE_CODE);
        when(obsoleteInfo.getCpqCharacteristicAssignedValues()).thenReturn(PartnercoreConstants.PRODUCT_SALE_STATE_CODE_VALUE);
        when(entry.getProductInfos()).thenReturn(List.of(obsoleteInfo));
        PartnerCpqPricingDetailModel pricingDetail = mock(PartnerCpqPricingDetailModel.class);
        when(pricingDetail.getEccPriceAvailable()).thenReturn(true);

        when(entry.getCpqPricingDetails()).thenReturn(List.of(pricingDetail));

        CommerceCartModificationException ex = assertThrows(
            CommerceCartModificationException.class,
            () -> validator.isPartsEditable(entry)
        );
        assertEquals("Cart provided is not eligible for the edit.", ex.getMessage());
    }
}
