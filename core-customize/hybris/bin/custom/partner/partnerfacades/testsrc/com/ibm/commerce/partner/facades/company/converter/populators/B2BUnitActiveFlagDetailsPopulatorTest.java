package com.ibm.commerce.partner.facades.company.converter.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BUnitActiveFlagDetailsPopulatorTest {


    private B2BUnitActiveFlagDetailsPopulator populator;
    private PartnerB2BUnitFacade mockB2BUnitFacade;

    @Before
    public void setUp() {
        mockB2BUnitFacade = mock(PartnerB2BUnitFacade.class);
        populator = new B2BUnitActiveFlagDetailsPopulator(mockB2BUnitFacade);
    }

    @Test
    public void testPopulate() {
        // Arrange
        B2BUnitModel source = new B2BUnitModel();
        B2BUnitData target = new B2BUnitData();

        // Mock behavior of PartnerB2BUnitFacade
        when(mockB2BUnitFacade.isActive(source)).thenReturn(true);

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals(true, target.isActive());
    }
    @Test
    public void testGetB2BUnitFacade() {
        assertEquals(mockB2BUnitFacade, populator.getB2BUnitFacade());
    }

}