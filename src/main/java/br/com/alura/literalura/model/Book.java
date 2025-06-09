package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    private Long id;

    @Column(unique = true)
    private String title;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> languages;

    private int downloadCount;

    public Book() {
    }

    public Book(Long id, String title, List<Author> authors, int downloadCount) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.downloadCount = downloadCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        String nameAuthors = (authors != null)
                ? authors.stream()
                .map(Author::getName)  // só imprime os nomes, evita chamar o toString inteiro
                .collect(Collectors.joining("/ "))
                : "Nenhum autor";
        return String.format("""
                Livro: %s
                Autor(es): %s
                Idiomas: %s
                Número de downloads: %d
                """, title, nameAuthors, languages, downloadCount);

    }
}
