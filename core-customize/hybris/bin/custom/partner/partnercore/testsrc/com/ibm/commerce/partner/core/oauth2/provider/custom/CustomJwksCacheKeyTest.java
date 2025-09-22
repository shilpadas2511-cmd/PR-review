package com.ibm.commerce.partner.core.oauth2.provider.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

@UnitTest
public class CustomJwksCacheKeyTest {

    @Test
    public void testGetCacheValueType() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertNull("CacheValueType should be null", customJwksCacheKey.getCacheValueType());
    }

    @Test
    public void testGetTypeCode() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertEquals("CustomCacheKeyType", customJwksCacheKey.getTypeCode());
    }

    @Test
    public void testGetTenantId() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertNull("TenantId should be null by default", customJwksCacheKey.getTenantId());
    }

    @Test
    public void testGetCacheId() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertEquals("region1:key1", customJwksCacheKey.getCacheId());
    }

    @Test
    public void testEquals_SameObject() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertEquals(customJwksCacheKey, customJwksCacheKey);
    }

    @Test
    public void testEquals_DifferentObject_SameValues() {
        CustomJwksCacheKey key1 = new CustomJwksCacheKey("region1", "key1");
        CustomJwksCacheKey key2 = new CustomJwksCacheKey("region1", "key1");
        assertEquals(key1, key2);
    }

    @Test
    public void testEquals_DifferentObject_DifferentValues() {
        CustomJwksCacheKey key1 = new CustomJwksCacheKey("region1", "key1");
        CustomJwksCacheKey key2 = new CustomJwksCacheKey("region2", "key2");
        assertNotEquals(key1, key2);
    }

    @Test
    public void testHashCode_SameObject() {
        CustomJwksCacheKey customJwksCacheKey = new CustomJwksCacheKey("region1", "key1");
        assertEquals(customJwksCacheKey.hashCode(), customJwksCacheKey.hashCode());
    }

    @Test
    public void testHashCode_SameValues() {
        CustomJwksCacheKey key1 = new CustomJwksCacheKey("region1", "key1");
        CustomJwksCacheKey key2 = new CustomJwksCacheKey("region1", "key1");
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void testHashCode_DifferentValues() {
        CustomJwksCacheKey key1 = new CustomJwksCacheKey("region1", "key1");
        CustomJwksCacheKey key2 = new CustomJwksCacheKey("region2", "key2");
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

}
