package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.utils.PartnerCountryUtils;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Populator class responsible for populating PriceLookUpHeaderRequestData from an
 * AbstractOrderModel. This class extracts common header information from the order and populates
 * the target data object for price lookup requests.
 */
public class PriceLookUpHeaderCommonRequestPopulator implements
    Populator<AbstractOrderModel, PriceLookUpHeaderRequestData> {

    private static final String PROGRAM_TYPE_PA = "PA";


    /**
     * Populates the target PriceLookUpHeaderRequestData object with common header information
     * extracted from the source AbstractOrderModel.
     *
     * @param source the AbstractOrderModel containing the source data
     * @param target the PriceLookUpHeaderRequestData object to be populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(final AbstractOrderModel source, final PriceLookUpHeaderRequestData target)
        throws ConversionException {
        if (source instanceof final IbmPartnerCartModel cart) {
            final B2BUnitModel distributor = cart.getBillToUnit();
            final B2BUnitModel reseller = cart.getSoldThroughUnit();
            final IbmPartnerEndCustomerB2BUnitModel endCustomer = (IbmPartnerEndCustomerB2BUnitModel) cart.getUnit();

            setAgreementDetails(target, cart);

            if (endCustomer != null) {
                if (endCustomer.getCountry() != null) {
                    target.setCountry(
                        PartnerCountryUtils.getCountryCode(endCustomer.getCountry()));
                }
                target.setSoldToSite(endCustomer.getUid());
                target.setIbmCustomerNumber(endCustomer.getId());
            }
            if (source.getCurrency() != null) {
                target.setCurrency(source.getCurrency().getIsocode());
            }

            Set<PartnerSalesOrganisationModel> salesOrgs = ((IbmB2BUnitModel) distributor).getPartnerSalesOrganisations();
            if (CollectionUtils.isNotEmpty(salesOrgs)) {
                target.setSalesOrg(salesOrgs.iterator().next().getCode());
            }

            if (distributor != null) {
                target.setPayerSite(distributor.getUid());
            }
            if (reseller != null) {
                target.setResellerSite(reseller.getUid());
            }
            target.setDistChannel(
                StringUtils.defaultIfBlank(cart.getCpqDistributionChannel(),
                    PartnerQuoteChannelEnum.J.getCode()));
            target.setDocCategory("Q");
            target.setQuoteId(StringUtils.defaultIfBlank(cart.getPriceUid(), cart.getCode()));
        }
    }

    private void setAgreementDetails(final PriceLookUpHeaderRequestData target,
        final IbmPartnerCartModel cart) {
        final IbmPartnerAgreementDetailModel agreementDetail = cart.getAgreementDetail();
        if (agreementDetail != null) {
            final String programType = agreementDetail.getProgramType();
            target.setLob(programType);
            final String agreementNumber =
                PROGRAM_TYPE_PA.equalsIgnoreCase(programType)
                    ? agreementDetail.getAgreementNumber()
                    : null;
            target.setAgreementNumber(agreementNumber);
        }
    }

}
