package com.ibm.commerce.partner.webservicescommons.testsupport.client;

import de.hybris.platform.webservicescommons.testsupport.client.WsAbstractRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


/**
 * Webservice request builder class, to build oauth request using grant type 'Partner'.
 */
public class PartnerWsSecuredRequestBuilder extends WsSecuredRequestBuilder
{
	private static final Logger LOG = Logger.getLogger(PartnerWsSecuredRequestBuilder.class);

	private String oAuthClientId;
	private String oAuthClientSecret;
	private String oAuthResourceOwnerName;
	private String oAuthResourceOwnerPassword;
	private String oAuthScope;
	private OAuthGrantType oAuthGrantType;

	public PartnerWsSecuredRequestBuilder grantPartnerCredentials()
	{
		this.oAuthGrantType = PartnerWsSecuredRequestBuilder.OAuthGrantType.PARTNER;
		return this.getThis();
	}

	@Override
	public PartnerWsSecuredRequestBuilder extensionName(final String extensionName)
	{
		setExtensionName(extensionName);
		return this.getThis();

	}

	@Override
	public PartnerWsSecuredRequestBuilder client(final String clientId, final String clientSecret)
	{
		this.oAuthClientId = clientId;
		this.oAuthClientSecret = clientSecret;
		return this.getThis();
	}

	@Override
	public PartnerWsSecuredRequestBuilder resourceOwner(final String oAuthResourceOwnerName,
			final String oAuthResourceOwnerPassword)
	{
		this.oAuthResourceOwnerName = oAuthResourceOwnerName;
		this.oAuthResourceOwnerPassword = oAuthResourceOwnerPassword;
		return this.getThis();
	}

	@Override
	public PartnerWsSecuredRequestBuilder scope(final String... scope)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("scope has to have not null value");
		}
		else
		{
			this.oAuthScope = String.join(",", scope);
			return this.getThis();
		}
	}

	@Override
	protected String getOAuth2Token()
	{
		if (this.oAuthGrantType == null)
		{
			throw new WsAbstractRequestBuilder.WsRequestBuilderException("OAuth grant type not set!");
		}
		else
		{
			switch (this.oAuthGrantType)
			{
				case CLIENT_CREDENTIALS:
					return this.getOAuth2TokenUsingClientCredentials();
				case RESOURCE_OWNER_PASSWORD_CREDENTIALS:
					return this.getOAuth2TokenUsingResourceOwnerPassword();
				case PARTNER:
					return this.getOAuth2TokenUsingPartner();
				default:
					return null;
			}
		}
	}

	protected String getOAuth2TokenUsingPartner()
	{
		return this.getOAuth2TokenUsingPartner(this.buildOAuthWebTarget(), this.oAuthClientId, this.oAuthClientSecret,
				this.oAuthResourceOwnerName, this.oAuthResourceOwnerPassword, this.oAuthScope);
	}

	protected String getOAuth2TokenUsingPartner(final WebTarget oAuthWebTarget, final String clientID,
			final String clientSecret, final String resourceOwnerName, final String resourceOwnerPassword, final String scope)
	{
		try
		{
			final Response result = oAuthWebTarget.queryParam("grant_type", new Object[]
			{ "partner" }).queryParam("username", new Object[]
			{ resourceOwnerName }).queryParam("password", new Object[]
			{ resourceOwnerPassword }).queryParam("client_id", new Object[]
			{ clientID }).queryParam("client_secret", new Object[]
			{ clientSecret }).queryParam("scope", new Object[]
			{ scope }).request().accept(new String[]
			{ "application/json" }).post(Entity.entity((Object) null, "application/json"));
			result.bufferEntity();
			if (result.hasEntity())
			{
				return this.getTokenFromJsonStr(result.readEntity(String.class));
			}
			else
			{
				LOG.error("Empty response body!!");
				return null;
			}
		}
		catch (final IOException var8)
		{
			LOG.error("Error during authorizing REST client using Resource owner password!!", var8);
			return null;
		}
	}

	@Override
	protected PartnerWsSecuredRequestBuilder getThis()
	{
		return this;
	}

	public static enum OAuthGrantType
	{
		RESOURCE_OWNER_PASSWORD_CREDENTIALS, CLIENT_CREDENTIALS, PARTNER;
	}
}
