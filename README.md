# PikselHUD

Autorski, kliencki mod HUD do Minecraft Java Edition 1.21.11 na Fabric.

## Funkcje

- HUD z koordynatami, FPS i pingiem.
- Klawisz **P** otwiera panel moda.
- Każdy element można włączać/wyłączać, przeciągać i skalować od 0.2x do 10x.
- 4 style tekstu: normalny, pogrubiony, kursywa, pogrubiony + kursywa.
- Paleta kolorów.
- Presety: zapisywanie, wczytywanie i usuwanie.
- Konfiguracja w `config/pikselhud.json`.

## Budowanie

Wymagany JDK 21.

```text
./gradlew build
```

JAR pojawi się w `build/libs/`.

## Instalacja

Potrzebujesz Minecraft Java 1.21.11 + Fabric Loader 0.18.0 lub nowszy oraz Fabric API 0.141.1+ dla 1.21.11. Wrzuc JAR PikselHUD i Fabric API do folderu `mods`.

## GitHub Actions

Każdy push oraz ręczne uruchomienie workflow buduje JAR i publikuje go jako artifact.
