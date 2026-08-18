package com.fintogether.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class JwtKeyConfig {
    @Bean
    public RSAKey rsaKey() throws JOSEException {
        // Generate a new RSA key pair for development purposes
        return new RSAKeyGenerator(2048)
                .keyID("dev-key")
                .generate();
    }
}
