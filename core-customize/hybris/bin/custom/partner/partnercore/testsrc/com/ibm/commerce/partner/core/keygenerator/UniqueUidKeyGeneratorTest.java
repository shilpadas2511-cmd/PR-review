package com.ibm.commerce.partner.core.keygenerator;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class UniqueUidKeyGeneratorTest {

    private UniqueUidKeyGenerator keyGenerator;
    private static final String INPUTSTRING = "TEST";

    @Before
    public void setUp() {
        keyGenerator = new UniqueUidKeyGenerator();
    }

    @Test
    public void testGenerateForString() {
        Object key = keyGenerator.generateFor(INPUTSTRING);

        Assert.assertNotNull(key);
        Assert.assertTrue(key instanceof String);

        String keyString = (String) key;
        Assert.assertTrue(keyString.startsWith(INPUTSTRING));
    }

    @Test
    public void testGenerate() {
        Object key = keyGenerator.generate();

        Assert.assertNotNull(key);
        Assert.assertTrue(key instanceof String);

        String keyString = (String) key;
        Assert.assertNotNull(keyString);
    }

    @Test
    public void testGenerateForNonString() {
        try {
            keyGenerator.generateFor(new Object());
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            Assert.assertTrue(e instanceof UnsupportedOperationException);
        }
    }
}
