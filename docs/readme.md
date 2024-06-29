# Überblick

Dieses Repository stellt

1.  die Sprachdefinition für **[Mini-Python](syntax_definition.md)**, und
2.  einen **Java-basierten [Builder](../builder/src/main/java/minipython/builder)**, und
3.  eine kompatible **[C-Laufzeitumgebung](../c-runtime/)** bereit.

Der Builder erzeugt Code über API-Aufrufe (analog zu [LLVM](https://llvm.org/)):
Zur Generierung von Code für ein Mini Python-Programm rufen Sie während der AST-Traversierung die jeweiligen Builder-Funktionen auf.
Daraus wird dann zum Beispiel C-Code generiert;
dieser kann dann mit einem Standard-C-Compiler und der mitgelieferten C-Laufzeitumgebung zu
einer ausführbaren Anwendung kompiliert werden.

Sie finden hier die Dokumentation zum [syntaktischen](syntax_definition.md) und
[semantischen](semantic_definition.md) Sprachumfang von Mini-Python sowie die Dokumentation zum [Builder](./builder.md).

Sie brauchen ein **Java JDK** ab Version 17 (empfohlen: **JDK 21 LTS**) und **make** sowie den **gcc**-
oder **clang**-Compiler.
