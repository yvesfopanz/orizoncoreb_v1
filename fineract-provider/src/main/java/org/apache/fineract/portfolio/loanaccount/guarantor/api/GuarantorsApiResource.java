/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.guarantor.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.data.GlobalEntityType;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.UploadRequest;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.PortfolioAccountDTO;
import org.apache.fineract.portfolio.account.data.PortfolioAccountData;
import org.apache.fineract.portfolio.account.service.PortfolioAccountReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.guarantor.GuarantorConstants;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorsRequest;
import org.apache.fineract.portfolio.loanaccount.guarantor.domain.GuarantorType;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorEnumerations;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

@Path("/v1/loans/{loanId}/guarantors")
@Component
@Tag(name = "Guarantors", description = "")
@RequiredArgsConstructor
public class GuarantorsApiResource {

    private static final String RESOURCE_NAME_FOR_PERMISSION = "GUARANTOR";

    private final GuarantorReadPlatformService guarantorReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final DefaultToApiJsonSerializer<GuarantorData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;
    private final PortfolioAccountReadPlatformService portfolioAccountReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final BulkImportWorkbookService bulkImportWorkbookService;
    private final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService;

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public GuarantorData newGuarantorTemplate(@PathParam("loanId") final Long loanId) {
        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);

        final List<EnumOptionData> guarantorTypeOptions = GuarantorEnumerations.guarantorType(GuarantorType.values());
        final Collection<CodeValueData> allowedClientRelationshipTypes = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(GuarantorConstants.GUARANTOR_RELATIONSHIP_CODE_NAME);
        final Collection<PortfolioAccountData> accountLinkingOptions = null;
        return GuarantorData.template(guarantorTypeOptions, allowedClientRelationshipTypes, accountLinkingOptions);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public List<GuarantorData> retrieveGuarantorDetails(@PathParam("loanId") final Long loanId) {
        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);

        return this.guarantorReadPlatformService.retrieveGuarantorsForValidLoan(loanId);
    }

    @GET
    @Path("{guarantorId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public GuarantorData retrieveGuarantorDetails(@Context final UriInfo uriInfo, @PathParam("loanId") final Long loanId,
            @PathParam("guarantorId") final Long guarantorId) {
        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);

        GuarantorData guarantorData = this.guarantorReadPlatformService.retrieveGuarantor(loanId, guarantorId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isTemplate()) {
            final Collection<CodeValueData> allowedClientRelationshipTypes = this.codeValueReadPlatformService
                    .retrieveCodeValuesByCode(GuarantorConstants.GUARANTOR_RELATIONSHIP_CODE_NAME);
            final List<EnumOptionData> guarantorTypeOptions = GuarantorEnumerations.guarantorType(GuarantorType.values());
            final Collection<PortfolioAccountData> accountLinkingOptions = null;
            guarantorData = GuarantorData.templateOnTop(guarantorData, guarantorTypeOptions, allowedClientRelationshipTypes,
                    accountLinkingOptions);
        }
        return guarantorData;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public CommandProcessingResult createGuarantor(@PathParam("loanId") final Long loanId, final GuarantorsRequest guarantorsRequest) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createGuarantor(loanId)
                .withJson(apiJsonSerializerService.serialize(guarantorsRequest)).build();

        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @PUT
    @Path("{guarantorId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public CommandProcessingResult updateGuarantor(@PathParam("loanId") final Long loanId, @PathParam("guarantorId") final Long guarantorId,
            final GuarantorsRequest guarantorsRequest) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateGuarantor(loanId, guarantorId)
                .withJson(apiJsonSerializerService.serialize(guarantorsRequest)).build();

        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @DELETE
    @Path("{guarantorId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public CommandProcessingResult deleteGuarantor(@PathParam("loanId") final Long loanId, @PathParam("guarantorId") final Long guarantorId,
            @QueryParam("guarantorFundingId") final Long guarantorFundingId) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteGuarantor(loanId, guarantorId, guarantorFundingId).build();

        return this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    }

    @GET
    @Path("accounts/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public GuarantorData accountsTemplate(@QueryParam("clientId") final Long clientId, @PathParam("loanId") final Long loanId) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);

        PortfolioAccountDTO portfolioAccountDTO = new PortfolioAccountDTO(PortfolioAccountType.SAVINGS.getValue(), clientId, null);
        Collection<PortfolioAccountData> accountLinkingOptions = null;
        if (this.loanReadPlatformService.isGuaranteeRequired(loanId)) {
            accountLinkingOptions = this.portfolioAccountReadPlatformService.retrieveAllForLookup(portfolioAccountDTO);
        }
        return GuarantorData.template(null, null, accountLinkingOptions);
    }

    @GET
    @Path("downloadtemplate")
    @Produces("application/vnd.ms-excel")
    public Response getGuarantorTemplate(@QueryParam("officeId") final Long officeId, @QueryParam("dateFormat") final String dateFormat,
            @PathParam("loanId") final Long loanId) {
        return bulkImportWorkbookPopulatorService.getTemplate(GlobalEntityType.GUARANTORS.toString(), officeId, null, dateFormat);
    }

    @POST
    @Path("uploadtemplate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RequestBody(description = "Upload guarantor template", content = {
            @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = UploadRequest.class)) })
    public String postGuarantorTemplate(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("locale") final String locale,
            @FormDataParam("dateFormat") final String dateFormat, @PathParam("loanId") final Long loanId) {
        final Long importDocumentId = this.bulkImportWorkbookService.importWorkbook(GlobalEntityType.GUARANTORS.toString(),
                uploadedInputStream, fileDetail, locale, dateFormat);
        return this.apiJsonSerializerService.serialize(importDocumentId);
    }
}
