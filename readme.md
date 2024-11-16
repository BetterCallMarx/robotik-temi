# Anleitung und Benutzung der App

## Anleitung

### 1: Projekt in Android Studio öffnen und builden
- Die entpackte Datei als Projekt in Android Studio öffnen und das Projekt builden.

### 2: Verbindung mit einem Temi-Roboter herstellen
- Mittels `adb` mit dem Temi-Roboter verbinden:
  1. Wechseln Sie in das Verzeichnis, in dem sich `adb` befindet. Standardmäßig ist dies unter `AppData\Local\Android\Sdk\platform-tools`.
  2. Führen Sie den Befehl `adb connect <IP des Roboters>` aus. (Die IP des Roboters muss bekannt sein, und der Port des Roboters muss geöffnet sein.)

### 3: Programm starten
- Gelegentlich muss vor dem Starten der App der Temi-Roboter unter **Available Devices** ausgewählt werden.

---

## Benutzung der App

### Tour starten
1. **Auf "Tour starten" drücken.**
2. **Ort auswählen:**
   - Wählen Sie auf dem nächsten Bildschirm den Ort aus, an dem sich der Temi befindet. Die Orte werden aus der Datenbank geladen.
3. **Länge und Ausführlichkeit der Tour festlegen:**
   - Beide Optionen müssen ausgewählt werden, bevor eine Tour gestartet werden kann.
   - Je nach Auswahl der Ausführlichkeit werden nur die entsprechende Art der Texte gesprochen:
     - *Ausführlich*: Detaillierte Beschreibungen.
     - *Nicht ausführlich*: Kurzbeschreibungen.
     - Mischformen werden nicht verwendet.
   - Wenn *Individuelle Tour* ausgewählt wird, können auf der nächsten Seite die gewünschten Locations ausgewählt werden.

### Tour ausführen
4. Auf der folgenden Seite befindet sich ein **Start-Button**, mit dem die Tour gestartet wird:
   - Eine Tour ist nicht zirkulär; sie hat einen festen Start- und Endpunkt.
5. **Pausieren der Tour:**
   - Mit dem **Stop-Button** kann die Tour pausiert werden.
   - Wird danach erneut der **Start-Button** gedrückt, beginnt die Tour von vorne.

### Nach der Tour
6. Nach Abschluss der Tour wird der Nutzer auf eine Bewertungsseite weitergeleitet:
   - Die Bewertung wird in einer `.txt`-Datei gespeichert.

--- 

