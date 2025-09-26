import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

// Clase Principal
public class main implements Directions {
    public static void main(String[] args) {

        // Cargar el mundo creado en el ejercicio 2
        World.readWorld("MundoParte2.kwld");
        World.placeBeepers(1, 7, 500); // 500 beepers en la Avenida 7, Calle 1 (zona azul)
        World.placeBeepers(12, 23, 500); // 500 beepers en la Avenida 23, Calle 12 (zona verde)
        World.setVisible(true);
        World.setDelay(1);

        // Recuadro azul - Creacion de robots
        BusRobot[] busesAzules = new BusRobot[28];
        int idx = 0;
        
        for (int avenue = 6; avenue >= 1; avenue--) {
            busesAzules[idx++] = new BusRobot(1, avenue, East, 0, true, Color.blue);
        }
        for (int avenue = 1; avenue <= 7; avenue++) {
            busesAzules[idx++] = new BusRobot(2, avenue, West, 0, true, Color.blue);
        }
        for (int avenue = 7; avenue >= 1; avenue--) {
            busesAzules[idx++] = new BusRobot(3, avenue, East, 0, true, Color.blue);
        }
        for (int avenue = 1; avenue <= 8; avenue++) {
            busesAzules[idx++] = new BusRobot(4, avenue, West, 0, true, Color.blue);
        }



        // Recuadro verde - Creacion de robots
        BusRobot[] busesVerdes = new BusRobot[28];
        int idxVerde = 0;

        for (int avenue = 23; avenue <= 28; avenue++) {
            busesVerdes[idxVerde++] = new BusRobot(13, avenue, West, 0, false, Color.green);
        }
        for (int avenue = 28; avenue <= 29; avenue++) {
            busesVerdes[idxVerde++] = new BusRobot(12, avenue, West, 0, false, Color.green);
        }
        for (int street = 13; street <= 14; street++) {
            busesVerdes[idxVerde++] = new BusRobot(street, 29, South, 0, false, Color.green);
        }
        for (int avenue = 28; avenue >= 23; avenue--) {
            busesVerdes[idxVerde++] = new BusRobot(14, avenue, East, 0, false, Color.green);
        }
        for (int avenue = 23; avenue <= 29; avenue++) {
            busesVerdes[idxVerde++] = new BusRobot(15, avenue, West, 0, false, Color.green);
        }
        for (int avenue = 29; avenue <= 30; avenue++) {
            busesVerdes[idxVerde++] = new BusRobot(16, avenue, West, 0, false, Color.green);
        }
        for (int street = 15; street >= 13; street--) {
            busesVerdes[idxVerde++] = new BusRobot(street, 30, North, 0, false, Color.green);
        }

        // Ajustar velocidad para que se vea movimiento de los robots
        World.setDelay(5);

        // Arrancar todos los robots azules
        for (BusRobot bus : busesAzules) {
            new Thread(bus).start();
        }

        // Arrancar todos los robots verdes
        for (BusRobot bus : busesVerdes) {
            new Thread(bus).start();
        }

    }
}