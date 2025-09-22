package com.ibm.commerce.appconfiguration.core.strategies;

import org.json.JSONObject;

public interface IbmCommonEntityAttributesGenerationStrategy {

    /**
     * Generates JSONObject.
     *<p>
     * This map of key-value pairs serves as input for feature evaluation.
     * <p>Thekey represents the attribute name of a rule defined for a segment in the App Config, while
     * the value denotes the corresponding data utilized by the rule during evaluation.
     * <p>
     * For e.g.
     * <p>JSONObject entityAttributes = new JSONObject();
     * <p>entityAttributes.put("role","developer");
     *
     * @return entityAttributes object of JSONObject
     */
    JSONObject generate();
}
