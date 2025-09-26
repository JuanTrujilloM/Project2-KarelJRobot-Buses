# Project 2 - KarelJRobot - Buses

## Tabla de Contenidos

1. [Descripción](#descripción)
2. [Requisitos](#requisitos)
3. [Integrantes](#integrantes)
4. [Instrucciones de ejecución](#instrucciones-de-ejecución)

## Descripción
El objetivo de este proyecto es diseñar e implementar un simulador de transporte en Java utilizando KarelJRobot, donde múltiples autobuses (robots) deben transportar pasajeros entre dos zonas del mapa: la zona azul y la zona verde.

El enfoque principal está en el paralelismo con bloqueos en un ambiente controlado, lo que permite aplicar y validar los conceptos de concurrencia y sincronización vistos en clase. La simulación modela un sistema con recursos compartidos (intersecciones y bahías) y políticas de control del tráfico para garantizar un tránsito seguro y eficiente.

Para lograrlo, el sistema implementa:
- Manejo de múltiples hilos de ejecución, donde cada bus corre en su propio hilo.
- Sincronización del paso de buses en tramos críticos mediante monitores y la regla de Pare y siga.
- Estrategias para evitar deadlocks mediante ordenación de bloqueos y reglas de entrada/salida a tramos.
- Mecanismos para detectar congestión (zonas llenas) y forzar rutas alternativas cuando es necesario.
- Un timeout de seguridad que apaga robots atascados para evitar bloqueos permanentes en la simulación.

Cada bus puede transportar hasta 4 pasajeros (beepers) por viaje y debe mover 500 pasajeros desde cada zona de origen hasta su destino. Dependiendo del estado del tráfico, los buses seleccionan entre rutas rápidas (con tramos regulados por Pare y siga) o rutas largas (alternativas que evitan los tramos críticos). La simulación garantiza que:
- No se produzcan choques (dos robots en la misma celda).
- El flujo por intersecciones compartidas se gestione de forma ordenada.
- El sistema responda dinámicamente a distintos niveles de congestión.

## Requisitos
- Java JDK 8+ instalado.
- El JAR de la librería KarelJRobot: [KarelJRobot.jar](KarelJRobot.jar).
- Mundo de prueba: [MundoParte2.kwld](MundoParte2.kwld).
- WSL o Windows

## Integrantes

- Juan Esteban Trujillo
- Camilo Alvarez
- Samuel Calderon

## Instrucciones de ejecución

Sigue estos pasos para obtener, compilar y ejecutar el simulador:

1. Clona el repositorio (si no lo tienes local):
```bash
git clone https://github.com/JuanTrujilloM/Project2-KarelJRobot-Buses
cd Project2-KarelJRobot-Buses
```

2. Compilar (Linux / WSL) — usando script:
```bash
./Comp_Run_Comm_WSL/compilar.sh
```

Compilar (Windows) — usando script:
```bat
Comp_Run_Comm_WIN/Compilar.bat
```

3. Ejecutar (Linux / WSL) — usando script:
```bash
./Comp_Run_Comm_WSL/run.sh
```

Ejecutar (Windows) — usando script:
```bat
Comp_Run_Comm_WIN/Run.bat
```

4. Editar mundos (opcional) — usando script:
- Linux/WSL:
```bash
./Comp_Run_Comm_WSL/editarmundos.sh
```
- Windows:
```bat
Comp_Run_Comm_WIN/EditarMundos.bat
```

## Scripts disponibles

- `Comp_Run_Comm_WSL/compilar.sh` — script de compilación para WSL/Linux.
- `Comp_Run_Comm_WSL/run.sh` — script de ejecución para WSL/Linux.
- `Comp_Run_Comm_WSL/editarmundos.sh` — abrir editor de mundos en WSL/Linux.
- `Comp_Run_Comm_WIN/Compilar.bat` — compilación en Windows.
- `Comp_Run_Comm_WIN/Run.bat` — ejecución en Windows.
- `Comp_Run_Comm_WIN/EditarMundos.bat` — abrir editor de mundos en Windows.


## Ejemplo de salida

Al ejecutar la simulación se abrirá la ventana gráfica de Karel donde se verán los autobuses moviéndose por el mapa, recogiendo y dejando beepers.
Con base en el mundo (`MundoParte2.kwld`), la salida mostrará la interacción concurrente de los robots y la correcta sincronización en los tramos críticos.

Al final, cuando los buses hagan todos los recorridos y lleven a los pasajeros hasta el destino, deberia quedar asi.

<img width="1918" height="1140" alt="Resultado" src="https://github.com/user-attachments/assets/4583bbfd-4a19-44a8-b429-6f96a5ccb4be" />


