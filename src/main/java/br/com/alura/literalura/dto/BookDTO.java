package br.com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDTO(@JsonAlias("id") Long id,
                      @JsonAlias("title") String title,
                      @JsonAlias("authors") List<AuthorDTO> authors,
                      @JsonAlias("download_count") int downloadCount,
                      @JsonAlias("languages") List<String> languages) {
}
