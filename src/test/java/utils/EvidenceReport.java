package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EvidenceReport {

    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(0, 102, 204); // Azul
    private static final DeviceRgb COLOR_EXITO = new DeviceRgb(40, 167, 69);    // Verde
    private static final DeviceRgb COLOR_FALLO = new DeviceRgb(220, 53, 69);    // Rojo
    private static final DeviceRgb COLOR_GRIS = new DeviceRgb(200, 200, 200);

    public static void main(String[] args) {
        System.out.println("🚀 GENERANDO REPORTE - DISEÑO MINIMALISTA POR COLORES...");
        String jsonPath = "target/site/serenity/";
        String outputPath = obtenerCarpetaSeguencial("target/Evidencias_PDF/"); 
        
        try {
            File folder = new File(jsonPath);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json") && !name.contains("manifest"));

            if (files != null && files.length > 0) {
                new File(outputPath).mkdirs();
                for (File file : files) {
                    generatePdfFromJson(file, outputPath);
                }
                System.out.println("\n📂 REPORTE FINALIZADO EN: " + outputPath);
            } else {
                System.out.println("❌ Error: No se encontraron archivos JSON.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String obtenerCarpetaSeguencial(String basePath) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        int con = 1;
        String path;
        while (new File(path = basePath + fecha + "-" + String.format("%03d", con) + "/").exists()) con++;
        return path;
    }

    private static void generatePdfFromJson(File jsonFile, String outputPath) throws Exception {
        JsonObject json = JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();
        String scenarioTitle = json.get("title").getAsString();
        String featureName = json.getAsJsonObject("userStory").get("storyName").getAsString();
        String result = json.get("result").getAsString();
        
        // --- TAGS ---
        StringBuilder tagsStr = new StringBuilder();
        if (json.has("tags")) {
            JsonArray tagsArray = json.getAsJsonArray("tags");
            for (JsonElement t : tagsArray) {
                String tagType = t.getAsJsonObject().get("type").getAsString();
                if (!tagType.equalsIgnoreCase("feature")) {
                    tagsStr.append("@").append(t.getAsJsonObject().get("name").getAsString()).append(" ");
                }
            }
        }

        long durationMs = json.has("duration") ? json.get("duration").getAsLong() : 0;
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String startTimeStr = json.has("startTime") ? json.get("startTime").getAsString() : "";
        LocalDateTime start;
        try { start = LocalDateTime.parse(startTimeStr); } catch (Exception e) { start = LocalDateTime.now(); }
        LocalDateTime end = start.plusNanos(durationMs * 1000000);

        PdfWriter writer = new PdfWriter(outputPath + "Evidencia_" + scenarioTitle.replace(" ", "_") + ".pdf");
        Document doc = new Document(new PdfDocument(writer));

        // --- ENCABEZADO ---
        doc.add(new Paragraph("REPORTE DE EVIDENCIA DE PRUEBA")
                .setBold().setFontSize(22).setFontColor(COLOR_PRIMARIO).setTextAlignment(TextAlignment.CENTER));

        DeviceRgb colorGlobal = result.equals("SUCCESS") ? COLOR_EXITO : COLOR_FALLO;
        doc.add(new Paragraph(result.equals("SUCCESS") ? "RESULTADO: PASADO" : "RESULTADO: FALLIDO")
                .setBold().setFontSize(26).setFontColor(colorGlobal).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER).setFontColor(COLOR_GRIS));

        // --- DATOS TÉCNICOS ---
        doc.add(new Paragraph("DATOS DE EJECUCIÓN").setBold().setFontSize(11).setUnderline());
        doc.add(new Paragraph().add(new Text("Inicio: ").setBold()).add(new Text(start.format(displayFormat)))
                .add(new Text("  |  Fin: ").setBold()).add(new Text(end.format(displayFormat))).setFontSize(9));
        
        // --- ESCENARIO Y TAGS ---
        doc.add(new Paragraph("\nESCENARIO").setBold().setUnderline().setFontSize(11));
        doc.add(new Paragraph().add(new Text("Feature: ").setBold()).add(new Text(featureName)).setFontSize(9));
        doc.add(new Paragraph().add(new Text("Nombre: ").setBold()).add(new Text(scenarioTitle)).setFontSize(9));
        
        if (tagsStr.length() > 0) {
            doc.add(new Paragraph().add(new Text("Tags: ").setBold()).add(new Text(tagsStr.toString().trim()).setBold()) 
                    .setFontSize(9).setFontColor(COLOR_PRIMARIO));
        }

        // --- RESUMEN GHERKIN A COLOR ---
        doc.add(new Paragraph("\nFLUJO DEFINIDO:").setBold().setFontSize(10));
        JsonArray steps = json.getAsJsonArray("testSteps");
        JsonArray stepsToPrint = (steps.size() > 0 && steps.get(0).getAsJsonObject().has("children")) 
                                ? steps.get(0).getAsJsonObject().getAsJsonArray("children") : steps;

        for (int i = 0; i < stepsToPrint.size(); i++) {
            JsonObject s = stepsToPrint.get(i).getAsJsonObject();
            String resStep = s.has("result") ? s.get("result").getAsString() : "SUCCESS";
            DeviceRgb colorPaso = resStep.equals("SUCCESS") ? COLOR_EXITO : COLOR_FALLO;

            doc.add(new Paragraph("  " + (i + 1) + ". " + s.get("description").getAsString())
                    .setFontSize(9).setItalic().setFontColor(colorPaso).setMarginLeft(20));
        }

        // --- DETALLE CON CAPTURAS ---
        int stepCounter = 1;
        for (JsonElement element : steps) {
            JsonObject step = element.getAsJsonObject();
            if (step.has("children")) {
                JsonArray children = step.getAsJsonArray("children");
                for (int j = 0; j < children.size(); j++) {
                    doc.add(new AreaBreak());
                    agregarPasoConImagen(doc, children.get(j).getAsJsonObject(), j + 1);
                }
            } else {
                doc.add(new AreaBreak());
                agregarPasoConImagen(doc, step, stepCounter++);
            }
        }
        doc.close();
    }

    private static void agregarPasoConImagen(Document doc, JsonObject step, int num) throws Exception {
        String res = step.has("result") ? step.get("result").getAsString() : "SUCCESS";
        DeviceRgb colorTitulo = res.equals("SUCCESS") ? COLOR_EXITO : COLOR_FALLO;

        // Título del paso pintado de color según resultado
        doc.add(new Paragraph("PASO " + num)
                .setBold().setFontSize(16).setFontColor(colorTitulo));
        
        doc.add(new Paragraph(step.get("description").getAsString())
                .setItalic().setMarginBottom(10).setFontSize(11).setFontColor(new DeviceRgb(50, 50, 50)));

        if (step.has("screenshots")) {
            JsonArray sc = step.getAsJsonArray("screenshots");
            String name = sc.get(sc.size() - 1).getAsJsonObject().get("screenshot").getAsString();
            File imgFile = new File("target/site/serenity/" + name);
            if (imgFile.exists()) {
                doc.add(new Image(ImageDataFactory.create(imgFile.getAbsolutePath()))
                        .setWidth(UnitValue.createPercentValue(65))
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(COLOR_GRIS, 1)));
            }
        }
    }
}