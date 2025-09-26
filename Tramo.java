    import kareltherobot.*;
    import java.awt.Color;
    import java.util.concurrent.Semaphore;

    // Clase para manejar tramos críticos
    public class Tramo {
        // Definición del tramo: calles y avenidas que abarca
        private int streetIni, streetFin, avenueIni, avenueFin;
        private boolean ocupado = false;

        // Constructor
        public Tramo(int streetIni, int streetFin, int avenueIni, int avenueFin) {
            this.streetIni = streetIni;
            this.streetFin = streetFin;
            this.avenueIni = avenueIni;
            this.avenueFin = avenueFin;
        }

        // Espera a que pueda entrar al tramo (de a 1) con timeout.
        // Devuelve true si pudo entrar, false si el timeout expiró.
        public synchronized boolean esperarYEntrar(long timeoutMs) {
            long end = System.currentTimeMillis() + timeoutMs;
            while (ocupado) {
                long remaining = end - System.currentTimeMillis();
                if (remaining <= 0)
                    return false;
                try {
                    wait(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            ocupado = true; // marcar como ocupado
            return true;
        }

        // Salir del tramo
        public synchronized void salir() {
            ocupado = false;
            notifyAll(); // Avisar que puede entrar el siguiente
        }

        // Devuelve true si TODAS las celdas del tramo están ocupadas
        public boolean zonaLlena() {
            for (int s = streetIni; s <= streetFin; s++) {
                for (int a = avenueIni; a <= avenueFin; a++) {
                    synchronized (MapaCeldas.celdas[s][a]) {
                        if (!MapaCeldas.celdas[s][a].estaOcupada())
                            return false;
                    }
                }
            }
            return true;
        }
    }