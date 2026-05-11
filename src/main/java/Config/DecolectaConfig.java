package Config;

public class DecolectaConfig {

    private static final String DEFAULT_BASE_URL = "https://api.decolecta.com";
    private static final String PLACEHOLDER_BEARER_TOKEN = "COLOCA_AQUI_TU_BEARER_TOKEN";
    private static final String DEFAULT_BEARER_TOKEN = "sk_15366.da9yyVbawV67SYpAJeglZSix9Cryle90";
    private static final int TIMEOUT_SECONDS = 15;

    private DecolectaConfig() {
    }

    public static String getBaseUrl() {
        String baseUrl = System.getenv("DECOLECTA_BASE_URL");

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return DEFAULT_BASE_URL;
        }

        return baseUrl.trim();
    }

    public static String getBearerToken() {
        String envToken = System.getenv("DECOLECTA_BEARER_TOKEN");

        if (envToken != null && !envToken.trim().isEmpty()) {
            return envToken.trim();
        }

        return DEFAULT_BEARER_TOKEN;
    }

    public static int getTimeoutSeconds() {
        return TIMEOUT_SECONDS;
    }

    public static boolean hasTokenConfigured() {
        String envToken = System.getenv("DECOLECTA_BEARER_TOKEN");

        if (envToken != null && !envToken.trim().isEmpty()) {
            return true;
        }

        return DEFAULT_BEARER_TOKEN != null
                && !DEFAULT_BEARER_TOKEN.trim().isEmpty()
                && !PLACEHOLDER_BEARER_TOKEN.equals(DEFAULT_BEARER_TOKEN.trim());
    }
}
