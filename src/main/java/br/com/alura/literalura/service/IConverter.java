package br.com.alura.literalura.service;

public interface IConverter {
    <T> T getData(String json, Class<T> className);
}
