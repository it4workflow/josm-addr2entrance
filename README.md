## Beschreibung
Das Plugin stellt ein paar Überprüfungen und vereinfachte Aktionen im Bereich [Eingänge](https://wiki.openstreetmap.org/wiki/Key:entrance) im Zusammenspiel mit [Adressen](https://wiki.openstreetmap.org/wiki/Key:addr) zur Verfügung.

### Wie kann das Plugin genutzt werden?
Entweder über das Menü Daten>Addr2Entrance oder über die Tastaturkombination SHIFT+K

### Wann kann das Plugin genutzt werden?
Nachdem ein einzelner Node selektiert wurde, welcher
- in einem geschlossenen Weg liegt, z.B. Gebäude
- nicht von einer anderen angrenzenden Linie genutzt wird

### Was macht das Plugin?
Zunächst kommt wie gewohnt das "Eingang"-Preset. Anschließend werden ein paar Prüfungen gemacht und das Ergebnis sowie mögliche Aktionen angezeigt.

### Welche Prüfungen nimmt das Plugin vor und welche Aktionen bietet es an?
#### Bereich Eingänge
- Mehrere Haupteingänge vorhanden? (Sollte die Ausnahme sein, deswegen eine Warnung)
- Eingang ist noch mit veraltetem building=entrance ausgezeichnet, es wird angeboten dieses zu löschen
#### Bereich Adresse
- Weder am Gebäude noch am Eingang gibt es eine Adresse, es wird angeboten (mit Hilfe des "Adresse"-Presets) eine zu erfassen
- Der eine Eingang hat eine Adresse, es wird angeboten diese ans Gebäude zu verschieben oder zu kopieren
- Das Gebäude hat eine Adresse, es wird angeboten diese an den einen Eingang zu verschieben oder zu kopieren
- Wird ein zweiter Eingang erfasst und der erste hält bereits eine Adresse, wird vorgeschlagen die Adresse ans Gebäude zu verschieben oder für eine neue Adresse für den neuen/zweiten Eingang zu erfassen
- Gibt es mehrere Eingänge mit Adresse, wird vorgeschlagen für den neuen Eingang ebenfalls eine zu erfassen
- Gibt es mehrere Eingänge mit *und* ohne Adresse gibt es eine Warnung zur Prüfung

## Author
Harald Hartmann <osm@haraldhartmann.de>, [OSM-Profile](https://wiki.openstreetmap.org/wiki/User:Haribo)

### Thanks
Oliver Raupach and his HouseNumberTaggingTool - initial blueprint for this tool.

