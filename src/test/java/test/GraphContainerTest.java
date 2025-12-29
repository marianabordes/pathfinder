package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.pathfinder.FileGraphLoader;
import software.ulpgc.pathfinder.GraphContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileGraphLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void load_validGraph_shouldReturnContainer() throws IOException {
        // 1. Preparar un archivo de prueba falso
        File file = tempDir.resolve("graph.txt").toFile();
        List<String> lines = List.of(
                "A,B,1.5",
                "B,C,2.0",
                "A,C,10.0"
        );
        Files.write(file.toPath(), lines);

        // 2. Ejecutar el loader (lo que queremos probar)
        FileGraphLoader loader = new FileGraphLoader(file);
        GraphContainer container = loader.load();

        // 3. Verificar que leyó bien los datos (Assertions)
        assertNotNull(container);
        // Debe existir camino entre A y B
        assertEquals(1.5, container.pathWeightBetween("A", "B"));
        // Debe existir camino entre B y C
        assertEquals(2.0, container.pathWeightBetween("B", "C"));
    }

    @Test
    void load_invalidLines_shouldSkipAndContinue() throws IOException {
        // Probamos que el sistema no explote si hay basura en el archivo
        File file = tempDir.resolve("bad_graph.txt").toFile();
        List<String> lines = List.of(
                "A,B,1.0",
                "LINEA_INVALIDA_SIN_COMA",  // Esto debería dar error de parseo interno pero no parar
                "A,D,NoEsUnNumero",         // Esto también falla
                "B,C,2.0"
        );
        Files.write(file.toPath(), lines);

        FileGraphLoader loader = new FileGraphLoader(file);
        GraphContainer container = loader.load();

        // Si llega aquí, es que no explotó. Verificamos que leyó las líneas buenas.
        assertEquals(1.0, container.pathWeightBetween("A", "B"));
        assertEquals(2.0, container.pathWeightBetween("B", "C"));
    }
}