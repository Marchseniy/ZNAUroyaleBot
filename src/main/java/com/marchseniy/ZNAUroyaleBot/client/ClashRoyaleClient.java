package com.marchseniy.ZNAUroyaleBot.client;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.*;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.concurrent.CompletableFuture;

@Component
@PropertySource("application.properties")
public class ClashRoyaleClient extends Client<ClashRoyaleApi> {
    private static final String BASE_URL = "https://api.clashroyale.com/v1/";
    private final String royaleToken;

    @Autowired
    public ClashRoyaleClient(ClashRoyaleConfig clashRoyaleConfig) {
        royaleToken = clashRoyaleConfig.getToken();
    }

    @Override
    protected String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    protected Class<ClashRoyaleApi> getService() {
        return ClashRoyaleApi.class;
    }

    public CompletableFuture<Player> getPlayer(String playerTag) {
        Call<Player> call = service.getPlayer(getTokenTxt(), playerTag);
        return getFutureObject(call);
    }

    public CompletableFuture<Clan> getClan(String clanTag) {
        Call<Clan> call = service.getClan(getTokenTxt(), clanTag);
        return getFutureObject(call);
    }

    public CompletableFuture<RiverRace> getRiverRace(String clanTag) {
        Call<RiverRace> call = service.getRiverRace(getTokenTxt(), clanTag);
        return getFutureObject(call);
    }

    public CompletableFuture<RiverRaceLog> getRiverRaceLog(String clanTag) {
        Call<RiverRaceLog> call = service.getRiverRaceLog(getTokenTxt(), clanTag);
        return getFutureObject(call);
    }

    public CompletableFuture<UpcomingChests> getUpcomingChests(String playerTag) {
        Call<UpcomingChests> call = service.getUpcomingChests(getTokenTxt(), playerTag);
        return getFutureObject(call);
    }

    private String getTokenTxt() {
        return "Bearer " + royaleToken;
    }
}