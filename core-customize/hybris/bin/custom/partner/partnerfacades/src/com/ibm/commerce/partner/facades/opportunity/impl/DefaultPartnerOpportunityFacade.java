package com.ibm.commerce.partner.facades.opportunity.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.opportunity.data.request.OpportunityDetailsResponseData;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.facades.opportunity.PartnerOpportunityFacade;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityCustomerNumberSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityOwnerMailSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchbyNumberRequestData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation for {@link PartnerOpportunityFacade}
 */
public class DefaultPartnerOpportunityFacade implements PartnerOpportunityFacade {

    private final PartnerOpportunityService opportunityService;

    private final Converter<IbmPartnerOpportunityData, IbmPartnerOpportunityModel> opportunityReverseConverter;

    private final Converter<OpportunityDetailsResponseData, OpportunityDetailsData> opportunityResponseConverter;

    private final Converter<OpportunityDetailsSearchResponseData, OpportunitySearchDetailsData> opportunitySearchResponseConverter;

    private final ModelService modelService;


    public DefaultPartnerOpportunityFacade(final PartnerOpportunityService opportunityService,
        final Converter<IbmPartnerOpportunityData, IbmPartnerOpportunityModel> opportunityReverseConverter,
        final ModelService modelService,
        final Converter<OpportunityDetailsResponseData, OpportunityDetailsData> opportunityResponseConverter,
        Converter<OpportunityDetailsSearchResponseData, OpportunitySearchDetailsData> opportunitySearchResponseConverter) {
        this.opportunityService = opportunityService;
        this.opportunityReverseConverter = opportunityReverseConverter;
        this.modelService = modelService;
        this.opportunityResponseConverter = opportunityResponseConverter;
        this.opportunitySearchResponseConverter = opportunitySearchResponseConverter;
    }

    @Override
    public IbmPartnerOpportunityModel getOrCreate(final IbmPartnerOpportunityData opportunityData) {

        if (opportunityData == null || StringUtils.isBlank(opportunityData.getCode())) {
            return null;
        }
        IbmPartnerOpportunityModel opportunityModel = getOpportunityService().get(
            opportunityData.getCode());
        if (opportunityModel != null) {
            return opportunityModel;
        }
        IbmPartnerOpportunityModel newOpportunityModel = getOpportunityReverseConverter().convert(
            opportunityData);
        getModelService().save(newOpportunityModel);
        return newOpportunityModel;
    }

    /**
     * @param opportunitySearchRequestData Calling opportunityService.getOpportunityDetails to
     *                                     retrieve the details of the valid opportunity list
     */
    @Override
    public List<OpportunitySearchDetailsData> fetchOpportunityDetails(
        final OpportunitySearchRequestData opportunitySearchRequestData) {

        List<OpportunityDetailsSearchResponseData> responseData = opportunityService.getOpportunities(
            opportunitySearchRequestData.getResellerID(),
            opportunitySearchRequestData.getDistributorID(),
            opportunitySearchRequestData.getCustomerICN());
        return  getOpportunitySearchResponseConverter().convertAll(responseData);
    }

    /**
     * @param opportunitySearchbyNumberRequestData Calling opportunityService.fetchOpportunitiesByNumber
     *                                       to retrieve the details of the valid opportunity list
     */
    @Override
    public List<OpportunitySearchDetailsData> fetchOpportunitiesByNumber(
        final OpportunitySearchbyNumberRequestData opportunitySearchbyNumberRequestData) {
        return getOpportunitySearchResponseConverter().convertAll(opportunityService.fetchOpportunitiesByNumber(
            opportunitySearchbyNumberRequestData.getOpportunityNumber()));
    }

    /**
     * @param opportunityOwnerMailSearchRequestData Calling
     *                                                opportunityService.fetchOpportunitiesByOwnerMail
     *                                                to retrieve the details of the valid
     *                                                opportunity list
     */
    @Override
    public List<OpportunitySearchDetailsData> fetchOpportunitiesByOwnerEmail(
        final OpportunityOwnerMailSearchRequestData opportunityOwnerMailSearchRequestData) {
        return getOpportunitySearchResponseConverter().convertAll(opportunityService.fetchOpportunitiesByOwnerMail(
            opportunityOwnerMailSearchRequestData.getOwnerMail()));
    }

    /**
     * @param opportunityCustomerNumberSearchRequestData Calling
     *                                                     opportunityService.fetchOpportunitiesByCustomerNumber
     *                                                     to retrieve the details of the valid
     *                                                     opportunity list
     */
    @Override
    public List<OpportunitySearchDetailsData> fetchOpportunitiesByCustomerNumber(
        final OpportunityCustomerNumberSearchRequestData opportunityCustomerNumberSearchRequestData) {
        return getOpportunitySearchResponseConverter().convertAll(opportunityService.fetchOpportunitiesByCustomerNumber(
            opportunityCustomerNumberSearchRequestData.getCustomerNumber(),
            opportunityCustomerNumberSearchRequestData.getCountryCode()));
    }

    public PartnerOpportunityService getOpportunityService() {
        return opportunityService;
    }

    public Converter<IbmPartnerOpportunityData, IbmPartnerOpportunityModel> getOpportunityReverseConverter() {
        return opportunityReverseConverter;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public Converter<OpportunityDetailsResponseData, OpportunityDetailsData> getOpportunityResponseConverter() {
        return opportunityResponseConverter;
    }

    public Converter<OpportunityDetailsSearchResponseData, OpportunitySearchDetailsData> getOpportunitySearchResponseConverter() {
        return opportunitySearchResponseConverter;
    }

}
