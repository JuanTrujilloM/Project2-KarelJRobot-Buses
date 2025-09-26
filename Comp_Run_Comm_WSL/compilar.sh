#!/bin/bash

# Script de compilación para Linux/WSL - equivalente a Compilar.bat
javac -d . -cp ".:KarelJRobot.jar" *.java

if [ $? -eq 0 ]; then
    echo "Compilación exitosa"
else
    echo "Error en la compilación"
fi

echo "Presiona Enter para continuar..."
read