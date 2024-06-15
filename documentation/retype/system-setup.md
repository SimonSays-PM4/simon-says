---
label: System Setup
icon: ":building_construction:"
order: 900
---
# System Setup

In der Vorbereitung auf deinen Event benötigst du eine funktionierende Umgebung. Hier findest du eine Anleitung, wie du die notwendigen Tools installierst und konfigurierst. Wir stellen die Applikation SimonSays in einer Cloud-Umgebung zur Verfügung.

!!!
Diese Anleitung beschreibt das technische Setup und ist für technisch versierte Personen gedacht. Wenn du Unterstützung benötigst, kannst du dich gerne [per Mail](mailto:hello@simonsays.ch) an uns wenden.
!!!

## Voraussetzungen
1. Cloud-Anbieter-Konto (z.B. AWS, Google Cloud, Azure)
2. Kubernetes-Cluster für das Hosting von Frontend und Backend
4. Drucker für die Bestellungsausdrucke
5. Raspberry Pi als lokales Printing-Gateway
6. Stabiler Internetzugang am Event-Ort
7. Mobile Endgeräte für das Event Staff (Smartphones oder Tablets)
8. Tablets für die Kitchen Displays

## Cloud-Setup
### Schritt 1: Helm Chart bereitstellen
Das Helm-Chart enthält das Frontend, das Backend und die Datenbank. Sämtliche Konfigurationen, Services als auch das Ingress Objekt sind im Chart enthalten.

Für die Installation des Helm-Charts muss folgender Befehl ausgeführt werden:
```bash
cd helm
helm dependency update . 
helm package . -d output
package=$(find output -name "*.tgz")
helm upgrade simon-says $package  -f values.yaml  --set mysql.auth.rootPassword=<<my-secret-password>>  --set mysql.auth.password=<<my-secret-password>> --install -n simon-says --wait
```

Das `application.yaml` des Backends kann im `values.yaml` ergänzt werden.

### Schritt 2: Teste die Applikation
1. Überprüfe die Deployments und Services im Kubernetes-Dashboard oder mit `kubectl get pods`, `kubectl get services`.
2. Öffne die Anwendung im Browser über die im Ingress-Objekt definierte URL.

## Hardware-Aufbau vor Ort

### Schritt 1: Drucker einrichten
Stelle sicher, dass der Drucker korrekt installiert und konfiguriert ist. Verbinde ihn mit demselben Netzwerk wie den Raspberry Pi, der als lokales Printing-Gateway dient.

### Schritt 2: Raspberry Pi konfigurieren
1. Installiere das Betriebssystem auf dem Raspberry Pi.
2. Installiere Docker und Docker-Compose auf dem Raspberry Pi.
3. Starten des Druck-Servers auf dem Raspberry Pi mit dem [Docker Compose File](https://github.com/SimonSays-PM4/simon-says/blob/main/printer/compose.yaml).
4. Stelle sicher, dass der Raspberry Pi mit dem Internet verbunden ist und der Druckserver läuft.

### Schritt 3: Internetzugang sichern
1. Teste die Internetverbindung am Veranstaltungsort.
2. Stelle sicher, dass der Router und alle benötigten Netzwerkkomponenten funktionsfähig sind.
3. Richte gegebenenfalls einen separaten Netzwerkbereich für die Applikation ein, um Stabilität und Sicherheit zu gewährleisten.

### Schritt 4: Mobile Endgeräte vorbereiten
1. Stelle sicher, dass alle mobilen Endgeräte (Smartphones oder Tablets) die SimonSays-Applikation installiert haben und funktionsfähig sind.
2. Teste die Verbindung zu der Applikation und den Druckern.

### Schritt 5: Tablets für Kitchen Displays konfigurieren
1. Öffne die SimonSays-Applikation auf den Tablets.
2. Platziere die Tablets in der Küche an gut sichtbaren Orten.
3. Teste die Anzeige und Funktionalität der Applikation auf den Tablets.

Mit diesen Schritten stellst du sicher, dass die SimonSays-Applikation während der Veranstaltung reibungslos läuft und alle notwendigen Hardware-Komponenten korrekt konfiguriert sind.