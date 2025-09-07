package com.api.tokbaro.global.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class ApnsConfig {

    @Value("${apns.p8-file-path}")
    private String p8FilePath;

    @Value("${apns.team-id}")
    private String teamId;

    @Value("${apns.key-id}")
    private String keyId;

    @Bean
    public ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        final ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setSigningKey(ApnsSigningKey.loadFromPkcs8File(new File(p8FilePath), teamId, keyId))
                .build();
        return apnsClient;
    }

}
