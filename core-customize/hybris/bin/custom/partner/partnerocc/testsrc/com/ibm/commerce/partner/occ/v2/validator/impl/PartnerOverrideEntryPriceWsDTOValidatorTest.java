package com.ibm.commerce.partner.occ.v2.validator.impl;

import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideEntryPriceWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerOverrideEntryPriceWsDTOValidatorTest {

    @InjectMocks
    private PartnerOverrideEntryPriceWsDTOValidator partnerOverrideEntryPriceWsDTOValidator;

    private PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO;
    private Errors validationErrors;

    @Before
    public void setUp() {
        partnerOverrideEntryPriceWsDTOValidator = new PartnerOverrideEntryPriceWsDTOValidator();
        partnerOverrideEntryPriceWsDTO = new PartnerOverrideEntryPriceWsDTO();
        validationErrors = new BeanPropertyBindingResult(partnerOverrideEntryPriceWsDTO,
            "partnerOverrideEntryPriceWsDTO");
    }

    @Test
    public void validateEntryNull() {
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(null);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(null);
        partnerOverrideEntryPriceWsDTO.setEntryNumber(null);
        partnerOverrideEntryPriceWsDTO.setOverrideObsoletePrice(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertTrue(validationErrors.hasErrors());
        Assert.assertEquals(1, validationErrors.getErrorCount());
        Assert.assertEquals("entryNumber cannot null",
            validationErrors.getFieldError("entryNumber").getDefaultMessage());
    }

    @Test
    public void validatePriceLessThanZero() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(-1.00);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertTrue(validationErrors.hasErrors());
        Assert.assertEquals(1, validationErrors.getErrorCount());
        Assert.assertEquals("Bid unit price cannot be less than $0",
            validationErrors.getFieldError("overridePrice").getDefaultMessage());
    }

    @Test
    public void validateDiscountLessThanZero() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(-1.00);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertTrue(validationErrors.hasErrors());
        Assert.assertEquals(1, validationErrors.getErrorCount());
        Assert.assertEquals("Discount cannot be less than 0%",
            validationErrors.getFieldError("overrideDiscount").getDefaultMessage());
    }

    @Test
    public void validateDiscountGreaterThanHundred() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(101.01);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertTrue(validationErrors.hasErrors());
        Assert.assertEquals(1, validationErrors.getErrorCount());
        Assert.assertEquals("Discount cannot be greater than 100%",
            validationErrors.getFieldError("overrideDiscount").getDefaultMessage());
    }

    @Test
    public void validatePriceGreaterThanZero() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(1.00);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertFalse(validationErrors.hasErrors());
        Assert.assertEquals(0, validationErrors.getErrorCount());
    }

    @Test
    public void validateOverrideObsoletePriceLessThanZero() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(null);
        partnerOverrideEntryPriceWsDTO.setOverrideObsoletePrice(-1.00);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(null);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertTrue(validationErrors.hasErrors());
        Assert.assertEquals(1, validationErrors.getErrorCount());
        Assert.assertEquals("Obsolete unit price cannot be less than $0",
            validationErrors.getFieldError("overrideObsoletePrice").getDefaultMessage());
    }

    @Test
    public void validateOverrideObsoletePriceGreaterThanZero() {
        partnerOverrideEntryPriceWsDTO.setEntryNumber(0);
        partnerOverrideEntryPriceWsDTO.setOverridePrice(null);
        partnerOverrideEntryPriceWsDTO.setOverrideDiscount(null);
        partnerOverrideEntryPriceWsDTO.setOverrideObsoletePrice(1.00);
        partnerOverrideEntryPriceWsDTOValidator.validate(partnerOverrideEntryPriceWsDTO, validationErrors);
        Assert.assertFalse(validationErrors.hasErrors());
        Assert.assertEquals(0, validationErrors.getErrorCount());
    }

    @Test
    public void validateSupport() {
        Assert.assertFalse(partnerOverrideEntryPriceWsDTOValidator.supports(
            PartnerOverrideEntryPriceWsDTO.class));
    }

    @Test
    public void testValidOverrideYearToYearGrowth_shouldPass() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(50.0);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertFalse("Expected no validation errors for valid YearToYearGrowth", localErrors.hasErrors());
    }

    @Test
    public void testOverrideYearToYearGrowthLessThanZero_shouldFail() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(-1.0);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertTrue(localErrors.hasFieldErrors("OverrideYearToYearGrowth"));
        Assert.assertEquals("Discount cannot be less than 0%", localErrors.getFieldError("OverrideYearToYearGrowth").getDefaultMessage());
    }

    @Test
    public void testOverrideYearToYearGrowthGreaterThan100_shouldFail() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(101.0);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertTrue(localErrors.hasFieldErrors("OverrideYearToYearGrowth"));
        Assert.assertEquals("Discount cannot be greater than 100%", localErrors.getFieldError("OverrideYearToYearGrowth").getDefaultMessage());
    }

    @Test
    public void testOverrideYearToYearGrowthZero_shouldPass() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(0.0);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertFalse("Expected no validation errors for YearToYearGrowth of 0%", localErrors.hasErrors());
    }

    @Test
    public void testOverrideYearToYearGrowthHundred_shouldPass() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(100.0);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertFalse("Expected no validation errors for YearToYearGrowth of 100%", localErrors.hasErrors());
    }

    @Test
    public void testOverrideYearToYearGrowthDecimalWithinRange_shouldPass() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(99.99);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertFalse("Expected no validation errors for YearToYearGrowth of 99.99%", localErrors.hasErrors());
    }

    @Test
    public void testOverrideYearToYearGrowthDecimalLessThanZero_shouldFail() {
        PartnerOverrideEntryPriceWsDTO dto = new PartnerOverrideEntryPriceWsDTO();
        dto.setEntryNumber(1);
        dto.setOverrideYearToYearGrowth(-0.1);
        Errors localErrors = new BeanPropertyBindingResult(dto, "dto");
        partnerOverrideEntryPriceWsDTOValidator.validate(dto, localErrors);
        Assert.assertTrue(localErrors.hasFieldErrors("OverrideYearToYearGrowth"));
        Assert.assertEquals("Discount cannot be less than 0%", localErrors.getFieldError("OverrideYearToYearGrowth").getDefaultMessage());
    }
}
