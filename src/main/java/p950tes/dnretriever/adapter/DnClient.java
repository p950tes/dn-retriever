package p950tes.dnretriever.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import p950tes.dnretriever.properties.DnCookieProperties;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class DnClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DnClient.class);

    private final HttpClient httpClient;

    @Autowired
    public DnClient(DnCookieProperties cookieProperties) {
        this.httpClient = createHttpClient(cookieProperties);
    }

    public String fetch(String url) throws Exception {
        LOGGER.debug("Fetching from {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Unexpected HTTP status code: " + response.statusCode() + ". Body: " + response.body());
        }
        return response.body();
    }

    private static HttpClient createHttpClient(DnCookieProperties cookieProperties) {

        CookieManager cookieManager = new CookieManager();

        if (cookieProperties.isUseCookies()) {
            cookieManager.getCookieStore().add(URI.create("dn.se"), createCookie("didomi_token", cookieProperties.getDidomiToken()));
            cookieManager.getCookieStore().add(URI.create("dn.se"), createCookie("euconsent", cookieProperties.getEuConsent()));
            cookieManager.getCookieStore().add(URI.create("dn.se"), createCookie("af", cookieProperties.getAf()));
            cookieManager.getCookieStore().add(URI.create("dn.se"), createCookie("sid", cookieProperties.getSid()));
            cookieManager.getCookieStore().add(URI.create("dn.se"), createCookie("appId", cookieProperties.getAppId()));
        }

        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .followRedirects(Redirect.NEVER)
                .cookieHandler(cookieManager)
                .build();
    }

    private static HttpCookie createCookie(String name, String value) {
        HttpCookie memberCookie = new HttpCookie(name, value);
        memberCookie.setDomain(".dn.se");
        memberCookie.setPath("/");
        memberCookie.setHttpOnly(true);
        memberCookie.setSecure(true);
        return memberCookie;
    }

}
