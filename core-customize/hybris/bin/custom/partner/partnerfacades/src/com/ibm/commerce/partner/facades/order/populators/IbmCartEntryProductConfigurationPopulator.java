package com.ibm.commerce.partner.facades.order.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * populate config id from order main entry
 */
public class IbmCartEntryProductConfigurationPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {


    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) throws ConversionException {

       if(Objects.nonNull(source.getProductConfiguration())) {
           target.setConfigId(source.getProductConfiguration().getConfigurationId());
           if (Objects.nonNull(source.getProductConfiguration().getProduct())) {
               source.getProductConfiguration().getProduct().stream().findFirst()
                   .map(product -> product.getCode()).ifPresent(target::setConfiguratorPidId);
           }
       }

    }

}