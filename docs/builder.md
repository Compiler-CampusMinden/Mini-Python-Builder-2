# Builder

Der Builder stellt das Backend für unseren Mini-Python-Compiler bereit.
Damit müssen Sie die Code-Generierung nicht selbst schreiben,
sondern können
über die Java-API des Builders
aus Ihrem AST den passenden Code erzeugen lassen.

Der Builder unterstützt mehrere Zielsprachen für die Code-Generierung.
Jede Zielsprache hat ihre eigene Builder-API,
es gibt jedoch auch einen generischen Builder,
der dann in die jeweilige Zielsprache umgewandelt werden kann.
Es wird die Verwendung des generischen Builders,
mit anschließender Umwandlung in die WASM-Builder API,
empfohlen.

Mehr Informationen zu den aktuell unterstützen Zielsprachen/Buildern finden Sie unter:

1. [generische API](usage_generic_builder.md)
2. [WASM Builder](usage_wasm_builder.md)
3. [cbuilder](usage_cbuilder.md)

Unter [Setup](setup.md) ist beschrieben,
wie Sie den Builder in ihr Java-Projekt einbinden können.
