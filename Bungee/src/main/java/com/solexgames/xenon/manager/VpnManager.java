package com.solexgames.xenon.manager;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.model.VpnRequestData;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @author GrowlyX
 * @since 7/1/2021
 */

@Getter
public class VpnManager {

    public static final String API_KEY = CorePlugin.getInstance().getApiKey();
    public static final String API_FORMAT = "http://api.vpnblocker.net/v2/json/%s/" + VpnManager.API_KEY;

    @SneakyThrows
    public VpnRequestData fetchVpnData(String ipv4Address) {
        final HttpResponse<JsonNode> request = Unirest.get(String.format(API_FORMAT, ipv4Address)).asJsonAsync().get();

        return CorePlugin.GSON.fromJson(request.getBody().toString(), VpnRequestData.class);
    }
}
