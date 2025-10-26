# 🛒 Application E-Commerce - JSF & JPA

Application web e-commerce développée avec **Jakarta EE**, **JSF** et **JPA**.

---

## 📖 Description

Plateforme e-commerce complète pour clients et administrateur.

---

## ✨ Fonctionnalités

### 👥 Espace Client
- Inscription et connexion
- Navigation dans le catalogue de produits
- Gestion du panier (ajout, modification, suppression)
- Validation de commandes
- Historique des commandes

### 👨‍💼 Espace Administrateur
- Gestion des produits (CRUD)
- Gestion des commandes (mise à jour des statuts)

---

## 🏗️ Architecture

### Technologies
- **JSF** 4.0
- **JPA** 3.0 (Hibernate)
- **PrimeFaces** 14.0
- **WildFly** 37
- **MySQL** 8.0
- **Maven**

### Entités JPA
- `User` - Utilisateurs (clients et admins)
- `Produit` - Catalogue de produits
- `Panier` - Panier d'achat
- `LignePanier` - Lignes du panier
- `Commande` - Commandes validées
- `LigneCommande` - Détails des commandes

---

## 📋 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/salmaaz29/ecommerce-jee-jsf.git
cd ecommerce-jee-jsf
```

### 2. Créer la base de données
```sql
CREATE DATABASE ecommerce_db_2;
```

### 3. Configurer WildFly

Éditez `standalone.xml` (dans `WILDFLY_HOME/standalone/configuration/`) :

```xml
<datasource jndi-name="java:/MySqlDS" pool-name="MySqlDS" enabled="true">
    <connection-url>jdbc:mysql://localhost:3306/ecommerce_db_2?useSSL=false&amp;serverTimezone=UTC</connection-url>
    <driver>mysql</driver>
    <security>
        <user-name>root</user-name>
        <password>votre_mot_de_passe</password>
    </security>
</datasource>
```

### 4. Compiler et déployer
```bash
mvn clean package
cp target/ecommerce_jsf-1.0-SNAPSHOT.war WILDFLY_HOME/standalone/deployments/
```

### 5. Démarrer WildFly
```bash
cd WILDFLY_HOME/bin
./standalone.sh  # Linux/Mac
standalone.bat   # Windows
```

### 6. Accéder à l'application
🌐 http://localhost:8080/ecommerce_jsf-1.0-SNAPSHOT/

---

### Objectifs
✅ Maîtriser l'API JPA  
✅ Développer avec JSF 4.0  
✅ Intégrer PrimeFaces  
✅ Gérer la persistance avec Hibernate

---

## 📁 Structure du Projet

```
src/
├── main/
│   ├── java/ma.fstt.ecommerce_jsf/
│   │   ├── Entities/      # Entités JPA
│   │   ├── DAO/           # Accès aux données
│   │   └── Beans/         # Managed Beans JSF
│   ├── webapp/
│   │   ├── *.xhtml        # Pages JSF
│   │   └── WEB-INF/
│   └── resources/
│       └── META-INF/
│           └── persistence.xml
```
