package com.ibm.commerce.partner.core.utils;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * Utility Class for the order
 */
public final class PartnerOrderUtils {

    public static final String SAAS_DEPLOYMENT = "Saas";

    /**
     * Retrieves CPQ product information from the source AbstractOrderEntryModel based on the given
     * type.
     *
     * @param source the AbstractOrderEntryModel containing the product information
     * @param type   the type of information to retrieve (e.g., "startDate", "endDate")
     * @return the requested product information or an empty string if not found
     */
    public static String getProductInfo(AbstractOrderEntryModel source, String type) {
        Optional<CPQOrderEntryProductInfoModel> typeInfo = Optional.empty();
        if (source.getProductInfos() != null) {
            typeInfo = source.getProductInfos().stream()
                .filter(CPQOrderEntryProductInfoModel.class::isInstance)
                .map(CPQOrderEntryProductInfoModel.class::cast)
                .filter(info -> info.getCpqCharacteristicName().equalsIgnoreCase(type)).findFirst();
        }
        if (typeInfo.isPresent()) {
            CPQOrderEntryProductInfoModel infoModel = typeInfo.get();
            return infoModel.getCpqCharacteristicAssignedValues();
        }

        return StringUtils.EMPTY;
    }


    public static Map<String, AbstractOrderEntryModel> getSubIdEntryNumber(
        AbstractOrderEntryModel source, Set<String> licenceTypeCodes) {
        return getSubIdEntryNumber(source.getChildEntries(), licenceTypeCodes);
    }

    public static Map<String, AbstractOrderEntryModel> getSubIdEntryNumber(
        Collection<AbstractOrderEntryModel> entries, Set<String> licenceTypeCodes) {
        if (CollectionUtils.isEmpty(entries)) {
            return Collections.emptyMap();
        }

        Map<String, AbstractOrderEntryModel> subIdToItemNumberMap = new HashMap<>();
        entries.forEach(childEntry -> {
            String subId = getProductInfo(childEntry, PartnercoreConstants.ORDER_ENTRY_SUB_ID);
            String licenceTypeCode = getProductInfo(childEntry,
                PartnercoreConstants.ORDER_ENTRY_LICENCE_TYPE_CODE);
            if (StringUtils.isNotBlank(subId) && !subIdToItemNumberMap.containsKey(subId) && (
                CollectionUtils.isEmpty(licenceTypeCodes) || licenceTypeCodes.contains(
                    licenceTypeCode))) {
                subIdToItemNumberMap.put(subId, childEntry);
            }
        });
        return subIdToItemNumberMap;
    }

    public static int getItemNumber(AbstractOrderEntryModel entryModel) {

        if (entryModel.getMasterEntry() != null) {
            return ((entryModel.getMasterEntry().getEntryNumber() + NumberUtils.INTEGER_ONE) * 100)
                + entryModel.getEntryNumber();
        }
        return entryModel.getEntryNumber() + NumberUtils.INTEGER_ONE;
    }

    public static boolean validateYTYOverridden(
        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel) {
        return MapUtils.isNotEmpty(partnerCpqHeaderPricingDetailModel.getYtyYears())
            && partnerCpqHeaderPricingDetailModel.getYtyYears().entrySet().stream().anyMatch(
            entry -> entry.getValue() == null || entry.getValue() > NumberUtils.DOUBLE_ZERO);
    }
    /*
    * check the Cart have SaaS product or not
    * @param cartModel
    * @return boolean
    * */
    public static boolean checkSaasProduct(final CartModel cartModel) {
        return cartModel.getEntries().stream()
            .flatMap(entry -> entry.getChildEntries().stream())
            .anyMatch(childEntry -> childEntry.getProduct() instanceof IbmPartProductModel
                && ((IbmPartProductModel) childEntry.getProduct()).getDeploymentType().getSapCode().equalsIgnoreCase(
                SAAS_DEPLOYMENT));
    }

}
