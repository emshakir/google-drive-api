package com.google.drive.controller;

import com.google.drive.config.*;
import com.google.drive.service.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@Controller
public class AuthorisationController {

    private final GoogleDriveConfig config;
    private final GoogleDriveService service;

    public AuthorisationController(GoogleDriveConfig config, GoogleDriveService service) {
        this.config = config;
        this.service = service;
    }


    @GetMapping("/")
    public String indexPage() {
        return "index.html";
    }

    @PostMapping("/google/signin")
    public @ResponseBody
    String doSignIntoGoogle(@RequestParam("userName") String userName) throws IOException {
        config.init();
        String authorisationUrl = config.createAuthorizationUrl(userName);
        return authorisationUrl;

    }

    @GetMapping("/oauth")
    public String storeCredential(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String username = new String(Base64.getDecoder().decode(request.getParameter("state")));
        String page = "redirect:/google/signin";
        if (code != null) {
            String accessToken = config.storeCredential(code, username);
            page = "redirect:/files/" + username;
        }
        return page;
    }

    @GetMapping("/files/{userName}")
    public List<Object> listFiles(@PathVariable String userName, HttpServletRequest request) throws IOException {
        List<Object> objectList = service.listFiles(userName);
        return objectList;
    }
}
