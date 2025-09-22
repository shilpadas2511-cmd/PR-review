package com.ibm.commerce.partner.facades.quoteservice.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import com.ibm.commerce.partner.core.util.model.PartnerQuoteApprovalsInfoResponseDataTestGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.comment.data.CommentData;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Date;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@UnitTest
public class PartnerQuoteServicePopulatorTest {
    @InjectMocks
    private  PartnerQuoteServicePopulator partnerQuoteServicePopulator;

    @Test
    public void testPopulate() throws ConversionException {
        partnerQuoteServicePopulator = new PartnerQuoteServicePopulator();
        PartnerQuoteApprovalsInfoResponseData partnerQuoteApprovalsInfoResponseData = PartnerQuoteApprovalsInfoResponseDataTestGenerator.create();
        CommentData commentData = new CommentData();
        partnerQuoteServicePopulator.populate(partnerQuoteApprovalsInfoResponseData, commentData);
    }
}
