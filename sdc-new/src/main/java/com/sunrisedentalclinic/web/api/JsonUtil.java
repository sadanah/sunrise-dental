package com.sunrisedentalclinic.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtil() {}

    public static void writeJson(HttpServletResponse response, int statusCode, Object body) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    public static <T> T readJson(javax.servlet.http.HttpServletRequest request, Class<T> clazz) throws IOException {
        return mapper.readValue(request.getReader(), clazz);
    }
}