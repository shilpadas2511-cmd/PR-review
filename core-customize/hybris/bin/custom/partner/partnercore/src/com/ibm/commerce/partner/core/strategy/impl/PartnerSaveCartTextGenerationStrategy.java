package com.ibm.commerce.partner.core.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceSaveCartTextGenerationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * This class contains the logic to update cloned cart Name using a regex pattern
 */
public class PartnerSaveCartTextGenerationStrategy extends
    DefaultCommerceSaveCartTextGenerationStrategy {

    public static final String SINGLE_WHITE_SPACE_SEPARATOR = " ";

    /**
     * Customizing the CommerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel,patternString)
     * implementation to generate text for the saved cart's name using a regex pattern.
     *
     * @param savedCartToBeCloned
     * @param numberRegex
     */
    @Override
    public String generateCloneSaveCartName(final CartModel savedCartToBeCloned, final String numberRegex){
        validateParameterNotNull(savedCartToBeCloned,"saved cart parameter cannot be null");
        validateParameterNotNull(numberRegex,"regex parameter cannot be null");
        final String baseCartName = StringUtils.trim(savedCartToBeCloned.getName());

        if(StringUtils.isNotBlank(baseCartName)) {
            final StringBuilder nameBuilder = new StringBuilder();
            final Pattern numberPrefixedPattern = Pattern.compile(numberRegex);
            final Matcher suffixWithNumber = numberPrefixedPattern.matcher(baseCartName);

            if (suffixWithNumber.find()) {
                final String matchedSuffix = suffixWithNumber.group();
                final String prefixCartName = StringUtils.removeEndIgnoreCase(baseCartName, matchedSuffix);
                final String openParenthesisFromCartNameSuffix = suffixWithNumber.group(1);         //suffixWithNumber.group(1) --> open parenthesis character '(' from cartName suffix value
                final String valueBetweenParenthesis = suffixWithNumber.group(2);             //suffixWithNumber.group(2) --> value between parenthesis from cartName suffix value
                final String closeParenthesisFromCartNameSuffix = suffixWithNumber.group(3);        //suffixWithNumber.group(3) --> close parenthesis character ')' from cartName suffix value
                int count = Integer.parseInt(valueBetweenParenthesis);
                appendSaveCartNameToStringBuilder(nameBuilder,
                    prefixCartName.trim(), SINGLE_WHITE_SPACE_SEPARATOR, openParenthesisFromCartNameSuffix, String.valueOf(++count), closeParenthesisFromCartNameSuffix );
            }
            else
            {
                final String openParenthesisFromRegexPattern = String.valueOf(numberRegex.charAt(2)); // numberRegex.charAt(2) --> open parenthesis character '(' from regex pattern String
                final String closeParenthesisFromRegexPattern = String.valueOf(numberRegex.charAt(7));// numberRegex.charAt(7) --> close parenthesis character ')' from regex pattern String
                appendSaveCartNameToStringBuilder(nameBuilder,
                    baseCartName, SINGLE_WHITE_SPACE_SEPARATOR, openParenthesisFromRegexPattern, NumberUtils.INTEGER_ONE.toString(), closeParenthesisFromRegexPattern );
            }
            return nameBuilder.toString();
        }
        return generateSaveCartName(savedCartToBeCloned);
    }
}

