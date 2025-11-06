package org.techcorp.service;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.techcorp.exception.ApiException;
import org.techcorp.models.Employee;
import org.techcorp.models.enums.Position;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiUrl;

    public ApiService(HttpClient httpClient, Gson gson, @Value("${app.api.url}") String apiUrl) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.apiUrl = apiUrl;
    }

    public List<Employee> fetchEmployeesFromApi() throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException("HTTP Error: " + response.statusCode());
            }

            JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
            List<Employee> employees = new ArrayList<>();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                String[] nameParts = obj.get("name").getAsString().split(" ", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                String email = obj.get("email").getAsString();

                JsonObject companyObj = obj.getAsJsonObject("company");
                String company = companyObj != null && companyObj.has("name")
                        ? companyObj.get("name").getAsString()
                        : "Unknown";

                employees.add(new Employee(
                        firstName + " " + lastName,
                        email,
                        company,
                        Position.PROGRAMISTA,
                        Position.PROGRAMISTA.getBaseSalary()
                ));
            }

            return employees;

        } catch (IOException | InterruptedException | IllegalStateException e) {
            Thread.currentThread().interrupt(); // zgodnie z best practice
            throw new ApiException("Error fetching employees from API", e);
        }
    }
}