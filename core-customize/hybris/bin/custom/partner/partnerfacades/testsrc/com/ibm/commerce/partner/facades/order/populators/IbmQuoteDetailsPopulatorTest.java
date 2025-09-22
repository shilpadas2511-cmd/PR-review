package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.enums.CpqQuoteStatusType;
import com.ibm.commerce.partner.core.model.*;
import com.ibm.commerce.partner.core.order.services.PartnerCommerceOrderService;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.order.data.PartnerOrderData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

@UnitTest
public class IbmQuoteDetailsPopulatorTest {

    @InjectMocks
    private IbmQuoteDetailsPopulator populator;

    @Mock
    private Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDataConverter;
    @Mock
    private Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustomerB2BUnitDataConverter;
    @Mock
    private Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter;
    @Mock
    private Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> opportunityDataConverter;
    @Mock
    private Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    @Mock
    private Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> specialBidReasonConverter;
    @Mock
    private Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> questionsSelectionConverter;
    @Mock
    private Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> headerPricingDetailConverter;
    @Mock
    private Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionFormConverter;
    @Mock
    private Converter<OrderModel, PartnerOrderData> orderConverter;
    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Mock
    private PartnerUserService userService;
    @Mock
    private PartnerCommerceOrderService orderService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    private IbmPartnerQuoteModel source;
    private QuoteData target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(null);
        when(userService.isVadView(any(), any())).thenReturn(true);

        populator = new IbmQuoteDetailsPopulator(
            agreementDataConverter,
            endCustomerB2BUnitDataConverter,
            b2bUnitDataConverter,
            opportunityDataConverter,
            displayTypeDataConverter,
            headerPricingDetailConverter,
            provisionFormConverter,
            "yyyy-MM-dd",
            specialBidReasonConverter,
            questionsSelectionConverter,
            orderConverter,
            userService,
            orderService,
            currencyConverter,
            configurationService
        );

        source = new IbmPartnerQuoteModel();
        target = new QuoteData();

        source.setAgreementDetail(new IbmPartnerAgreementDetailModel());
        source.setUnit(new IbmPartnerEndCustomerB2BUnitModel());
        source.setSoldThroughUnit(new IbmB2BUnitModel());
        source.setBillToUnit(new IbmB2BUnitModel());
        source.setOpportunity(new IbmPartnerOpportunityModel());
        source.setQuoteExpirationDate(new Date());
        source.setYtyPercentage(11.11);
        source.setTotalMEPPrice(111.11);
        source.setTotalFullPrice(222.22);
        source.setSubmittedDate(new Date());
        source.setCpqQuoteStatus(CpqQuoteStatusType.SUBMITTED);
        source.setState(QuoteState.BUYER_SUBMITTED);
        source.setEccQuoteNumber("ECC123");
        source.setSpecialBidBusinessJustification("Justify");
        source.setSpecialBidReason(new PartnerSpecialBidReasonModel());
        source.setSalesApplication(SalesApplication.PARTNER_COMMERCE);
        source.setCollaboratorEmails(Set.of("email@ibm.com"));
        source.setCartReference(mock(IbmPartnerCartModel.class));
        source.setCurrency(new CurrencyModel());

        PartnerQuestionsSelectionModel q1 = new PartnerQuestionsSelectionModel();
        source.setPartnerQuestionsSelections(List.of(q1));

        PartnerCpqHeaderPricingDetailModel pricingDetail = new PartnerCpqHeaderPricingDetailModel();
        source.setPricingDetailsQuote(List.of(pricingDetail));

        PartnerProvisionFormsModel forms = new PartnerProvisionFormsModel();
        PartnerProvisionFormModel form = new PartnerProvisionFormModel();
        forms.setPartnerProvisionForm(Set.of(form));
        source.setProvisionForms(forms);

        when(agreementDataConverter.convert(any())).thenReturn(new IbmPartnerAgreementDetailData());
        when(endCustomerB2BUnitDataConverter.convert(any())).thenReturn(
            new IbmPartnerEndCustomerB2BUnitData());
        when(b2bUnitDataConverter.convert(any())).thenReturn(new IbmPartnerB2BUnitData());
        when(opportunityDataConverter.convert(any())).thenReturn(new IbmPartnerOpportunityData());
        when(displayTypeDataConverter.convert(any())).thenReturn(new DisplayTypeData());
        when(specialBidReasonConverter.convert(any())).thenReturn(
            new PartnerSpecialBidReasonData());
        when(questionsSelectionConverter.convert(any())).thenReturn(
            new PartnerQuestionsSelectionData());
        when(headerPricingDetailConverter.convert(any())).thenReturn(
            new PartnerCpqHeaderPricingDetailData());
        when(provisionFormConverter.convert(any())).thenReturn(new ProvisioningFormData());
        when(orderConverter.convert(any())).thenReturn(new PartnerOrderData());
        when(orderService.findOrdersByQuote(any())).thenReturn(List.of(new OrderModel()));
        when(currencyConverter.convert(any())).thenReturn(new CurrencyData());
        when(
            ((IbmPartnerCartModel) source.getCartReference()).getTotalBidExtendedPrice()).thenReturn(
            123.45);
        when(((IbmPartnerCartModel) source.getCartReference()).getFullPriceReceived()).thenReturn(
            true);
    }

    @Test
    public void testPopulate_fullFlow() {
        populator.populate(source, target);
        assertNotNull(target.getAgreementDetail());
        assertNotNull(target.getShipToUnit());
        assertNotNull(target.getSoldThroughUnit());
        assertNotNull(target.getBillToUnit());
        assertNotNull(target.getOpportunity());
        assertEquals("ECC123", target.getEccQuoteNumber());
        assertEquals("Justify", target.getSpecialBidBusinessJustification());
        assertEquals(1, target.getCollaboratorEmails().size());
        assertEquals(123.45, target.getTotalBidExtendedPrice(), 0.01);
        assertTrue(target.isFullPriceReceived());
        assertNotNull(target.getCurrency());
        assertEquals(1, target.getPartnerQuestionsSelections().size());
        assertEquals(1, target.getPartnerCpqHeaderPricingDetails().size());
        assertEquals(1, target.getOrders().size());
        assertEquals(1, target.getProvisionForms().size());
        assertNotNull(target.getQuoteStatus());
        assertNotNull(target.getCpqQuoteStatus());
        assertNotNull(target.getSubmittedDate());
    }

    @Test
    public void testPopulate_multipleSpecialBidDisabled_false() {
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
        PartnerSpecialBidReasonModel reason = new PartnerSpecialBidReasonModel();
        source.setSpecialBidReasons(Set.of(reason));
        when(specialBidReasonConverter.convertAll(any())).thenReturn(
            List.of(new PartnerSpecialBidReasonData()));
        populator.populate(source, target);
        assertEquals(1, target.getSpecialBidReasons().size());
        assertTrue(target.isSpecialBid());
    }

    @Test
    public void testPopulate_specialBidReasonWhenMultipleDisabledTrue() {
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        PartnerSpecialBidReasonModel reason = new PartnerSpecialBidReasonModel();
        source.setSpecialBidReason(reason);
        PartnerSpecialBidReasonData reasonData = new PartnerSpecialBidReasonData();
        when(specialBidReasonConverter.convert(reason)).thenReturn(reasonData);

        populator.populate(source, target);
    }

    @Test
    public void testPopulate_whenProvisionFormsHasErrors() {
        PartnerProvisionFormsModel forms = new PartnerProvisionFormsModel();
        forms.setErrors("Form Error Found");
        source.setProvisionForms(forms);
        populator.populate(source, target);
        assertEquals("Form Error Found", target.getProvisionFormError());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulate_sourceNull() {
        populator.populate(null, new QuoteData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulate_targetNull() {
        populator.populate(new IbmPartnerQuoteModel(), null);
    }

    @Test
    public void testPopulate_emptyQuoteModel() {
        IbmPartnerQuoteModel empty = new IbmPartnerQuoteModel();
        populator.populate(empty, target);
        assertNull(target.getAgreementDetail());
        assertNull(target.getShipToUnit());
        assertNull(target.getBillToUnit());
    }

}
