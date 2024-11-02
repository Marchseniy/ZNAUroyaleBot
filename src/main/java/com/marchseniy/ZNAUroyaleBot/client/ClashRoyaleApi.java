package com.marchseniy.ZNAUroyaleBot.client;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ClashRoyaleApi extends ApiService {
    @GET("players/{playerTag}")
    Call<Player> getPlayer(@Header("Authorization") String token, @Path("playerTag") String playerTag);

    @GET("clans/{clanTag}")
    Call<Clan> getClan(@Header("Authorization") String token, @Path("clanTag") String clanTag);

    @GET("clans/{clanTag}/currentriverrace")
    Call<RiverRace> getRiverRace(@Header("Authorization") String token, @Path("clanTag") String clanTag);

    @GET("clans/{clanTag}/riverracelog")
    Call<RiverRaceLog> getRiverRaceLog(@Header("Authorization") String token, @Path("clanTag") String clanTag);

    @GET("players/{playerTag}/upcomingchests")
    Call<UpcomingChests> getUpcomingChests(@Header("Authorization") String token, @Path("playerTag") String playerTag);
}
