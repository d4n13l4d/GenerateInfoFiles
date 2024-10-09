import java.io.*;
import java.util.*;

public class Main {

    // Método para leer el archivo de productos
    public static Map<Integer, String[]> readProductsFile(String fileName) {
        Map<Integer, String[]> products = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                int productId = Integer.parseInt(parts[0]);
                String[] productInfo = {parts[1], parts[2]};
                products.put(productId, productInfo);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de productos: " + e.getMessage());
        }
        return products;
    }

    // Método para leer los archivos de ventas de cada vendedor
    public static Map<String, Double> processSalesFiles(String folderPath, Map<Integer, String[]> products) {
        Map<String, Double> salesData = new HashMap<>();
        File folder = new File(folderPath);
        File[] salesFiles = folder.listFiles((dir, name) -> name.startsWith("sales_"));

        if (salesFiles != null) {
            for (File file : salesFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String sellerInfo = br.readLine().split(";")[1]; // Leer la segunda parte del encabezado
                    double totalSales = 0.0;
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(";");
                        int productId = Integer.parseInt(parts[0]);
                        int quantitySold = Integer.parseInt(parts[1]);
                        double productPrice = Double.parseDouble(products.get(productId)[1]);
                        totalSales += productPrice * quantitySold;
                    }
                    salesData.put(sellerInfo, totalSales);
                } catch (IOException e) {
                    System.err.println("Error al procesar el archivo de ventas: " + e.getMessage());
                }
            }
        }
        return salesData;
    }

    // Método para escribir el reporte de vendedores ordenado por ventas
    public static void writeSalesReport(Map<String, Double> salesData, String fileName) {
        List<Map.Entry<String, Double>> sortedSales = new ArrayList<>(salesData.entrySet());
        sortedSales.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        try (FileWriter writer = new FileWriter(fileName)) {
            for (Map.Entry<String, Double> entry : sortedSales) {
                writer.write(entry.getKey() + ";" + entry.getValue() + "\n");
            }
            System.out.println("Reporte de ventas generado exitosamente: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al generar el reporte de ventas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String productsFilePath = "products.txt";
        String salesFolderPath = ".";  // Ruta actual para buscar archivos de ventas

        // Leer el archivo de productos
        Map<Integer, String[]> products = readProductsFile(productsFilePath);

        // Procesar archivos de ventas
        Map<String, Double> salesData = processSalesFiles(salesFolderPath, products);

        // Escribir reporte de ventas
        writeSalesReport(salesData, "sales_report.csv");
    }
}