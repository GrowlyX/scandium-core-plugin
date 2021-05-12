package com.solexgames.xenon.proxy;

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
        return this.proxies.stream().mapToInt(Proxy::getOnlinePlayers).sum();
    }
}
