# Vive Paso a Paso

Aplicación móvil desarrollada en **Kotlin + Jetpack Compose** para gestionar hábitos saludables, registrar rutinas diarias y motivar al usuario mediante estadísticas, recordatorios y recomendaciones basadas en APIs externas.

---

## Descripción General

Vive Paso a Paso permite al usuario llevar control de sus hábitos esenciales:

* Hidratación
* Sueño
* Alimentación
* Actividad física

La app proporciona recordatorios, estadísticas semanales/mensuales y recomendaciones generadas mediante la integración de **Nutritionix API** y **OpenWeather API**, además de autenticación segura con **Firebase Authentication**.

---

## Objetivo del Proyecto

Crear una aplicación **intuitiva, ligera y accesible** que permita a los usuarios organizar y mejorar su estilo de vida mediante la gestión simple y motivadora de sus hábitos diarios.

---

## Usuarios Objetivo

* **Julián (20 años)** – Necesita regularizar sus horarios de sueño.
* **Luisa (28 años)** – Quiere recibir recordatorios para hidratarse y mantenerse activa.
* **Pablo (35 años)** – Desea mejorar su alimentación y controlar el estrés mediante estadísticas.

---

## Funcionalidades Principales

### ✔ Gestión de Usuarios

* Registro e inicio de sesión con Firebase Authentication.
* Login con email/contraseña o proveedor externo (Google).
* Recuperación de contraseña.

### ✔ Dashboard (Home)

* Resumen del día:

  * Agua consumida
  * Horas dormidas
  * Pasos
  * Calorías ingeridas
* Botón "Agregar hábito"
* Tip motivacional del día

### ✔ Registro de Hábitos

* Registro de:

  * Agua (L)
  * Sueño (hrs)
  * Ejercicio (min)
  * Alimentación (calorías)
* Alimentación integra Nutritionix API
* Opción de añadir notas

### ✔ Estadísticas & Progreso

* Gráficas semanales y mensuales
* Filtros por hábito
* Cálculo de promedios y totales
* Sistema de rachas

### ✔ Perfil & Ajustes

* Configuración de recordatorios
* Personalización de metas
* Gestión de cuenta

### ✔ Integraciones externas

* **Nutritionix API** → calorías y nutrientes
* **OpenWeather API** → recomendaciones de actividad según clima

---

## Arquitectura del Proyecto

* **Jetpack Compose** para toda la UI
* **Jetpack Navigation** para navegación
* Arquitectura **MVI**:

  * ViewModel
  * Estados (State)
  * Eventos
  * Use Cases
* **Firebase Authentication**
* **Room o DataStore** para almacenamiento local

El diagrama ASCII completo se encuentra en el archivo **ESTRUCTURA_PROYECTO.md**.

---

## Estructura del Proyecto (Resumen)

```
app/
 ├─ ui/                 # Pantallas en Jetpack Compose
 ├─ navigation/         # Grafo de navegación
 ├─ data/               # Repositorios, modelos y fuentes de datos
 ├─ domain/             # Use Cases y entidades
 ├─ presentation/       # ViewModels, estados, eventos
 └─ resources/          # Strings, dimensiones, colores
```

---

## Requisitos Técnicos

* Android Studio
* Kotlin
* Compose
* Gradle (Kotlin DSL)
* Firebase Authentication
* Room / DataStore
* Nutritionix API
* OpenWeather API

---

## ▶ Cómo ejecutar

1. Clonar el repositorio:

```bash
git clone https://github.com/SarAvi21805/Vive_paso_a_paso.git
```

2. Abrir en Android Studio.
3. Esperar sincronización de Gradle.
4. Ejecutar en emulador o dispositivo físico.

---
