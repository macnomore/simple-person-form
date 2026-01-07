package com.example;

import java.sql.*;

public class Db {

    // Read from environment variables (DON'T hardcode secrets)
    private static final String DB_URL = mustGetEnv("DB_URL"); // e.g. jdbc:postgresql://host:5432/dbname
    private static final String DB_USER = mustGetEnv("DB_USER");
    private static final String DB_PASS = mustGetEnv("DB_PASS");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void init() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS people (
                      id BIGSERIAL PRIMARY KEY,
                      first_name TEXT NOT NULL,
                      last_name  TEXT NOT NULL,
                      created_at TIMESTAMPTZ NOT NULL DEFAULT now()
                    );
                """;

        try (Connection conn = getConnection();
                Statement st = conn.createStatement()) {
            st.execute(sql);
            System.out.println("DB ready: table people is available.");
        } catch (Exception e) {
            System.err.println("DB init failed. Check DB_URL/DB_USER/DB_PASS.");
            e.printStackTrace();
        }
    }

    public static long insertPerson(String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO people(first_name, last_name) VALUES (?, ?) RETURNING id";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private static String mustGetEnv(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required env var: " + key);
        }
        return v;
    }
}
