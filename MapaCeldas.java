import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

// Mapa de celdas
public class MapaCeldas {

    // Dimensiones del mapa
    public static final int MAX_CALLES = 21;
    public static final int MAX_AVENIDAS = 31;
    public static Celda[][] celdas = new Celda[MAX_CALLES][MAX_AVENIDAS];

    // Definicion de Tramos críticos
    public static Tramo tramo1_16_21 = new Tramo(1, 1, 16, 21);
    public static Tramo tramo1_26_29 = new Tramo(1, 1, 26, 29);
    public static Tramo tramo5_10_30 = new Tramo(5, 10, 30, 30);
    public static Tramo tramo1_22_25 = new Tramo(1, 1, 22, 25);

    // Tramos para que no se acumulen si no hay espacios y hagan esperar innecesariamente
    // Bahia en avenida 29: calles 2 a 5, avenida 29
    public static Tramo tramoBahia29 = new Tramo(2, 5, 29, 29);
    // Avenida 30, calles 1 a 4
    public static Tramo tramo1_4_30 = new Tramo(1, 4, 30, 30);
    // Calle 1 avenidas 22 a 25
    public static Tramo tramo1_22_25_v2 = new Tramo(1, 1, 22, 25);
    // calle 1, avenidas 26 a 29
    public static Tramo tramo1_26_29_v2 = new Tramo(1, 1, 26, 29);

    // Inicializar celdas
    static {
        for (int i = 0; i < MAX_CALLES; i++) {
            for (int j = 0; j < MAX_AVENIDAS; j++) {
                celdas[i][j] = new Celda();
            }
        }
    }

    // Cuenta cuántas celdas ocupadas hay en una franja de una misma calle entre
    // avenueStart y avenueEnd.
    public static int contarOcupadosEn(int street, int avenueStart, int avenueEnd) {
        if (street < 0 || street >= MAX_CALLES)
            return 0;
        avenueStart = Math.max(0, avenueStart);
        avenueEnd = Math.min(MAX_AVENIDAS - 1, avenueEnd);
        int cnt = 0;
        for (int a = avenueStart; a <= avenueEnd; a++) {
            synchronized (celdas[street][a]) {
                if (celdas[street][a].estaOcupada())
                    cnt++;
            }
        }
        return cnt;
    }
}