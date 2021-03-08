package com.solexgames.proxy;

import com.solexgames.CorePlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
public class ProxyManager {

    private final ArrayList<Proxy> proxies = new ArrayList<>();

    public Proxy getByRegion(String region){
        return this.proxies.stream()
                .filter(proxy -> proxy.getRegion().equalsIgnoreCase(region))
                .findFirst()
                .orElse(null);
    }

    public int getOnlineAllProxies() {
        int[] online = new int[1];
        this.proxies.forEach(proxy -> online[0] = online[0] + proxy.getOnlinePlayers());
        return online[0];
    }
}
