package com.ibm.commerce.partner.core.oauth2.provider.custom;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import java.util.Objects;

/**
 * Custom Cache Key class to implement the CacheKey
 */
public class CustomJwksCacheKey implements CacheKey {
    private final String regionName;
    private final String key;

    public CustomJwksCacheKey(String regionName, String key) {
        this.regionName = regionName;
        this.key = key;
    }

    /**
     * This method returns the CacheValueType
     *
     * @return String
     */
    @Override
    public CacheUnitValueType getCacheValueType() {
        return null;
    }

    /**
     * This method gets the TypeCode
     *
     * @return String
     */
    @Override
    public String getTypeCode() {
        return "CustomCacheKeyType";
    }

    /**
     * This method returns the default tenant id
     *
     * @return String
     */
    @Override
    public String getTenantId() {
        return null;  // Default tenant
    }

    /**
     * This method gets the cache key id
     *
     * @return String
     */
    public String getCacheId() {
        return regionName + ":" + key;
    }

    /**
     * This method checks the key
     *
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomJwksCacheKey that = (CustomJwksCacheKey) o;
        return Objects.equals(regionName, that.regionName) && Objects.equals(key, that.key);
    }

    /**
     * This method creates hash of the key
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(regionName, key);
    }
}