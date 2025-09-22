package com.ibm.commerce.partner.facades.specialbidreason.converter.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidReasonTestDataGenerator;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Locale;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerSpecialBidReasonPopulatorTest {

    private PartnerSpecialBidReasonPopulator partnerSpecialBidReasonPopulator;
    private static final String CODE = "test";
    @Mock
    private CommerceCommonI18NService commerceCommonI18NService;
    @Mock
    private PartnerSpecialBidReasonModel source;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSpecialBidReasonPopulator = new PartnerSpecialBidReasonPopulator();
    }

    @Test
    public void testPopulate() {
        PartnerSpecialBidReasonData target = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreasonData();
        given(source.getCode()).willReturn(CODE);
        given(source.getName(Locale.ENGLISH)).willReturn(CODE);
        given(source.getDefinition(Locale.ENGLISH)).willReturn(CODE);
        given(source.getDescription(Locale.ENGLISH)).willReturn(CODE);
        partnerSpecialBidReasonPopulator.populate(source, target);
        assertEquals(CODE, target.getCode());
    }
}
