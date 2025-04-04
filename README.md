# DeliveryPro Platform

Access the live application here:  
👉 [DeliveryPro Platform](https://main-bvxea6i-jenwo4in7lt44.fr-3.platformsh.site/)

## 🔐 Authentification

### Planificateur (Claudia Tessier)
- **Email:** ctr@mobilis.fr
- **Mot de passe:** 147258

### Livreur (Malik Kristophe)
⚠️ Application Livreur **pas encore ajoutée**

- **Email:** mkr@mobilis.fr
- **Mot de passe:** 123456

---

## 📁 Structure du Projet

- `Client/` – Application Angular (non compilée)
- `backend/` – Application Spring Boot

---

## 🚀 Lancer l'application en local

### 1. Avec Docker Compose

```bash
docker compose -f docker/docker-compose-local.yml up --detach
cd backend
mvn clean install
mvn spring-boot:run -f server
```