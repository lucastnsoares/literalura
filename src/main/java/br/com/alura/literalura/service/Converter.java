package br.com.alura.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;

public class Converter implements IConverter{
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T getData(String json, Class<T> className) {
        try {
            return mapper.readValue(json, className);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter.");
        }
    }

    public <T, R> List<R> dtoToEntity(List<T> list, Class<R> classToConverter) {
        try {
            return list.stream()
                    .map(element -> mapper.convertValue(element, classToConverter))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter.");

        }

    }
}
