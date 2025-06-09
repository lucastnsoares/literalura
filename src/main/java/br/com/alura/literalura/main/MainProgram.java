package br.com.alura.literalura.main;

import br.com.alura.literalura.dto.BookDTO;
import br.com.alura.literalura.dto.ResultDTO;
import br.com.alura.literalura.model.Author;
import br.com.alura.literalura.model.Book;
import br.com.alura.literalura.repository.AuthorRepository;
import br.com.alura.literalura.repository.BookRepository;
import br.com.alura.literalura.service.Converter;
import br.com.alura.literalura.service.DataApi;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainProgram {
    private final Scanner scanner = new Scanner(System.in);
    private final String queryAdress = "https://gutendex.com/books/?search=";
    private final Converter converter = new Converter();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public MainProgram(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void showMenu() {
        System.out.println("----------- BEM VINDO AO LITERALURA! -----------");
        int option = -1;
        while (option != 0) {
            System.out.print("""
                ******************************
                MENU DE OPÇÕES
                
                1 - Buscar livro pelo título
                2 - Listar livros registrados no banco de dados
                3 - Listar autores registrados no banco de dados
                4 - Listar autores vivos em um determinado ano
                5 - Listar livros de um determinado idioma
                0 - Sair da aplicação
                
                """);
            System.out.print("Escolha o número da opção desejada: ");
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException e){
                option = -1;
                scanner.next();
            }
            scanner.nextLine();
            switch (option) {
                case 1:
                    searchBookByTitle();
                    break;
                case 2:
                    listAllBooksFromDB();
                    break;
                case 3:
                    listAllAuthorsFromDB();
                    break;
                case 4:
                    findAuthorsAliveInAGivenYear();
                    break;
                case 5:
                    searchBooksByLanguage();
                    break;
                case 0:
                    System.out.println("Encerrando aplicação...");
                    break;
                default:
                    System.out.println("Opção inválida! Escolha uma opção ente 1 e 5.");
            }
            pressAnyKeyToContinue();
        }
    }

    private void searchBookByTitle() {
        System.out.print("Insira o título do livro para buscar: ");
        String query = scanner.nextLine().trim().replaceAll(" ", "+");
        scanner.next();
        String json = DataApi.getData(queryAdress + query);
        if (json != null) {
            List<BookDTO> bookDTO = converter.getData(json, ResultDTO.class).results();
            if (bookDTO.isEmpty()) {
                System.out.println("Nenhum livro encontrado na API");
            } else {
                List<Book> books = converter.dtoToEntity(bookDTO, Book.class);
                books.forEach(b -> saveBookOnDB(b));
                System.out.println("**** Livros encontrados na API *****");
                books.forEach(System.out::println);
            }
        }
    }

    private void saveBookOnDB(Book book) {
        List<Author> authors = book.getAuthors().stream()
                .map(a -> saveAuthorOnDB(a))
                .collect(Collectors.toList());
        book.setAuthors(authors);
        String titleBook = book.getTitle();
        Optional<Book> bookOptional = bookRepository.findByTitle(titleBook);
        if (bookOptional.isEmpty()){
            bookRepository.save(book);
        }
    }

    private Author saveAuthorOnDB(Author author) {
        String nameAuthor = author.getName();
        Optional<Author> authorOptional = authorRepository.findByName(nameAuthor);
        if (authorOptional.isPresent()) {
            return authorOptional.get();
        } else {
            authorRepository.save(author);
            return author;
        }
    }

    private void listAllBooksFromDB() {
        List<Book> books = bookRepository.findAll();
        books.forEach(System.out::println);
    }

    private void listAllAuthorsFromDB() {
        List<Author> authors = authorRepository.findAll();
        authors.forEach(System.out::println);
    }

    private void findAuthorsAliveInAGivenYear() {
        System.out.print("Digite um ano para pesquisar: ");
        int query = scanner.nextInt();
        scanner.next();
        List<Author> authorsAlive = authorRepository.findAuthorsAlive(query);
        if(authorsAlive.isEmpty()) {
            System.out.println("Nenhum autor encontrado no banco de dados que esteja vivo em " + query);
        } else {
            System.out.println("******* Lista de autores vivos em " + query + " *******");
            authorsAlive.forEach(System.out::println);
        }
    }

    private void searchBooksByLanguage() {
        System.out.print("Digite a sigla de um idioma para pesquisar: ");
        String query = scanner.nextLine();
        scanner.next();
        List<Book> books = bookRepository.findBooksByLanguages(query);
        if (books.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o idioma informado!");
        } else {
            System.out.println("***** Lista de livros com o idioma informado ******");
            books.forEach(System.out::println);
        }
    }

    private void pressAnyKeyToContinue() {
        System.out.println("Pressione qualquer tecla para continuar.");
        scanner.nextLine();
    }
}
