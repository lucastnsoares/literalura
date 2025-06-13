package br.com.alura.literalura.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

public class DataApi {
    public static String getData(String url) {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Erro ao obter dados da API.");
            } else {
                return response.body();
            }

        }
        catch (HttpTimeoutException e) {
            System.out.println("Erro. Tempo de resposta da API excedido!");;
        }
        catch (ConnectException e) {
            System.out.println("Erro. Verifique sua conexão!");
        }
        catch (IOException | InterruptedException | RuntimeException e) {
            System.out.println("Erro ao obter dados da API.");
        }
        return null;
    }
}
