package Config;

public class CompanyConfig {

    private static final String BUSINESS_NAME = "KML LOGISTICS S.A.C.";
    private static final String TRADE_NAME = "KML Logistics";
    private static final String RUC = "20601234567";
    private static final String ADDRESS = "Av. Javier Prado Este 1234 - San Isidro - Lima";
    private static final String PHONE = "(01) 555-0101";
    private static final String EMAIL = "ventas@kmllogistics.pe";
    private static final String FOOTER_TEXT = "Gracias por su compra";

    private CompanyConfig() {
    }

    public static String getBusinessName() {
        return BUSINESS_NAME;
    }

    public static String getTradeName() {
        return TRADE_NAME;
    }

    public static String getRuc() {
        return RUC;
    }

    public static String getAddress() {
        return ADDRESS;
    }

    public static String getPhone() {
        return PHONE;
    }

    public static String getEmail() {
        return EMAIL;
    }

    public static String getFooterText() {
        return FOOTER_TEXT;
    }
}
