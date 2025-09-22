package com.ibm.commerce.partner.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Month;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

public class PartnerUtils {

    private static final Logger log = Logger.getLogger(PartnerUtils.class);


    /**
     * @param anniversaryMonth String to get the anniversary Month.
     * @return month
     */
    public static String getAnniversaryMonth(String anniversaryMonth) {
        try {
            int dateMonth = Integer.parseInt(anniversaryMonth);
            return Month.of(dateMonth).name();
        } catch (NumberFormatException | DateTimeException e) {
            log.debug(String.format(" Agreement Month exception for : [%s]", anniversaryMonth));
            return StringUtils.EMPTY;
        }
    }

    /**
     * String to Date
     *
     * @return month
     */
    public static Date convertStringToDate(String dateInString, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
            return formatter.parse(dateInString);
        } catch (NumberFormatException | DateTimeException | ParseException e) {
            log.debug(
                String.format(" exception for the value of dateInString : [%s]", dateInString));
            return null;
        }
    }

    /**
     * old String date pattern to new String date pattern
     *
     * @return String
     */
    public static String convertDateStringPattern(String dateInString, String originalPattern,
        String convertToPattern) {
        if(StringUtils.isEmpty((dateInString))) {
            return null;
        }
            Date date = convertStringToDate(dateInString, originalPattern);
            return date != null ? DateFormatUtils.format(date, convertToPattern) : null;
    }

    public static String getValue(Double value) {
        return value != null ? String.valueOf(value) : null;
    }

}