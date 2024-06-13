package com.distribuida;

import com.distribuida.db.Books;
import com.distribuida.service.BooksService;
import com.google.gson.Gson;
import io.helidon.webserver.*;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.enterprise.inject.spi.CDI;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import java.util.List;


public class Main {

    private static ContainerLifecycle lifecycle = null;

    private static void listarTodos(ServerRequest req, ServerResponse res, BooksService booksService) {
        List<Books> books = booksService.findAll();

        if (books.isEmpty()) {
            res.send("No existen libros.");
        } else {
            res.send(new Gson().toJson(books));
        }
    }

    private static void buscarPorId(ServerRequest req, ServerResponse res, BooksService booksService) {
        Books book = booksService.findById(Integer.valueOf(req.path().pathParameters().get("id")));

        if(book == null){
            res.send("No existe libro con ese id.");
        } else {
            res.send(new Gson().toJson(book));
        }
    }

    private static void insertar(ServerRequest req, ServerResponse res, BooksService booksService) {
        String body = req.content().as(String.class);
        Books book = new Gson().fromJson(body, Books.class);
        Integer insertado = booksService.insert(book);

        if (insertado > 0) {
            res.send(new Gson().toJson(book));
        } else {
            res.send("No se pudo insertar.");
        }
    }

    private static void actualizar(ServerRequest req, ServerResponse res, BooksService booksService) {
        String body = req.content().as(String.class);
        Books book = new Gson().fromJson(body, Books.class);
        book.setId(Integer.valueOf(req.path().pathParameters().get("id")));
        Boolean actualizado = booksService.update(book);

        if(actualizado) {
            res.send(new Gson().toJson(book));
        } else {
            res.send("Error al actualizar el libro.");
        }
    }

    private static void eliminar(ServerRequest req, ServerResponse res, BooksService booksService) {
        Boolean eliminado = booksService.delete(Integer.valueOf(req.path().pathParameters().get("id")));
        if(eliminado) {
            res.send("Eliminado Exitoso");
        } else {
            res.send("Error al eliminar");
        }
    }

    public static void main(String[] args) {
        lifecycle = WebBeansContext.getInstance().getService(ContainerLifecycle.class);
        lifecycle.startApplication(null);

        BooksService booksService = CDI.current().select(BooksService.class).get();

        booksService.findAll();

        WebServer server = WebServer.builder().port(8080).routing(b -> b
                .get("/books", (req, res) -> listarTodos(req, res, booksService))
                .get("/books/{id}", (req, res) -> buscarPorId(req, res, booksService))
                .post("/books", (req, res) -> insertar(req, res, booksService))
                .put("/books/{id}", (req, res) -> actualizar(req, res, booksService))
                .delete("/books/{id}", (req, res) -> eliminar(req, res, booksService))
                ).build();

        server.start();

        shutdown();
    }

    public static void shutdown()  {
        lifecycle.stopApplication(null);
    }
}