package com.ibm.commerce.partner.core.opportunity.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.opportunity.dao.PartnerOpportunityDao;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityOutboundService;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPartnerOpportunityServiceTest {

    @Mock
    private PartnerOpportunityDao opportunityDao;

    @Mock
    private PartnerOpportunityOutboundService opportunityOutboundService;

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerB2BUnitService b2BUnitService;

    @Mock
    private PartnerUserService userService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @InjectMocks
    private DefaultPartnerOpportunityService service;

    @InjectMocks
    private PartnerB2BUnitService partnerB2BUnitService;

    private final String mockJson = """
        {
            "opportunities": [
                {
                    "opportunityName": "Test Opp",
                    "opportunityNumber": "OPP123",
                    "opportunityStatus": "Open",
                    "customerName": "Test Customer",
                    "ExpirationDate": "2025-12-31",
                    "CustomerICN": "CUST123",
                    "ResellerCEID": "RES123",
                    "DistributorCEID": "DIST456"
                }
            ]
        }
        """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        service = new DefaultPartnerOpportunityService(
            opportunityDao, opportunityOutboundService, modelService,
            b2BUnitService, userService, configurationService, partnerB2BUnitService);
    }

    @Test
    void testFetchOpportunitiesByNumber_MockEnabled_ShouldReturnMockData() {

        List<OpportunityDetailsSearchResponseData> result = service.fetchOpportunitiesByNumber("OPP123");

        assertEquals(1, result.size());
        assertEquals("Test Opp", result.get(0).getOpportunityName());
        assertEquals("OPP123", result.get(0).getOpportunityNumber());
    }

    @Test
    void testFetchOpportunitiesByNumber_ServiceException_ShouldReturnEmptyList() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());

        when(opportunityOutboundService.getOpportunitiesSearchByOpportunityNumber(
            any(), any(), any())).thenThrow(new IbmWebServiceFailureException());

        List<OpportunityDetailsSearchResponseData> result = service.fetchOpportunitiesByNumber("OPP123");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpportunityById_ShouldReturnOpportunity() {
        IbmPartnerOpportunityModel expected = mock(IbmPartnerOpportunityModel.class);
        when(opportunityDao.fetch("OPP789")).thenReturn(expected);

        IbmPartnerOpportunityModel actual = service.get("OPP789");

        assertEquals(expected, actual);
    }

    @Test
    void testFetchOpportunitiesByNumber_InvalidMockJson_ShouldThrowException() {
        String badJson = "{ malformed json ";
        DefaultPartnerOpportunityService brokenService = new DefaultPartnerOpportunityService(
            opportunityDao, opportunityOutboundService, modelService,
            b2BUnitService, userService, configurationService, partnerB2BUnitService);

        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());

        assertThrows(IbmWebServiceFailureException.class,
            () -> brokenService.fetchOpportunitiesByNumber("BAD_JSON"));
    }
    @Test
    public void testGetOpportunityByAuthToken_ShouldReturnOpportunityList() {
        String resellerCEID = "RES123";
        String customerICN = "CUST456";
        String distributerCEID = "DIST123";

        OpportunityDetailsSearchResponseData opportunity = new OpportunityDetailsSearchResponseData();
        opportunity.setOpportunityName("Sample Opp");
        List<OpportunityDetailsSearchResponseData> mockResponse = List.of(opportunity);

        when(
            opportunityOutboundService.getOpportunities(resellerCEID, distributerCEID, customerICN,
                Boolean.FALSE))
            .thenReturn(mockResponse);
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunityByAuthToken(
            resellerCEID, distributerCEID, customerICN, Boolean.FALSE);
        assertEquals(1, result.size());
        assertEquals("Sample Opp", result.get(0).getOpportunityName());
        verify(opportunityOutboundService, times(1)).getOpportunities(resellerCEID, distributerCEID,
            customerICN, Boolean.FALSE);
    }
    @Test
    public void testFetchOpportunitiesByOwnerMail_MockDisabled_ReturnsServiceData() {

        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());

        OpportunityDetailsSearchResponseData opp = new OpportunityDetailsSearchResponseData();
        opp.setOpportunityName("Real Opp");
        when(opportunityOutboundService.getOpportunitiesSearchByOwnerMail(
            anyString(), anyList(), anyList()))
            .thenReturn(List.of(opp));

        List<OpportunityDetailsSearchResponseData> result =
            service.fetchOpportunitiesByOwnerMail("owner@example.com");

        assertEquals(1, result.size());
        assertEquals("Real Opp", result.get(0).getOpportunityName());
    }

    @Test
    public void testFetchOpportunitiesByOwnerMail_ServiceFails_ReturnsEmptyList() {


        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());

        when(opportunityOutboundService.getOpportunitiesSearchByOwnerMail(
            anyString(), anyList(), anyList()))
            .thenThrow(new IbmWebServiceFailureException());

        List<OpportunityDetailsSearchResponseData> result =
            service.fetchOpportunitiesByOwnerMail("owner@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpportunities_ResellerNull() {
        when(b2BUnitService.getUnitForUid(anyString(), eq(Boolean.TRUE))).thenReturn(null);
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunities("unitId",
            "distributorID", "customerICN");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpportunities_ParentNull() {
        B2BUnitModel reseller = mock(B2BUnitModel.class);
        when(b2BUnitService.getUnitForUid(anyString(), eq(Boolean.TRUE))).thenReturn(reseller);
        when(b2BUnitService.getParent(reseller)).thenReturn(null);
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunities("unitId",
            "distributorID", "customerICN");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpportunities_Exception() {
        B2BUnitModel reseller = mock(B2BUnitModel.class);
        B2BUnitModel parent = mock(B2BUnitModel.class);
        when(b2BUnitService.getUnitForUid(anyString(), eq(Boolean.TRUE))).thenReturn(reseller);
        when(b2BUnitService.getParent(reseller)).thenReturn(parent);
        when(parent.getUid()).thenReturn("parentUid");
        doThrow(new IbmWebServiceFailureException()).when(opportunityOutboundService)
            .getOpportunities(anyString(), anyString(), anyString(), anyBoolean());
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunities("unitId",
            "distributorID", "customerICN");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpportunities_Success() {
        B2BUnitModel reseller = mock(B2BUnitModel.class);
        B2BUnitModel parent = mock(B2BUnitModel.class);
        when(b2BUnitService.getUnitForUid(anyString(), eq(Boolean.TRUE))).thenReturn(reseller);
        when(b2BUnitService.getParent(reseller)).thenReturn(parent);
        when(parent.getUid()).thenReturn("parentUid");
        OpportunityDetailsSearchResponseData data = new OpportunityDetailsSearchResponseData();
        when(opportunityOutboundService.getOpportunities(anyString(), anyString(),
            anyString(), anyBoolean())).thenReturn(List.of(data));
        List<OpportunityDetailsSearchResponseData> result = service.getOpportunities("unitId",
            "distributorID", "customerICN");
        assertEquals(1, result.size());
    }

    @Test
    void testFetchOpportunitiesByCustomerNumber_Success() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());
        OpportunityDetailsSearchResponseData opp = new OpportunityDetailsSearchResponseData();
        when(opportunityOutboundService.getOpportunitiesSearchByCustomerNumber(anyString(), anyList(), anyList())).thenReturn(List.of(opp));
        List<OpportunityDetailsSearchResponseData> result = service.fetchOpportunitiesByCustomerNumber("custNum", "country");
        assertEquals(1, result.size());
    }

    @Test
    void testFetchOpportunitiesByCustomerNumber_Exception() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getGroups()).thenReturn(Set.of());

        when(opportunityOutboundService.getOpportunitiesSearchByCustomerNumber(
            anyString(), anyList(), anyList()))
            .thenThrow(new IbmWebServiceFailureException());

        List<OpportunityDetailsSearchResponseData> result =
            service.fetchOpportunitiesByCustomerNumber("customerNumber", "countryCode");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchResellerOrDistributorData_EmptyList() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(user.getGroups()).thenReturn(Collections.emptySet());
        List<String> result = service.fetchResellerOrDistributorData(user, "RESELLER");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchResellerOrDistributorData_NonEmptyList() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(user.getGroups()).thenReturn(Set.of());
        IbmPartnerB2BUnitModel unit = mock(IbmPartnerB2BUnitModel.class);
        IbmPartnerB2BUnitType type = mock(IbmPartnerB2BUnitType.class);
        when(type.getCode()).thenReturn("RESELLER");
        when(unit.getType()).thenReturn(type);
        B2BUnitModel parent = mock(B2BUnitModel.class);
        when(user.getGroups()).thenReturn(Set.of(unit));
        when(b2BUnitService.getParent(unit)).thenReturn(parent);
        when(parent.getUid()).thenReturn("parentUid");
        List<String> result = service.fetchResellerOrDistributorData(user, "RESELLER");
        assertEquals(List.of("parentUid"), result);
    }

    @Test
    void testFetchResellerOrDistributorData_NullParent() {
        B2BCustomerModel user = mock(B2BCustomerModel.class);
        when(user.getGroups()).thenReturn(Set.of());
        IbmPartnerB2BUnitModel unit = mock(IbmPartnerB2BUnitModel.class);
        IbmPartnerB2BUnitType type = mock(IbmPartnerB2BUnitType.class);
        when(type.getCode()).thenReturn("RESELLER");
        when(unit.getType()).thenReturn(type);
        when(user.getGroups()).thenReturn(Set.of(unit));
        when(b2BUnitService.getParent(unit)).thenReturn(null);
        List<String> result = service.fetchResellerOrDistributorData(user, "RESELLER");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetters() {
        assertNotNull(service.getModelService());
        assertNotNull(service.getOpportunityOutboundService());
        assertNotNull(service.getB2BUnitService());
        assertNotNull(service.getOpportunityDao());
        assertNotNull(service.getUserService());
        assertNotNull(service.getConfigurationService());
    }
}