package br.com.alura.literalura;

import br.com.alura.literalura.main.MainProgram;
import br.com.alura.literalura.repository.AuthorRepository;
import br.com.alura.literalura.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;


    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MainProgram main = new MainProgram(bookRepository, authorRepository);
        main.showMenu();
    }
}
