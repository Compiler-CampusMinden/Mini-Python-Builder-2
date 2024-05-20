# Setup

Der Builder in ein Java-basiertes Projekt.
Es sollten alle Java-Versionen ab Java 17 nutzbar sein.
Empfohlen wird das **[JDK 21 LTS](https:://openjdk.org/projects/jdk/21)**.
Abhängig von der von Ihnen verwendeten Zielsprache für die Code-Generierung benötigen Sie noch weitere Werkzeuge.

## Einbinden des Builders in Ihr Java-Projekt

Es existieren mehrere Möglichkeiten.

Sie können das zur Verfügung gestellte .jar-Archiv des Builders verwenden.
Damit können Sie den Builder entsprechend dem üblichen Vorgehen in Ihr eigenens Java-Projekt einbinden.
Ein weiteres Setup ist für den Builder selbst nicht erforderlich.
Diese Variante wird empfohlen,
weil so keine weiteren Werkzeuge zur Verwendung des WASM-Builders benötigt werden.

Alternativ können Sie den Builder als Grundlage Ihres Projekts verwenden.
Es existiert eine einfache [Gradle-Konfiguration](../builder/build.gradle), mit der Sie das Projekt
in der Konsole oder auch in der IDE bauen können. Vermutlich wollen Sie dort noch Ihre
Main-Klasse festlegen, indem sie das folgende Snippet in die `build.gradle` einfügen und
anpassen:

```gradle
application {
    mainClass = 'wuppie.fluppie.foo.Main'
}
```

(Dabei ist `wuppie.fluppie.foo.Main` mit Ihrer Main-Klasse zu ersetzen.)

Beachten Sie zudem,
dass um das Kompilieren des Builder-Projekts zu ermöglichen,
die Hinweise zur
[Verwendung des WASM-Builders](#verwenden-des-wasm-builders-webassembly-code-generierung) zu beachten sind.

## Verwenden des CBuilders (C-Code Generierung)

Für das Übersetzen des mit dem CBuilder erzeugten C-Codes benötigen Sie noch einen C-Compiler
wie in [Verwendung des generierten C-Codes](usage_generated_code.md) beschrieben.

## Verwenden des WASM-Builders (WebAssembly-Code Generierung)

Wenn Sie das zur Verfügung gestelle .jar-Archiv nutzen,
benötigen Sie keine weiteren Werkzeuge.

Wenn Sie das Builder-Projekt als Grundlage Ihres eigenen Projekts verwenden,
benötigen Sie einen C-Compiler,
der die Cross-Compilierung nach WASM unterstützt.
Empfohlen wird die Nutzung von Docker.
Bauen Sie dann die für die Ausführung des WASM-Codes benötigte
Bibliothek mit `make c-runtime/lib_wasm-docker`.
Wenn Sie einen entsprechenden Compiler lokal installiert haben,
verwenden Sie stattdesen `make c-runtime/lib_wasm`.
