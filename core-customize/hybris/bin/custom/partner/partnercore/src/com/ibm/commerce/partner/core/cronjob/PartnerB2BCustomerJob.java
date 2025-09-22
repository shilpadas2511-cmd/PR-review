package com.ibm.commerce.partner.core.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;

/**
 * Cronjob to fetch PartnerB2BCustomer and trigger the business process to assign siteIds
 */
public class PartnerB2BCustomerJob extends AbstractJobPerformable<CronJobModel>
{
    private static final Logger LOG = LoggerFactory.getLogger(PartnerB2BCustomerJob.class);
	private final PartnerUserService userService;

	public PartnerB2BCustomerJob(final PartnerUserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Perform job to fetch all active PartnerB2BCustomer and trigger the business process on the PartnerB2BCustomer to
	 * assign the site ids.
	 *
	 * @param cronJobModel
	 * @return
	 */
    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
        try {
            final List<PartnerB2BCustomerModel> activePartnerB2BCustomers =
                    getUserService().getActivePartnerB2BCustomers();
            for (final PartnerB2BCustomerModel userModel : activePartnerB2BCustomers) {
                getUserService().createUpdateSiteIdBusinessProcess(userModel);
            }
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final Exception e) {
            LOG.error("Exception occurred during the execution of the PartnerB2BCustomerJob: {}",
                    e.getMessage());
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }
    }

	public PartnerUserService getUserService()
	{
		return userService;
	}
}
