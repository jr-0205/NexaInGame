# NEXA In-Game

Cliente modular para Minecraft Java desarrollado sobre Fabric. NEXA ofrece una
experiencia integrada similar en alcance a un cliente de Minecraft, sin copiar
codigo, recursos, marcas ni interfaces propietarias.

## Versiones oficiales

- Minecraft 1.21.1
- Minecraft 1.21.8
- Minecraft 1.21.10
- Java 21
- Fabric Loader 0.17.3 o posterior

El producto vive en `shared/`. Cada subproyecto `fabric-*` contiene solamente la
configuracion y los adaptadores requeridos por esa version de Minecraft.

## Funciones de alpha.2

- Portada NEXA opcional.
- Control Center con `Right Shift`.
- Editor HUD con `H`.
- Perfiles Default, PvP y Survival.
- Configuracion JSON validada, migracion desde schema 1 y respaldo de archivos danados.
- Posiciones normalizadas usando el tamano real de cada componente.
- Escala, opacidad, z-index, ajuste al centro y limites de pantalla.
- HUD funcional de FPS, ping, memoria, reloj, coordenadas, armadura, inventario,
  keystrokes y CPS.
- Crosshair configurable.
- Fullbright reversible.
- Zoom temporal con `C` y FOV configurable.
- FOV Changer reversible.
- Modulos que requieren Mixins marcados como `PROXIMAMENTE`.

En el Control Center, clic izquierdo activa o desactiva un modulo y clic derecho
cambia su ajuste rapido. En el editor HUD, la rueda cambia la escala,
`Shift + rueda` cambia la opacidad y clic derecho restablece un componente.

## Compilacion

En Windows:

```powershell
.\tools\build-nexa.bat 1.21.1
.\tools\build-nexa.bat 1.21.8
.\tools\build-nexa.bat 1.21.10
.\tools\build-nexa.bat all
```

Alternativamente:

```powershell
.\tools\Build-Nexa.ps1 -Target all -Clean
```

Los JAR remapeados se generan en `fabric-*/build/libs/`. La tarea
`releaseCandidate` compila los tres targets y actualiza sus ZIP de fuentes
verificadas. GitHub Actions genera un manifiesto que el launcher puede consumir,
incluyendo URL, SHA-256, Java, dependencias y fecha de publicacion.

## Estado de seguridad

Todas las funciones actuales son visuales y locales. NEXA no automatiza acciones,
no modifica paquetes de red y no intenta evadir reglas de servidores.
