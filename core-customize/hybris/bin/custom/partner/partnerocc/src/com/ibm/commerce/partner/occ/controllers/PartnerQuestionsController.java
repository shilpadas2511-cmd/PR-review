package com.ibm.commerce.partner.occ.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ibm.commerce.partner.facades.partnerquestions.PartnerQuestionsFacade;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsDataListData;
import com.ibm.commerce.partnerwebservicescommons.dto.partnerquestions.PartnerQuestionsListWsDTO;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "partner questions controller")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}/partnerquestions")
public class PartnerQuestionsController extends PartnerBaseController {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerQuestionsController.class);

    @Resource(name = "partnerQuestionsFacade")
    private PartnerQuestionsFacade partnerQuestionsFacade;


    @GetMapping(value = "/getquestions", produces = APPLICATION_JSON_VALUE)
    @Operation(operationId = "partner questions", summary = "Return the list of partner questions", description = "Populate partner questions /{baseSiteId}/users/{userId}/carts/{cartId}/partnerquestions/getquestions")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PartnerQuestionsListWsDTO getPartnerQuestionsDetails(
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
        @RequestParam(required = false) final String partnerQuestionsType) {
        List<PartnerQuestionsData> partnerQuestionsDataList = partnerQuestionsFacade.getAllPartnerQuestions(
            partnerQuestionsType);
        return getDataMapper().map(getAllPartnerQuestions(partnerQuestionsDataList),
            PartnerQuestionsListWsDTO.class, fields);
    }

    @PostMapping(value = "/savequestions", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "save partner questions information", hidden = true, summary = "save partner questions and answers ", description = "save partner questions and answers")
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void savePartnerQuestions(
        @Parameter(description = "Base site identifier.", required = true) @PathVariable final String cartId,
        @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
        @Parameter(description = "PartnerQuestionsListWsDTO DTO", required = true) @RequestBody @Nonnull final PartnerQuestionsListWsDTO partnerQuestionsListWsDTO)
        throws CommerceCartModificationException {
        if (partnerQuestionsListWsDTO != null) {
            List<PartnerQuestionsData> partnerQuestionsDataList = convertToData(
                partnerQuestionsListWsDTO);
            partnerQuestionsFacade.savePartnerQuestions(partnerQuestionsDataList);
        }

    }

    private List<PartnerQuestionsData> convertToData(PartnerQuestionsListWsDTO partnerQuestionsListWsDTO) {
        return partnerQuestionsListWsDTO.getPartnerQuestions().stream()
            .map(cartWsDTO -> {
                var questionsData = new PartnerQuestionsData();
                getDataMapper().map(cartWsDTO, questionsData);
                return questionsData;
            })
            .collect(Collectors.toList());
    }


    private PartnerQuestionsDataListData getAllPartnerQuestions(
        List<PartnerQuestionsData> partnerQuestionsList) {
        final PartnerQuestionsDataListData qustionsList = new PartnerQuestionsDataListData();
        qustionsList.setPartnerQuestions(partnerQuestionsList);
        return qustionsList;
    }


}
