package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class PersonServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=utf-8");

        try {
            // Parse JSON body into Map
            Map<String, Object> body = mapper.readValue(req.getInputStream(), Map.class);

            String firstName = str(body.get("firstName"));
            String lastName  = str(body.get("lastName"));

            // basic validation
            if (firstName.isBlank() || lastName.isBlank()) {
                resp.setStatus(400);
                mapper.writeValue(resp.getOutputStream(), Map.of(
                        "ok", false,
                        "message", "firstName and lastName are required."
                ));
                return;
            }

            // normalize: trim + capitalize-ish (simple)
            firstName = normalize(firstName);
            lastName  = normalize(lastName);

            long id = Db.insertPerson(firstName, lastName);

            resp.setStatus(201);
            mapper.writeValue(resp.getOutputStream(), Map.of(
                    "ok", true,
                    "id", id,
                    "fullName", firstName + " " + lastName
            ));

        } catch (Exception e) {
            resp.setStatus(500);
            mapper.writeValue(resp.getOutputStream(), Map.of(
                    "ok", false,
                    "message", "Server error. Check logs."
            ));
            e.printStackTrace();
        }
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static String normalize(String s) {
        s = s.trim();
        if (s.isEmpty()) return s;
        // very simple capitalization: "iAn" -> "Ian"
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
