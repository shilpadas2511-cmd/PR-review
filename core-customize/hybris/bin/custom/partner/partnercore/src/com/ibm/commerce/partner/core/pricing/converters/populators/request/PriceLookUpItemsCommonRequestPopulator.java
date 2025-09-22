package com.ibm.commerce.partner.core.pricing.converters.populators.request;


import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import com.ibm.commerce.partner.core.utils.PartnerUtils;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * * Populator class responsible for populating CommonPriceLookUpItemsRequestData * from an
 * AbstractOrderEntryModel.
 */
public class PriceLookUpItemsCommonRequestPopulator implements
    Populator<AbstractOrderEntryModel, CommonPriceLookUpItemsRequestData> {


    private final IbmProductService productService;
    private final PartnerPidAgreementService partnerPidAgreementService;
    private final Map<String, String> billingFrequencyMap;

    public PriceLookUpItemsCommonRequestPopulator(final IbmProductService productService,
        PartnerPidAgreementService partnerPidAgreementService,
        Map<String, String> billingFrequencyMap) {
        this.productService = productService;
        this.partnerPidAgreementService = partnerPidAgreementService;
        this.billingFrequencyMap = billingFrequencyMap;
    }

    /**
     * Populates the target CommonPriceLookUpItemsRequestData with data from the source
     * AbstractOrderEntryModel.
     *
     * @param source the source AbstractOrderEntryModel object from which data is populated
     * @param target the target CommonPriceLookUpItemsRequestData object to which data is populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(AbstractOrderEntryModel source, CommonPriceLookUpItemsRequestData target)
        throws ConversionException {
        if (source != null) {
            IbmPartProductModel product = (IbmPartProductModel) source.getProduct();

            target.setPartNumber(getProductService().getProductCode(product));
            target.setMaterialType(product.getSapMaterialCode());
            target.setRevenueStreamCode(product.getRevenueStream().getCode());

            target.setItemNumber(getItemNumber(source));
            target.setQuantity(source.getQuantity().toString());

            target.setStartDate(
                PartnerUtils.convertDateStringPattern(
                    PartnerOrderUtils.getProductInfo(source, "startDate"),
                    PartnercoreConstants.ORIGINAL_DATE_PATTERN,
                    PartnercoreConstants.DEFAULT_PRICING_DATE_PATTERN));
            target.setEndDate(
                PartnerUtils.convertDateStringPattern(
                    PartnerOrderUtils.getProductInfo(source, "endDate"),
                    PartnercoreConstants.ORIGINAL_DATE_PATTERN,
                    PartnercoreConstants.DEFAULT_PRICING_DATE_PATTERN));

            if (Objects.nonNull(source.getMasterEntry())) {
                if (Objects.nonNull(source.getMasterEntry().getProduct()) && source.getMasterEntry()
                    .getProduct() instanceof IbmVariantProductModel pidProduct) {
                    target.setProductType(
                        StringUtils.defaultIfBlank(pidProduct.getDeploymentType().getSapCode(),
                            pidProduct.getDeploymentType().getCode()));
                    target.setPid(pidProduct.getPartNumber());
                    populateAdditionalValues(pidProduct, source, target);
                }
                if (Objects.nonNull(source.getMasterEntry().getProductConfiguration())
                    && Objects.nonNull(
                    source.getMasterEntry().getProductConfiguration().getConfigurationId())) {
                    target.setConfigurationId(
                        source.getMasterEntry().getProductConfiguration().getConfigurationId());
                }
            }
        }
    }

    /**
     * Populates additional values into the {@link CommonPriceLookUpItemsRequestData} object based
     * on the deployment type of the given {@link IbmVariantProductModel}.
     * <p>
     * Behavior by deployment type:
     * <ul>
     *   <li><b>SaaS</b>: Retrieves {@link PartnerPIDAgreementModel} and sets
     *       disableBM, disablePID, and saFlag values accordingly. Then populates SaaS-specific values.</li>
     *   <li><b>Subscription</b>: Sets {@code saFlag = true} and populates SaaS-specific values.</li>
     *   <li><b>Monthly</b>: Sets {@code saFlag = false} and populates SaaS-specific values.</li>
     *   <li><b>Other</b>: No additional values are populated.</li>
     * </ul>
     *
     * @param pidProduct the IBM variant product, used to determine deployment type and fetch
     *                   agreements
     * @param source     the order entry model, providing source context for SaaS value population
     * @param target     the target object that will be enriched with additional values
     */

    protected void populateAdditionalValues(IbmVariantProductModel pidProduct,
        AbstractOrderEntryModel source, CommonPriceLookUpItemsRequestData target) {
        String pidDeploymentType = pidProduct.getDeploymentType().getSapCode();
        if (PartnercoreConstants.DEPLOYMENT_TYPE_SAAS_SAPCODE.equals(
            pidDeploymentType)) {
            PartnerPIDAgreementModel partnerPIDAgreementModel = getPartnerPidAgreementService().getPIDAgreementByPid(
                pidProduct.getPartNumber());
            if (Objects.nonNull(partnerPIDAgreementModel)) {
                target.setDisableBM(partnerPIDAgreementModel.isDisableBM());
                target.setDisablePID(partnerPIDAgreementModel.isDisablePID());
                target.setSaFlag(partnerPIDAgreementModel.isSaFlag());
            }
            populateSaasValues(source, target);
        } else if (PartnercoreConstants.DEPLOYMENT_TYPE_SUBSCRIPTION.equals(
            pidDeploymentType)) {
            target.setSaFlag(true);
            populateSaasValues(source, target);
        } else if (PartnercoreConstants.DEPLOYMENT_TYPE_MONTHLY.equals(
            pidDeploymentType)) {
            target.setSaFlag(false);
            populateSaasValues(source, target);
        }
    }

    /**
     * Populates SaaS-specific values into the {@link CommonPriceLookUpItemsRequestData} target
     * based on the given {@link IbmVariantProductModel} and {@link AbstractOrderEntryModel}.
     *
     * @param source     AbstractOrderEntryModel
     * @param target     CommonPriceLookUpItemsRequestData
     */
    protected void populateSaasValues(
        AbstractOrderEntryModel source, CommonPriceLookUpItemsRequestData target) {
        String term = PartnerOrderUtils.getProductInfo(source, PartnercoreConstants.CONTRACT_TERM);
        if (StringUtils.isNotEmpty(term) && !StringUtils.equalsIgnoreCase(term,
            PartnercoreConstants.NOT_APPLICABLE)) {
            target.setTerm(Integer.parseInt(term));
        }
        String totalTerm = PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.TOTAL_TERM);
        if (StringUtils.isNotEmpty(totalTerm) && !StringUtils.equalsIgnoreCase(totalTerm,
            PartnercoreConstants.NOT_APPLICABLE)) {
            target.setTotalTerm(totalTerm);
        }
        target.setBillingFrequency(
            getBillingFrequencyMap().get(PartnerOrderUtils.getProductInfo(source,
                PartnercoreConstants.BILLING_FREQUENCY)));
        target.setRampUpFlag((PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.RAMP_UP_FLAG)));
        target.setRenewalModel((PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.RENEWAL_TYPE)));
        target.setFuturePriceType(PartnercoreConstants.FUTURE_PRICE_TYPE);

        target.setReferenceItem(getReferenceItem(source));

    }

    /**
     * Retrieves the item number of the referenced order entry for a given source entry.
     *
     * <p>This method extracts a reference identifier from the provided {@code source} order entry.
     * It then searches through the other entries in the same order to find one whose unique
     * identifier matches the reference identifier (ignoring case). If such a match is found, the
     * item number of the matching entry is returned as a string.</p>
     *
     * @param source the {@link AbstractOrderEntryModel} from which to extract the reference
     *               identifier
     * @return the item number (as a string) of the referenced entry if found; otherwise, an empty
     * string
     */
    protected String getReferenceItem(AbstractOrderEntryModel source) {
        String referenceIdentifier = PartnerOrderUtils.getProductInfo(source,
            PartnercoreConstants.REFERENCE_IDENTIFIER);

        if (StringUtils.isEmpty(referenceIdentifier)) {
            return StringUtils.EMPTY;
        }

        AbstractOrderEntryModel referenceEntry = source.getOrder().getEntries().stream()
            .filter(entry -> source != entry && referenceIdentifier.equalsIgnoreCase(
                PartnerOrderUtils.getProductInfo(entry, PartnercoreConstants.UNIQUE_IDENTIFIER)))
            .findAny()
            .orElse(null);

        if (ObjectUtils.isNotEmpty(referenceEntry)) {
            return String.valueOf(getItemNumber(referenceEntry));
        }
        return StringUtils.EMPTY;
    }


    protected int getItemNumber(AbstractOrderEntryModel entryModel) {

        if (entryModel.getMasterEntry() != null) {
            return ((entryModel.getMasterEntry().getEntryNumber() + NumberUtils.INTEGER_ONE) * 100)
                + entryModel.getEntryNumber();
        }
        return entryModel.getEntryNumber() + NumberUtils.INTEGER_ONE;
    }


    public IbmProductService getProductService() {
        return productService;
    }

    public PartnerPidAgreementService getPartnerPidAgreementService() {
        return partnerPidAgreementService;
    }

    public Map<String, String> getBillingFrequencyMap() {
        return billingFrequencyMap;
    }


}
