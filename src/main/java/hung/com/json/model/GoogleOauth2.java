
package hung.com.json.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "web"
})
public class GoogleOauth2 {

    @JsonProperty("web")
    private Web web;

    @JsonProperty("web")
    public Web getWeb() {
        return web;
    }

    @JsonProperty("web")
    public void setWeb(Web web) {
        this.web = web;
    }



}
