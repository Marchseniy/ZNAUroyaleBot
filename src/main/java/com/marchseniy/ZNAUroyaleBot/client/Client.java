package com.marchseniy.ZNAUroyaleBot.client;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class Client<S extends ApiService> {
    protected final S service;

    public Client() {
        service = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(getService());
    }

    protected abstract String getBaseUrl();

    protected abstract Class<S> getService();

    protected <T> CompletableFuture<T> getFutureObject(Call<T> call) {
        CompletableFuture<T> future = new CompletableFuture<>();

        call.enqueue(getCallback(future));

        return future;
    }

    private <T> Callback<T> getCallback(CompletableFuture<T> future) {
        return new Callback<>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                onResponseHandler(response, future);
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        };
    }

    private <T> void onResponseHandler(Response<T> response, CompletableFuture<T> future) {
        if (response.isSuccessful() && response.body() != null) {
            future.complete(response.body());
        } else {
            onErrorHandler(future, response);
        }
    }

    private <T> void onErrorHandler(CompletableFuture<T> future, Response<T> response) {
        String errorMessage = "Error code: " + response.code();
        String errorBody = null;

        if (response.errorBody() != null) {
            try {
                errorBody = response.errorBody().string();
                errorMessage += ", Error body: " + errorBody;
            } catch (IOException e) {
                errorMessage += ", Error reading error body";
            }
        }

        future.completeExceptionally(new RuntimeException(errorMessage));
    }
}
