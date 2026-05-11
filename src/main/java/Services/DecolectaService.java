package Services;

import Config.DecolectaConfig;
import Models.ReniecPerson;
import Models.SunatCompany;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecolectaService {

    private final HttpClient httpClient;

    public DecolectaService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(DecolectaConfig.getTimeoutSeconds()))
                .build();
    }

    public ReniecPerson findByDni(String dni) throws Exception {
        validateConfig();

        String normalizedDni = normalizeDni(dni);
        String endpoint = DecolectaConfig.getBaseUrl()
                + "/v1/reniec/dni?numero="
                + URLEncoder.encode(normalizedDni, StandardCharsets.UTF_8);
        String responseBody = sendGetRequest(endpoint);

        ReniecPerson person = new ReniecPerson();
        person.setFirstName(extractValue(responseBody, "first_name"));
        person.setFirstLastName(extractValue(responseBody, "first_last_name"));
        person.setSecondLastName(extractValue(responseBody, "second_last_name"));
        person.setFullName(extractValue(responseBody, "full_name"));
        person.setDocumentNumber(extractValue(responseBody, "document_number"));

        if (isBlank(person.getFirstName())
                && isBlank(person.getFirstLastName())
                && isBlank(person.getSecondLastName())) {
            throw new Exception("No se encontraron datos para el DNI ingresado.");
        }

        return person;
    }

    public SunatCompany findByRuc(String ruc) throws Exception {
        validateConfig();

        String normalizedRuc = normalizeRuc(ruc);
        String endpoint = DecolectaConfig.getBaseUrl()
                + "/v1/sunat/ruc?numero="
                + URLEncoder.encode(normalizedRuc, StandardCharsets.UTF_8);
        String responseBody = sendGetRequest(endpoint);

        SunatCompany company = new SunatCompany();
        company.setBusinessName(extractValue(responseBody, "razon_social"));
        company.setDocumentNumber(extractValue(responseBody, "numero_documento"));
        company.setAddress(extractValue(responseBody, "direccion"));
        company.setDistrict(extractValue(responseBody, "distrito"));
        company.setProvince(extractValue(responseBody, "provincia"));
        company.setDepartment(extractValue(responseBody, "departamento"));

        if (isBlank(company.getBusinessName())) {
            throw new Exception("No se encontraron datos para el RUC ingresado.");
        }

        return company;
    }

    private void validateConfig() throws Exception {
        if (!DecolectaConfig.hasTokenConfigured()) {
            throw new Exception("Configure su bearer token en Config/DecolectaConfig.java o en la variable DECOLECTA_BEARER_TOKEN.");
        }
    }

    private String normalizeDni(String dni) throws Exception {
        String value = dni == null ? "" : dni.trim();

        if (!value.matches("\\d{8}")) {
            throw new Exception("Ingrese un DNI valido de 8 digitos.");
        }

        return value;
    }

    private String normalizeRuc(String ruc) throws Exception {
        String value = ruc == null ? "" : ruc.trim();

        if (!value.matches("\\d{11}")) {
            throw new Exception("Ingrese un RUC valido de 11 digitos.");
        }

        return value;
    }

    private String sendGetRequest(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(DecolectaConfig.getTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + DecolectaConfig.getBearerToken())
                .GET()
                .build();

        HttpResponse<String> response;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new Exception("No se pudo consultar Decolecta. Verifique su conexion o el token configurado.");
        }

        String responseBody = response.body() == null ? "" : response.body().trim();

        if (response.statusCode() != 200) {
            String apiError = extractValue(responseBody, "error");

            if (apiError != null && !apiError.isBlank()) {
                throw new Exception("Decolecta respondio: " + apiError);
            }

            throw new Exception("Decolecta respondio con estado HTTP " + response.statusCode() + ".");
        }

        return responseBody;
    }

    private String extractValue(String json, String key) {
        if (json == null || json.isBlank()) {
            return null;
        }

        String pattern = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);

        if (!matcher.find()) {
            return null;
        }

        return unescapeJson(matcher.group(1));
    }

    private String unescapeJson(String value) {
        String result = value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");

        Matcher unicodeMatcher = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(result);
        StringBuffer buffer = new StringBuffer();

        while (unicodeMatcher.find()) {
            char unicodeChar = (char) Integer.parseInt(unicodeMatcher.group(1), 16);
            unicodeMatcher.appendReplacement(buffer, Matcher.quoteReplacement(String.valueOf(unicodeChar)));
        }

        unicodeMatcher.appendTail(buffer);
        return buffer.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
