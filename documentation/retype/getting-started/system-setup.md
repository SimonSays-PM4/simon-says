---
label: System Setup
---
!!! :zap: DRAFT :zap:
Diese Seite ist noch in Bearbeitung
!!!
# System Setup

In der Vorbereitung auf deinen Event benötigst du eine funktionierende Umgebung. Hier findest du eine Anleitung, wie du die notwendigen Tools installierst und konfigurierst. Wir stellen die Applikation SimonSays in einer Cloud-Umgebung zur Verfügung.

## Voraussetzungen
1. Cloud-Anbieter-Konto (z.B. AWS, Google Cloud, Azure)
2. Kubernetes-Cluster für das Hosting von Frontend und Backend
3. MySQL-Datenbankserver
4. Drucker für die Bestellungsausdrucke
5. Raspberry Pi als lokales Printing-Gateway
6. Stabiler Internetzugang am Event-Ort
7. Mobile Endgeräte für das Event Staff (Smartphones oder Tablets)
8. Tablets für die Kitchen Displays

## Cloud-Setup

### Schritt 1: Kubernetes-Cluster erstellen
1. Melde dich bei deinem Cloud-Anbieter an und erstelle ein neues Kubernetes-Cluster.
    - [AWS EKS](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html)
    - [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs/quickstart)
    - [Azure Kubernetes Service](https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough)

### Schritt 2: MySQL-Datenbank einrichten
1. Erstelle eine MySQL-Datenbankinstanz in deiner Cloud-Umgebung.
    - [AWS RDS](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_GettingStarted.CreatingConnecting.MySQL.html)
    - [Google Cloud SQL](https://cloud.google.com/sql/docs/mysql/create-instance)
    - [Azure Database for MySQL](https://docs.microsoft.com/en-us/azure/mysql/quickstart-create-mysql-server-database-using-azure-portal)

2. Notiere dir die Verbindungsdetails (Host, Port, Benutzername, Passwort).

### Schritt 3: Anwendungscode und Docker-Images vorbereiten
1. Clone das SimonSays Repository:
   ```bash
   git clone https://github.com/SimonSays-PM4/simon-says.git
   cd SimonSays
   ```
2. Erstelle Docker-Images für Frontend und Backend:
   ```bash
   docker build -t simonsays-frontend ./frontend
   docker build -t simonsays-backend ./backend
   ```

### Schritt 4: Docker-Images in eine Container Registry hochladen
1. Melde dich bei deiner Cloud Container Registry an und lade die Docker-Images hoch:
    - [Amazon ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/getting-started-cli.html)
    - [Google Container Registry](https://cloud.google.com/container-registry/docs/pushing-and-pulling)
    - [Azure Container Registry](https://docs.microsoft.com/en-us/azure/container-registry/container-registry-get-started-docker-cli)

   Beispiel für AWS ECR:
   ```bash
   aws ecr create-repository --repository-name simonsays-frontend
   aws ecr create-repository --repository-name simonsays-backend
   docker tag simonsays-frontend:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/simonsays-frontend:latest
   docker tag simonsays-backend:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/simonsays-backend:latest
   docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/simonsays-frontend:latest
   docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/simonsays-backend:latest
   ```

### Schritt 5: Kubernetes-Deployments und Services erstellen
1. Erstelle Kubernetes-Manifestdateien für Frontend, Backend und MySQL-Verbindung (z.B. `frontend-deployment.yaml`, `backend-deployment.yaml`, `mysql-secret.yaml`).

   Beispiel für das Backend-Deployment:
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: simonsays-backend
   spec:
     replicas: 3
     selector:
       matchLabels:
         app: simonsays-backend
     template:
       metadata:
         labels:
           app: simonsays-backend
       spec:
         containers:
         - name: backend
           image: <aws_account_id>.dkr.ecr.<region>.amazonaws.com/simonsays-backend:latest
           ports:
           - containerPort: 3000
           env:
           - name: MYSQL_HOST
             valueFrom:
               secretKeyRef:
                 name: mysql-secret
                 key: host
           - name: MYSQL_USER
             valueFrom:
               secretKeyRef:
                 name: mysql-secret
                 key: user
           - name: MYSQL_PASSWORD
             valueFrom:
               secretKeyRef:
                 name: mysql-secret
                 key: password
           - name: MYSQL_DATABASE
             value: "simonsays"
   ```

2. Erstelle die Ressourcen im Kubernetes-Cluster:
   ```bash
   kubectl apply -f mysql-secret.yaml
   kubectl apply -f backend-deployment.yaml
   kubectl apply -f frontend-deployment.yaml
   ```

3. Erstelle Services, um den Zugriff auf Frontend und Backend zu ermöglichen:
   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: simonsays-backend-service
   spec:
     selector:
       app: simonsays-backend
     ports:
       - protocol: TCP
         port: 80
         targetPort: 3000
     type: LoadBalancer
   ```

4. Füge alle notwendigen Ingress-Ressourcen hinzu, um den externen Zugriff auf die Applikation zu ermöglichen.

### Schritt 6: Teste die Applikation
1. Überprüfe die Deployments und Services im Kubernetes-Dashboard oder mit `kubectl get pods`, `kubectl get services`.
2. Öffne die Anwendung im Browser über die LoadBalancer-IP oder die konfigurierte Domain.

## Hardware-Aufbau vor Ort

### Schritt 1: Drucker einrichten
Stelle sicher, dass der Drucker korrekt installiert und konfiguriert ist. Verbinde ihn mit dem Raspberry Pi, der als lokales Printing-Gateway dient.

### Schritt 2: Raspberry Pi konfigurieren
1. Installiere das Betriebssystem auf dem Raspberry Pi.
2. Verbinde den Raspberry Pi mit dem Drucker.
3. Installiere die notwendigen Drucker-Treiber und richte das Netzwerk-Printing ein.
4. Stelle sicher, dass der Raspberry Pi mit dem Internet verbunden ist und der Druckserver läuft.

### Schritt 3: Internetzugang sichern
1. Teste die Internetverbindung am Event-Ort.
2. Stelle sicher, dass der Router und alle benötigten Netzwerkkomponenten funktionsfähig sind.
3. Richte gegebenenfalls einen separaten Netzwerkbereich für die Applikation ein, um Stabilität und Sicherheit zu gewährleisten.

### Schritt 4: Mobile Endgeräte vorbereiten
1. Stelle sicher, dass alle mobilen Endgeräte (Smartphones oder Tablets) die SimonSays-Applikation installiert haben und funktionsfähig sind.
2. Teste die Verbindung zu der Applikation und den Druckern.

### Schritt 5: Tablets für Kitchen Displays konfigurieren
1. Installiere die SimonSays-Applikation auf den Tablets.
2. Platziere die Tablets in der Küche an gut sichtbaren Orten.
3. Teste die Anzeige und Funktionalität der Applikation auf den Tablets.

Durch diese Schritte stellst du sicher, dass die Applikation SimonSays reibungslos während des Events läuft und alle notwendigen Hardware-Komponenten korrekt eingerichtet sind.