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

    public static void main(String[] args) {
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
                System.out.println("\n📂 EVIDENCIAS GENERADAS EN: " + outputPath);
            } else {
                System.out.println("❌ No se encontraron JSONs. Ejecuta 'gradle clean test aggregate' primero.");
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
        
        // --- CÁLCULO DE TIEMPOS ---
        String startTimeStr = json.has("startTime") ? json.get("startTime").getAsString() : "";
        long durationMs = json.has("duration") ? json.get("duration").getAsLong() : 0;
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(startTimeStr); 
        } catch (Exception e) {
            start = LocalDateTime.now().minusNanos(durationMs * 1000000);
        }
        LocalDateTime end = start.plusNanos(durationMs * 1000000);
        String navegador = json.has("driver") ? json.get("driver").getAsString().toUpperCase() : "CHROME";

        PdfWriter writer = new PdfWriter(outputPath + "Evidencia_" + scenarioTitle.replace(" ", "_") + ".pdf");
        Document doc = new Document(new PdfDocument(writer));

        // --- ENCABEZADO ---
        doc.add(new Paragraph("REPORTE DE EVIDENCIA DE PRUEBA")
                .setBold().setFontSize(22).setFontColor(new DeviceRgb(0, 102, 204)).setTextAlignment(TextAlignment.CENTER));

        String textoResultado = result.equals("SUCCESS") ? "RESULTADO: PASADO" : "RESULTADO: FALLIDO";
        DeviceRgb colorStatus = result.equals("SUCCESS") ? new DeviceRgb(40, 167, 69) : new DeviceRgb(220, 53, 69);

        doc.add(new Paragraph(textoResultado)
                .setBold().setFontSize(26).setFontColor(colorStatus).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("------------------------------------------------------------------------------------------")
                .setTextAlignment(TextAlignment.CENTER).setFontColor(new DeviceRgb(200, 200, 200)));

        // --- CRONOGRAMA TÉCNICO ---
        doc.add(new Paragraph("DATOS DE EJECUCIÓN").setBold().setFontSize(12).setUnderline());
        doc.add(new Paragraph()
                .add(new Text("Inicio: ").setBold()).add(new Text(start.format(displayFormat)))
                .add(new Text("   |   ").setFontColor(new DeviceRgb(200, 200, 200)))
                .add(new Text("Finalización: ").setBold()).add(new Text(end.format(displayFormat)))
                .setFontSize(10));
        
        doc.add(new Paragraph()
                .add(new Text("Navegador: ").setBold()).add(new Text(navegador))
                .setFontSize(10).setMarginBottom(10));

        // --- DETALLES DEL ESCENARIO ---
        doc.add(new Paragraph("ESCENARIO").setBold().setUnderline().setFontSize(12));
        doc.add(new Paragraph().add(new Text("Feature: ").setBold()).add(featureName).setFontSize(10));
        doc.add(new Paragraph().add(new Text("Nombre: ").setBold()).add(scenarioTitle).setFontSize(10));

        // --- RESUMEN DE PASOS GHERKIN ---
        doc.add(new Paragraph("\nFLUJO DEFINIDO:").setBold().setMarginTop(5));
        JsonArray steps = json.getAsJsonArray("testSteps");
        
        JsonArray stepsToPrint = steps;
        if (steps.size() > 0 && steps.get(0).getAsJsonObject().has("children")) {
            stepsToPrint = steps.get(0).getAsJsonObject().getAsJsonArray("children");
        }

        for (int i = 0; i < stepsToPrint.size(); i++) {
            String desc = stepsToPrint.get(i).getAsJsonObject().get("description").getAsString();
            doc.add(new Paragraph("  " + (i + 1) + ". " + desc).setFontSize(9).setItalic().setMarginLeft(20));
        }

        // --- DETALLE CON CAPTURAS (AQUÍ ESTABA EL ERROR / WARNING) ---
        int stepCounter = 1;
        for (JsonElement element : steps) {
            JsonObject step = element.getAsJsonObject();
            
            if (step.has("children")) {
                JsonArray children = step.getAsJsonArray("children");
                for (int j = 0; j < children.size(); j++) {
                    doc.add(new AreaBreak()); // Salto de página para cada evidencia
                    agregarPasoConImagen(doc, children.get(j).getAsJsonObject(), j + 1);
                }
            } else {
                doc.add(new AreaBreak()); // Salto de página para cada evidencia
                agregarPasoConImagen(doc, step, stepCounter++);
            }
        }

        doc.close();
    }

    private static void agregarPasoConImagen(Document doc, JsonObject step, int num) throws Exception {
        doc.add(new Paragraph("PASO " + num).setBold().setFontSize(14).setFontColor(new DeviceRgb(0, 102, 204)));
        doc.add(new Paragraph(step.get("description").getAsString()).setItalic().setMarginBottom(10));

        if (step.has("screenshots")) {
            JsonArray sc = step.getAsJsonArray("screenshots");
            String name = sc.get(sc.size() - 1).getAsJsonObject().get("screenshot").getAsString();
            File imgFile = new File("target/site/serenity/" + name);
            if (imgFile.exists()) {
                Image img = new Image(ImageDataFactory.create(imgFile.getAbsolutePath()))
                        .setWidth(UnitValue.createPercentValue(45))
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(200, 200, 200), 1));
                doc.add(img);
            }
        }
    }
}