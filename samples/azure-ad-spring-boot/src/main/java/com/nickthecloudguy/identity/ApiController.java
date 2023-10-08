package com.nickthecloudguy.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('APPROLE_Admin')")
    public String admin() {
        log.info("admin");
        return "Welcome to the admin page!";
    }

    @GetMapping("/readonly")
    @PreAuthorize("hasAuthority('APPROLE_ReadOnly')")
    public String readonly() {
        log.info("readonly");
        return "Welcome to the readonly page!";
    }

}
