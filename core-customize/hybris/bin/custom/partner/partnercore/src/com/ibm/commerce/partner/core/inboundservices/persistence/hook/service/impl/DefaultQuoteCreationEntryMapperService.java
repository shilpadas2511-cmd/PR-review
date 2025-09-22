package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationPriceMapperService;
import com.ibm.commerce.partner.core.model.CPQIbmPartnerOrderEntryProductInfoModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqPartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.CpqPartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import org.apache.log4j.Logger;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default Partner DefaultQuoteCreationEntryMapperService MapperService class is used to
 * populate or map the entry  details from CpqIbmPartnerQuoteModel field values to IbmPartnerQuoteModel object
 */
public class DefaultQuoteCreationEntryMapperService implements
    PartnerQuoteCreationMapperService<CpqIbmPartnerQuoteModel, IbmPartnerQuoteModel> {
    private static final Logger LOG = Logger.getLogger(DefaultQuoteCreationEntryMapperService.class);

    private static final String PIECES = "pieces";
    private static final String ONLINE = "Online";
    private static final String PRODUCT_CATALOG = "partnerProductCatalog";
    private static final String PID_QUOTE = "PIDQUOTE";
    private static final String INVALID_DATE = "Invalid Date Format";
    private ModelService modelService;
    private ProductService productService;
    private PartnerUserService partnerUserService;
    private UnitService unitService;

    private final KeyGenerator pidQuoteKeyGenerator;
    private PartnerQuoteCreationPriceMapperService priceMappers;

    private CatalogVersionService catalogVersionService;
    public DefaultQuoteCreationEntryMapperService(ModelService modelService,
        ProductService productService,
        PartnerUserService partnerUserService,
        UnitService unitService,
        KeyGenerator pidQuoteKeyGenerator,
        PartnerQuoteCreationPriceMapperService priceMappers,
        CatalogVersionService catalogVersionService) {
        this.modelService = modelService;
        this.productService = productService;
        this.partnerUserService = partnerUserService;
        this.unitService = unitService;
        this.pidQuoteKeyGenerator = pidQuoteKeyGenerator;
        this.priceMappers = priceMappers;
        this.catalogVersionService = catalogVersionService;
    }


    /**
     * Maps the entries from the {@link CpqIbmPartnerQuoteModel} to the {@link IbmPartnerQuoteModel}.
     * This method processes each entry, creating a corresponding {@link IbmPartnerQuoteEntryModel}, and
     * associates it with the provided quote model. Additionally, it creates the related CPQ pricing,
     * product configuration, and child entries, if available.
     *
     * @param cpqIbmPartnerQuoteModel the source CPQ IBM Partner Quote model containing the quote entries. Must not be null.
     * @param quoteModel              the target IBM Partner Quote model to which the entries will be mapped. Must not be null.
     * @throws IllegalArgumentException if either `cpqIbmPartnerQuoteModel` or `quoteModel` is null.
     */
    @Override
    public void map(CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,
        IbmPartnerQuoteModel quoteModel) {
        CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion(
            PRODUCT_CATALOG, ONLINE);

        if (Objects.nonNull(cpqIbmPartnerQuoteModel.getEntries())) {
            List<AbstractOrderEntryModel> quoteEntries = new ArrayList<>();

            if (Objects.nonNull(cpqIbmPartnerQuoteModel.getEntries())) {
                cpqIbmPartnerQuoteModel.getEntries().forEach(entry -> setChildEntry(entry,catalogVersionModel,quoteModel,cpqIbmPartnerQuoteModel,quoteEntries));
            }

            quoteModel.setEntries(quoteEntries);
            getModelService().save(quoteModel);
        }
    }
    /**
     * Creates an {@link IbmPartnerQuoteEntryModel} based on the provided {@link CpqIbmPartnerQuoteEntryModel} entry.
     * This method populates the quote entry model with details such as external quote entry ID, quantity, product,
     * and unit. If any required data is missing, the method handles the null checks and ensures that valid models
     * are created or retrieved from the respective services.
     *
     * @param catalogVersionModel the catalog version to which the product belongs. Must not be null.
     * @param entry               the CPQ IBM partner quote entry model containing the entry details. Must not be null.
     * @return                    a newly created {@link IbmPartnerQuoteEntryModel} populated with the provided details.
     * @throws IllegalArgumentException if either the {@code catalogVersionModel} or {@code entry} is null.
     */
    protected IbmPartnerQuoteEntryModel createQuoteEntry(CatalogVersionModel catalogVersionModel,
        CpqIbmPartnerQuoteEntryModel entry) {

        IbmPartnerQuoteEntryModel quoteEntry = modelService.create(IbmPartnerQuoteEntryModel.class);

        if (entry.getEntryNumber() != null) {
            quoteEntry.setCpqExternalQuoteEntryId(entry.getEntryNumber());
        }
        if (entry.getQuantity() != null) {
            try {
                quoteEntry.setQuantity(Long.valueOf(entry.getQuantity()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity format: " + entry.getQuantity(), e);
            }
        }
        if (entry.getProduct() != null) {
            ProductModel product = getProductService().getProductForCode(catalogVersionModel, entry.getProduct());
            if (product != null) {
                quoteEntry.setProduct(product);
            } else {
                throw new IllegalArgumentException("Product with code " + entry.getProduct() + " not found.");
            }
        }
        UnitModel unit = getUnitService().getUnitForCode(PIECES);
        if (unit != null) {
            quoteEntry.setUnit(unit);
        } else {
            throw new IllegalArgumentException("Unit for code 'PIECES' not found.");
        }
        return quoteEntry;
    }

    /**
     * Creates child entries for a given {@link IbmPartnerQuoteEntryModel} and associates them with a parent quote entry.
     * Each child entry is processed by creating an {@link IbmPartnerPidQuoteEntryModel} and adding it to a new {@link IbmPartnerPidQuoteModel}.
     * @param catalogVersionModel the catalog version associated with the products. Must not be null.
     * @param childEntries        a collection of child entries to be processed. Can be null or empty.
     * @param quoteEntry          the parent quote entry that will hold the child entries. Must not be null.
     * @param quoteModel          the parent quote model. Must not be null.
     *
     * @param cpqIbmPartnerQuoteModel
     * @throws IllegalArgumentException if {@code catalogVersionModel}, {@code quoteEntry}, or {@code quoteModel} is null.
     */
    protected void createChildEntries(
        CatalogVersionModel catalogVersionModel,
        Collection<CpqIbmPartnerQuoteEntryModel> childEntries,
        IbmPartnerQuoteEntryModel quoteEntry,
        IbmPartnerQuoteModel quoteModel,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {

        if (Objects.nonNull(childEntries) && !childEntries.isEmpty()) {

            IbmPartnerPidQuoteModel pidQuoteModel = getModelService().create(
                IbmPartnerPidQuoteModel.class);

            List<AbstractOrderEntryModel> pidQuoteEntriesList = new ArrayList<>();
            pidQuoteModel.setEntries(pidQuoteEntriesList);
            pidQuoteModel.setCurrency(quoteModel.getCurrency());
            pidQuoteModel.setDate(quoteModel.getDate());
            pidQuoteModel.setUser(quoteModel.getCreator());

            final String orderCode = PID_QUOTE + "_" + quoteEntry.getProduct().getCode() + "_"
                + getPidQuoteKeyGenerator().generate().toString();
            pidQuoteModel.setCode(orderCode);

            childEntries.forEach(childEntry -> setQuoteChildEntries(childEntry,catalogVersionModel,cpqIbmPartnerQuoteModel,pidQuoteModel,pidQuoteEntriesList));
            getModelService().save(pidQuoteModel);
            quoteEntry.setChildEntries(new ArrayList<>(pidQuoteEntriesList));
            getModelService().save(quoteEntry);
        }
    }
    /**
     * Creates and associates product configurations for the given quote entry.
     *
     * @param entry
     * @param catalogVersionModel the catalog version associated with the products. Must not be null.
     * @param quoteEntry          the quote entry that will be associated with the created product configuration. Must not be null.
     * @param cpqIbmPartnerQuoteModel the CPQ partner quote model containing the entries for product configurations. Must not be null.
     *
     * @throws IllegalArgumentException if {@code catalogVersionModel}, {@code quoteEntry}, or {@code cpqIbmPartnerQuoteModel} is null.
     */
    protected void createProductConfiguration(
        CpqIbmPartnerQuoteEntryModel entry,
        CatalogVersionModel catalogVersionModel,
        IbmPartnerQuoteEntryModel quoteEntry,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {

        ProductConfigurationModel productConfigurationModel = getModelService().create(
            ProductConfigurationModel.class);

        productConfigurationModel.setConfigurationId(entry.getConfigurationId());
        productConfigurationModel.setProduct(
            Arrays.asList(
                getProductService().getProductForCode(catalogVersionModel, entry.getProduct()))
        );
        productConfigurationModel.setUser(
            getPartnerUserService().getCustomerByEmail(
                cpqIbmPartnerQuoteModel.getQuoteCreator().getEmail())
        );
        getModelService().save(productConfigurationModel);
        quoteEntry.setProductConfiguration(productConfigurationModel);
    }

    /**
     * Creates and associates product information for a given quote entry.
     * @param productInfos the list of product information models to be created and associated. Must not be null.
     * @param pidQuoteEntry the quote entry to which the product information will be associated. Must not be null.
     *
     * @param cpqIbmPartnerQuoteModel
     * @throws IllegalArgumentException if {@code productInfos} or {@code pidQuoteEntry} is null.
     */
    protected void createProductInfos(List<CPQIbmPartnerOrderEntryProductInfoModel> productInfos,
        IbmPartnerPidQuoteEntryModel pidQuoteEntry,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {

        productInfos.forEach(infoData -> {
            List<AbstractOrderEntryProductInfoModel> infos = createProductInfo(infoData,cpqIbmPartnerQuoteModel);

            pidQuoteEntry.setProductInfos(Stream.concat(
                    pidQuoteEntry.getProductInfos() == null ? Stream.empty()
                        : pidQuoteEntry.getProductInfos().stream(),
                    infos.stream().peek(item -> item.setOrderEntry(pidQuoteEntry))
                        .peek(getModelService()::save))
                .collect(Collectors.toList()));

            getModelService().save(pidQuoteEntry);
        });
    }

    /**
     * Creates a list of product information models based on the provided product info data.
     * @param infoData the product info data used to create the product information model. Must not be null.
     * @param cpqIbmPartnerQuoteModel
     * @return a list containing a single {@link AbstractOrderEntryProductInfoModel} representing the product info.
     * @throws IllegalArgumentException if {@code infoData} is null.
     */
    protected List<AbstractOrderEntryProductInfoModel> createProductInfo(
        final CPQIbmPartnerOrderEntryProductInfoModel infoData,
        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel)  {

        final CPQOrderEntryProductInfoModel result = new CPQOrderEntryProductInfoModel();
        result.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
        if (PartnercoreConstants.STARTDATE.equals(infoData.getName()) || PartnercoreConstants.ENDDATE.equals(infoData.getName())) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(PartnercoreConstants.ORIGINAL_DATE_PATTERN, Locale.ENGLISH);
                formatter.parse(infoData.getValues());
                result.setCpqCharacteristicAssignedValues(infoData.getValues());
            } catch (NumberFormatException | DateTimeException | ParseException e)  {
                logError(infoData.getName(),e,cpqIbmPartnerQuoteModel);
                throw new RuntimeException(INVALID_DATE, e);
            }
        } else {
            result.setCpqCharacteristicAssignedValues(infoData.getValues());
        }
        result.setCpqCharacteristicName(infoData.getName());
        return Collections.singletonList(result);
    }


    /**
     * Creates and populates a {@link PartnerCommerceRampUpModel} based on the provided
     * {@link CpqPartnerCommerceRampUpModel} and associates it with the provided
     * {@link IbmPartnerPidQuoteEntryModel}.
     * @param commerceRampUp The {@link CpqPartnerCommerceRampUpModel} object containing the
     *                           commerce ramp-up data.
     * @param pidQuoteEntry      The {@link IbmPartnerPidQuoteEntryModel} object to associate the
     *                           created ramp-up data with.
     */
    protected void createCommerceRampUp(CpqPartnerCommerceRampUpModel commerceRampUp,
        IbmPartnerPidQuoteEntryModel pidQuoteEntry) {
        PartnerCommerceRampUpModel partnerCommerceRampUpModel = getModelService().create(
            PartnerCommerceRampUpModel.class);
        if(Objects.nonNull(commerceRampUp.getId())) {
            partnerCommerceRampUpModel.setCode(commerceRampUp.getId());
        }
        if (commerceRampUp.getRampUpPeriod() != null) {
            partnerCommerceRampUpModel.setRampUpPeriod(
                commerceRampUp.getRampUpPeriod());
        }
        if (commerceRampUp.getCpqPartnerRampUpSummary() != null) {
            List<PartnerRampUpSummaryModel> rampUpSummaryList;
            rampUpSummaryList = commerceRampUp.getCpqPartnerRampUpSummary().stream()
                .map(this::createRampUpSummaryData).collect(
                    Collectors.toList());
            partnerCommerceRampUpModel.setPartnerRampUpSummary(rampUpSummaryList);
        }
        getModelService().save(partnerCommerceRampUpModel);
        pidQuoteEntry.setCommerceRampUp(partnerCommerceRampUpModel);
        getModelService().save(pidQuoteEntry);
    }

    /**
     * Creates and populates a {@link PartnerRampUpSummaryModel} based on the provided {@link CpqPartnerRampUpSummaryModel}.
     * @param rampUpSummary The {@link CpqPartnerRampUpSummaryModel} object containing the ramp-up summary data to be transferred
     * @return A new {@link PartnerRampUpSummaryModel} populated with values from the provided {@link CpqPartnerRampUpSummaryModel}.
     */
    protected PartnerRampUpSummaryModel createRampUpSummaryData(
        CpqPartnerRampUpSummaryModel rampUpSummary) {

        PartnerRampUpSummaryModel partnerRampUpSummaryModel = getModelService().create(
            PartnerRampUpSummaryModel.class);
        if(Objects.nonNull(rampUpSummary.getId())){
            partnerRampUpSummaryModel.setCode(rampUpSummary.getId());
        }
        if (rampUpSummary.getRampUpQuantity() != null) {
            partnerRampUpSummaryModel.setRampUpQuantity(rampUpSummary.getRampUpQuantity());
        }
        if (rampUpSummary.getRampUpPeriodDuration() != null) {
            partnerRampUpSummaryModel.setRampUpPeriodDuration(
                (rampUpSummary.getRampUpPeriodDuration()));
        }
        return partnerRampUpSummaryModel;
    }
    /**
     * Logs an error message when an invalid value is encountered for a specific field in the
     * CPQ IBM Partner Quote model. The log entry includes details such as the field name,
     * quote ID, CPQ quote number, CPQ external ID, and the exception message.
     *
     * @param name the name of the field that encountered an invalid value
     * @param exception the exception thrown due to the invalid value
     * @param cpqIbmPartnerQuoteModel the CPQ IBM Partner Quote model associated with the error
     */
    protected void logError(String name, Exception exception, CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel) {
        LOG.info(String.format("Invalid value encountered for field [%s]. QuoteId: [%s], CPQ QuoteNumber: [%s], CPQ External ID: [%s]. Error: [%s]",
            name,
            cpqIbmPartnerQuoteModel.getCode(),
            cpqIbmPartnerQuoteModel.getCpqQuoteNumber(),
            cpqIbmPartnerQuoteModel.getCpqQuoteExternalId(),
            exception.getMessage()));
    }

    /**
     * This method is used to map child entry  from CPQ to Commerce
     * @param entry
     * @param catalogVersionModel
     * @param quoteModel
     * @param cpqIbmPartnerQuoteModel
     * @param quoteEntries
     */
    protected void setChildEntry(CpqIbmPartnerQuoteEntryModel entry,CatalogVersionModel catalogVersionModel, IbmPartnerQuoteModel quoteModel,CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,List<AbstractOrderEntryModel> quoteEntries ){
        {
            if (entry != null) {
                IbmPartnerQuoteEntryModel quoteEntry = createQuoteEntry(catalogVersionModel,
                    entry);
                quoteEntry.setOrder(quoteModel);
                quoteEntries.add(quoteEntry);

                if (Objects.nonNull(entry.getPartnerCpqPricingDetails())) {
                    getPriceMappers().mapPricing(entry.getPartnerCpqPricingDetails(),
                        quoteEntry);
                }
                createProductConfiguration(entry, catalogVersionModel, quoteEntry,
                    cpqIbmPartnerQuoteModel);

                if (Objects.nonNull(entry.getChildEntries())) {
                    createChildEntries(catalogVersionModel, entry.getChildEntries(),
                        quoteEntry, quoteModel,cpqIbmPartnerQuoteModel);
                }
            }
        }
    }

    /**
     *      * Creates child entries for a given {@link IbmPartnerQuoteEntryModel} and associates them with a parent quote entry.
     * @param childEntry
     * @param catalogVersionModel
     * @param cpqIbmPartnerQuoteModel
     * @param pidQuoteModel
     * @param pidQuoteEntriesList
     */
    protected void setQuoteChildEntries(CpqIbmPartnerQuoteEntryModel childEntry,CatalogVersionModel catalogVersionModel,CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel,IbmPartnerPidQuoteModel pidQuoteModel, List<AbstractOrderEntryModel> pidQuoteEntriesList){
        {
            if (childEntry != null) {
                IbmPartnerPidQuoteEntryModel pidQuoteEntry = modelService.create(
                    IbmPartnerPidQuoteEntryModel.class);
                pidQuoteEntry.setCpqExternalQuoteEntryId(childEntry.getEntryNumber());
                pidQuoteEntry.setUnit(getUnitService().getUnitForCode(PIECES));
                pidQuoteEntry.setQuantity(Long.valueOf(childEntry.getQuantity()));
                pidQuoteEntry.setProduct(
                    getProductService().getProductForCode(catalogVersionModel,
                        childEntry.getProduct()));
                pidQuoteEntry.setOrder(pidQuoteModel);
                pidQuoteEntriesList.add(pidQuoteEntry);

                if (childEntry.getPartnerCpqPricingDetails() != null) {
                    getPriceMappers().mapPricing(childEntry.getPartnerCpqPricingDetails(),
                        pidQuoteEntry);
                }
                if (childEntry.getProductInfos() != null) {
                    createProductInfos(childEntry.getProductInfos(), pidQuoteEntry,cpqIbmPartnerQuoteModel);
                }
                if (childEntry.getCommerceRampUp() != null) {
                    createCommerceRampUp(childEntry.getCommerceRampUp(), pidQuoteEntry);
                }
            }
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public PartnerUserService getPartnerUserService() {
        return partnerUserService;
    }

    public UnitService getUnitService() {
        return unitService;
    }
    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }
    public KeyGenerator getPidQuoteKeyGenerator() {
        return pidQuoteKeyGenerator;
    }

    public PartnerQuoteCreationPriceMapperService getPriceMappers() {
        return priceMappers;
    }

}