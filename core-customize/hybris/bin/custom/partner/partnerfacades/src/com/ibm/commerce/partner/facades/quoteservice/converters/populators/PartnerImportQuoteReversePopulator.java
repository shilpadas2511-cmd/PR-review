package com.ibm.commerce.partner.facades.quoteservice.converters.populators;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Date;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * This class is created  for  populate PartnerImportQuoteFileRequestData to IbmPartnerQuoteModel
 */
public class PartnerImportQuoteReversePopulator implements
    Populator<PartnerImportQuoteFileRequestData, IbmPartnerQuoteModel> {

    private static final Logger LOG = Logger.getLogger(PartnerImportQuoteReversePopulator.class);

    private ModelService modelService;
    private CommonI18NService commonI18NService;

    private final KeyGenerator quoteCodeKeyGenerator;

    private UserService userService;
    private final Converter<PartnerImportQuoteFileRequestData, MediaModel> partnerImportQuoteMediaReverseConverter;


    public PartnerImportQuoteReversePopulator(
        ModelService modelService, CommonI18NService commonI18NService,
        KeyGenerator quoteCodeKeyGenerator,
        UserService userService,
        Converter<PartnerImportQuoteFileRequestData, MediaModel> partnerImportQuoteMediaReverseConverter) {
        this.modelService = modelService;
        this.commonI18NService = commonI18NService;
        this.quoteCodeKeyGenerator = quoteCodeKeyGenerator;
        this.userService = userService;
        this.partnerImportQuoteMediaReverseConverter = partnerImportQuoteMediaReverseConverter;
    }

    /**
     * @param partnerImportQuoteFileRequestData the source object
     * @param ibmPartnerQuoteModel              the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(PartnerImportQuoteFileRequestData partnerImportQuoteFileRequestData,
        IbmPartnerQuoteModel ibmPartnerQuoteModel) throws ConversionException {
        ibmPartnerQuoteModel.setCode(getQuoteCodeKeyGenerator().generate().toString());
        ibmPartnerQuoteModel.setState(QuoteState.IMPORT_FILE_UPLOAD_INITIATED);
        if (Objects.nonNull(getUserService().getCurrentUser())) {
            ibmPartnerQuoteModel.setUser((CustomerModel) getUserService().getCurrentUser());
        }
        ibmPartnerQuoteModel.setDate(new Date());
        ibmPartnerQuoteModel.setCurrency(getCommonI18NService().getCurrency(PartnercoreConstants.ISO_CODE));
        ibmPartnerQuoteModel.setProposalDocument(
            createMediaForImportQuote(partnerImportQuoteFileRequestData));
    }

    protected MediaModel createMediaForImportQuote(
        PartnerImportQuoteFileRequestData fileRequestData) {
        MediaModel mediaModel = getModelService().create(MediaModel.class);
        getModelService().save(
            getPartnerImportQuoteMediaReverseConverter().convert(fileRequestData, mediaModel));
        LOG.info("Media is created for Imported Quote file" + mediaModel.getCode());
        return mediaModel;
    }

    public Converter<PartnerImportQuoteFileRequestData, MediaModel> getPartnerImportQuoteMediaReverseConverter() {
        return partnerImportQuoteMediaReverseConverter;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(
        CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public KeyGenerator getQuoteCodeKeyGenerator() {
        return quoteCodeKeyGenerator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
