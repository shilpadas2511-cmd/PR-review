package com.ibm.commerce.partner.core.cronjob;

import com.ibm.commerce.partner.core.order.strategies.impl.DefaultPartnerUpdateQuoteFromCartStrategy;
import com.ibm.commerce.partner.core.outbound.service.PartnerCommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class PartnerCloneBuyerFailedJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(PartnerB2BCustomerJob.class);

    private final PartnerCommerceQuoteService commerceQuoteService;

    private final ModelService modelService;
    private int cloneCreatedThresholdTime;


    private DefaultPartnerUpdateQuoteFromCartStrategy updateQuoteFromCartStrategy;

    public PartnerCloneBuyerFailedJob(PartnerCommerceQuoteService commerceQuoteService,
        ModelService modelService, int cloneCreatedThresholdTime,
        DefaultPartnerUpdateQuoteFromCartStrategy updateQuoteFromCartStrategy) {
        this.commerceQuoteService = commerceQuoteService;
        this.modelService = modelService;
        this.cloneCreatedThresholdTime = cloneCreatedThresholdTime;
        this.updateQuoteFromCartStrategy = updateQuoteFromCartStrategy;
    }

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        LOG.info("PartnerCloneBuyerFailedJob started.");

        List<IbmPartnerQuoteModel> quotes = getActiveQuotesInCloneBuyerCreatedState();

        quotes.stream().filter(this::isCloneThresholdExceeded).forEach(this::setQuoteFailedState);

        LOG.info("PartnerCloneBuyerFailedJob completed.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected List<IbmPartnerQuoteModel> getActiveQuotesInCloneBuyerCreatedState() {
        return getCommerceQuoteService().getActiveQuotesInCloneCreatedState();
    }

    protected boolean isCloneThresholdExceeded(final IbmPartnerQuoteModel quote) {
        Date creationTime = quote.getCreationtime();
        Date thresholdTime = getCloneCreatedThresholdTimeAsDate();

        return creationTime.before(thresholdTime);
    }

    protected Date getCloneCreatedThresholdTimeAsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -getCloneCreatedThresholdTime());
        return calendar.getTime();
    }

    protected void setQuoteFailedState(final QuoteModel quote) {

        IbmPartnerQuoteModel failedQuote = getUpdateQuoteFromCartStrategy().updateQuoteFromCart(
            quote.getCartReference());
        getCommerceQuoteService().removeQuoteCart(failedQuote);
        failedQuote.setState(QuoteState.CLONE_BUYER_FAILED);
        getModelService().save(failedQuote);
    }

    public int getCloneCreatedThresholdTime() {
        return cloneCreatedThresholdTime;
    }

    /**
     * @return commerceQuoteService
     */
    public PartnerCommerceQuoteService getCommerceQuoteService() {
        return commerceQuoteService;
    }

    public DefaultPartnerUpdateQuoteFromCartStrategy getUpdateQuoteFromCartStrategy() {
        return updateQuoteFromCartStrategy;
    }

    public ModelService getModelService() {
        return modelService;
    }

}