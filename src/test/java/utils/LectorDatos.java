package utils;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LectorDatos {
    public static List<String[]> leerUsuarios(String ruta) {
        List<String[]> datos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(ruta))) {
            reader.readNext(); // Saltamos el encabezado (usuario, password)
            datos = reader.readAll();
        } catch (Exception e) {
            System.out.println("🚨 ERROR: No se pudo leer el CSV: " + e.getMessage());
        }
        return datos;
    }
}