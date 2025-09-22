package com.ibm.commerce.partner.occ.v2.validator;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerEndCustomerB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.deal.dto.IbmPartnerOpportunityWsDTO;
import com.ibm.dto.order.IbmAddToCartParamsWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.validation.Errors;
@UnitTest
public class OpportunityCodeValidatorTest {

    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;

    @Mock
    private PartnerOpportunityService opportunityService;

    @Mock
    private Errors errors;

    @Mock
    private IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO;

    @Mock
    private IbmPartnerB2BUnitWsDTO b2BUnitModel;
    @Mock
    private OpportunityDetailsSearchResponseData opportunityDetailsResponseData;

    @InjectMocks
    private OpportunityCodeValidator opportunityCodeValidator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        opportunityCodeValidator = new OpportunityCodeValidator( opportunityService);
    }

    @Test
    public void testValidate_validOpportunityCode() {
        when(ibmAddToCartParamsWsDTO.getSoldThroughUnit()).thenReturn(b2BUnitModel);
        when(ibmAddToCartParamsWsDTO.getOpportunity()).thenReturn(mock(IbmPartnerOpportunityWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getOpportunity().getCode()).thenReturn("validOpportunityCode");
        when(b2BUnitModel.getUid()).thenReturn("validUid");
        when(ibmAddToCartParamsWsDTO.getShipToUnit()).thenReturn(mock(
            IbmPartnerEndCustomerB2BUnitWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getShipToUnit().getIbmCustomerNumber()).thenReturn("validCustomerNumber");
        when(opportunityService.getOpportunities(any(), any(), any())).thenReturn(
            List.of(opportunityDetailsResponseData));
        when(opportunityDetailsResponseData.getOpportunityNumber()).thenReturn("12345");
        opportunityCodeValidator.validate(ibmAddToCartParamsWsDTO, errors);
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testValidate_invalidOpportunityCode() {
        // Mocking the inputs
        when(ibmAddToCartParamsWsDTO.getSoldThroughUnit()).thenReturn(b2BUnitModel);
        when(ibmAddToCartParamsWsDTO.getOpportunity()).thenReturn(mock(IbmPartnerOpportunityWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getOpportunity().getCode()).thenReturn("invalidOpportunityCode");
        when(b2BUnitModel.getUid()).thenReturn("validUid");
        when(ibmAddToCartParamsWsDTO.getShipToUnit()).thenReturn(mock(IbmPartnerEndCustomerB2BUnitWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getShipToUnit().getIbmCustomerNumber()).thenReturn("validCustomerNumber");
        when(opportunityService.getOpportunities(any(), any(), any())).thenReturn(List.of());
        opportunityCodeValidator.validate(ibmAddToCartParamsWsDTO, errors);
        verify(errors).rejectValue(eq("Opportunity"), eq(null), eq("Opportunity Not valid"));
    }

    @Test
    public void testValidate_missingOpportunityCode() {
        // Mocking the inputs with missing opportunity code
        when(ibmAddToCartParamsWsDTO.getSoldThroughUnit()).thenReturn(b2BUnitModel);
        when(ibmAddToCartParamsWsDTO.getOpportunity()).thenReturn(mock(IbmPartnerOpportunityWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getOpportunity().getCode()).thenReturn(null);
        when(b2BUnitModel.getUid()).thenReturn("validUid");
        when(ibmAddToCartParamsWsDTO.getShipToUnit()).thenReturn(mock(IbmPartnerEndCustomerB2BUnitWsDTO.class));
        when(ibmAddToCartParamsWsDTO.getShipToUnit().getIbmCustomerNumber()).thenReturn("validCustomerNumber");
        opportunityCodeValidator.validate(ibmAddToCartParamsWsDTO, errors);
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void testIsNotValidOpportuity() {
        when(ibmAddToCartParamsWsDTO.getOpportunity()).thenReturn(
            mock(IbmPartnerOpportunityWsDTO.class));
        assertFalse(opportunityCodeValidator.isValidOpportuity(ibmAddToCartParamsWsDTO));
    }

    @Test
    public void testSupportsWithNonStringClass() {
        assertFalse("Supports should return false for Integer.class",
            opportunityCodeValidator.supports(Integer.class));
    }

}