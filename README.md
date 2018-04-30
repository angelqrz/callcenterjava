# Call Center

Este repositorio contiene la solución al ejercicio de Java para Almundo.

## Ejecución
Existe una clase ubicada en el paquete *util* llamada **Constants.java**  en la cual se pueden configurar valores usados durante la ejecucion de la aplicacion tales como: 

 - CALL_MIN_DURATION (5)
 - CALL_MAX_DURATION (10)
 - MAX_CONCURRENT_CALLS (10)
 - NUMBER_OF_OPERATORS
 - NUMBER_OF_SUPERVISORS
 - NUMBER_OF_DIRECTORS

Al ejecutar la aplicación se pedirá introducir un valor que representa la cantidad de llamadas que serán atendidas, al ingresar este valor se crearán *n* llamadas que serán procesadas por el **Dispatcher**. Para salir de la aplicación se debe ingresar "exit" en la consola. Los logs durante la ejecución se mantuvieron activos para apreciar de una mejor manera el funcionamiento interno de la aplicación.

## Pruebas Unitarias
Se desarrollaron diferentes pruebas para cubrir los casos especiales expuestos en el enunciado del ejercicio como lo son diez llamadas concurrentes, cantidad de llamadas mayor al numero de empleados disponibles, y mas de diez llamadas concurrentes. También se agregaron un par de pruebas extra con el fin de verificar que el tiempo de las llamadas se encuentre dentro del rango configurado y que el orden en el que los empleados atienden las llamadas sea el correcto (primero Operadores, luego Supervisores y finalmente Directores).

## Autor
 - **Angel Quiroz**

