/**
 *
 */
package com.ibm.commerce.partner.core.strategy;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Strategy to Fetch the session Country from User or AbstractOrderModel
 *
 * @author AditiKumari
 */
public interface PartnerSessionCountryStrategy {

    /**
     * To fetch the session country for the user.
     *
     * @param user
     */
    CountryModel getSessionCountry(final UserModel user);

    /**
     * To fetch the session country.
     *
     * @param user
     * @param order
     */
    CountryModel getSessionCountry(final UserModel user, final AbstractOrderModel order);
}
