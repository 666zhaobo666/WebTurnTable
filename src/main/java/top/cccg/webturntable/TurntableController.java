package top.cccg.webturntable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turntable")
public class TurntableController {
    private final TurntableService service;

    @Value("${turntable.admin.password}")
    private String adminPassword;

    public TurntableController(TurntableService service) {
        this.service = service;
    }

    @GetMapping("/entries")
    public List<TurntableEntry> getEntries() {
        return service.getEntries();
    }

    @PostMapping("/entries")
    public ResponseEntity<?> addEntry(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        String password = body.get("password");
        if (!adminPassword.equals(password)) {
            return ResponseEntity.status(403).body("密码错误");
        }
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("条目内容不能为空");
        }
        return ResponseEntity.ok(service.addEntry(text.trim()));
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id, @RequestParam String password) {
        if (!adminPassword.equals(password)) {
            return ResponseEntity.status(403).body("密码错误");
        }
        boolean removed = service.deleteEntry(id);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

