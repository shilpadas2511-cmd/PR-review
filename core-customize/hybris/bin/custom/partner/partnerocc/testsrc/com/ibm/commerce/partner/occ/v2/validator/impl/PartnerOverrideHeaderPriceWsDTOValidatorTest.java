package com.ibm.commerce.partner.occ.v2.validator.impl;

import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideEntryPriceWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideHeaderPriceWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Collection;

@UnitTest
@RunWith(Parameterized.class)
public class PartnerOverrideHeaderPriceWsDTOValidatorTest {

    @InjectMocks
    private PartnerOverrideHeaderPriceWsDTOValidator partnerOverrideHeaderPriceWsDTOValidator;

    private PartnerOverrideHeaderPriceWsDTO partnerOverrideHeaderPriceWsDTO;
    private Errors errors;

    private final Double totalPrice;
    private final Double totalDiscount;
    private final boolean expectErrors;
    private final String expectedMessage;
    private final String expectedField;

    public PartnerOverrideHeaderPriceWsDTOValidatorTest(Double totalPrice, Double totalDiscount, boolean expectErrors,
        String expectedMessage, String expectedField) {
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
        this.expectErrors = expectErrors;
        this.expectedMessage = expectedMessage;
        this.expectedField = expectedField;
    }

    @Before
    public void setUp() {
        partnerOverrideHeaderPriceWsDTOValidator = new PartnerOverrideHeaderPriceWsDTOValidator();
        partnerOverrideHeaderPriceWsDTO = new PartnerOverrideHeaderPriceWsDTO();
    }

    @Parameters(name = "{index}: price={0}, discount={1}, error={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            { -1.00, null, true, "Total Bid price cannot be less than $0", "overrideTotalPrice" },
            { null, -1.00, true, "Total discount cannot be less than 0%", "overrideTotalDiscount" },
            { null, 101.01, true, "Total discount cannot be greater than 100%", "overrideTotalDiscount" },
            { 1.00, null, false, null, null }
        });
    }

    @Test
    public void validate() {
        partnerOverrideHeaderPriceWsDTO.setOverrideTotalPrice(totalPrice);
        partnerOverrideHeaderPriceWsDTO.setOverrideTotalDiscount(totalDiscount);
        errors = new BeanPropertyBindingResult(partnerOverrideHeaderPriceWsDTO, "partnerOverrideHeaderPriceWsDTO");

        partnerOverrideHeaderPriceWsDTOValidator.validate(partnerOverrideHeaderPriceWsDTO, errors);

        Assert.assertEquals(expectErrors, errors.hasErrors());
        if (expectErrors) {
            Assert.assertEquals(1, errors.getErrorCount());
            Assert.assertEquals(expectedMessage, errors.getFieldError(expectedField).getDefaultMessage());
        } else {
            Assert.assertEquals(0, errors.getErrorCount());
        }
    }

    @Test
    public void validateNonInstance() {
        PartnerOverrideEntryPriceWsDTO entryPrice = new PartnerOverrideEntryPriceWsDTO();
        errors = new BeanPropertyBindingResult(entryPrice, "entryPrice");
        partnerOverrideHeaderPriceWsDTOValidator.validate(entryPrice, errors);
        Assert.assertFalse(errors.hasErrors());
    }

    @Test
    public void validateSupport() {
        Assert.assertFalse(partnerOverrideHeaderPriceWsDTOValidator.supports(PartnerOverrideHeaderPriceWsDTO.class));
    }
}
