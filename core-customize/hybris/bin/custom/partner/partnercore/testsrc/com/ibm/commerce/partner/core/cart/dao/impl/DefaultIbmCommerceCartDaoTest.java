package com.ibm.commerce.partner.core.cart.dao.impl;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmCommerceCartDaoTest {

    @InjectMocks
    DefaultIbmCommerceCartDao defaultIbmCommerceCartDao;
    @Mock
    DefaultCommerceCartDao defaultCommerceCartDao;

    @Mock
    FlexibleSearchService flexibleSearchService;
    private IbmPartnerB2BUnitModel mockIbmPartnerB2BUnitModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultIbmCommerceCartDao.setFlexibleSearchService(flexibleSearchService);
        mockIbmPartnerB2BUnitModel = mock(IbmPartnerB2BUnitModel.class);
    }

    @Test
    public void testGetCartByCodeAndSiteIdsAndStore_NullCartCode() {
        BaseSiteModel siteModel = mock(BaseSiteModel.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultIbmCommerceCartDao.getCartByCodeAndSiteIdsAndStore(null, Collections.singletonList(mockIbmPartnerB2BUnitModel),
                siteModel)
        );
        assertEquals("Cart Code cannot be null", exception.getMessage());
    }

    @Test
    public void testGetCartByCodeAndSiteIdsAndStore_NullB2BUnitGroups() {
        BaseSiteModel store = mock(BaseSiteModel.class);
        String cartCode = "testCode";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultIbmCommerceCartDao.getCartByCodeAndSiteIdsAndStore(cartCode, null,
                store)
        );
        assertEquals("SiteIds must not be null", exception.getMessage());
    }

    @Test
    public void testGetCartByCodeAndSiteIdsAndStore_NullStore() {
        String cartCode = "testCode";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultIbmCommerceCartDao.getCartByCodeAndSiteIdsAndStore(cartCode, Collections.singletonList(mockIbmPartnerB2BUnitModel),
                null)
        );
        assertEquals("site must not be null", exception.getMessage());
    }


    @Test(expected = NullPointerException.class)
    public void testGetCartByCodeAndSiteIdsAndStore_FlexibleSearchServiceThrowsException() {
        BaseSiteModel site = mock(BaseSiteModel.class);
        B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        List<B2BUnitModel> b2bUnitGroups = Collections.singletonList(b2bUnit);
        String cartCode = "testCode";
        defaultIbmCommerceCartDao.getCartByCodeAndSiteIdsAndStore(cartCode, Collections.singletonList(mockIbmPartnerB2BUnitModel), site);
    }
    @Test
    public void testGetPidCartByCodeAndStore_ReturnsCart() {
        String code = "PID123";
        BaseSiteModel site = mock(BaseSiteModel.class);
        IbmPartnerPidCartModel expectedCart = mock(IbmPartnerPidCartModel.class);
        IbmPartnerPidCartModel result = defaultIbmCommerceCartDao.getPidCartByCodeAndStore(code, site);
        assertNotNull(result);
        assertEquals(expectedCart, result);
    }

    @Test
    public void testGetPidCartByCodeAndStore_NoCartFound_ReturnsNull() {
        String code = "PID456";
        BaseSiteModel site = mock(BaseSiteModel.class);
        IbmPartnerPidCartModel result = defaultIbmCommerceCartDao.getPidCartByCodeAndStore(code, site);
        assertNull(result);
    }

    @Test
    public void testGetPidCartByCodeAndStore_NullInputs_StillReturnsNull() {
        IbmPartnerPidCartModel result = defaultIbmCommerceCartDao.getPidCartByCodeAndStore(null, null);
        assertNull(result);
    }
}