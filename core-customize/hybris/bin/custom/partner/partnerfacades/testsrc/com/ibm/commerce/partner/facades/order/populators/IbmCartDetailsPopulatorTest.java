package com.ibm.commerce.partner.facades.order.populators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.*;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.*;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import com.ibm.commerce.partner.data.order.pricing.YtyYearData;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class IbmCartDetailsPopulatorTest {

    IbmCartDetailsPopulator populator;

    Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementConv;
    Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustConv;
    Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bConv;
    Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> oppConv;
    Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> specialBidConv;
    Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> headerPricingConv;
    Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> questionsConv;
    Converter<PartnerCpqHeaderPricingDetailModel, List<YtyYearData>> ytyConv;
    Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionConv;
    ConfigurationService configService;
    PartnerUserService userService;
    Converter<CurrencyModel, CurrencyData> currencyConv;
    Configuration config;

    @Before
    public void setUp() {
        agreementConv = mock(Converter.class);
        endCustConv = mock(Converter.class);
        b2bConv = mock(Converter.class);
        oppConv = mock(Converter.class);
        specialBidConv = mock(Converter.class);
        headerPricingConv = mock(Converter.class);
        questionsConv = mock(Converter.class);
        ytyConv = mock(Converter.class);
        provisionConv = mock(Converter.class);
        configService = mock(ConfigurationService.class);
        config = mock(Configuration.class);
        when(configService.getConfiguration()).thenReturn(config);
        // always enable edit special-bid, multiple selection disabled
        when(config.getBoolean(anyString(), anyBoolean())).thenReturn(true);

        userService = mock(PartnerUserService.class);
        when(userService.isVadView(any(), any())).thenReturn(false);

        currencyConv = mock(Converter.class);

        populator = new IbmCartDetailsPopulator(
            agreementConv, endCustConv, b2bConv, oppConv,
            specialBidConv, headerPricingConv, questionsConv, ytyConv, provisionConv,
            configService, userService, currencyConv);
    }

    @Test
    public void testPopulateAllBranches() {
        IbmPartnerCartModel source = new IbmPartnerCartModel();
        CartData target = new CartData();

        // Opportunity
        source.setOpportunity(new IbmPartnerOpportunityModel());
        IbmPartnerOpportunityData oppData = new IbmPartnerOpportunityData();
        when(oppConv.convert(source.getOpportunity())).thenReturn(oppData);

        // Agreement
        source.setAgreementDetail(new IbmPartnerAgreementDetailModel());
        IbmPartnerAgreementDetailData agrData = new IbmPartnerAgreementDetailData();
        when(agreementConv.convert(source.getAgreementDetail())).thenReturn(agrData);

        // Units
        IbmPartnerEndCustomerB2BUnitModel endCust = new IbmPartnerEndCustomerB2BUnitModel();
        source.setUnit(endCust);
        source.setSoldThroughUnit(endCust);
        source.setBillToUnit(endCust);
        IbmPartnerEndCustomerB2BUnitData endCustData = new IbmPartnerEndCustomerB2BUnitData();
        when(endCustConv.convert(endCust)).thenReturn(endCustData);

        // Pricing details & YTY
        PartnerCpqHeaderPricingDetailModel hdr = new PartnerCpqHeaderPricingDetailModel();
        hdr.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        source.setPricingDetails(List.of(hdr));
        when(headerPricingConv.convert(hdr)).thenReturn(new PartnerCpqHeaderPricingDetailData());
        when(ytyConv.convert(hdr)).thenReturn(List.of(new YtyYearData()));

        // Metadata
        Date now = new Date();
        source.setModifiedtime(now);
        source.setYtyPercentage(5.0);
        source.setTotalMEPPrice(10.0);
        source.setTotalFullPrice(20.0);
        source.setTotalBidExtendedPrice(30.0);
        source.setTotalDiscounts(2.5);
        source.setErrorMesaage("err");
        source.setQuoteExpirationDate(now);
        source.setFullPriceReceived(Boolean.TRUE);
        source.setPriceStale(Boolean.TRUE);

        // SpecialBid
        PartnerSpecialBidReasonModel sb = new PartnerSpecialBidReasonModel();
        when(specialBidConv.convert(sb)).thenReturn(new PartnerSpecialBidReasonData());
        source.setSpecialBidReason(sb);
        source.setSpecialBidBusinessJustification("justification");

        // Questions
        PartnerQuestionsSelectionModel qm = new PartnerQuestionsSelectionModel();
        source.setPartnerQuestionsSelections(Set.of(qm));
        PartnerQuestionsSelectionData qd = new PartnerQuestionsSelectionData();
        when(questionsConv.convert(qm)).thenReturn(qd);

        // Provision forms
        PartnerProvisionFormsModel formsModel = new PartnerProvisionFormsModel();
        PartnerProvisionFormModel pf = new PartnerProvisionFormModel();
        formsModel.setPartnerProvisionForm(Set.of(pf));
        source.setProvisionForms(formsModel);
        ProvisioningFormData pd = new ProvisioningFormData();
        when(provisionConv.convert(pf)).thenReturn(pd);

        // Collaborator emails
        source.setCollaboratorEmails(Set.of("a@b.com"));

        // Entries to test editable logic
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        CPQOrderEntryProductInfoModel cpqInfo = mock(CPQOrderEntryProductInfoModel.class);
        when(cpqInfo.getCpqCharacteristicName()).thenReturn(
            PartnercoreConstants.PRODUCT_SALE_STATE_CODE);
        when(cpqInfo.getCpqCharacteristicAssignedValues()).thenReturn(
            PartnercoreConstants.PRODUCT_SALE_STATE_CODE_VALUE);
        when(entry.getProductInfos()).thenReturn(List.of(cpqInfo));
        PartnerCpqPricingDetailModel pdm = mock(PartnerCpqPricingDetailModel.class);
        when(pdm.getEccPriceAvailable()).thenReturn(Boolean.FALSE);
        when(entry.getCpqPricingDetails()).thenReturn(List.of(pdm));
        // Wrap entry into OrderEntryData
        OrderEntryData oed = new OrderEntryData();
        oed.setEntries(List.of(new OrderEntryData()));
        target.setEntries(List.of(oed));
        source.setEntries(List.of(entry));

        // Currency
        CurrencyModel cm = new CurrencyModel();
        CurrencyData cd = new CurrencyData();
        when(currencyConv.convert(cm)).thenReturn(cd);
        source.setCurrency(cm);

        populator.populate(source, target);

        // assertions
        assertEquals("justification", target.getSpecialBidBusinessJustification());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateNullSource() {
        populator.populate(null, new CartData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPopulateNullTarget() {
        populator.populate(new IbmPartnerCartModel(), null);
    }

    @Test
    public void testGetPartnerCpqHeaderPricingDetail_NoFullMatch() {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        PartnerCpqHeaderPricingDetailModel p1 = new PartnerCpqHeaderPricingDetailModel();
        p1.setPricingType("PARTIAL");
        cart.setPricingDetails(List.of(p1));
        assertNull(populator.getPartnerCpqHeaderPricingDetail(cart));
    }

    @Test
    public void testEditableFalseWhenFeatureOff() {
        when(config.getBoolean(PartnercoreConstants.FEATURE_FLAG_ENABLE_QUOTE_EDIT_SPECIAL_BID,
            false))
            .thenReturn(false);
        IbmPartnerCartModel source = new IbmPartnerCartModel();
        CartData target = new CartData();
        target.setEntries(List.of(new OrderEntryData()));
        populator.populate(source, target);
        assertFalse(target.isEditable());
    }

    @Test
    public void testPopulateSpecialBidDetails_MultiSelectEnabled() {
        when(config.getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
            true))
            .thenReturn(false);

        IbmPartnerCartModel source = new IbmPartnerCartModel();
        CartData target = new CartData();

        PartnerSpecialBidReasonModel sb1 = new PartnerSpecialBidReasonModel();
        PartnerSpecialBidReasonData sbData = new PartnerSpecialBidReasonData();
        Set<PartnerSpecialBidReasonModel> reasons = new HashSet<>();
        reasons.add(sb1);
        source.setSpecialBidReasons(reasons);
        source.setSpecialBidBusinessJustification("multi-justification");

        when(specialBidConv.convertAll(reasons)).thenReturn(List.of(sbData));

        populator.populate(source, target);

        assertEquals(1, target.getSpecialBidReasons().size());
        assertEquals("multi-justification", target.getSpecialBidBusinessJustification());
    }

    @Test
    public void testPopulateProvisionFormWithErrors() {
        IbmPartnerCartModel source = new IbmPartnerCartModel();
        CartData target = new CartData();

        PartnerProvisionFormsModel formsModel = new PartnerProvisionFormsModel();
        formsModel.setErrors("Form Error!");
        source.setProvisionForms(formsModel);
        populator.populate(source, target);
        assertEquals("Form Error!", target.getProvisionFormError());
    }

    @Test
    public void testIsHeaderEditableForObsolete_NoMatch() {
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        CPQOrderEntryProductInfoModel info = mock(CPQOrderEntryProductInfoModel.class);
        when(info.getCpqCharacteristicName()).thenReturn("OtherCode");
        when(info.getCpqCharacteristicAssignedValues()).thenReturn("OtherValue");
        when(entry.getProductInfos()).thenReturn(List.of(info));

        assertFalse(populator.isHeaderEditableForObsolete(entry));
    }

    @Test
    public void testIsPriceAvailableInECC_WithTrueValue() {
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        PartnerCpqPricingDetailModel pdm = mock(PartnerCpqPricingDetailModel.class);
        when(pdm.getEccPriceAvailable()).thenReturn(Boolean.TRUE);
        when(entry.getCpqPricingDetails()).thenReturn(List.of(pdm));

        assertFalse(populator.isPriceAvailableInECC(entry)); // All must be FALSE to return true
    }


}
