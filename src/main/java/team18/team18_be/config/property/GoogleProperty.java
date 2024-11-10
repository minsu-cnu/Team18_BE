package team18.team18_be.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleProperty(
    String clientId,
    String clientSecret,
    String redirectUriLocal,
    String redirectUriProd,
    String grantType
) {

}
