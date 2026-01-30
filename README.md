# URL Shortener

Aplicație full-stack pentru scurtarea URL-urilor, cu statistici, cache Redis și rate limiting. Construită cu arhitectură layered (Clean Architecture), gata de deployment cu Docker.

---

## Cuprins

- [Funcționalități](#funcționalități)
- [Tehnologii](#tehnologii)
- [Arhitectură](#arhitectură)
- [Cerințe](#cerințe)
- [Rulare locală (fără Docker)](#rulare-locală-fără-docker)
- [Rulare cu Docker](#rulare-cu-docker)
- [API](#api)
- [Structura proiectului](#structura-proiectului)
- [Variabile de mediu](#variabile-de-mediu)
- [Oprire aplicație](#opriere-aplicație)
- [Testing](#testing)

---

## Funcționalități

- **Scurtare URL** – transformă URL-uri lungi în link-uri scurte (ex: `http://localhost:8080/abc123`)
- **Redirect** – accesarea URL-ului scurt redirecționează către URL-ul original (HTTP 302)
- **Statistici** – număr de click-uri, data creării, URL original; căutare statistici după cod scurt
- **Cache Redis** – redirecționări rapide pentru URL-uri accesate frecvent
- **Rate limiting** – limitare cereri per IP (ex: 100 request-uri/minut pentru creare URL)
- **Validare** – URL-uri validate (http/https); mesaje de eroare clare (404, 400, 429)
- **Deployment** – Docker Compose pentru PostgreSQL, Redis, Backend și Frontend

---

## Tehnologii

**Backend**  
Java 17 · Spring Boot · Spring Web · Spring Data JPA · Maven

**Frontend**  
React 18 · TypeScript · Vite · npm

**Baza de date**  
PostgreSQL 15

**Cache & rate limiting**  
Redis 7

**Deployment**  
Docker · Docker Compose

---

## Arhitectură

- **Backend:** Layered (Controller → Service → Repository); DTOs, excepții custom, validare.
- **Frontend:** Componente React (UrlShortener, UrlDisplay, UrlStats), API service, tipuri TypeScript.
- **Cod scurt:** Base62 encoding (coduri scurte, URL-safe).
- **Cache:** Cache-aside cu Redis; fallback la PostgreSQL dacă Redis nu e disponibil.
- **Rate limiting:** Redis (contor per IP + endpoint); fallback permis dacă Redis e down.

---

## Cerințe

- **Pentru rulare locală:** Java 17, Node.js 18+, Maven, PostgreSQL, Redis (sau doar Docker)
- **Pentru Docker:** Docker Desktop (sau Docker Engine + Docker Compose)

---

## Rulare locală (fără Docker)

### 1. PostgreSQL și Redis

- Pornește PostgreSQL pe `localhost:5432`, creează baza `urlshortener`.
- Pornește Redis pe `localhost:6379` (ex: `docker run -d -p 6379:6379 redis:7-alpine`).

### 2. Backend

```bash
cd Backend
mvn spring-boot:run
```

Backend: `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:3000` (proxy către backend `/api` în Vite).

---

## Rulare cu Docker

1. **Deschide proiectul** și intră în rădăcina proiectului (același nivel cu `docker-compose.yml`).

2. **Creează fișierul `.env`** (copiază din `.env.example`):

   ```bash
   copy .env.example .env
   ```

   Editează `.env` și setează parola PostgreSQL:

   ```env
   POSTGRES_DB=urlshortener
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=parola_ta
   ```

3. **Asigură-te că Docker Desktop rulează** (iconița verde în system tray).

4. **Pornește toate serviciile:**

   ```bash
   docker compose up --build
   ```

5. **Acces:**
   - **Frontend:** http://localhost:3000  
   - **Backend API:** http://localhost:8080  

6. **Oprire:** vezi secțiunea [Oprire aplicație](#opriere-aplicație).

---

## API

### Creare URL scurtat

```http
POST /api/urls
Content-Type: application/json

{
  "originalUrl": "https://example.com/path"
}
```

**Răspuns 200:**  
`{ "shortCode": "abc123", "shortUrl": "http://localhost:8080/abc123", "originalUrl": "...", "clickCount": 0, "createdAt": "...", "expiresAt": null }`

**Erori:** 400 (URL invalid), 429 (rate limit depășit).

### Redirect

```http
GET /{shortCode}
```

**Răspuns 302** cu header `Location: <originalUrl>`.

**Erori:** 404 (URL inexistent / expirat / inactiv).

### Statistici URL

```http
GET /api/urls/{shortCode}
```

**Răspuns 200:** același format ca la creare (inclusiv `clickCount`, `createdAt`).

**Erori:** 404.

---

## Structura proiectului

```
Url Shortener/
├── Backend/                          # Spring Boot
│   ├── src/main/java/urlshort/com/backend/
│   │   ├── BackendApplication.java
│   │   ├── Config/                  # RedisConfig
│   │   ├── Controller/              # UrlController (REST)
│   │   ├── dto/                     # CreateUrlRequest, UrlResponse
│   │   ├── Entity/                  # Url (JPA)
│   │   ├── Exception/               # GlobalExceptionHandler, custom exceptions
│   │   ├── Repository/              # UrlRepository (JPA)
│   │   ├── Service/                 # UrlService, RateLimitService, RedisService
│   │   └── util/                    # Base62Encoder
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   ├── pom.xml
│   └── .dockerignore
├── frontend/                         # React + TypeScript + Vite
│   ├── src/
│   │   ├── components/              # UrlShortener, UrlDisplay, UrlStats
│   │   ├── services/                # api.ts
│   │   ├── types/                   # TypeScript interfaces
│   │   ├── App.tsx, main.tsx
│   │   └── vite-env.d.ts
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vite.config.ts
│   ├── package.json
│   └── .dockerignore
├── docker-compose.yml
├── .env.example
├── .env                              # Nu se comite (în .gitignore)
├── .gitignore
└── README.md
```

---

## Variabile de mediu

| Variabilă | Descriere | Default (în docker-compose) |
|-----------|-----------|-----------------------------|
| `POSTGRES_DB` | Nume baza de date | `urlshortener` |
| `POSTGRES_USER` | User PostgreSQL | `postgres` |
| `POSTGRES_PASSWORD` | Parolă PostgreSQL | — (obligatoriu în `.env`) |
| `APP_BASE_URL` | URL de bază pentru link-uri scurte | `http://localhost:8080` |

Backend-ul primește din `docker-compose.yml`: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`.

---

## Oprire aplicație

**Când rulezi cu Docker:**

- În terminalul unde rulează `docker compose up`: apasă **Ctrl + C**.
- Sau într-un alt terminal, din rădăcina proiectului:

  ```bash
  docker compose down
  ```

- Pentru ștergere și volume (date din PostgreSQL):

  ```bash
  docker compose down -v
  ```

---

## Testing

- **Backend:** `cd Backend && mvn test`
- **Frontend:** `cd frontend && npm run test` (dacă ai configurat teste)

---

## Licență

Proiect educațional / portfolio. Adaptează după nevoie.
