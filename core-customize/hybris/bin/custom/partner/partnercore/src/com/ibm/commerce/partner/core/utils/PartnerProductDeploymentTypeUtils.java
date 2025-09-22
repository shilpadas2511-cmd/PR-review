package com.ibm.commerce.partner.core.utils;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;


public class PartnerProductDeploymentTypeUtils {

    /**
     * this metod check the product deployment type as SaaS
     * @param product
     * @return true or false
     */
    public static boolean isSaasProduct(IbmVariantProductModel product) {
        return PartnercoreConstants.DEPLOYMENT_TYPE_SAAS.equalsIgnoreCase(product.getDeploymentType().getCode());
    }

    /**
     * this metod check the product deployment type as bespoke
     * @param product
     * @return true or false
     */
    public static boolean isBespokeProduct(IbmVariantProductModel product) {
        return PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE.equalsIgnoreCase(product.getDeploymentType().getCode());
    }

    /**
     * this metod check the product deployment type as bespoke-multipid
     * @param product
     * @return true or false
     */
    public static boolean isBeespokeMultiPid(IbmVariantProductModel product) {
        return PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID.equalsIgnoreCase(product.getDeploymentType().getCode());
    }

}
