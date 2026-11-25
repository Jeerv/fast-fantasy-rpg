package Api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final ApiService service;

    public ApiController(ApiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestParam String path,
                                 @RequestParam(required = false) String search,
                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        return switch (path) {
            case "health" -> ResponseEntity.ok(service.health());
            case "auth/me" -> ResponseEntity.ok(service.currentUser(bearer(authorization)));
            case "monsters" -> ResponseEntity.ok(service.listMonsters(bearer(authorization), search));
            case "state" -> ResponseEntity.ok(service.loadState(bearer(authorization)));
            case "campaigns" -> ResponseEntity.ok(service.listCampaigns(bearer(authorization)));
            case "campaign" -> ResponseEntity.ok(service.getCampaignByCode(bearer(authorization), search));
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rota não encontrada."));
        };
    }

    @PostMapping
    public ResponseEntity<?> postForm(@RequestParam String path,
                                      @RequestParam MultiValueMap<String, String> form,
                                      @RequestHeader(value = "Authorization", required = false) String authorization) {
        return switch (path) {
            case "auth/register" -> ResponseEntity.status(HttpStatus.CREATED).body(service.register(
                    form.getFirst("name"), form.getFirst("email"), form.getFirst("password")));
            case "auth/login" -> ResponseEntity.ok(service.login(form.getFirst("email"), form.getFirst("password")));
            case "auth/logout" -> {
                service.logout(bearer(authorization));
                yield ResponseEntity.ok(Map.of("message", "Sessão encerrada."));
            }
            case "campaign/create" -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.createCampaign(bearer(authorization), form.getFirst("table")));
            case "campaign/join" -> ResponseEntity.ok(service.joinCampaign(bearer(authorization), form.getFirst("code"),
                    form.getFirst("member"), form.getFirst("token")));
            case "campaign/leave" -> {
                service.leaveCampaign(bearer(authorization), form.getFirst("id"));
                yield ResponseEntity.ok(Map.of("left", true));
            }
            case "campaign/delete" -> {
                service.deleteCampaign(bearer(authorization), form.getFirst("id"));
                yield ResponseEntity.ok(Map.of("deleted", true));
            }
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rota não encontrada."));
        };
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putJson(@RequestParam String path,
                                     @RequestBody Map<String, Object> body,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        if ("state".equals(path)) {
            return ResponseEntity.ok(service.saveState(bearer(authorization), body));
        }
        if ("campaign/save".equals(path)) return ResponseEntity.ok(service.saveCampaign(bearer(authorization), body));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Rota não encontrada."));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> apiError(ApiException error) {
        return ResponseEntity.status(error.getStatus()).body(Map.of("message", error.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> serverError(Exception error) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Não foi possível concluir a solicitação."));
    }

    private String bearer(String header) {
        return header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)
                ? header.substring(7).trim() : "";
    }
}
