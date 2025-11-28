# Автоматизация тестирования веб-сервиса "Путешествие дня"

> **Дипломный проект по курсу "Автоматизация тестирования"**

## Техническая часть

## Зависимости
- Java 11
- Gradle 8.10
- Docker 20.10+
- Chrome 114 (совместим с Selenium 4.13)

## СУБД
Поддержка двух СУБД:
- **MySQL** (по умолчанию)
- **PostgreSQL** (включается через переменные окружения)

## Банковские сервисы
Эмулятор (`bank-emulator`) на Node.js, запускается через Docker.

---

# Как запустить проект

# 1. Установите зависимости

## Убедитесь, что установлены:

java  → 11

gradle  → 8.10+

docker  → 20.10+

## Установите Chrome 114 (совместим с Selenium 4.13)

wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.198-1_amd64.deb

sudo apt install ./google-chrome-stable_114.0.5735.198-1_amd64.deb

# 2. Подготовьте инфраструктуру для запуска (на Linux - Ubuntu)

## 2.1. Очистите старые данные
sudo rm -rf pg_data mysql_data

sudo mkdir -p mysql_data

sudo mkdir -p pg_data

# 3. Запустите тесты

## Для MySQL:

sudo DB_TYPE=mysql docker compose up --build

sudo DB_TYPE=mysql ./gradlew test

sudo ./gradlew allureReport

sudo ./gradlew allureServe  

## Для PostgreSQL (по умолчанию):

sudo docker compose up --build

sudo ./gradlew test

sudo ./gradlew allureReport

sudo ./gradlew allureServe

## Остановка:

sudo docker compose down -v
