package com.ibm.commerce.partner.core.outbound.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteCustomerMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.customer.impl.B2BCustomerEmailResolutionService;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteCustomersMapperService}
 */
@UnitTest
public class DefaultPartnerCpqCpiQuoteCustomersMapperServiceTest {

    private static final String QUOTE_CREATOR_EMAIL_ID="userUID@commerce.com";
    @InjectMocks
    private DefaultPartnerCpqCpiQuoteCustomersMapperService mapperService;

    @Mock
    private B2BCustomerEmailResolutionService customerEmailResolutionService;

    @Mock
    private IbmPartnerQuoteModel partnerQuoteModel;


    private List<PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>>> partnerCpqCpiQuoteCustomerMappers;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private UserModel user;

    @Mock
    private UserModel submitter;

    @Mock
    private B2BUnitModel unit;

    @Mock
    private B2BUnitModel billToUnit;

    @Mock
    private B2BUnitModel soldThroughUnit;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    B2BCustomerEmailResolutionService customerEmailResolutionServiceMock;


    DefaultPartnerCpqCpiQuoteResellerMapperService partnerCpqCpiQuoteResellerMapperService;

    DefaultPartnerCpqCpiQuoteDistributorMapperService partnerCpqCpiQuoteDistributorMapperService;

    DefaultPartnerCpqCpiQuoteEndCustomerMapperService partnerCpqCpiQuoteEndCustomerMapperService;

    DefaultPartnerCpqCpiQuoteOwnerMapperService partnerCpqCpiQuoteOwnerMapperService;

    DefaultPartnerCpqCpiQuoteSubmitterMapperService partnerCpqCpiQuoteSubmitterMapperService;

    DefaultPartnerCpqCpiQuoteCreaterMapperService partnerCpqCpiQuoteCreaterMapperService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        partnerCpqCpiQuoteResellerMapperService =
            new DefaultPartnerCpqCpiQuoteResellerMapperService();
        partnerCpqCpiQuoteDistributorMapperService =
            new DefaultPartnerCpqCpiQuoteDistributorMapperService();
        partnerCpqCpiQuoteEndCustomerMapperService =
            new DefaultPartnerCpqCpiQuoteEndCustomerMapperService();
        partnerCpqCpiQuoteOwnerMapperService = new DefaultPartnerCpqCpiQuoteOwnerMapperService(
            customerEmailResolutionService);
        partnerCpqCpiQuoteSubmitterMapperService =
            new DefaultPartnerCpqCpiQuoteSubmitterMapperService(customerEmailResolutionService);
        partnerCpqCpiQuoteCreaterMapperService =
            new DefaultPartnerCpqCpiQuoteCreaterMapperService(customerEmailResolutionService);

        partnerCpqCpiQuoteCustomerMappers = new ArrayList<>();
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteCreaterMapperService);
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteSubmitterMapperService);
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteOwnerMapperService);
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteEndCustomerMapperService);
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteDistributorMapperService);
        partnerCpqCpiQuoteCustomerMappers.add(partnerCpqCpiQuoteResellerMapperService);

        mapperService = new DefaultPartnerCpqCpiQuoteCustomersMapperService(
            partnerCpqCpiQuoteCustomerMappers);
        when(partnerQuoteModel.getUser()).thenReturn(user);
        when(user.getUid()).thenReturn("userUID");
        when(user.getName()).thenReturn("userName");

        when(partnerQuoteModel.getSubmitter()).thenReturn(submitter);
        when(submitter.getUid()).thenReturn("submitterUID");
        when(submitter.getName()).thenReturn("submitterName");

        when(partnerQuoteModel.getUnit()).thenReturn(unit);
        when(unit.getUid()).thenReturn("unitUID");
        when(unit.getName()).thenReturn("unitName");

        when(partnerQuoteModel.getBillToUnit()).thenReturn(billToUnit);
        when(billToUnit.getUid()).thenReturn("billToUnitUID");

        when(partnerQuoteModel.getSoldThroughUnit()).thenReturn(soldThroughUnit);
        when(soldThroughUnit.getUid()).thenReturn("soldThroughUnitUID");
        when(soldThroughUnit.getName()).thenReturn("soldThroughUnitName");
        when(customerEmailResolutionService.getEmailForCustomer(Mockito.any())).thenReturn(QUOTE_CREATOR_EMAIL_ID);
    }

    @Test
    public void testQuoteCreatorMap() {
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("userUID");
        user.setName("userName");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "userUID", "userName", PartnercoreConstants.QUOTE_CREATOR_CPQ);
    }

    @Test
    public void testQuoteSubmitterCpqMap(){
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("submitterUID");
        user.setName("submitterName");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "submitterUID", "submitterName",
            PartnercoreConstants.QUOTE_SUBMITTER_CPQ);

    }

    @Test
    public void testQuoteOwnerCpqMap(){
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("userUID");
        user.setName("userName");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "userUID", "userName", PartnercoreConstants.QUOTE_OWNER_CPQ);

    }

    @Test
    public void testEndCustomerCpqMap(){
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("unitUID");
        user.setName("unitName");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "unitUID", "unitName", PartnercoreConstants.END_CUSTOMER_CPQ);

    }

    @Test
    public void testDistributorCpqMap(){
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("billToUnitUID");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "billToUnitUID", null, PartnercoreConstants.DISTRIBUTOR_CPQ);

    }

    @Test
    public void testResellerCpqMap(){
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();
        partnerQuoteModel = new IbmPartnerQuoteModel();
        user = new UserModel();
        user.setUid("soldThroughUnitUID");
        user.setName("soldThroughUnitName");

        partnerQuoteModel.setCreator(user);
        partnerQuoteModel.setSubmitter(user);
        partnerQuoteModel.setUnit(unit);
        partnerQuoteModel.setBillToUnit(billToUnit);
        partnerQuoteModel.setSoldThroughUnit(soldThroughUnit);

        mapperService.map(partnerQuoteModel, sapcpqOutboundQuoteModel);

        Set<SAPCPQOutboundQuoteCustomerModel> customers = sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers();
        assertEquals(6, customers.size());

        verifyCustomer(customers, "soldThroughUnitUID", "soldThroughUnitName",
            PartnercoreConstants.RESELLER_CPQ);

    }


    @Test
    public void testInvalidType() {
        SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel = new SAPCPQOutboundQuoteModel();

        mapperService.map(quoteModel, sapcpqOutboundQuoteModel);
        assertNull(sapcpqOutboundQuoteModel.getSapCPQOutboundQuoteCustomers());
    }


    private void verifyCustomer(Set<SAPCPQOutboundQuoteCustomerModel> customers,
        String expectedCustomerCode, String expectedName, String expectedRoleType) {
        for (SAPCPQOutboundQuoteCustomerModel customer : customers) {
            if (customer.getCustomerCode().equals(expectedCustomerCode) && customer.getRoleType()
                .equals(expectedRoleType)) {
                assertEquals(expectedName, customer.getName());
                return;
            }
        }
        throw new AssertionError(
            "Customer not found: " + expectedCustomerCode + ", " + expectedRoleType);
    }
}