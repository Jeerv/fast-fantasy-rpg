package View;

import Api.FastFantasyApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/** Janela desktop que inicia o servidor local e exibe o front do RPG. */
public class MainView extends Application {
    private static final String DEFAULT_WEB_ROOT = "C:\\xampp\\htdocs\\fast_fantasy_rpg";
    private static final String DEFAULT_PHP = "C:\\xampp\\php\\php.exe";
    private static final int DEFAULT_PORT = 8090;

    private Process phpServer;
    private ConfigurableApplicationContext apiContext;

    @Override
    public void start(Stage stage) {
        Label status = new Label("Preparando o Fast Fantasy RPG...");
        status.getStyleClass().add("status");

        WebView browser = new WebView();
        WebEngine engine = browser.getEngine();
        engine.setOnError(event -> status.setText("Erro ao carregar o front: " + event.getMessage()));
        engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                status.setText("Fast Fantasy RPG está pronto.");
            } else if (newState == Worker.State.FAILED) {
                status.setText("Não foi possível carregar o front. Confira se o XAMPP está instalado.");
            }
        });

        Button reload = new Button("Recarregar");
        reload.setOnAction(event -> engine.reload());
        HBox toolbar = new HBox(12, status, reload);
        toolbar.getStyleClass().add("toolbar");
        HBox.setHgrow(status, Priority.ALWAYS);

        BorderPane root = new BorderPane(browser);
        root.setTop(toolbar);
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-background-color: #f4eee5;");
        Scene scene = new Scene(root, 1280, 820);
        stage.setTitle("Fast Fantasy RPG");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> stopWebServer());
        stage.show();

        String hostedFront = System.getenv("FAST_FANTASY_FRONT_URL");
        if (hostedFront != null && !hostedFront.isBlank()) {
            status.setText("Conectando ao Fast Fantasy RPG na nuvem...");
            String hostedApi = System.getenv("FAST_FANTASY_API_URL");
            engine.load(hostedApi == null || hostedApi.isBlank()
                    ? hostedFront
                    : addApiOverride(hostedFront, hostedApi));
            return;
        }
        startLocalApi(engine, status);
    }

    private String addApiOverride(String frontUrl, String apiUrl) {
        URI front = URI.create(frontUrl);
        int fragmentIndex = frontUrl.indexOf('#');
        String base = fragmentIndex < 0 ? frontUrl : frontUrl.substring(0, fragmentIndex);
        String fragment = fragmentIndex < 0 ? "" : frontUrl.substring(fragmentIndex);
        String separator = front.getRawQuery() == null ? "?" : "&";
        return base + separator + "api="
                + URLEncoder.encode(apiUrl, StandardCharsets.UTF_8) + fragment;
    }

    private void startLocalApi(WebEngine engine, Label status) {
        status.setText("Iniciando o back-end Java...");
        CompletableFuture.supplyAsync(() -> SpringApplication.run(
                FastFantasyApplication.class,
                "--server.port=8081",
                "--server.address=127.0.0.1"))
                .whenComplete((context, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        status.setText("Não foi possível iniciar o back-end. Confira o MySQL e a variável DB_PASSWORD.");
                        return;
                    }
                    apiContext = context;
                    startWebServer(engine, status);
                }));
    }

    private void startWebServer(WebEngine engine, Label status) {
        Path webRoot = Path.of(System.getenv().getOrDefault("FAST_FANTASY_WEB_ROOT", DEFAULT_WEB_ROOT));
        Path php = Path.of(System.getenv().getOrDefault("FAST_FANTASY_PHP", DEFAULT_PHP));
        int port = readPort();

        if (!Files.isRegularFile(php) || !Files.isRegularFile(webRoot.resolve("index.php"))) {
            status.setText("Front ou PHP não encontrado. Configure FAST_FANTASY_WEB_ROOT e FAST_FANTASY_PHP.");
            return;
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    php.toString(), "-S", "127.0.0.1:" + port, "-t", webRoot.toString());
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            phpServer = builder.start();
        } catch (IOException error) {
            status.setText("Não foi possível iniciar o servidor PHP: " + error.getMessage());
            return;
        }

        String api = URLEncoder.encode("http://127.0.0.1:8081/api", StandardCharsets.UTF_8);
        URI frontUri = URI.create("http://127.0.0.1:" + port + "/index.php?api=" + api);
        status.setText("Iniciando o front local...");
        waitForServer(frontUri).whenComplete((ready, error) -> Platform.runLater(() -> {
            if (error != null || !Boolean.TRUE.equals(ready)) {
                status.setText("O servidor PHP não respondeu. Verifique a instalação do XAMPP.");
                return;
            }
            engine.load(frontUri.toString());
        }));
    }

    private CompletableFuture<Boolean> waitForServer(URI uri) {
        return CompletableFuture.supplyAsync(() -> {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();
            for (int attempt = 0; attempt < 30; attempt++) {
                if (phpServer == null || !phpServer.isAlive()) return false;
                try {
                    HttpRequest request = HttpRequest.newBuilder(uri)
                            .timeout(Duration.ofSeconds(2))
                            .GET()
                            .build();
                    HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                    if (response.statusCode() >= 200 && response.statusCode() < 500) return true;
                } catch (Exception ignored) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
            return false;
        });
    }

    private int readPort() {
        String configured = System.getenv("FAST_FANTASY_PORT");
        if (configured == null || configured.isBlank()) return DEFAULT_PORT;
        try {
            int port = Integer.parseInt(configured);
            return port >= 1024 && port <= 65535 ? port : DEFAULT_PORT;
        } catch (NumberFormatException ignored) {
            return DEFAULT_PORT;
        }
    }

    private void stopWebServer() {
        if (phpServer != null && phpServer.isAlive()) {
            phpServer.destroy();
        }
        if (apiContext != null) {
            apiContext.close();
            apiContext = null;
        }
    }

    @Override
    public void stop() {
        stopWebServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
