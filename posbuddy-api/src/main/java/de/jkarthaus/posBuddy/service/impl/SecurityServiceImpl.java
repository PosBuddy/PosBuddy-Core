package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.model.Constants;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.security.x509.X509Authentication;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

@Singleton
@Slf4j
public class SecurityServiceImpl implements de.jkarthaus.posBuddy.service.SecurityService {

    private PublicKey publicKey;


    @Value("${micronaut.server.ssl.enabled}")
    private boolean isSslActive;


    @PostConstruct
    public void init() {
        try {
            ClassPathResourceLoader loader = new ResourceResolver()
                    .getLoader(ClassPathResourceLoader.class)
                    .get();
            Optional<InputStream> resource = loader.getResourceAsStream("classpath:ssl/caPubkey.txt");

            X509EncodedKeySpec spec = new X509EncodedKeySpec(resource.get().readAllBytes());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            //TODO: Fix this
            publicKey = kf.generatePublic(spec);
        } catch (Exception e) {
            log.error("error on parsing public key");
        }
    }



    @Override
    public void verifyX509Certificate(X509Authentication x509Authentication) {
        try {

            x509Authentication.getCertificate().verify(x509Authentication.getCertificate().getPublicKey());
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isServeStation(X509Authentication x509Authentication) {
        if (!isSslActive) {
            log.info("SSL disabled -> isServeStation ->> true");
            return true;
        }
        if (x509Authentication == null) {
            log.warn("try to access without certificate");
            return false;
        }
        if (x509Authentication
                .getName()
                .equals(Constants.PERMISSION_SERVE_CERTIFICATE_NAME)) {
            return true;
        }
        log.warn("need CN:{} in client certificate found:{}",
                Constants.PERMISSION_SERVE_CERTIFICATE_NAME,
                x509Authentication.getName()
        );
        return false;
    }

    @Override
    public boolean isCheckoutStation(X509Authentication x509Authentication) {
        if (!isSslActive) {
            log.info("SSL disabled -> isCheckoutStation ->> true");
            return true;
        }
        if (x509Authentication == null) {
            log.warn("try to access without certificate");
            return false;
        }
        if (x509Authentication
                .getName()
                .equals(Constants.PERMISSION_CHECKOUT_CERTIFICATE_NAME)
        ) {
            return true;
        }
        log.warn("need CN:{} in client certificate found:{}",
                Constants.PERMISSION_CHECKOUT_CERTIFICATE_NAME,
                x509Authentication.getName()
        );
        return false;
    }

    @Override
    public boolean isServeOrCheckout(X509Authentication x509Authentication) {
        if (!isSslActive) {
            log.info("SSL disabled -> serveOrCheckout ->> true");
            return true;
        }
        return isCheckoutStation(x509Authentication) && isServeStation(x509Authentication);
    }


}
