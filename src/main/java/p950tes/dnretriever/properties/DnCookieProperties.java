package p950tes.dnretriever.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("dnretriever.cookies")
public class DnCookieProperties {

    private boolean useCookies;
    private String didomiToken;
    private String euConsent;
    private String sid;
    private String af;
    private String appId;

    public boolean isUseCookies() {
        return useCookies;
    }

    public void setUseCookies(boolean useCookies) {
        this.useCookies = useCookies;
    }

    public String getDidomiToken() {
        return didomiToken;
    }

    public void setDidomiToken(String didomiToken) {
        this.didomiToken = didomiToken;
    }

    public String getEuConsent() {
        return euConsent;
    }

    public void setEuConsent(String euConsent) {
        this.euConsent = euConsent;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAf() {
        return af;
    }

    public void setAf(String af) {
        this.af = af;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
