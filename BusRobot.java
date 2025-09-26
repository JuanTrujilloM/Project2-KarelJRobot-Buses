import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

// Clase para los buses
public class BusRobot extends Robot implements Runnable {

    private int MAX_PASAJEROS = 4;
    private boolean esZonaAzul;
    private int currentPassengers = 0;
    private int street, avenue;

    // Timeout: si un robot espera más de 10 segundos sin avanzar -> apagar
    private static final long TIMEOUT_MS = 4_000;
    // Última vez que el robot hizo un progreso (movimiento o acción)
    private volatile long lastProgressMs;

    // Constructor
    public BusRobot(int street, int avenue, Direction dir, int beeps, boolean esZonaAzul, Color color) {
        super(street, avenue, dir, beeps, color);
        this.esZonaAzul = esZonaAzul;
        this.street = street;
        this.avenue = avenue;

        // Ocupar celda inicial
        synchronized (MapaCeldas.celdas[street][avenue]) {
            while (!MapaCeldas.celdas[street][avenue].intentarEntrar()) {
                try {
                    MapaCeldas.celdas[street][avenue].wait();
                } catch (InterruptedException e) {
                }
            }
        }

        // Inicializar último progreso
        this.lastProgressMs = System.currentTimeMillis();
        World.setupThread(this);
    }

    // Movimiento con lock
    private boolean moverConLock() {

        // Calcular próxima celda
        int nextStreet = street;
        int nextAvenue = avenue;
        if (facingEast())
            nextAvenue++;
        else if (facingWest())
            nextAvenue--;
        else if (facingNorth())
            nextStreet++;
        else if (facingSouth())
            nextStreet--;

        // Verificar límites del mapa
        if (nextStreet < 1 || nextAvenue < 1 ||
                nextStreet >= MapaCeldas.MAX_CALLES ||
                nextAvenue >= MapaCeldas.MAX_AVENIDAS)
            return false;

        // Intentar entrar a la celda destino
        Celda destino = MapaCeldas.celdas[nextStreet][nextAvenue];
        Celda origen = MapaCeldas.celdas[street][avenue];

        // Bloquear ambas celdas en orden para evitar deadlock
        synchronized (destino) {
            if (destino.intentarEntrar()) {
                synchronized (origen) {
                    origen.salir();
                    origen.notifyAll();
                }
                street = nextStreet;
                avenue = nextAvenue;
                move();
                // actualizar último progreso al moverse
                lastProgressMs = System.currentTimeMillis();
                return true;
            } else {
                return false;
            }
        }
    }

    // Avanzar n pasos con lock
    private void avanzarPasos(int pasos) {

        // Intentar avanzar, pero respetando reglas de tramos críticos y timeout
        for (int i = 0; i < pasos; i++) {

            // Antes de entrar a tramos críticos
            if (street == 1 && avenue == 15) {
                if (!MapaCeldas.tramo1_16_21.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (street == 2 && avenue == 21) {
                if (!MapaCeldas.tramo1_16_21.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (street == 1 && avenue == 25) {
                if (!MapaCeldas.tramo1_26_29.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (street == 2 && avenue == 29) {
                if (!MapaCeldas.tramo1_26_29.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (street == 4 && avenue == 30) {
                if (!MapaCeldas.tramo5_10_30.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (street == 10 && avenue == 29) {
                if (!MapaCeldas.tramo5_10_30.esperarYEntrar(TIMEOUT_MS)) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Regla específica: si la bahía en avenida 29 (calles 2..5) está llena,
            // los robots en la posición (10,29) deben esperar para no avanzar por el
            // recorrido
            if (street == 10 && avenue == 29) {
                while (MapaCeldas.tramoBahia29.zonaLlena()) {
                    if (System.currentTimeMillis() - lastProgressMs > TIMEOUT_MS) {
                        turnOff();
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                lastProgressMs = System.currentTimeMillis();
            }

            // Regla específica: si la avenida 30 en calles 1..4 está llena,
            // los robots en (1,25) deben esperar hasta que haya espacio
            if (street == 1 && avenue == 25) {
                while (MapaCeldas.tramo1_4_30.zonaLlena()) {
                    if (System.currentTimeMillis() - lastProgressMs > TIMEOUT_MS) {
                        turnOff();
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                lastProgressMs = System.currentTimeMillis();
            }

            // Regla específica: si la calle 1 en avenidas 22..25 está llena,
            // los robots en (1,15) deben esperar hasta que haya espacio
            if (street == 1 && avenue == 15) {
                while (MapaCeldas.tramo1_22_25_v2.zonaLlena()) {
                    if (System.currentTimeMillis() - lastProgressMs > TIMEOUT_MS) {
                        turnOff();
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                lastProgressMs = System.currentTimeMillis();
            }

            // Regla específica: si la calle 1 en avenidas 26..29 está llena,
            // los robots en (2,29) deben esperar hasta que haya espacio
            if (street == 2 && avenue == 29) {
                while (MapaCeldas.tramo1_26_29_v2.zonaLlena()) {
                    if (System.currentTimeMillis() - lastProgressMs > TIMEOUT_MS) {
                        turnOff();
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                lastProgressMs = System.currentTimeMillis();
            }

            // Intentar avanzar con lock, esperar si no puede
            while (!moverConLock()) {
                if (System.currentTimeMillis() - lastProgressMs > TIMEOUT_MS) {
                    turnOff();
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Al salir de tramos críticos
            if (street == 1 && avenue == 22)
                MapaCeldas.tramo1_16_21.salir();
            if (street == 2 && avenue == 16)
                MapaCeldas.tramo1_16_21.salir();
            if (street == 1 && avenue == 30)
                MapaCeldas.tramo1_26_29.salir();
            if (street == 2 && avenue == 26)
                MapaCeldas.tramo1_26_29.salir();
            if (street == 11 && avenue == 30)
                MapaCeldas.tramo5_10_30.salir();
            if (street == 5 && avenue == 29)
                MapaCeldas.tramo5_10_30.salir();

            // actualizar último progreso después de completar el paso
            lastProgressMs = System.currentTimeMillis();
        }
    }

    // Métodos para orientar
    private void faceEast() {
        while (!facingEast())
            turnLeft();
    }

    private void faceWest() {
        while (!facingWest())
            turnLeft();
    }

    private void faceNorth() {
        while (!facingNorth())
            turnLeft();
    }

    private void faceSouth() {
        while (!facingSouth())
            turnLeft();
    }

    // ZONA AZUL
    // Ir a calle 1
    private void irStreet1() {
        if (street == 2) {
            desdeStreet2();
        } else if (street == 3) {
            desdeStreet3();
        } else if (street == 4) {
            desdeStreet4();
        }
        faceEast();
        avanzarPasos(7 - avenue);
    }

    // Movimiento desde Street 2 hacia Street 1
    private void desdeStreet2() {
        if (avenue <= 7) {
            faceWest();
            avanzarPasos(avenue - 1);
            faceSouth();
            avanzarPasos(1);
        }
    }

    // Movimiento desde Street 3 hacia Street 1
    private void desdeStreet3() {
        if (avenue >= 1 && avenue <= 7) {
            faceEast();
            avanzarPasos(7 - avenue);
            faceSouth();
            avanzarPasos(1);
            desdeStreet2();
        }
    }

    // Movimiento desde Street 4 hacia Street 1
    private void desdeStreet4() {
        if (avenue >= 1 && avenue <= 8) {
            faceWest();
            avanzarPasos(avenue - 1);
            faceSouth();
            avanzarPasos(1);
            desdeStreet3();
        }
    }

    // ZONA VERDE
    // Ir a calle 13
    private void irStreet13() {
        if (street == 12) {
            desdeStreet12();
        } else if (street == 13) {
            desdeStreet13();
        } else if (street == 14) {
            desdeStreet14();
        } else if (street == 15) {
            desdeStreet15();
        } else if (street == 16) {
            desdeStreet16();
        }

        if (avenue == 30) {
            if (street >= 13 && street <= 15) {
                faceNorth();
                avanzarPasos(16 - street);
                if (street == 16) {
                    desdeStreet16();
                }
            }
        }

        faceWest();
        avanzarPasos(avenue - 23);
        faceSouth();
        avanzarPasos(street - 12);
    }

    // Desde street 12
    private void desdeStreet12() {
        if (avenue >= 28 && avenue <= 29) {
            faceWest();
            avanzarPasos(avenue - 28);
            faceNorth();
            avanzarPasos(1);
        }
    }

    // Desde street 13
    private void desdeStreet13() {
        if (avenue == 29) {
            faceSouth();
            avanzarPasos(1);
            if (street == 12) {
                desdeStreet12();
            }
        }
    }

    // Desde street 14
    private void desdeStreet14() {
        if (avenue >= 23 && avenue <= 29) {
            faceEast();
            avanzarPasos(29 - avenue);
            faceSouth();
            avanzarPasos(1);
            if (street == 13) {
                desdeStreet13();
            }
        }
    }

    // Desde street 15
    private void desdeStreet15() {
        if (avenue >= 23 && avenue <= 29) {
            faceWest();
            avanzarPasos(avenue - 23);
            faceSouth();
            avanzarPasos(1);
            if (street == 14) {
                desdeStreet14();
            }
        }
    }

    // Desde street 16
    private void desdeStreet16() {
        if (avenue >= 29 && avenue <= 30) {
            faceWest();
            avanzarPasos(avenue - 29);
            faceSouth();
            avanzarPasos(1);
            if (street == 15) {
                desdeStreet15();
            }
        }
    }

    // Funciones para recoger y dejar pasajeros
    private void pickUpPassengers() {
        while (currentPassengers < MAX_PASAJEROS && nextToABeeper()) {
            pickBeeper();
            currentPassengers++;
            lastProgressMs = System.currentTimeMillis();
        }
    }

    private void dropPassengers() {
        while (currentPassengers > 0) {
            putBeeper();
            currentPassengers--;
            lastProgressMs = System.currentTimeMillis();
        }
    }

    // Rutas Rapidas
    private void rutaRapidaAzul() {
        pickUpPassengers();
        if (currentPassengers == 0)
            return;
        avanzarPasos(30 - avenue);
        faceNorth();
        avanzarPasos(12 - street);
        dropPassengers();
        avanzarPasos(1);
    }

    private void rutaRapidaVerde() {
        pickUpPassengers();
        if (currentPassengers == 0)
            return;
        avanzarPasos(street - 10);
        faceEast();
        avanzarPasos(30 - avenue);
        faceSouth();
        avanzarPasos(street - 5);
        faceWest();
        avanzarPasos(avenue - 29);
        faceSouth();
        avanzarPasos(street - 1);
        faceWest();
        avanzarPasos(avenue - 26);
        faceNorth();
        avanzarPasos(2 - street);
        faceWest();
        avanzarPasos(avenue - 21);
        faceSouth();
        avanzarPasos(street - 1);
        faceWest();
        avanzarPasos(avenue - 16);
        faceNorth();
        avanzarPasos(2 - street);
        faceWest();
        avanzarPasos(avenue - 9);
        dropPassengers();
        avanzarPasos(1);
        faceNorth();
        avanzarPasos(2);
    }

    // Rutas Lentas
    private void rutaLentaAzul() {
        pickUpPassengers();
        if (currentPassengers == 0)
            return;
        avanzarPasos(11 - avenue);
        faceNorth();
        avanzarPasos(11 - street);
        faceWest();
        avanzarPasos(avenue - 8);
        faceNorth();
        avanzarPasos(14 - street);
        faceEast();
        avanzarPasos(16 - avenue);
        faceSouth();
        avanzarPasos(street - 10);
        faceWest();
        avanzarPasos(avenue - 13);
        faceSouth();
        avanzarPasos(street - 5);
        faceEast();
        avanzarPasos(20 - avenue);
        faceNorth();
        avanzarPasos(10 - street);
        faceEast();
        avanzarPasos(30 - avenue);
        faceNorth();
        avanzarPasos(12 - street);
        dropPassengers();
        avanzarPasos(1);
    }

    private void rutaLentaVerde() {
        pickUpPassengers();
        if (currentPassengers == 0)
            return;
        avanzarPasos(street - 11);
        faceWest();
        avanzarPasos(avenue - 20);
        faceNorth();
        avanzarPasos(19 - street);
        faceWest();
        avanzarPasos(avenue - 18);
        faceSouth();
        avanzarPasos(street - 15);
        faceWest();
        avanzarPasos(avenue - 1);
        faceSouth();
        avanzarPasos(street - 10);
        faceEast();
        avanzarPasos(10 - avenue);
        faceSouth();
        avanzarPasos(street - 2);
        faceWest();
        avanzarPasos(avenue - 9);
        dropPassengers();
        avanzarPasos(1);
        faceNorth();
        avanzarPasos(2);
    }

    // Run
    @Override
    public void run() {
        if (esZonaAzul) {
            while (true) {
                irStreet1(); // Ir a zona azul
                if (!nextToABeeper())
                    break; // Si ya no hay beepers, detener

                // Regla Pare y Siga: si hay >=4 robots en calle 1 avenidas 12..15,
                // los que salen deben tomar el camino largo (ruta lenta)
                int ocupadosZonaCriticaAzul = MapaCeldas.contarOcupadosEn(1, 12, 15);
                if (ocupadosZonaCriticaAzul >= 4) {
                    rutaLentaAzul();
                } 
                else {
                    rutaRapidaAzul();
                }
                irStreet13();

                // Regla Pare y Siga para regreso: si hay >=6 robots en calle 10 avenidas
                // 24..29,
                // el regreso se hace por el camino largo hasta que se libere al menos uno.
                int ocupadosZonaCriticaVerde = MapaCeldas.contarOcupadosEn(10, 24, 29);
                if (ocupadosZonaCriticaVerde >= 6) {
                    rutaLentaVerde();
                } else {
                    rutaRapidaVerde();
                }
            }
        } 
        
        else {
            while (true) {
                irStreet13(); // Ir a zona verde
                if (!nextToABeeper())
                    break; // Si ya no hay beepers, detener
                int ocupadosZonaCriticaVerde = MapaCeldas.contarOcupadosEn(10, 24, 29);
                if (ocupadosZonaCriticaVerde >= 6) {
                    rutaLentaVerde();
                } else {
                    rutaRapidaVerde();
                }
                irStreet1();
                int ocupadosZonaCriticaAzul = MapaCeldas.contarOcupadosEn(1, 12, 15);
                if (ocupadosZonaCriticaAzul >= 4) {
                    rutaLentaAzul();
                } else {
                    rutaRapidaAzul();
                }
            }
        }
        turnOff(); // Apagar bus cuando ya no hay trabajo
    }

}
