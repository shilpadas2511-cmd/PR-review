package com.ibm.commerce.partner.core.strategy;

import static org.junit.Assert.assertEquals;

import com.ibm.commerce.partner.core.strategy.impl.PartnerSaveCartTextGenerationStrategy;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;

import de.hybris.platform.core.model.order.CartModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerSavedCartTextGenerationStrategyTest {

    @InjectMocks
    private PartnerSaveCartTextGenerationStrategy partnerSaveCartTextGenerationStrategy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private static final String COPY_COUNT_REGEX = "(\\()([^)]+)(\\))$";
    private static final String BASE_CART_NAME = "test saved cart";
    private static final String PERANTHESIS_COUNT = "(1)";
    private static final String PERANTHESIS_OPEN = "(";
    private static final String PERANTHESIS_CLOSE = ")";
    private final static String SINGLE_WHITE_SPACE = " ";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGenerateCloneSaveCartNameWithNumberSuffix() {
        final CartModel cartModel = CartModelTestDataGenerator.createCartModel("433");
        cartModel.setName(BASE_CART_NAME);
        String name = partnerSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
        assertEquals(BASE_CART_NAME + SINGLE_WHITE_SPACE + PERANTHESIS_COUNT, name);

        for (int i = 2; i <= 10; i++)
        {
            cartModel.setName(name);
            name = partnerSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
            assertEquals(BASE_CART_NAME + SINGLE_WHITE_SPACE + PERANTHESIS_OPEN + i + PERANTHESIS_CLOSE, name);
        }
    }

    @Test
    public void testGenerateCloneSaveCartNameNumberSuffixWithNullRegex()
    {
        final CartModel cartModel = CartModelTestDataGenerator.createCartModel("434");
        thrown.expect(IllegalArgumentException.class);
        partnerSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, null);

    }

    @Test
    public void testGenerateCloneSaveCartNameNumberSuffixWithNullCartParam()
    {
        thrown.expect(IllegalArgumentException.class);
        partnerSaveCartTextGenerationStrategy.generateCloneSaveCartName(null, COPY_COUNT_REGEX);
    }
}
