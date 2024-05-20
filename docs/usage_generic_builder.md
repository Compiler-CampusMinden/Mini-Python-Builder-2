# Verwendung des generischen Builders für Mini-Python

Der generische Builder ist von der Zielsprache unabhängig.
Damit können Sie den passenden Code
für beliebige, vom Builder unterstütze Zielsprachen,
aus Ihrem AST erzeugen lassen.

## Setup

Das Einbinden des (C)Builders in Ihr Projekt ist unter [Setup](setup.md) beschrieben.

Beachten Sie, dass abhängig von der von Ihnen gewählten Zielsprache,
noch weitere, ebenfalls im Setup beschriebene, Schritt notwendig sind.

## Basis-Funktionalität

Alle Klassen des generischen Builders befinden sich im Paket `minipython.builder.lang`.

### MPyModule

Die Klasse `MPyModule` stellt den generischen Builder bereit.

Erzeugen Sie ein Objekt vom Typ `MPyModule`,
fügen Sie nach Bedarf Elemente zu den Übergebenen Collections hinzu,
oder übergeben Sie bereits fertige Collections
und transformieren am Ende den generischen Builder in einen Zielsprache spezifischen Builder,
mit dem Sie dann den passenden Code erzeugen.

``` java
List<Statement> body = ...;
Set<VariableDeclaration> globalVariables = ...;
Set<MPyClass> classes = ...;
LinkedHashSet<FunctionDeclaration> functions = ...;

globalVariables.add(new VariableDeclaration(...)); // Deklaration einer globalen Variable
classes.add(new MPyClass(...)); // Deklaration einer Klasse
functions.add(new FunctionDeclaration(...)); // Deklaration einer Funktion
body.add(new Call(...)); // Alle weiteren Programmteile, hier z. B. Funktionsaufruf

MPyModule module = new MPyModule(body, globalVariables, classes, functions); // Einen neuen Builder erzeugen

// Transformiert in den WebAssembly Builder,
// generiert den WASM-Code
// und führt diesen aus
new WasmtimeCliRunner().run(
    minipython.builder.wasm.Transform.transform(module).build()
);

// Transformiert in den CBuilder,
// erzeugt den C-Code im übergeben Verzeichnis
minipython.builder.cbuilder.Transform.transform(module).writeProgram(outputFolder);
```

### Builtin-Datentypen

Die in [Datentypen](syntax_definition.md#datentypen) beschriebenen eingebauten
Basis-Datentypen können über die zugehörigen Klassen in der CBuilder-API erzeugt werden.

``` java
new StringLiteral("foo");   // String mit Inhalt "foo" erzeugen
new IntLiteral(5);          // Integer mit Wert "5" erzeugen
new BoolLiteral(true);      // Boolean mit Wert "True" erzeugen
```

### Referenzen

Für den Zugriff auf Variablen, Funktionen und Klassen
nutzen Sie das zur Deklaration verwendete Objekt.
Dieses hat bereits einen Namen,
mit welchem diese Referenz später zur Laufzeit aufgelöst wird,
und so den Zugriff auf das jeweilige Variablen-/Funktions-/Klassen-Objekt im aktuellen Scope ermöglicht.

``` java
// an einem früheren Punkt in Ihrem Programm:
// erzeugen des Funktion-Objekts
FunctionDeclaration funcA = new FunctionDeclaration("a", ...);
// und deklaration der Funktion
functions.add(funcA);

// später, Referenz um die zuvor deklarierte Funktion aufzurufen:
body.add(new Call(funcA)); // funcA wird hier automatisch als Referenz auf die zuvor deklarierte Funktion verwendet
```

### Variablen

``` python
a = 10
```

Variablen müssen Sie zunächst über das Objekt `VariableDeclaration` deklarieren.
Globale Variablen machen Sie anschließend im Builder über das hinzufügen zum Set `globalVariables` bekannt.
Für global Variable kann der Parameter `scope` mit dem Wert `Scope.SCOPE_GLOBAL` dem Konstruktor übergeben werden (dies ist aber auch der Default Wert);
für lokale Variablen *muss* hingegen der Wert `Scope.SCOPE_LOCAL` übergeben werden.

Anschließend können Sie auf die Variablen über die jeweiligen bereits Objekte zugreifen.

``` java
VariableDeclaration varADecl = new VariableDeclaration("a");
globalVariables.add(varADecl); // nur für globale Variablen
```

Die Zuweisung erfolgt über ein Objekt der Klasse `Assignment`,
welches Sie in der Liste `body` dem Builder übergeben.

``` java
Assignment assignIntToA = new Assignment(varADecl, new IntLiteral(10));

body.add(assignIntToA);
```

## Funktionen

### Funktionen aufrufen

``` python
print(a)
```

Für Funktionsaufrufe greifen Sie auf das von Ihnen erstellte Funktionsdeklarations-Objekt zu (s. u.).
Für eine eventuell vorhandene Parameterliste erzeugen Sie sich eine List von `Expression`-Objekten,
die Sie beispielsweise über Referenzen oder Builtin-Datentypen befüllen.
Mit diesen beiden “Zutaten” können Sie nun ein `Call`-Objekt fyr den Funktionsaufurf anlegen und dieses über hinzufügen zur `body` Liste an den Builder übergeben.

[Builtin-Funktionen](semantic_definition.md#globale-builtin-funktionen) können über die entsprechende Konstante in der `Builtins`-Klasse referenziert werden.

``` java
// Referenz zur Builtin-Funktion print
Expression printRef = Builtins.FUNCTION_PRINT;
// Parameterliste mit Referenz auf Variable "a"
List<Expression> parameterRefList = List.of(varADecl);
// Call mit Referenz auf die Funktion und die Parameterliste
Call printCall = new Call(printRef, parameterRefList);

// Funktionsaufurf als Statement im Builder
body.add(printCall);
```

### Funktionen definieren

``` python
def func1(x):
    y = x
    print(y)
    return y
#end

print(func1(a))
```

Eine Funktion wird über ein Objekt der Klasse `FunctionDeclaration` erzeugt.
Der Konstruktor hat einen Parameter `funcName`,
welcher der Name der Funktion wie im Python-Code ist,
und intern zum Auflösen der Referenz zur Funktion innerhalb des Scopes dient.

Der Body einer Funktion wird als Liste ovn Objekten vom Typ `Statement` repräsentiert
und im Konstruktor über den Parameter `body` übergeben.

Die Parameter der Funktion werden als Liste von Objekten vom Typ `VariableDeclaration` repräsentiert
und im Konstruktor über den Parameter `arguments` übergeben.
Die Position des Parameters in der Parameterliste ergibt sich aus der Position in der `arguments` Liste.
Die `VariableDeclaration` Objekte müssen mit dem Argument `Scope.SCOPE_LOCAL` erstellt werden.

Sämtliche lokalen Variablen einer Funktion müssen als `VariableDeclaration` angelegt
und über den Parameter `localVariables` and den Konstruktor von `Function` übergeben werden.
Die `VariableDeclaration` Objekte müssen mit dem Argument `Scope.SCOPE_LOCAL` erstellt werden.

Für die Rückgabewerte im `return`-Statement in der Funktion können Sie die Klasse `ReturnStatement` nutzen.

``` java
// Argument "x" (darf NICHT dem Builder DIREKT übergeben werden)
VariableDeclaration funcArgXDecl = new VariableDeclaration("x", Scope.SCOPE_LOCAL);

// Lokale Variable "y" (darf NICHT dem Builder DIREKT übergeben werden)
VariableDeclaration localVarYDecl = new VariableDeclaration("y", Scope.SCOPE_LOCAL);
// Zuweisung "y = x"
Assignment assignYWithX = new Assignment(localVarYDecl, funcArgXDecl);
// Aufruf von "print(y)"
Call printY = new Call(Builtins.FUNCTION_PRINT, List.of(localVarYDecl));
// Rückgabe aus der Funktion
Statement returnY = new ReturnStatement(localVarYDecl);

// Argumente für Konstruktor von FunctionDeclaration
List<VariableDeclaration> arguments = List.of(funcArgXDecl);
Set<VariableDeclaration> localVariables = Set.of(localVarYDecl);
List<Statement> body = List.of(assignYWithX, printY, returnY);

// FunctionDeclaration erstellen
FunctionDeclaration func1 = new Function("func1", arguments, localVariables, body);
// Funktion dem Builder übergeben
functions.add(func1);
```

``` java
// Aufruf der vorher definierten Funktion
Call callFunc1 = new Call(func1, List.of(varADecl)); // Aufruf von "func1(a)"
Call callPrint = new Call(Builtins.FUNCTION_PRINT, List.of(callFunc1)); // Aufruf von "print(func1(a))"
body.add(callPrint); // Aufruf dem Builder übergeben
```

## Operatoren

Die [Operatoren](semantic_definition.md#operatoren) müssen in entsprechende Methodenaufrufe
der jeweiligen Klasse der linken Seite des Operators umgewandelt werden.

``` python
a + b           # Verwendung eines Operators
a.__add__(b)    # äquivalent als Methodenaufruf
```

### Logische Operatoren

[Logische Operatoren](semantic_definition.md#logische-operatoren) weichen von der obigen
Regel ab. Hier werden im Paket `keywords.bool` spezielle Klassen angeboten.

``` python
k = True
l = False

print(not k)
print(k or l)
print(k and True)
```

Entsprechend würde der obige Python-Code in folgende Aufrufe der CBuilder-API übersetzt:

``` java
// Globale Variable "k" mit dem Wert "true" erzeugen
VariableDeclaration varK = new VariableDeclaration("k");
globalVariables.add(varK);
body.add(new Assignment(varK, new BoolLiteral(true)));

// Globale Variable "l" mit dem Wert "false" erzeugen
VariableDeclaration varL = new VariableDeclaration("l");
globalVariables.add(varL);
body.add(new Assignment(varL, new BoolLiteral(false)));

// Erzeugen und Ausgabe der negierten Variable "k"
Expression notK = new NotKeyword(varK);
body.add(new Call(Builtins.FUNCTION_PRINT, List.of(notK)));

// Erzeugen und Ausgabe von "k or l"
Expression kOrL = new OrKeyword(varK, varL);
body.add(new Call(Builtins.FUNCTION_PRINT, List.of(kOrL)));

// Erzeugen und Ausgabe von "k and True"
Expression kAndTrue = new AndKeyword(varK, new BoolLiteral(true));
body.add(new Call(Builtins.FUNCTION_PRINT, List.of(kAndTrue)));
```

## Kontrollstrukturen

### While-Schleife

``` python
while True:
    print("foo")
#end
```

Die Bedingung muss ein atomarer Boolean sein oder eine Expression (die implizit über die
eingebaute Methode `__bool__` zu einem atomaren Boolean auflöst werden kann). Der
Schleifenkörper besteht wie üblich aus einer Liste von Statements.

``` java
Expression printFoo = new Call(Builtins.FUNCTION_PRINT, List.of(new StringLiteral("foo")));
List<Statement> whileBody = List.of(printFoo);
Statement whileStatement = new WhileStatement(new BoolLiteral(true), whileBody);
body.add(whileStatement);
```

### Bedingte Anweisung (If-Elif-Else)

``` python
if False:
    print("if")
elif k and True:
    print("elif")
else:
    print("else")
#end
```

Der Aufbau der `if`- und `elif`-Blöcke erfolgt nach dem Prinzip eines While-Statements. Der
Aufbau des `else`-Blocks weicht etwas davon ab, da dieser keine `condition` besitzt.

Abschließend werden die einzelnen Blöcke in einem Objekt der Klasse `IfThenElseStatement`
zusammengefasst und dem CBuilder übergeben. Dabei werden gegebenenfalls vorhandene `elif`-
und `else`-Blöcke in ein `Optional` verpackt.

``` java
// Die Bedingungen erstellen
Expression conditionIf = new BoolLiteral(false);
Expression conditionElif = new AndKeyword(varK, new BoolLiteral(true));

// Den Body der einzelnen Blöcke anlegen
Statement printIf = new Call(Builtins.FUNCTION_PRINT, List.of(new StringLiteral("if")));
Statement printElif = new Call(Builtins.FUNCTION_PRINT, List.of(new StringLiteral("elif")));
Statement printElse = new Call(Builtins.FUNCTION_PRINT, List.of(new StringLiteral("else")));
List<Statement> bodyIf = List.of(printIf);
List<Statement> bodyElif = List.of(printElif);
List<Statement> bodyElse = List.of(printElse);

// Die einzelnen Blöcke des Conditional Statements erstellen
ConditionalBlock ifStatement = new ConditionalBlock(conditionIf, bodyIf);
ConditionalBlock elifStatement = new ConditionalBlock(conditionElif, bodyElif);
List<Statement> elseStatement = bodyElse;
List<ConditionalBlock> elifList = List.of(elifStatement);

// Conditional Statement zusammensetzen
Statement conditionalStatement = new IfThenElseStatement(ifStatement, elifList, elseStatement);
body.add(conditionalStatement);
```

## Klassen

### Klasse anlegen

``` python
class A:
    def foo(self, x):
        # Beim originalen Python müsste das Schlüsselwort `pass` für eine leere Function verwendet werden
    #end
#end
```

Klassen können mit Objekten vom Typ `MPyClass` angelegt werden. Dabei muss eine Referenz auf
die jeweilige Elternklasse oder auf `__MPyType_Object` (`Builtins.CLASS_MPY_OBJECT`) mitgegeben werden - alle Klassen
erben also direkt oder indirekt von `__MPyType_Object`.

Alle Klassen müssen die Methode `__init__` implementieren. In dieser `__init__`-Methode muss
zwingend als erstes Statement ein Aufruf von `super` (Klasse `SuperCall`) erfolgen. Falls
dies im geparsten Mini-Python-Code nicht vorhanden ist, muss dies hier entsprechend ergänzt
werden.

Alle Methoden müssen bei der Deklaration/Definition als ersten Parameter `self` besitzen.
Beim Methodenaufruf darf `self` aber nicht in der Parameterliste vorkommen. (Statische
Methoden, die nicht umgesetzt werden müssen, haben kein `self`.)

Methoden haben zwei Methodennamen. Der Parameter `funcName` im `Function`-Konstruktor
entspricht dem Funktions-/Methodennamen im Python-Code. Über diesen Namen werden die
Methoden im jeweiligen Scope aufgelöst. Da Methoden vom CBuilder als normale C-Funktion im
globalen Scope angelegt werden, müssen sie noch einen im gesamten Programm eindeutigen
(internen) Namen bekommen, der mit der Methode `Function#createUniqueCName()` festgelegt
wird - diese Methode wird automatisch vom Konstruktor von `MPyClass` aufgerufen. (Dennoch
können Methoden nicht als normale Funktionen aufgerufen werden, sondern immer nur über den
Kontext ihrer Klasse.) Beim Überschreiben von Methoden muss der `funcName` entsprechend
identisch sein.

*Anmerkung*: Der Parameter `classAttributes` im Konstruktor von `MPyClass` wird nur für
statische Attribute verwendet und kann ignoriert werden, da statische Attribute hier nicht
zum geforderten Sprachumfang gehören. Sie können hier einfach ein `Map.of()` übergeben.

``` java
// Methode "__init__(self)" anlegen
Statement simpleSuperCall = new SuperCall(); // Aufruf von Super
List<Statement> initBody = List.of(simpleSuperCall); // Body der Method: super() kommt als erstes Statement
List<VariableDeclaration> initParamList = List.of(new VariableDeclaration("self", Scope.SCOPE_LOCAL)); // Parameterliste für "__init__" erstellen
FunctionDeclaration methodInit = new FunctionDeclaration("__init__", initParamList, Set.of(), initBody, Scope.SCOPE_LOCAL); // Methode "__init__(self)" erstellen

// Methode "foo(self, x)" anlegen
List<VariableDeclaration> fooParamList = List.of(new VariableDeclaration("self", Scope.SCOPE_LOCAL), new VariableDeclaration("x", Scope.SCOPE_LOCAL));
FunctionDeclaration methodFoo = new FunctionDeclaration("foo", fooParamList, Set.of(), List.of(), Scope.SOCPE_LOCAL); // Methode "foo" mit leerem Body

// Klasse "A" anlegen: Erbt implizit von __MPyType_Object
Set<FunctionDeclaration> functionSetA = Set.of(methodInit, methodFoo); // List der Methoden in A
Expression refToObject = Builtins.CLASS_MPY_OBJECT; // Referenz auf die globale Superklasse __MPyType_Object
MPyClass classA = new MPyClass("A", refToObject, functionSetA, Map.of()); // Klasse "A"

// Und die Klasse "A" dem Builder übergeben
classes.add(classA);
```

### Vererbung

``` python
class B(A):
    def __init__(self):
        super()
    #end

    def foo(self, x):
        print(x)
    #end
#end

i = B()
i.foo("test")
```

Für die Vererbung werden die bereits bekannten Elemente verwendet.

``` java
// "__init__(self)"
Statement simpleSuperCall = new SuperCall();
List<Statement> initBody = List.of(simpleSuperCall);
List<VariableDeclaration> initParamList = List.of(new VariableDeclaration("self", Scope.SCOPE_LOCAL));
FunctionDeclaration methodInitB = new FunctionDeclaration("__init__", initParamList, Set.of(), initBody, Scope.SCOPE_LOCAL);

// "foo(self, x)"
VariableDeclaration varXFoo = new VariableDeclaration("x", Scope.SCOPE_LOCAL);
Statement fooPrint = new Call(Builtins.FUNCTION_PRINT, List.of(varXFoo));
List<VariableDeclaration> fooParamList = List.of(new VariableDeclaration("self", Scope.SCOPE_LOCAL), varXFoo);
List<Statement> fooBody = List.of(fooPrint);
FunctionDeclaration methodFooB = new FunctionDeclaration("foo", fooParamList, Set.of(), fooBody, Scope.SOCPE_LOCAL);

// Klasse "B"
Set<FunctionDeclaration> functionSetB = Set.of(methodInitB, methodFooB);
MPyClass classA = new MPyClass("B", classA, functionSetB, Map.of());
classes.add(classA);


// Ausblick: Benutzung der Klassen
VariableDeclaration varIDecl = new VariableDeclaration("i");
globalVariables.add(varIDecl);

Assignment assignB = new Assignment(varIDecl, new Call(classB, List.of()));
body.add(assignB);

Expression callFoo = new Call(new AttributeReference(varIDecl, "foo"), List.of(new StringLiteral("test")));
body.add(callFoo);
```

Ausblick: Für den Aufruf von Methoden auf Objekten erzeugen Sie wieder einen `Call`,
der als Referenz eine `AttributeReference` erhält.
Diese `AttributeReference` gruppiert eine Referenz auf das Objekt
und den Methodennamen (`funcName`).
Weitere Details
[siehe unten](usage_cbuilder.md#methoden-auf-objekten-aufrufen).

### Verwendung von `self`

``` python
class C:
    def __init__(self, y):
        self.x = y
    #end

    def getX(self):
        return self.x
    #end
#end
```

Der Zugriff auf Attribute des `self`-Objekts erfolgt über eine `AttributeReference`. Die
Zuweisung von Attributen des `self`-Objekts erfolgt über ein `AttributeAssignment`.

``` java
// Weise "self.x" den Methodenparameter "y" zu
VariableDeclaration varDeclSelfInit = new VariableDeclaration("self", Scope.SCOPE_LOCAL);
VariableDeclaration varDeclYInit = new VariableDeclaration("y", Scope.SCOPE_LOCAL);
Statement assignSelfX = new AttributeAssignment(new AttributeReference(varDeclSelfInit, "x"), varDeclYInit);

// Zugriff auf "self.x" in "getX(self)"
VariableDeclaration varDeclSelfGetX = new VariableDeclaration("self", Scope.SCOPE_LOCAL);
Expression getSelfX = new AttributeReference(varDeclSelfGetX, "x");
Statement returnX = new ReturnStatement(getSelfX);

// "__init__(self, y)"
List<Statement> initBodyWithSelfAssign = List.of(simpleSuperCall, assignSelfX);
List<VariableDeclaration> initParamListWithY = List.of(varDeclSelfInit, varDeclYInit);
FunctionDeclaration methodInitWithSelf = new FunctionDeclaration("__init__", initParamListWithY, Set.of(), initBodyWithSelfAssign, Scope.SCOPE_LOCAL);

// "getX(self)"
List<Statement> getXBody = List.of(returnX);
List<VariableDeclaration> paramListGetX = List.of(varDeclSelfGetX);
FunctionDeclaration getX = new FunctionDeclaration("getX", paramListGetX, Set.of(), getXBody, Scope.SCOPE_LOCAL);

// Class "C"
Set<FunctionDeclaration> functionSetC = Set.of(methodInitWithSelf, getX);
MPyClass classC = new MPyClass("C", Builtins.CLASS_MPY_OBJECT, functionSetC, Map.of());
classes.add(classC);
```

### Methoden auf Objekten aufrufen

``` python
objectC = C(5)

print(objectC.getX())
```

Das Instanziieren einer Klasse erfolgt wie der Aufruf einer Funktion.

Der Aufruf von Methoden wird über ein Objekt der Klasse `AttributeReference` und einen
`Call` realisiert. Diese `AttributeReference` gruppiert den Methodennamen (`funcName`) und
eine Referenz auf das Objekt. Der Parameter `self` darf beim Methodenaufruf nicht übergeben
werden.

``` java
// Variable "objectC"
VariableDeclaration varObjectCDecl = new VariableDeclaration("objectC");
globalVariables.add(varObjectCDecl);

// Erzeugung und Zuweisung eines Objekts der Klasse "C" mit "__init__(self, 5)"
Call newC = new Call(classC, List.of(new IntLiteral(5)));
Assignment assignObjectC = new Assignment(varObjectCDecl, newC);
body.add(assignObjectC);

// Auf dem Objekt der Klasse "C" die Methode "getX" aufrufen und Rückgabewert ausgeben.
Expression callGetX = new Call(new AttributeReference(varObjectCDecl, "getX"), List.of());
body.add(new Call(Builtins.FUNCTION_PRINT, List.of(callGetX)));
```

Analog können Sie (nicht-logische) [Operatoren](semantic_definition.md#operatoren) in den
passenden Methodenaufruf umsetzen. Dabei können die Operator-Methoden der Builtin-Datentypen
ohne vorherige Deklaration verwendet werden. Der Parameter `self` darf nicht in der
Parameterliste vorkommen, hier wird automatisch das Objekt, auf dem der Aufruf stattfindet,
ergänzt.

``` python
a + 10
a.__add__(10)   # Äquivalenter Methodenaufruf
```

``` java
AttributeReference varAAdd = new AttributeReference(varADecl, "__add__"); // Referenz auf Methode `__add__` des Objekts `a`
Call addInteger = new Call(varAAdd, List.of(new IntLiteral(10))); // Erzeugen des Aufrufs `a + 10`
body.add(addInteger);
```
