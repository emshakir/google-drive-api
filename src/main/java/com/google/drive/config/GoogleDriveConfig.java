package com.google.drive.config;


import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.*;
import com.google.api.client.json.jackson2.*;
import com.google.api.client.util.store.*;
import com.google.api.services.drive.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.io.*;

import java.io.*;
import java.util.*;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.oauth.callback.uri}")
    private String callback;

    @Value("${google.secret.key.path}")
    private Resource secretKeys;

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String APPLICATION_NAME = "iVisionDXPNimic";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JACKSON_FACTORY = JacksonFactory.getDefaultInstance();

    private GoogleAuthorizationCodeFlow flow;

    public void init() throws IOException {
        GoogleClientSecrets secrets = GoogleClientSecrets.load(
                JACKSON_FACTORY, new InputStreamReader(secretKeys.getInputStream())
        );
        if (flow == null) {
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JACKSON_FACTORY, secrets, SCOPES)
                    .setDataStoreFactory(new MemoryDataStoreFactory())
                    .build();
        }
    }

    public String createAuthorizationUrl(String username) {
        return flow.newAuthorizationUrl()
                .setRedirectUri(callback)
                .setAccessType("offline").setState(Base64.getEncoder().encodeToString(username.getBytes()))
                .build();
    }

    public boolean isUserAuthenticated(String username) throws IOException {
        Credential credential = flow.loadCredential(username);
        return credential != null && credential.getExpirationTimeMilliseconds() > System.currentTimeMillis();
    }

    public String getAccessToken(String username) throws IOException {
        Credential credential = flow.loadCredential(username);
        return credential.getAccessToken();
    }

    public String storeCredential(String code, String username) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(callback).execute();
        flow.createAndStoreCredential(response, username);
        Credential credential = flow.loadCredential(username);
        return credential.getAccessToken();
    }

    public Drive getDriveInstance(String username) throws IOException {
        Credential credential = flow.loadCredential(username);
        return new Drive.Builder(HTTP_TRANSPORT, JACKSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
