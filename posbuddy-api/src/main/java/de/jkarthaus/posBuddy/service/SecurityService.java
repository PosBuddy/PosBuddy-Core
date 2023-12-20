package de.jkarthaus.posBuddy.service;

import io.micronaut.security.x509.X509Authentication;

public interface SecurityService {
    void verifyX509Certificate(X509Authentication x509Authentication);

    boolean isServeStation(X509Authentication x509Authentication);

    boolean isCheckoutStation(X509Authentication x509Authentication);
}
