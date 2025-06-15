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
    private final Converter converter = new Converter();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public MainProgram(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void showMenu() {
        System.out.print("""
                
                ----------- BEM VINDO(A) AO APLICATIVO LITERALURA! -----------
                
                """);
        int option = -1;
        while (option != 0) {
            System.out.print("""
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
            } catch (InputMismatchException e) {
                option = -1;
            }
            scanner.nextLine();
            switch (option) {
                case 1:
                    searchBookByTitle();
                    pressAnyKeyToContinue();
                    break;
                case 2:
                    listAllBooksFromDB();
                    pressAnyKeyToContinue();
                    break;
                case 3:
                    listAllAuthorsFromDB();
                    pressAnyKeyToContinue();
                    break;
                case 4:
                    findAuthorsAliveInAGivenYear();
                    pressAnyKeyToContinue();
                    break;
                case 5:
                    searchBooksByLanguage();
                    pressAnyKeyToContinue();
                    break;
                case 0:
                    System.out.print("""
                            
                            
                            Encerrando aplicação...
                            
                            
                            """);
                    break;
                default:
                    System.out.println("Opção inválida! Escolha uma opção entre 1 e 5.");
            }
        }
    }

    private void searchBookByTitle() {
        System.out.print("Insira o título do livro para buscar: ");
        String query = scanner.nextLine().trim().replaceAll(" ", "+");
        String queryAdress = "https://gutendex.com/books/?search=";
        String json = DataApi.getData(queryAdress + query);
        if (json != null) {
            List<BookDTO> bookDTO = converter.getData(json, ResultDTO.class).results();
            if (bookDTO.isEmpty()) {
                System.out.print("""
                        
                        ---------------------------------------------------------------------------
                        Nenhum livro encontrado na API!
                        ---------------------------------------------------------------------------
                        
                        """);
            } else {
                List<Book> books = converter.dtoToEntity(bookDTO, Book.class);
                books.forEach(this::saveBookOnDB);
                System.out.print("""
                        
                        ---------------------------------------------------------------------------
                        ****** Livros encontrados na API ******
                        ---------------------------------------------------------------------------
                        
                        """);
                books.forEach(System.out::println);
                System.out.print("""
                        
                        ---------------------------------------------------------------------------
                        
                        """);
            }
        }
    }

    private void saveBookOnDB(Book book) {
        List<Author> authors = book.getAuthors().stream()
                .map(this::saveAuthorOnDB)
                .collect(Collectors.toList());
        book.setAuthors(authors);
        String titleBook = book.getTitle();
        Optional<Book> bookOptional = bookRepository.findByTitle(titleBook);
        if (bookOptional.isEmpty()) {
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
        if (books.isEmpty()) {
            System.out.print("""
                    
                    ---------------------------------------------------------------------------
                    Nenhum livro encontrado no banco de dados!
                    ---------------------------------------------------------------------------
                    
                    """);
        } else {
            System.out.print("""
                    
                    ---------------------------------------------------------------------------
                    ****** Lista contendo todos os livros salvos no banco de dados ******
                    ---------------------------------------------------------------------------
                    
                    """);
            books.forEach(System.out::println);
            System.out.printf("""
                    
                    ---------------------------------------------------------------------------
                    Total de livros armazenados no banco de dados: %d
                    ---------------------------------------------------------------------------
                    
                    """, books.size());
        }
    }

    private void listAllAuthorsFromDB() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            System.out.println("Nenhum autor encontrado no banco de dados.");
        } else {
            System.out.print("""
                    
                    ---------------------------------------------------------------------------
                    ****** Lista contendo todos os autores salvos no banco de dados ******
                    ---------------------------------------------------------------------------
                    
                    """);
            authors.forEach(System.out::println);
            System.out.printf("""
                    \s
                     ---------------------------------------------------------------------------
                     Total de autores armazenados no banco de dados: %d
                     ---------------------------------------------------------------------------
                                      \s
                    \s""", authors.size());
        }
    }

    private void findAuthorsAliveInAGivenYear() {
        int query = -1;
        boolean yearValid = false;
        while (!yearValid) {
            System.out.print("Digite um ano para pesquisar: ");
            try {
                query = scanner.nextInt();
                scanner.nextLine();
                yearValid = true;
            } catch (InputMismatchException e) {
                System.out.println("Erro. Ano inválido! Tente novamente!");
                scanner.nextLine();
            }

            if (yearValid) {
                List<Author> authorsAlive = authorRepository.findAuthorsAlive(query);
                if (authorsAlive.isEmpty()) {
                    System.out.print("""
                            
                            ---------------------------------------------------------------------------
                            Nenhum autor encontrado com o parâmetro informado!
                            ---------------------------------------------------------------------------
                            
                            """);
                } else {
                    System.out.printf("""
                            
                            ---------------------------------------------------------------------------
                            ****** Lista de autores vivos em %d ******
                            ---------------------------------------------------------------------------
                            
                            """, query);
                    authorsAlive.forEach(System.out::println);
                    System.out.printf("""
                            
                            ------------------------------------------------------------------------------
                            Total de autores armazenados no banco de dados e que estavam vivos em %d: %d
                            ------------------------------------------------------------------------------
                            
                            
                            """, query, authorsAlive.size());
                }
            }
        }
    }

    private void searchBooksByLanguage() {
        System.out.print("Digite a sigla de um idioma para pesquisar: ");
        String query = scanner.nextLine().trim().toLowerCase();
        List<Book> books = bookRepository.findBooksByLanguages(query);
        if (books.isEmpty()) {
            System.out.print("""
                    
                    ---------------------------------------------------------------------------
                    Nenhum autor encontrado com o idioma informado!
                    ---------------------------------------------------------------------------
                    
                    """);
        } else {
            System.out.println("""
                    
                    ----------------------------------------------------------------------------------
                    ****** Lista de livros armazenados no banco de dados com o idioma informado ******
                    ----------------------------------------------------------------------------------
                    
                    """);
            books.forEach(System.out::println);
            System.out.printf("""
                    
                    ---------------------------------------------------------------------------
                    Total de livros armazenados no banco de dados com o idioma pesquisado: %d
                    ---------------------------------------------------------------------------
                    
                    """, books.size());
        }
    }

    private void pressAnyKeyToContinue() {
        System.out.println("Pressione qualquer tecla para continuar.");
        scanner.nextLine();
    }
}
