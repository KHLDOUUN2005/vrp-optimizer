package io.github.khaldouun2005.vrp.api;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {
    @GetMapping("/api/me")
    public Map<String, String> me(Authentication auth) {
        return Map.of("email", auth.getName());
    }
}
