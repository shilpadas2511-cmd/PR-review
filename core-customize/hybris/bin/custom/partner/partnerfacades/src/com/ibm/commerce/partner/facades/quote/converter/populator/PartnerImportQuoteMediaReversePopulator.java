package com.ibm.commerce.partner.facades.quote.converter.populator;

import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * This class is created  to  populate data from PartnerImportQuoteFileRequestData to MediaModel
 */
public class PartnerImportQuoteMediaReversePopulator implements
    Populator<PartnerImportQuoteFileRequestData, MediaModel> {

    private final KeyGenerator mediaCodeKeyGenerator;
    private final static String OR = " | ";

    public PartnerImportQuoteMediaReversePopulator(KeyGenerator mediaCodeKeyGenerator) {
        this.mediaCodeKeyGenerator = mediaCodeKeyGenerator;
    }


    /**
     * @param partnerImportQuoteFileRequestData the source object
     * @param mediaModel                        the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerImportQuoteFileRequestData partnerImportQuoteFileRequestData,
        MediaModel mediaModel) throws ConversionException {

        if (Objects.nonNull(partnerImportQuoteFileRequestData)) {
            mediaModel.setCode(getMediaCodeKeyGenerator().generate().toString());
            mediaModel.setDescription(mediaDescriptionCreator(partnerImportQuoteFileRequestData));
        }
    }

    /**
     * @param partnerImportQuoteFileRequestData
     * @return String
     */

    protected String mediaDescriptionCreator(
        PartnerImportQuoteFileRequestData partnerImportQuoteFileRequestData) {
        return (new StringBuilder().append(partnerImportQuoteFileRequestData.getFileName())
            .append(OR).append(partnerImportQuoteFileRequestData.getFormat()).append(OR)
            .append(partnerImportQuoteFileRequestData.getFileSize())).toString();
    }

    public KeyGenerator getMediaCodeKeyGenerator() {
        return mediaCodeKeyGenerator;
    }
}
