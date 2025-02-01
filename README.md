# Online Knihovna

## Úvod

Cílem tohoto projektu je vytvořit webovou aplikaci pro online knihovnu, která umožňuje uživatelům registraci, rezervaci
knih pro fyzické výpůjčky, online půjčování knih, psaní recenzí a nákup elektronických knih.

## Technologie

- **Frontend:** React, Vite, SCSS
- **Backend:** Java, Spring Boot, PostgreSQL
- **Autentizace a autorizace:** JWT (JSON Web Tokens)
- **Emailová notifikace:** JavaMail API

## Architektura aplikace

Aplikace je postavena na architektuře klient-server s frontendem (React) a backendem (Spring Boot). Data jsou uchovávána
v relační databázi (PostgreSQL), frontend komunikuje s backendem prostřednictvím REST API.

### Frontend (React):

- **Komponenty:** Uživatelé interagují s aplikací pomocí různých komponent, které vykreslují obsah a reagují na
  uživatelské akce (např. rezervace knih, přihlášení).
- **Context API:** Používá se pro správu globálního stavu aplikace (např. přihlášení uživatele, aktuální rezervace).
- **Routing:** React Router pro navigaci mezi stránkami.

### Backend (Spring Boot):

- **Controller:** Každá hlavní funkcionalita (např. rezervace, uživatelé, knihy) je spravována pomocí controllerů, které
  zpracovávají HTTP požadavky.
- **Service:** Logika aplikace je implementována ve službách (services), které se starají o operace s daty.
- **Repository:** Data jsou spravována pomocí repository, které poskytují přístup k databázi (ORM - JPA).
- **Security:** Ochrana aplikace pomocí JWT tokenů pro autentizaci a autorizaci uživatelů.

### Databáze (PostgreSQL):

- Data jsou uložena v relační databázi, která obsahuje tabulky pro uživatele, knihy, rezervace, výpůjčky, nákupy a
  recenze.

#### Databázové tabulky

- **User:** Uchovává informace o uživatelských účtech.
- **Book:** Uchovává informace o knihách.
- **Booking:** Uchovává informace o rezervacích knih.
- **Order:** Uchovává informace o rezervacích.
- **Purchase:** Uchovává informace o nákupech knih.
- **Review:** Uchovává recenze knih.

---

## Funkce aplikace

### 1. **Uživatelský účet**

- **Registrace:** Uživatelé se mohou zaregistrovat pomocí registračního formuláře.
- **Přihlášení:** Po registraci se uživatelé mohou přihlásit.
- **Správa účtu:** Uživatelé mohou upravit své údaje.

### 2. **Rezervační systém pro fyzické výpůjčky**

- **Rezervace knihy:** Uživatelé mohou rezervovat knihy pro fyzické výpůjčky.
- **Rezervační fronta:** Systém zajišťuje správu rezervací podle FIFO principu.
- **Oznámení o dostupnosti:** Uživatelé dostanou e-mail o dostupnosti knihy.

### 3. **Online výpůjčky a nákup knih**

- **Okamžité zapůjčení:** Některé knihy lze okamžitě půjčit online.
- **Nákup knih "na neurčito":** Uživatelé mohou zakoupit elektronické knihy.

### 4. **Recenze knih**

- **Přidávání recenzí:** Uživatelé mohou přidávat recenze na knihy.
- **Zobrazení recenzí:** Recenze ostatních uživatelů jsou zobrazeny u knih.

### 5. **Správa katalogu knih**

- **Vyhledávání a filtrování:** Uživatelé mohou vyhledávat a filtrovat knihy podle různých parametrů (název, autor,
  žánr).

---

## Jak spustit aplikaci

1. Vytvoř potřebný .env soubor v kořenové složce projektu s proměnnými prostředí

```
SPRING_DATASOURCE_URL_TESTS="jdbc:postgresql://localhost:5432/your-database-name"

SPRING_DATASOURCE_DATABASE="your-database-name"
SPRING_DATASOURCE_USERNAME="your-database-username"
SPRING_DATASOURCE_PASSWORD="your-database-password"

SMTP_USERNAME="your-smtp-username"
SMTP_PASSWORD="your-smtp-password"
SERVICE_MAIL_ENABLED=true

JWT_SECRET="your-jwt-secret"
```

2. npm run app

---

Tento projekt je semestrální prací pro předmět NNPRO (Ročníkový projekt) - Univerzita Pardubice.
