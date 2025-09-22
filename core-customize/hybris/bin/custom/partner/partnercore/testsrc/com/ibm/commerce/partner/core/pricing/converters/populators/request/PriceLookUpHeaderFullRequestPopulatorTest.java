package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;

@UnitTest
public class PriceLookUpHeaderFullRequestPopulatorTest {

    private PriceLookUpHeaderFullRequestPopulator populator;

    @Before
    public void setUp() {
        populator = new PriceLookUpHeaderFullRequestPopulator();
    }

    @Test
    public void testPopulate() {
		 IbmPartnerCartModel source = new IbmPartnerCartModel();
		 source.setBillToUnit(new IbmB2BUnitModel());
		 source.setSoldThroughUnit(new IbmB2BUnitModel());
		 PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
		 List<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailModelList = new ArrayList<>();
		 PartnerCpqHeaderPricingDetailModel priceDetail = new PartnerCpqHeaderPricingDetailModel();
		 priceDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
		 priceDetail.setOverrideTotalPrice(10.0);
		 priceDetail.setInitialTotalExtendedPrice(5.0);
		 priceDetail.setOverrideTotalDiscount(10.0);
		 partnerCpqHeaderPricingDetailModelList.add(priceDetail);
		 source.setPricingDetails(partnerCpqHeaderPricingDetailModelList);

		 populator.populate(source, target);

		 assertEquals(false, target.isEntitledPriceOnly());
		 assertEquals("10.0", target.getOverrideTotalPrice());
		 assertEquals("5.0", target.getInitialTotalExtendedPrice());
		 assertEquals("10.0", target.getOverrideTotalDiscount());
    }

	 @Test
	 public void testPopulateWithNoDiscount() {
		 IbmPartnerCartModel source = new IbmPartnerCartModel();
		 source.setBillToUnit(new IbmB2BUnitModel());
		 source.setSoldThroughUnit(new IbmB2BUnitModel());
		 PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
		 List<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailModelList = new ArrayList<>();
		 PartnerCpqHeaderPricingDetailModel priceDetail = new PartnerCpqHeaderPricingDetailModel();
		 priceDetail.setPricingType(CpqPricingTypeEnum.FULL.getCode());
		 partnerCpqHeaderPricingDetailModelList.add(priceDetail);
		 source.setPricingDetails(partnerCpqHeaderPricingDetailModelList);

		 populator.populate(source, target);

		 assertEquals(false, target.isEntitledPriceOnly());
		 assertNull(target.getOverrideTotalPrice());
		 assertNull(target.getInitialTotalExtendedPrice());
		 assertNull(target.getOverrideTotalDiscount());
	 }

	 @Test
	 public void testPopulateWithNoPricingDetail() {
		 IbmPartnerCartModel source = new IbmPartnerCartModel();
		 source.setBillToUnit(new IbmB2BUnitModel());
		 source.setSoldThroughUnit(new IbmB2BUnitModel());
		 PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
		 List<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailModelList = new ArrayList<>();

		 populator.populate(source, target);

		 assertEquals(false, target.isEntitledPriceOnly());
		 assertNull(target.getOverrideTotalPrice());
		 assertNull(target.getInitialTotalExtendedPrice());
		 assertNull(target.getOverrideTotalDiscount());
	 }

    @Test
    public void testPopulateSourceNull() {
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();

        populator.populate(null, target);

        assertEquals(false, target.isEntitledPriceOnly());
    }
}