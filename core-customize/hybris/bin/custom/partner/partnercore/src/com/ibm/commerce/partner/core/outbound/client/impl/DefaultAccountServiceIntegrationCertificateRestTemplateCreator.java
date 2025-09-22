package com.ibm.commerce.partner.core.outbound.client.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.cache.DestinationRestTemplateId;
import de.hybris.platform.outboundservices.cache.impl.DefaultDestinationRestTemplateId;
import de.hybris.platform.outboundservices.client.impl.AbstractRestTemplateCreator;
import de.hybris.platform.sap.sapcpiadapter.exception.CredentialException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Creater for AccountService
 */
public class DefaultAccountServiceIntegrationCertificateRestTemplateCreator extends
    AbstractRestTemplateCreator {

    private static final Logger LOG = Log.getLogger(
        DefaultAccountServiceIntegrationCertificateRestTemplateCreator.class);

    private static final String JKS = "JKS";

    public DefaultAccountServiceIntegrationCertificateRestTemplateCreator(
        final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    private final MediaService mediaService;
    @Override
    public boolean isApplicable(final ConsumedDestinationModel destination) {
        return destination.getCredential() instanceof IbmPartnerConsumedCertificateCredentialModel;
    }

    private KeyStore createKeyStore(InputStream inputStream, char[] password)
        throws NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(JKS);
        keyStore.load(inputStream, password);
        return keyStore;
    }

    @Override
    protected RestTemplate createRestTemplate(final ConsumedDestinationModel destination) {
        IbmPartnerConsumedCertificateCredentialModel certificateCredentialModel = (IbmPartnerConsumedCertificateCredentialModel) destination.getCredential();

        try {
            final char[] password = certificateCredentialModel.getPassword().toCharArray();

            final InputStream inputStream = getMediaService().getStreamFromMedia(
                certificateCredentialModel.getFile());
            KeyStore keyStore = createKeyStore(inputStream, password);
            SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, password)
                .build();
            return initRestTemplateSelfSignedHttps(sslContext);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | KeyStoreException |
                 CertificateException | IOException | KeyManagementException |
                 UnrecoverableKeyException e) {
            LOG.error("Error when creating sslContext, message: {}, stacktrace: {}", e.getMessage(),
                ExceptionUtils.getStackTrace(e));
            throw new CredentialException(
                String.format("Fail to create sslContext for the credential: %s",
                    certificateCredentialModel.getId()));
        }
    }

    public static RestTemplate initRestTemplateSelfSignedHttps(SSLContext sslContext) {
        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = useApacheHttpClientWithSelfSignedSupport(
            sslContext);
        final RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        restTemplate.getMessageConverters().add(generateByteArrayHttpMessageConverter());
        return restTemplate;
    }

    private static HttpComponentsClientHttpRequestFactory useApacheHttpClientWithSelfSignedSupport(
        SSLContext sslContext) {
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
            NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
            .disableCookieManagement().build();
        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(
            Config.getInt("ibm.rest.client.url.connection.time.out.ms",
                PartnercoreConstants.TIMEOUT_TIME));
        httpComponentsClientHttpRequestFactory.setReadTimeout(
            Config.getInt("ibm.rest.client.url.read.time.out.ms",
                PartnercoreConstants.TIMEOUT_TIME));
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(
            Config.getInt("ibm.rest.client.url.connection.request.time.out.ms",
                PartnercoreConstants.TIMEOUT_TIME));
        return httpComponentsClientHttpRequestFactory;
    }

    private static ByteArrayHttpMessageConverter generateByteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }


    @Override
    protected DestinationRestTemplateId getDestinationRestTemplateId(
        final ConsumedDestinationModel destinationModel) {
        return DefaultDestinationRestTemplateId.from(destinationModel);
    }
}
