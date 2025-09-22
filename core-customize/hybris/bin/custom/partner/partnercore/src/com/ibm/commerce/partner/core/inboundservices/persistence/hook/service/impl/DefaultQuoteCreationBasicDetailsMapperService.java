package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultPartnerQuoteUpdateStateStrategy;

/**
 * Default Partner DefaultQuoteCreationBasicDetailsMapperService MapperService class is used to
 * populate or map the basic quote details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationBasicDetailsMapperService implements
    PartnerQuoteCreationMapperService {

    private static final String PARTNER = "partner";
    private CommonI18NService commonI18NService;
    private BaseStoreService baseStoreService;
    private PartnerUserService partnerUserService;
    private DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy;

    public DefaultQuoteCreationBasicDetailsMapperService(PartnerUserService partnerUserService,
        CommonI18NService commonI18NService,
        BaseStoreService baseStoreService,
        DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy) {
        this.partnerUserService = partnerUserService;
        this.commonI18NService = commonI18NService;
        this.baseStoreService = baseStoreService;
        this.quoteUpdateStateStrategy = quoteUpdateStateStrategy;
    }

    /**
     * Maps data from a {@link CpqIbmPartnerQuoteModel} object to an {@link IbmPartnerQuoteModel} object.
     *
     * @param cpqIbmPartnerQuote The source {@link CpqIbmPartnerQuoteModel} object, must not be null.
     * @param ibmPartnerQuoteModel The target {@link IbmPartnerQuoteModel} object, must not be null.
     *
     * @throws IllegalArgumentException if {@code cpqIbmPartnerQuote} or {@code ibmPartnerQuoteModel} is null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuote, IbmPartnerQuoteModel ibmPartnerQuoteModel) {
        setQuoteDetails(cpqIbmPartnerQuote,ibmPartnerQuoteModel);
        Optional.ofNullable(cpqIbmPartnerQuote.getQuoteCreator())
            .map(creator -> creator.getEmail())
            .flatMap(
                email -> Optional.ofNullable(getPartnerUserService().getCustomerByEmail(email)))
            .ifPresent(user -> ibmPartnerQuoteModel.setUser(user));
        ibmPartnerQuoteModel.setStore(getBaseStoreService().getBaseStoreForUid(PARTNER));
    }

    protected void setQuoteDetails(CpqIbmPartnerQuoteModel cpqIbmPartnerQuote, IbmPartnerQuoteModel ibmPartnerQuoteModel){
        if (StringUtils.isNotEmpty(cpqIbmPartnerQuote.getCode())) {
            ibmPartnerQuoteModel.setCode(cpqIbmPartnerQuote.getCode());
        }
        if (StringUtils.isNotEmpty(cpqIbmPartnerQuote.getName())) {
            ibmPartnerQuoteModel.setName(cpqIbmPartnerQuote.getName());
        }
        if (cpqIbmPartnerQuote.getQuoteCreator() != null
            && StringUtils.isNotBlank(cpqIbmPartnerQuote.getQuoteCreator().getEmail())) {
            var creatorUser = getPartnerUserService().getCustomerByEmail(
                cpqIbmPartnerQuote.getQuoteCreator().getEmail());
            if (creatorUser != null) {
                ibmPartnerQuoteModel.setCreator(creatorUser);
            }
        }
        if (StringUtils.isNotEmpty(cpqIbmPartnerQuote.getCpqQuoteStatus())
            && StringUtils.isNotEmpty(cpqIbmPartnerQuote.getEccQuoteStatus())) {
            getQuoteUpdateStateStrategy().updatePartnerQuoteState(ibmPartnerQuoteModel,
                cpqIbmPartnerQuote.getCpqQuoteStatus(),
                cpqIbmPartnerQuote.getEccQuoteStatus());
        }
        setPartnerquoteDetails(cpqIbmPartnerQuote,ibmPartnerQuoteModel);
    }
    protected void setPartnerquoteDetails(CpqIbmPartnerQuoteModel cpqIbmPartnerQuote, IbmPartnerQuoteModel ibmPartnerQuoteModel){
        if (cpqIbmPartnerQuote.getQuoteSubmitter() != null
            && StringUtils.isNotBlank(cpqIbmPartnerQuote.getQuoteSubmitter().getEmail())) {
            var submitterUser = getPartnerUserService().getCustomerByEmail(
                cpqIbmPartnerQuote.getQuoteSubmitter().getEmail());
            if (submitterUser != null) {
                ibmPartnerQuoteModel.setSubmitter(submitterUser);
            }
        }
        if (StringUtils.isNotEmpty(cpqIbmPartnerQuote.getCurrency())) {
            ibmPartnerQuoteModel.setCurrency(getCommonI18NService().getCurrency(cpqIbmPartnerQuote.getCurrency()));
        }
        if (cpqIbmPartnerQuote.getDate() != null) {
            ibmPartnerQuoteModel.setDate(cpqIbmPartnerQuote.getDate());
        }
        if (cpqIbmPartnerQuote.getSubmittedDate() != null) {
            ibmPartnerQuoteModel.setSubmittedDate(cpqIbmPartnerQuote.getSubmittedDate());
        }
        if (cpqIbmPartnerQuote.getQuoteExpirationDate() != null) {
            ibmPartnerQuoteModel.setQuoteExpirationDate(cpqIbmPartnerQuote.getQuoteExpirationDate());
        }
        if (cpqIbmPartnerQuote.getCpqQuoteNumber() != null) {
            ibmPartnerQuoteModel.setCpqQuoteNumber(cpqIbmPartnerQuote.getCpqQuoteNumber());
        }
        if (StringUtils.isNotBlank(cpqIbmPartnerQuote.getEccQuoteNumber())) {
            ibmPartnerQuoteModel.setEccQuoteNumber(cpqIbmPartnerQuote.getEccQuoteNumber());
        }
        if (cpqIbmPartnerQuote.getCpqQuoteExternalId() != null) {
            ibmPartnerQuoteModel.setCpqExternalQuoteId(cpqIbmPartnerQuote.getCpqQuoteExternalId());
        }
        if (cpqIbmPartnerQuote.getSalesApplication() != null) {
            ibmPartnerQuoteModel.setSalesApplication(cpqIbmPartnerQuote.getSalesApplication());
        }
        ibmPartnerQuoteModel.setCpqDistributionChannel(
            StringUtils.defaultIfBlank(cpqIbmPartnerQuote.getCpqDistributionChannel(),
                PartnerQuoteChannelEnum.J.getCode()));
        if (CollectionUtils.isNotEmpty(cpqIbmPartnerQuote.getCollaboratorEmails())) {
            ibmPartnerQuoteModel.setCollaboratorEmails(cpqIbmPartnerQuote.getCollaboratorEmails());
        }
    }

    public PartnerUserService getPartnerUserService() {
        return partnerUserService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }
    public DefaultPartnerQuoteUpdateStateStrategy getQuoteUpdateStateStrategy() {
        return quoteUpdateStateStrategy;
    }
}