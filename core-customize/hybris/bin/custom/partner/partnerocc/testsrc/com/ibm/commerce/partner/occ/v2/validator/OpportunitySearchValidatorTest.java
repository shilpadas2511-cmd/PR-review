package com.ibm.commerce.partner.occ.v2.validator;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunitySearchRequestWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

/**
 * Test class for {@link OpportunitySearchValidatorTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OpportunitySearchValidatorTest {

    private static final String RESELLER_ID = "aaaaa";
    private static final String INACTIVE_RESELLER_ID = "22222222";
    private static final String ACTIVE_RESELLER_ID = "aaaaaa";
    private static final String VALID_CUSTOMER_ICN = "44444444";



    private OpportunitySearchValidator opportunitySearchValidator;
    @Mock
    PartnerB2BUnitService partnerB2BUnitService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Errors errors;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opportunitySearchValidator = new OpportunitySearchValidator(partnerB2BUnitService,
            configurationService);
        Map<String, String> errorMap = new HashMap<>();
        errors = new MapBindingResult(errorMap, "opportunitySearchRequestWsDTO");
    }

    @Test
    public void testValidate_withNullCustomerICN() {
        OpportunitySearchRequestWsDTO request = new OpportunitySearchRequestWsDTO();
        request.setResellerID(RESELLER_ID);

        opportunitySearchValidator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("customerICN"));
    }

    @Test
    public void testValidate_withNullResellerID() {
        OpportunitySearchRequestWsDTO request = new OpportunitySearchRequestWsDTO();
        request.setCustomerICN(VALID_CUSTOMER_ICN);

        opportunitySearchValidator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("resellerID"));
    }

    @Test
    public void testValidate_withInactiveResellerID() {
        OpportunitySearchRequestWsDTO request = new OpportunitySearchRequestWsDTO();
        request.setCustomerICN(VALID_CUSTOMER_ICN);
        request.setResellerID(INACTIVE_RESELLER_ID);

        B2BUnitModel b2bUnitModel = new B2BUnitModel();
        when(partnerB2BUnitService.getUnitForUid(INACTIVE_RESELLER_ID, true)).thenReturn(
            b2bUnitModel);
        when(partnerB2BUnitService.isActive(b2bUnitModel)).thenReturn(false);

        opportunitySearchValidator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("resellerID"));
    }

    @Test
    public void testValidate_withValidResellerID() {
        OpportunitySearchRequestWsDTO request = new OpportunitySearchRequestWsDTO();
        request.setCustomerICN(VALID_CUSTOMER_ICN);
        request.setResellerID(ACTIVE_RESELLER_ID);

        B2BUnitModel b2bUnitModel = new B2BUnitModel();
        when(partnerB2BUnitService.getUnitForUid(ACTIVE_RESELLER_ID, true)).thenReturn(
            b2bUnitModel);
        when(partnerB2BUnitService.isActive(b2bUnitModel)).thenReturn(true);

        opportunitySearchValidator.validate(request, errors);

        assertTrue(!errors.hasErrors());
    }
    @Test
    public void testGetPartnerB2BUnitService() {
        PartnerB2BUnitService mockService = mock(PartnerB2BUnitService.class);
        OpportunitySearchValidator validator = new OpportunitySearchValidator(mockService,
            configurationService);

        assertSame(mockService, validator.getPartnerB2BUnitService(),
            "The getter should return the same service passed into the constructor");
    }
    @Test
    public void testSupports_CorrectType() {
        opportunitySearchValidator.supports(OpportunitySearchRequestWsDTO.class);
    }

}