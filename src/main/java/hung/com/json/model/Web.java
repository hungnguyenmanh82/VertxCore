
package hung.com.json.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "client_id",
    "project_id",
    "auth_uri",
    "token_uri",
    "auth_provider_x509_cert_url",
    "client_secret",
    "redirect_uris",
    "javascript_origins"
})
public class Web {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("auth_uri")
    private String authUri;
    @JsonProperty("token_uri")
    private String tokenUri;
    @JsonProperty("auth_provider_x509_cert_url")
    private String authProviderX509CertUrl;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("redirect_uris")
    private List<String> redirectUris = null;
    @JsonProperty("javascript_origins")
    private List<String> javascriptOrigins = null;

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("project_id")
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty("project_id")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("auth_uri")
    public String getAuthUri() {
        return authUri;
    }

    @JsonProperty("auth_uri")
    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    @JsonProperty("token_uri")
    public String getTokenUri() {
        return tokenUri;
    }

    @JsonProperty("token_uri")
    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    @JsonProperty("auth_provider_x509_cert_url")
    public String getAuthProviderX509CertUrl() {
        return authProviderX509CertUrl;
    }

    @JsonProperty("auth_provider_x509_cert_url")
    public void setAuthProviderX509CertUrl(String authProviderX509CertUrl) {
        this.authProviderX509CertUrl = authProviderX509CertUrl;
    }

    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    @JsonProperty("client_secret")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @JsonProperty("redirect_uris")
    public List<String> getRedirectUris() {
        return redirectUris;
    }

    @JsonProperty("redirect_uris")
    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    @JsonProperty("javascript_origins")
    public List<String> getJavascriptOrigins() {
        return javascriptOrigins;
    }

    @JsonProperty("javascript_origins")
    public void setJavascriptOrigins(List<String> javascriptOrigins) {
        this.javascriptOrigins = javascriptOrigins;
    }
 

}
