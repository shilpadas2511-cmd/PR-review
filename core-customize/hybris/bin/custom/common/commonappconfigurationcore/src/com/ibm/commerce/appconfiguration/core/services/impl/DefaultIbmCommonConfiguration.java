package com.ibm.commerce.appconfiguration.core.services.impl;

import com.ibm.cloud.appconfiguration.sdk.AppConfiguration;
import com.ibm.cloud.appconfiguration.sdk.configurations.models.Feature;
import com.ibm.commerce.appconfiguration.core.services.IbmCommonConfiguration;
import com.ibm.commerce.appconfiguration.core.strategies.IbmCommonEntityAttributesGenerationStrategy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * Default Implementation for {@link IbmCommonConfiguration}
 * <p>Fetches FeatureFlags from appConfig.
 */
public class DefaultIbmCommonConfiguration implements IbmCommonConfiguration {

    protected static final Logger LOG = Logger.getLogger(DefaultIbmCommonConfiguration.class);
    private static final String UNSUPPORTED_OPERATION = "Unsupported operation";
    private static final String EXCEPTION_MESSAGE = "Exception occurred to fetch featureFlag with id: %s ";
    private static final String NO_FEATURE_DEFINED_MESSAGE = "No Feature Flag defined with id: %s ";
    private static final String APP_CONFIGURATION_ERROR_MESSAGE = "CHECK APP CONFIG CONFIGURATION. NO FEATURES ARE FOUND ";
    private final String appConfigRegion;
    private final String guid;
    private final String apiKey;
    private final String collectionId;
    private final String environmentId;
    private final String entityId;
    private final IbmCommonEntityAttributesGenerationStrategy entityAttributesGenerationStrategy;

    DefaultIbmCommonConfiguration(final String appConfigRegion, final String guid,
        final String apiKey, final String collectionId, final String environmentId,
        final String entityId,
        final IbmCommonEntityAttributesGenerationStrategy entityAttributesGenerationStrategy) {
        this.appConfigRegion = appConfigRegion;
        this.guid = guid;
        this.apiKey = apiKey;
        this.collectionId = collectionId;
        this.environmentId = environmentId;
        this.entityId = entityId;
        this.entityAttributesGenerationStrategy = entityAttributesGenerationStrategy;
        initializeAppConfig();
    }


    @Override
    public Configuration subset(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean containsKey(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public void addProperty(final String s, final Object o) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public void setProperty(final String s, final Object o) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public void clearProperty(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public Object getProperty(final String s) {
        return getPropertyValue(s);
    }

    @Override
    public Iterator<String> getKeys(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public Iterator<String> getKeys() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public Properties getProperties(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean getBoolean(final String s) {
        return getPropertyValue(s);
    }

    @Override
    public boolean getBoolean(final String s, final boolean b) {
        return BooleanUtils.toBooleanDefaultIfNull(getPropertyValue(s), b);
    }

    @Override
    public Boolean getBoolean(final String s, final Boolean aBoolean) {
        return BooleanUtils.toBooleanDefaultIfNull(getPropertyValue(s), aBoolean);
    }

    @Override
    public byte getByte(final String s) {
        return getByte(s, NumberUtils.BYTE_ZERO);
    }

    @Override
    public byte getByte(final String s, final byte b) {
        return getByte(s, Byte.valueOf(b));
    }

    @Override
    public Byte getByte(final String s, final Byte defaultValue) {
        Byte byteValue = getPropertyValue(s);
        if (byteValue == null) {
            return defaultValue;
        }
        return byteValue;
    }

    @Override
    public double getDouble(final String s) {
        return getDouble(s, NumberUtils.DOUBLE_ZERO);
    }

    @Override
    public double getDouble(final String s, final double v) {
        return getDouble(s, Double.valueOf(v));
    }

    @Override
    public Double getDouble(final String s, final Double defaultValue) {
        Double value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public float getFloat(final String s) {
        return getFloat(s, NumberUtils.FLOAT_ZERO);
    }

    @Override
    public float getFloat(final String s, final float v) {
        return getFloat(s, Float.valueOf(v));
    }

    @Override
    public Float getFloat(final String s, final Float defaultValue) {
        Float value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public int getInt(final String s) {
        return getInt(s, NumberUtils.INTEGER_ZERO);
    }

    @Override
    public int getInt(final String s, final int i) {
        return getInteger(s, i);
    }

    @Override
    public Integer getInteger(final String s, final Integer defaultValue) {
        Integer value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public long getLong(final String s) {
        return getLong(s, NumberUtils.LONG_ZERO);
    }

    @Override
    public long getLong(final String s, final long l) {
        return getLong(s, Long.valueOf(l));
    }

    @Override
    public Long getLong(final String s, final Long defaultValue) {
        Long value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public short getShort(final String s) {
        return getShort(s, NumberUtils.SHORT_ZERO);
    }

    @Override
    public short getShort(final String s, final short i) {
        return getShort(s, Short.valueOf(i));
    }

    @Override
    public Short getShort(final String s, final Short defaultValue) {
        Short value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(final String s) {
        return getBigDecimal(s, null);
    }

    @Override
    public BigDecimal getBigDecimal(final String s, final BigDecimal defaultValue) {
        BigDecimal value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public BigInteger getBigInteger(final String s) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(final String s, final BigInteger defaultValue) {
        BigInteger value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public String getString(final String s) {
        return getString(s, null);
    }

    @Override
    public String getString(final String s, final String defaultValue) {
        String value = getPropertyValue(s);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public String[] getStringArray(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public List<Object> getList(final String s) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public List<Object> getList(final String s, final List<?> list) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public String getAppConfigRegion() {
        return appConfigRegion;
    }

    public String getGuid() {
        return guid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getPropertyValue(String propertyId) {
        final AppConfiguration appConfiguration = AppConfiguration.getInstance();
        if (MapUtils.isEmpty(appConfiguration.getFeatures())) {
            initializeAppConfig();
            if (MapUtils.isEmpty(appConfiguration.getFeatures())) {
                LOG.error(APP_CONFIGURATION_ERROR_MESSAGE);
                return null;
            }
        }

        Feature feature = appConfiguration.getFeature(propertyId);
        if (feature == null) {
            LOG.info(String.format(NO_FEATURE_DEFINED_MESSAGE, propertyId));
            return null;
        }
        try {
            return (T) feature.getCurrentValue(getEntityId(),
                getEntityAttributesGenerationStrategy().generate());
        } catch (Exception e) {
            LOG.error(String.format(EXCEPTION_MESSAGE, propertyId), e);
            return null;
        }
    }

    protected void initializeAppConfig() {
        AppConfiguration newAppConfigClient = AppConfiguration.getInstance();
        newAppConfigClient.init(getAppConfigRegion(), getGuid(), getApiKey());
        newAppConfigClient.setContext(getCollectionId(), getEnvironmentId());
    }

    public IbmCommonEntityAttributesGenerationStrategy getEntityAttributesGenerationStrategy() {
        return entityAttributesGenerationStrategy;
    }

    public String getEntityId() {
        return entityId;
    }
}
