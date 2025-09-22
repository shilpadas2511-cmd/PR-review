/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.order.data.PartnerOrderData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * This class populates the page specific QuoteData from the SearchResultValueData from solr
 * results
 */
public class PartnerQuoteSearchResultPopulator
    implements Populator<SearchResultValueData, QuoteData> {

    @Resource
    private CommonI18NService commonI18NService;
    private final Converter<CurrencyModel, CurrencyData> currencyConverter;
    private final String dateFormat;
    public static final String STATUSNAME = "statusName";
    public static final String QUOTE_CODE = "code";
    public static final String QUOTE_NAME = "name";
    public static final String QUOTE_CREATION_NAME = "creationtime";
    public static final String QUOTE_ECCQUOTE_NUMBER = "eccQuoteNumber";
    public static final String QUOTE_SUBMITTED_DATE = "submittedDate";
    public static final String QUOTE_STATE = "state";
    public static final String QUOTE_EXPIRATION_DATE = "quoteExpirationDate";
    public static final String QUOTE_PROGRAM_TYPE = "programType";
    public static final String QUOTE_PRICE = "totalBidExtendedPrice";
    public static final String QUOTE_CREATOR = "quoteCreator";
    public static final String STORE = "store";
    public static final String QUOTE_VERSION = "version";
    public static final String QUOTE_CARTID = "cartId";
    private static final String QUOTE_SITEID = "quoteAccessToSites";
    private static final String UNIT_ID = "unitId";
    private static final String UNIT_NAME = "unitName";
    private static final String BILL_TO_UNITID = "billToUnitId";
    private static final String BILL_TO_UNITNAME = "billToUnitName";
    public static final String SOLD_THROUGH_UNIT_ID = "soldThroughUnitId";
    public static final String SOLD_THROUGH_UNIT_NAME = "soldThroughUnitName";
    public static final String FULL_PRICE_RECEIVED = "fullPriceReceived";
    public static final String SALES_APPLICATION = "salesApplication";
    public static final String CPQ_QUOTE_NUMBER = "cpqQuoteNumber";

    public static final String ORDER_ID = "orderId";
    private EnumerationService enumerationService;
    public static final String CURRENCY_CODE = "currencyCode";


    public PartnerQuoteSearchResultPopulator(
        Converter<CurrencyModel, CurrencyData> currencyConverter, String dateFormat) {
        this.currencyConverter = currencyConverter;
        this.dateFormat = dateFormat;
    }

    /**
     * This method maps the quote data from solr search results
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final SearchResultValueData source, final QuoteData target)
        throws ConversionException {

        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        final String state = this.<String>getValue(source, QUOTE_STATE);

        target.setCode(this.<String>getValue(source, QUOTE_CODE));
        target.setName(this.<String>getValue(source, QUOTE_NAME));
        target.setCreationTime(this.<Date>getValue(source, QUOTE_CREATION_NAME));
        target.setEccQuoteNumber(this.<String>getValue(source, QUOTE_ECCQUOTE_NUMBER));
        final Date submittedDate = this.<Date>getValue(source, QUOTE_SUBMITTED_DATE);
        if (submittedDate != null) {
            final DateFormat dateFormat = new SimpleDateFormat(getDateFormat());
            final String submitDate = dateFormat.format(submittedDate);
            target.setSubmittedDate(submitDate);
        }
        target.setSoldThroughUnit(setSoldThroughUnit(source));
        target.setBillToUnit(setBillToUnit(source));
        target.setUnit(setUnit(source));
        if (state != null) {
            target.setState(QuoteState.valueOf(state));
        }
        final Date quoteExpirationDate = this.<Date>getValue(source, QUOTE_EXPIRATION_DATE);
        if (quoteExpirationDate != null) {
            target.setQuoteExpirationDate(quoteExpirationDate);
        }
        final String programType = this.<String>getValue(source, QUOTE_PROGRAM_TYPE);
        if (programType != null) {
            target.setAgreementDetail(setAgreementDetails(source));
        }
        target.setTotalBidExtendedPrice(this.getValue(source, QUOTE_PRICE));

        final String quoteCreator = this.<String>getValue(source, QUOTE_CREATOR);
        if (quoteCreator != null) {
            target.setQuoteCreater(setQuoteCreator(source));
        }
        target.setStore(this.<String>getValue(source, STORE));
        target.setVersion(this.<Integer>getValue(source, QUOTE_VERSION));
        target.setCartId(this.<String>getValue(source, QUOTE_CARTID));
        target.setQuoteAccessToSites(setPartnerSoldThroughSiteId(source));
        target.setQuoteCreater(setQuoteCreator(source));
        target.setStatusName(this.<String>getValue(source, STATUSNAME));
        target.setFullPriceReceived(this.<Boolean>getValue(source, FULL_PRICE_RECEIVED));
        target.setCpqQuoteNumber(this.<String>getValue(source, CPQ_QUOTE_NUMBER));
        if (this.getValue(source, ORDER_ID) != null) {
            target.setOrders(setOrderId(source));
        }
        String salesApplication = this.<String>getValue(source, SALES_APPLICATION);
        if (salesApplication != null) {
            target.setSalesApplication(setSalesApplication(source));
        }
        final String currencyIsoCode = this.<String>getValue(source, CURRENCY_CODE);
        if (StringUtils.isNotEmpty(currencyIsoCode)) {
            CurrencyModel currencyModel = commonI18NService.getCurrency(currencyIsoCode);
            if (ObjectUtils.isNotEmpty(currencyModel)) {
                CurrencyData currencyData = new CurrencyData();
                currencyConverter.convert(currencyModel, currencyData);
                target.setCurrency(currencyData);
            }
        }
    }

    private List<String> setPartnerSoldThroughSiteId(SearchResultValueData source) {
        return this.<ArrayList>getValue(source, QUOTE_SITEID);
    }

    private List<PartnerOrderData> setOrderId(SearchResultValueData source) {
        List<String> orderList = this.getValue(source, ORDER_ID);
        return orderList.stream()
            .map(orderId -> {
                PartnerOrderData orderData = new PartnerOrderData();
                orderData.setOrderId(orderId);
                return orderData;
            })
            .collect(Collectors.toList());
    }

    /**
     * @param source
     * @return DisplayTypeData
     */
    private DisplayTypeData setSalesApplication(SearchResultValueData source) {
        DisplayTypeData displayTypeData = new DisplayTypeData();
        displayTypeData.setCode(this.<String>getValue(source, SALES_APPLICATION));
        displayTypeData.setName(this.<String>getValue(source, SALES_APPLICATION));
        return displayTypeData;
    }

    private IbmPartnerAgreementDetailData setAgreementDetails(SearchResultValueData source) {
        IbmPartnerAgreementDetailData agreementDetailData = new IbmPartnerAgreementDetailData();
        agreementDetailData.setProgramType(this.<String>getValue(source, QUOTE_PROGRAM_TYPE));
        return agreementDetailData;
    }


    /**
     * This method sets customer
     */
    private IbmPartnerB2BUnitData setUnit(final SearchResultValueData source) {
        final IbmPartnerB2BUnitData unit = new IbmPartnerB2BUnitData();
        unit.setUid(this.<String>getValue(source, UNIT_ID));
        unit.setName(this.<String>getValue(source, UNIT_NAME));
        return unit;
    }

    /**
     * This method sets quoteCreator
     */
    private CustomerData setQuoteCreator(final SearchResultValueData source) {
        final CustomerData customer = new CustomerData();
        customer.setName(this.<String>getValue(source, QUOTE_CREATOR));
        return customer;
    }

    /**
     * This method sets billToUnit
     */
    private IbmPartnerB2BUnitData setBillToUnit(final SearchResultValueData source) {
        final IbmPartnerB2BUnitData billToUnit = new IbmPartnerB2BUnitData();
        billToUnit.setUid(this.<String>getValue(source, BILL_TO_UNITID));
        billToUnit.setName(this.<String>getValue(source, BILL_TO_UNITNAME));
        billToUnit.setActive(true);
        return billToUnit;
    }

    /**
     * This method sets soldThroughUnit
     */
    private IbmPartnerB2BUnitData setSoldThroughUnit(final SearchResultValueData source) {
        final IbmPartnerB2BUnitData soldThroughUnit = new IbmPartnerB2BUnitData();
        soldThroughUnit.setUid(this.<String>getValue(source, SOLD_THROUGH_UNIT_ID));
        soldThroughUnit.setName(this.<String>getValue(source, SOLD_THROUGH_UNIT_NAME));
        soldThroughUnit.setActive(true);
        return soldThroughUnit;
    }

    /**
     * This method null checks and returns the value from SearchResultValueData using the
     * propertyName
     *
     * @param source
     * @param propertyName
     * @param <T>
     * @return
     */
    protected <T> T getValue(final SearchResultValueData source, final String propertyName) {
        if (source.getValues() == null) {
            return null;
        }

        // DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
        return (T) source.getValues().get(propertyName);
    }


    public String getDateFormat() {
        return dateFormat;
    }

    protected EnumerationService getEnumerationService() {
        return enumerationService;
    }
}