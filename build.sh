#!/usr/bin/env bash
set -e

echo "==> Building frontend..."
cd frontend
npm install
npm run build
cd ..

echo "==> Copying frontend build to backend static resources..."
mkdir -p backend/src/main/resources/static
cp -r frontend/dist/. backend/src/main/resources/static/

echo "==> Building backend JAR..."
cd backend
mvn package -DskipTests -q
cd ..

echo "==> Build complete. Run with:"
echo "    java -jar backend/target/ftms-backend-1.0.0.jar --server.port=5000"
