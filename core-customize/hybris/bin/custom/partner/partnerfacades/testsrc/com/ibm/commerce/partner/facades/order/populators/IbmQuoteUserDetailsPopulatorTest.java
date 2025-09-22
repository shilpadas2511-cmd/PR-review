
package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.assertEquals;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test class for IbmQuoteUserDetailsPopulator
 */

@UnitTest
public class IbmQuoteUserDetailsPopulatorTest {

    @InjectMocks
    IbmQuoteUserDetailsPopulator ibmQuoteUserDetailsPopulator;

    @Mock
    private Converter<UserModel, CustomerData> b2bCustomerConverter;
    @Mock
    IbmPartnerQuoteModel ibmPartnerQuoteModel;
    @Mock
    PartnerB2BCustomerModel quoteCreater;
    @Mock
    PartnerB2BCustomerModel quoteSubmitter;
    @Mock
    QuoteData quoteData;
    @Mock
    CustomerData customerData;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        quoteData = mock(QuoteData.class);
        customerData = mock(CustomerData.class);
        quoteCreater = mock(PartnerB2BCustomerModel.class);
        quoteSubmitter = mock(PartnerB2BCustomerModel.class);
        ibmQuoteUserDetailsPopulator = new IbmQuoteUserDetailsPopulator(b2bCustomerConverter);
        ibmPartnerQuoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(
            quoteCreater, quoteSubmitter);
    }


    /**
     * test method of populate method of IbmQuoteUserDetailsPopulator class
     */

   /** @Test
    public void testPopulate() {
        when(quoteData.getQuoteCreater()).thenReturn(customerData);
        when(quoteData.getQuoteSubmitter()).thenReturn(customerData);
        when(b2bCustomerConverter.convert(any())).thenReturn(customerData);
        ibmQuoteUserDetailsPopulator.populate(ibmPartnerQuoteModel, quoteData);
        Assert.assertEquals(customerData, quoteData.getQuoteCreater());
        Assert.assertEquals(customerData, quoteData.getQuoteSubmitter());
    }*/
   @Test
   public void testPopulate() {
       QuoteData quoteData = new QuoteData();

       // Mock the input model
       UserModel creator = mock(UserModel.class);
       UserModel submitter = mock(UserModel.class);

       IbmPartnerQuoteModel ibmPartnerQuoteModel = mock(IbmPartnerQuoteModel.class);

       when(ibmPartnerQuoteModel.getCreator()).thenReturn(creator);
       when(ibmPartnerQuoteModel.getSubmitter()).thenReturn(submitter);

       // Create real return data
       CustomerData creatorData = new CustomerData();
       CustomerData submitterData = new CustomerData();

       when(b2bCustomerConverter.convert(creator)).thenReturn(creatorData);
       when(b2bCustomerConverter.convert(submitter)).thenReturn(submitterData);

       // Call populate
       ibmQuoteUserDetailsPopulator.populate(ibmPartnerQuoteModel, quoteData);

       // Assert the fields were set
       assertEquals(creatorData, quoteData.getQuoteCreater());
       assertEquals(submitterData, quoteData.getQuoteSubmitter());
   }



    /**
     * test method when QuoteCreater is null
     */

    @Test
    public void testPopulateWhenUserNull() {
        QuoteData quoteData = new QuoteData();
        ibmPartnerQuoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(null,
            quoteSubmitter);
        CustomerData b2bCustomerData = new CustomerData();
        when(b2bCustomerConverter.convert(any())).thenReturn(b2bCustomerData);
        ibmQuoteUserDetailsPopulator.populate(ibmPartnerQuoteModel, quoteData);
        Assert.assertNull(quoteData.getQuoteCreater());
        Assert.assertEquals(b2bCustomerData, quoteData.getQuoteSubmitter());
    }


    /**
     * test method when QuoteSubmitter is null
     */

    @Test
    public void testPopulateWhenSubmitterNull() {
        ibmPartnerQuoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(
            quoteCreater, null);
        when(b2bCustomerConverter.convert(any())).thenReturn(customerData);
        when(quoteData.getQuoteCreater()).thenReturn(customerData);
        ibmQuoteUserDetailsPopulator.populate(ibmPartnerQuoteModel, quoteData);
        Assert.assertEquals(customerData, quoteData.getQuoteCreater());
        Assert.assertNull(quoteData.getQuoteSubmitter());
    }


    /**
     * test method when QuoteCreater and QuoteSubmitter is null
     */

    @Test
    public void testPopulateWhenUserSubmitterNull() {
        QuoteData quoteData = new QuoteData();
        ibmPartnerQuoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(null,
            null);
        CustomerData b2bCustomerData = new CustomerData();
        when(b2bCustomerConverter.convert(any())).thenReturn(b2bCustomerData);
        ibmQuoteUserDetailsPopulator.populate(ibmPartnerQuoteModel, quoteData);
        Assert.assertNull(quoteData.getQuoteCreater());
        Assert.assertNull(quoteData.getQuoteSubmitter());
    }
}
