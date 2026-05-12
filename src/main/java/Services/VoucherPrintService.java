package Services;

import Config.CompanyConfig;
import Models.Sale;
import Models.SaleDetail;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class VoucherPrintService {

    private static final String PDF24_KEYWORD = "PDF24";
    private static final double POINTS_PER_MM = 72d / 25.4d;
    private static final DateTimeFormatter SALE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    private static final int TICKET_WIDTH_MM = 80;
    private static final int TICKET_MARGIN_MM = 4;
    private static final int CONTENT_MAX_CHARS = 30;
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 10);
    private static final Font FONT_TEXT = new Font("SansSerif", Font.PLAIN, 9);
    private static final Font FONT_TEXT_BOLD = new Font("SansSerif", Font.BOLD, 9);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 8);
    private static final Font FONT_TOTAL = new Font("SansSerif", Font.BOLD, 12);

    public void printSale(Sale sale) throws Exception {
        if (sale == null) {
            throw new Exception("No se encontro la venta a imprimir.");
        }

        PrintService pdf24Service = findPdf24PrintService();

        if (pdf24Service == null) {
            throw new Exception("No se encontro una impresora PDF24 instalada.");
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(pdf24Service);
        job.setJobName(buildJobName(sale));
        job.setPrintable(new VoucherPrintable(sale), buildPageFormat(job, sale));
        job.print();
    }

    private PrintService findPdf24PrintService() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {
            if (service != null
                    && service.getName() != null
                    && service.getName().toUpperCase().contains(PDF24_KEYWORD)) {
                return service;
            }
        }

        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

        if (defaultService != null
                && defaultService.getName() != null
                && defaultService.getName().toUpperCase().contains(PDF24_KEYWORD)) {
            return defaultService;
        }

        return null;
    }

    private PageFormat buildPageFormat(PrinterJob job, Sale sale) {
        Paper paper = new Paper();
        double width = mmToPoints(TICKET_WIDTH_MM);
        double height = mmToPoints(estimateTicketHeightMm(sale));
        double margin = mmToPoints(TICKET_MARGIN_MM);

        paper.setSize(width, height);
        paper.setImageableArea(margin, margin, width - (margin * 2), height - (margin * 2));

        PageFormat format = job.defaultPage();
        format.setPaper(paper);
        format.setOrientation(PageFormat.PORTRAIT);
        return format;
    }

    private int estimateTicketHeightMm(Sale sale) {
        int lineCount = 0;

        lineCount += 8;
        lineCount += estimateWrappedLines(CompanyConfig.getAddress(), CONTENT_MAX_CHARS);
        lineCount += estimateWrappedLines(CompanyConfig.getEmail(), CONTENT_MAX_CHARS);
        lineCount += 2;
        lineCount += 6;

        lineCount += estimateWrappedLines(buildCustomerName(sale), CONTENT_MAX_CHARS);

        if (hasCustomerDocument(sale)) {
            lineCount += estimateWrappedLines(buildCustomerDocument(sale), CONTENT_MAX_CHARS);
        }

        List<SaleDetail> details = sale.getDetails();
        if (details != null && !details.isEmpty()) {
            for (SaleDetail detail : details) {
                lineCount += 2;
                lineCount += estimateWrappedLines(safe(detail.getProductName(), "-"), 22);
            }
        } else {
            lineCount += 1;
        }

        lineCount += 10;
        return Math.max(170, 22 + (lineCount * 5));
    }

    private int estimateWrappedLines(String text, int maxChars) {
        String value = safe(text, "");

        if (value.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((double) value.length() / Math.max(1, maxChars));
    }

    private String buildJobName(Sale sale) {
        return normalizeText(sale.getDocumentKind()) + "-" + normalizeText(sale.getVoucherCode());
    }

    private String normalizeText(String value) {
        return value == null || value.trim().isEmpty() ? "DOC" : value.trim();
    }

    private double mmToPoints(double mm) {
        return mm * POINTS_PER_MM;
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }

        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String safe(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private boolean hasCustomerDocument(Sale sale) {
        return sale.getCustomerDocumentTypeName() != null
                && !sale.getCustomerDocumentTypeName().trim().isEmpty()
                && sale.getCustomerDocumentNumber() != null
                && !sale.getCustomerDocumentNumber().trim().isEmpty();
    }

    private String buildCustomerDocument(Sale sale) {
        if (!hasCustomerDocument(sale)) {
            return "Sin documento";
        }

        return sale.getCustomerDocumentTypeName() + ": " + sale.getCustomerDocumentNumber();
    }

    private String buildCustomerName(Sale sale) {
        if (sale.getCustomerName() == null || sale.getCustomerName().trim().isEmpty()) {
            return "Consumidor final";
        }

        return sale.getCustomerName().trim();
    }

    private String resolveDocumentTitle(Sale sale) {
        String label = safe(sale.getDocumentLabel(), "");

        if (!label.isEmpty()) {
            return label.toUpperCase();
        }

        String kind = safe(sale.getDocumentKind(), "TICKET").toUpperCase();
        return kind + " DE VENTA";
    }

    private String formatSaleDate(Sale sale) {
        if (sale.getSaleDate() == null) {
            return "-";
        }

        return sale.getSaleDate().format(SALE_DATE_FORMATTER).toLowerCase();
    }

    private String buildQrContent(Sale sale) {
        List<String> lines = new ArrayList<>();
        lines.add("COMPROBANTE DIGITAL");
        lines.add("Empresa: " + safe(CompanyConfig.getBusinessName(), "-"));
        lines.add("RUC: " + safe(CompanyConfig.getRuc(), "-"));
        lines.add("Documento: " + resolveDocumentTitle(sale));
        lines.add("Codigo: " + safe(sale.getVoucherCode(), "-"));
        lines.add("Fecha: " + formatSaleDate(sale));
        lines.add("Empleado: " + safe(sale.getUserName(), "-"));
        lines.add("Pago: " + safe(sale.getPaymentMethodName(), "-"));
        lines.add("Cliente: " + buildCustomerName(sale));

        if (hasCustomerDocument(sale)) {
            lines.add("Doc. cliente: " + buildCustomerDocument(sale));
        }

        lines.add("Subtotal: S/ " + money(sale.getSubtotal()));
        lines.add("Descuento: S/ -" + money(sale.getDiscountAmount()));
        lines.add("IGV: S/ " + money(sale.getIgvAmount()));
        lines.add("Total: S/ " + money(sale.getTotal()));
        lines.add("Pago con: S/ " + money(sale.getPaidAmount()));
        lines.add("Vuelto: S/ " + money(sale.getChangeAmount()));
        lines.add("Items:");

        List<SaleDetail> details = sale.getDetails();
        if (details != null) {
            for (SaleDetail detail : details) {
                lines.add(detail.getQuantity()
                        + " x "
                        + safe(detail.getProductName(), "-")
                        + " = S/ "
                        + money(detail.getSubtotal()));
            }
        }

        return String.join("\n", lines);
    }

    private BufferedImage generateQrImage(String content, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        return image;
    }

    private class VoucherPrintable implements Printable {

        private final Sale sale;

        VoucherPrintable(Sale sale) {
            this.sale = sale;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            drawVoucher(g2, (int) pageFormat.getImageableWidth());

            g2.dispose();
            return PAGE_EXISTS;
        }

        private void drawVoucher(Graphics2D g2, int width) throws PrinterException {
            int y = 0;

            y = drawCenteredWrapped(g2, safe(CompanyConfig.getBusinessName(), "-"), FONT_TITLE, width, y + 16, 0);
            y = drawCenteredWrapped(g2, "RUC: " + safe(CompanyConfig.getRuc(), "-"), FONT_TEXT, width, y + 14, 0);
            y = drawCenteredWrapped(g2, safe(CompanyConfig.getAddress(), "-"), FONT_TEXT, width, y + 14, 0);
            y = drawCenteredWrapped(g2, "Tel: " + safe(CompanyConfig.getPhone(), "-"), FONT_TEXT, width, y + 12, 0);
            y = drawCenteredWrapped(g2, safe(CompanyConfig.getEmail(), "-"), FONT_TEXT, width, y + 12, 0);
            y = drawDashedDivider(g2, width, y + 10);

            y = drawCenteredWrapped(g2, resolveDocumentTitle(sale), FONT_TITLE, width, y + 18, 0);
            y = drawCenteredWrapped(g2, safe(sale.getVoucherCode(), "-"), FONT_TEXT_BOLD, width, y + 14, 0);
            y = drawDashedDivider(g2, width, y + 10);

            y = drawLabelValue(g2, "Fecha:", formatSaleDate(sale), width, y + 14);
            y = drawLabelValue(g2, "Empleado:", safe(sale.getUserName(), "-"), width, y + 14);
            y = drawLabelValue(g2, "Metodo de pago:", safe(sale.getPaymentMethodName(), "-"), width, y + 14);
            y = drawLabelValue(g2, "Cliente:", buildCustomerName(sale), width, y + 14);

            if (hasCustomerDocument(sale)) {
                y = drawLabelValue(g2, "Documento:", buildCustomerDocument(sale), width, y + 14);
            }

            y = drawDashedDivider(g2, width, y + 10);
            y = drawTextLine(g2, "DESCRIPCION", FONT_SUBTITLE, 0, y + 16);

            List<SaleDetail> details = sale.getDetails();
            if (details != null && !details.isEmpty()) {
                for (SaleDetail detail : details) {
                    y = drawDetailRow(g2, detail, width, y + 16);
                }
            } else {
                y = drawTextLine(g2, "Sin productos registrados", FONT_TEXT, 0, y + 14);
            }

            y = drawDashedDivider(g2, width, y + 10);
            y = drawAmountRow(g2, "Productos", "S/ " + money(sale.getSubtotal()), width, y + 14, false);
            y = drawAmountRow(g2, "Descuento", "-S/ " + money(sale.getDiscountAmount()), width, y + 14, false);
            y = drawAmountRow(g2, "IGV 18%", "S/ " + money(sale.getIgvAmount()), width, y + 14, false);
            y = drawAmountRow(g2, "Pago con", "S/ " + money(sale.getPaidAmount()), width, y + 14, false);

            if (sale.getChangeAmount() != null && sale.getChangeAmount().compareTo(BigDecimal.ZERO) > 0) {
                y = drawAmountRow(g2, "Vuelto", "S/ " + money(sale.getChangeAmount()), width, y + 14, false);
            }

            y = drawSolidDivider(g2, width, y + 10);
            y = drawAmountRow(g2, "Total", "S/ " + money(sale.getTotal()), width, y + 18, true);

            try {
                BufferedImage qrImage = generateQrImage(buildQrContent(sale), 260);
                y = drawQr(g2, qrImage, width, y + 14);
            } catch (WriterException ex) {
                throw new PrinterException("No se pudo generar el QR del comprobante.");
            }

            y = drawCenteredWrapped(g2, "Escanea el QR para revisar tu comprobante digital.", FONT_SMALL, width, y + 10, 8);
            drawCenteredWrapped(g2, safe(CompanyConfig.getFooterText(), "Gracias por su compra"), FONT_TEXT, width, y + 20, 0);
        }

        private int drawDetailRow(Graphics2D g2, SaleDetail detail, int width, int y) {
            String quantity = String.valueOf(detail.getQuantity());
            String amount = "S/ " + money(detail.getSubtotal());
            int amountWidth;

            g2.setFont(FONT_TEXT_BOLD);
            g2.setColor(Color.BLACK);
            g2.drawString(quantity, 0, y);

            g2.setFont(FONT_TEXT_BOLD);
            amountWidth = g2.getFontMetrics().stringWidth(amount);
            g2.drawString(amount, Math.max(0, width - amountWidth), y);

            List<String> lines = wrapText(g2, safe(detail.getProductName(), "-"), FONT_TEXT, width - 34);
            int nameX = 20;

            for (int i = 0; i < lines.size(); i++) {
                g2.setFont(FONT_TEXT);
                g2.drawString(lines.get(i), nameX, y + (i * 12));
            }

            String unitPrice = "P. unit: S/ " + money(detail.getUnitPrice());
            y += Math.max(12, lines.size() * 12);
            y = drawTextLine(g2, unitPrice, FONT_SMALL, nameX, y + 10);
            return y;
        }

        private int drawLabelValue(Graphics2D g2, String label, String value, int width, int y) {
            String text = label + " " + safe(value, "-");
            List<String> lines = wrapText(g2, text, FONT_TEXT, width);

            for (String line : lines) {
                y = drawTextLine(g2, line, FONT_TEXT, 0, y);
                y += 12;
            }

            return y - 12;
        }

        private int drawAmountRow(Graphics2D g2, String label, String value, int width, int y, boolean total) {
            Font font = total ? FONT_TOTAL : FONT_TEXT;

            g2.setFont(font);
            g2.setColor(Color.BLACK);
            g2.drawString(label, 0, y);

            int valueWidth = g2.getFontMetrics().stringWidth(value);
            g2.drawString(value, Math.max(0, width - valueWidth), y);
            return y;
        }

        private int drawTextLine(Graphics2D g2, String text, Font font, int x, int y) {
            g2.setFont(font);
            g2.setColor(Color.BLACK);
            g2.drawString(text, x, y);
            return y;
        }

        private int drawCenteredWrapped(Graphics2D g2, String text, Font font, int width, int y, int extraSpacing) {
            List<String> lines = wrapText(g2, safe(text, "-"), font, width);

            for (String line : lines) {
                g2.setFont(font);
                g2.setColor(Color.BLACK);
                int textWidth = g2.getFontMetrics().stringWidth(line);
                g2.drawString(line, Math.max(0, (width - textWidth) / 2), y);
                y += 12;
            }

            return y - 12 + extraSpacing;
        }

        private int drawDashedDivider(Graphics2D g2, int width, int y) {
            Stroke originalStroke = g2.getStroke();
            g2.setColor(new Color(160, 160, 160));
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 3f}, 0f));
            g2.drawLine(0, y, width, y);
            g2.setStroke(originalStroke);
            return y;
        }

        private int drawSolidDivider(Graphics2D g2, int width, int y) {
            g2.setColor(Color.BLACK);
            g2.drawLine(0, y, width, y);
            return y;
        }

        private int drawQr(Graphics2D g2, BufferedImage qrImage, int width, int y) {
            int qrSize = 132;
            int x = Math.max(0, (width - qrSize) / 2);
            Object previousHint = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(qrImage, x, y, qrSize, qrSize, null);

            if (previousHint != null) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previousHint);
            }
            return y + qrSize;
        }

        private List<String> wrapText(Graphics2D g2, String text, Font font, int maxWidth) {
            List<String> lines = new ArrayList<>();
            String value = safe(text, "-");
            String[] words = value.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            g2.setFont(font);

            for (String word : words) {
                String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;

                if (g2.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                    currentLine.setLength(0);
                    currentLine.append(candidate);
                    continue;
                }

                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                }

                if (g2.getFontMetrics().stringWidth(word) <= maxWidth) {
                    currentLine.append(word);
                } else {
                    lines.addAll(splitLongWord(g2, word, maxWidth));
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            if (lines.isEmpty()) {
                lines.add(value);
            }

            return lines;
        }

        private List<String> splitLongWord(Graphics2D g2, String word, int maxWidth) {
            List<String> parts = new ArrayList<>();
            StringBuilder current = new StringBuilder();

            for (char character : word.toCharArray()) {
                String candidate = current.toString() + character;

                if (g2.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                    current.append(character);
                } else {
                    if (current.length() > 0) {
                        parts.add(current.toString());
                    }
                    current.setLength(0);
                    current.append(character);
                }
            }

            if (current.length() > 0) {
                parts.add(current.toString());
            }

            return parts;
        }
    }
}
