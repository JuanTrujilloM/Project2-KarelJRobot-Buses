import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

// Clases de soporte para manejo de celdas y tramos críticos
public class Celda {
    private boolean ocupada = false;

    // Intentar entrar a la celda. Devuelve true si pudo, false si ya estaba ocupada.
    public synchronized boolean intentarEntrar() {
        if (!ocupada) {
            ocupada = true;
            return true;
        }
        return false;
    }

    // Salir de la celda
    public synchronized void salir() {
        ocupada = false;
    }

    // Funcion para saber si la celda esta ocupada
    public synchronized boolean estaOcupada() {
        return ocupada;
    }
}