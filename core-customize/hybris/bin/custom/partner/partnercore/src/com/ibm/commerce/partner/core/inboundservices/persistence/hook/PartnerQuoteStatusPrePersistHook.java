package com.ibm.commerce.partner.core.inboundservices.persistence.hook;


import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.event.QuoteSubmittedEvent;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.lang.NonNull;

import com.ibm.commerce.partner.core.enums.CpqQuoteStatusType;
import com.ibm.commerce.partner.core.enums.EccQuoteStatusType;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;


/**
 * Quote Status for PrePersistHook
 */
public class PartnerQuoteStatusPrePersistHook implements PrePersistHook {

    private final QuoteService quoteService;
    private final ModelService modelService;
    private final EventService eventService;
    private final ConfigurationService configurationService;

    public PartnerQuoteStatusPrePersistHook(final QuoteService quoteService,
        final ModelService modelService, EventService eventService,
        ConfigurationService configurationService) {
        this.quoteService = quoteService;
        this.modelService = modelService;
        this.eventService = eventService;
        this.configurationService = configurationService;
    }


    @Override
    public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context) {
        if (item instanceof final IbmPartnerQuoteModel quoteModel) {
            return executeQuoteStatus(quoteModel, context);
        }
        return Optional.empty();
    }

    protected Optional<ItemModel> executeQuoteStatus(@NonNull final IbmPartnerQuoteModel item,
        final PersistenceContext context) {
        final IbmPartnerQuoteModel quote = ((IbmPartnerQuoteModel) getQuoteService().getCurrentQuoteForCode(
            item.getCode()));
        final IntegrationItem stateItem = get(context, QuoteModel.STATE);
        if (stateItem != null) {
            quote.setState(QuoteState.valueOf(stateItem.getIntegrationKey()));
        }
        final  Object cpqExternalIdItem = getCPQExternalId(context, QuoteModel.CPQEXTERNALQUOTEID);
        if (cpqExternalIdItem != null) {
            quote.setCpqExternalQuoteId(cpqExternalIdItem.toString());
        }
        final IntegrationItem eccQuoteStatusItem = get(context,
            IbmPartnerQuoteModel.ECCQUOTESTATUS);
        if (eccQuoteStatusItem != null) {
            quote.setEccQuoteStatus(
                EccQuoteStatusType.valueOf(eccQuoteStatusItem.getIntegrationKey()));
        }
        final IntegrationItem cpqQuoteStatusItem = get(context,
            IbmPartnerQuoteModel.CPQQUOTESTATUS);
        if (cpqQuoteStatusItem != null) {
            quote.setCpqQuoteStatus(
                CpqQuoteStatusType.valueOf(cpqQuoteStatusItem.getIntegrationKey()));
        }
        Object eccQuoteNumber = getValue(context, IbmPartnerQuoteModel.ECCQUOTENUMBER);
        if (ObjectUtils.isNotEmpty(eccQuoteNumber)) {
            quote.setEccQuoteNumber(eccQuoteNumber.toString());
        }
        getModelService().detach(item);
        getModelService().save(quote);
        if (isQuoteCommonApiFeatureDisabled() && QuoteState.IN_REVIEW.equals(quote.getState())
            && CollectionUtils.isNotEmpty(
            quote.getCollaboratorEmails())) {
            getEventService().publishEvent(new QuoteSubmittedEvent(quote.getCode()));
        }
        return Optional.of(quote);
    }

    protected IntegrationItem get(final PersistenceContext context, final String attribute) {
        final Object attributeDetails = ((StorageRequest) context).getPersistenceContext()
            .getIntegrationItem().getAttribute(attribute);
        return attributeDetails == null ? null : (IntegrationItem) attributeDetails;
    }
    protected Object getCPQExternalId(final PersistenceContext context, final String attribute) {
        final Object attributeDetails = ((StorageRequest) context).getPersistenceContext()
            .getIntegrationItem().getAttribute(attribute);
        return attributeDetails == null ? null : attributeDetails;
    }

    protected Object getValue(final PersistenceContext context, final String attribute) {
        return ((StorageRequest) context).getPersistenceContext().getIntegrationItem()
            .getAttribute(IbmPartnerQuoteModel.ECCQUOTENUMBER);
    }
    /**
     * Checks whether the Common Quote API feature is disabled based on the configuration setting.
     *
     * <p>This method retrieves the value of the configuration property
     * {@code CPQ_COMMON_QUOTE_API_FEATURE_DISABLED}. If the property is not explicitly set, it
     * defaults to {@code true}, meaning the feature is considered disabled by default.</p>
     *
     * @return {@code true} if the Common Quote API feature is disabled or not configured;
     * {@code false} if it is explicitly enabled in the configuration
     */
    public boolean isQuoteCommonApiFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED, Boolean.FALSE);
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}