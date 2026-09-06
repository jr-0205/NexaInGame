# Arquitectura

NEXA In-Game mantiene un producto compartido y tres adaptadores Fabric:

```text
shared -> compatibilidad por target -> Minecraft/Fabric
```

Los targets oficiales son 1.21.1, 1.21.8 y 1.21.10. Las diferencias incompatibles
de GUI y matrices se encapsulan en clases `compat` dentro de cada subproyecto. La
logica de configuracion, modulos, HUD, entrada y UI permanece en `shared/`.

## Ejecucion

1. El entrypoint carga y valida `config/nexa-ingame.json`.
2. `ModuleRegistry` crea el estado del perfil activo.
3. `ModuleRuntime` aplica y restaura Fullbright, Zoom y FOV.
4. `HudRenderer` resuelve widgets registrados, los ordena por z-index y renderiza.
5. El Control Center activa modulos y cambia ajustes rapidos.
6. El editor calcula limites con las dimensiones reales ya escaladas.
7. `ConfigStore` guarda de forma atomica y conserva una copia si el JSON esta danado.

## Compatibilidad

El codigo compartido no debe asumir una implementacion concreta de matrices o de
overlays de items. Esas llamadas pasan por `com.nexaclient.ingame.compat.HudCompat`.
Cuando una futura version cambie una API, debe agregarse o dividirse un adaptador,
sin duplicar el catalogo, configuracion o comportamiento del producto.

## Modulos

Los modulos implementados pueden activarse desde la UI. Los modulos que aun
requieren Mixins se mantienen visibles como `PROXIMAMENTE`, pero no pueden
activarse. Esto evita guardar estados que no producen ningun comportamiento.

La siguiente ampliacion arquitectonica sera extraer Scoreboard, Boss Bar,
Nametags, F3 Display e Hitbox mediante Mixins separados por familia de version.
