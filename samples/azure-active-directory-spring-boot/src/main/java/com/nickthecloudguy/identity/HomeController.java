package com.nickthecloudguy.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String index() {
        return "Welcome to the home page!";
    }

    @GetMapping("/whoami")
    public Identity whoami(@AuthenticationPrincipal OidcUser principal) {
        log.info("whoami");

        List<String> claimsList = new ArrayList<>();
        principal.getClaims().forEach((key, value) -> claimsList.add(key + "=" + value));

        List<String> authorities = principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        return new Identity(principal.getName(), claimsList, authorities);
    }

}
