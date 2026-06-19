#!/usr/bin/env bash
# Mode strict: stop sur erreur, variable non definie, ou erreur dans un pipe.
set -euo pipefail

# Dossier racine du projet (emplacement du script).
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Dossiers utilises par le build.
SRC_DIR="$ROOT_DIR/src/com"
LIB_DIR="$ROOT_DIR/lib"
BUILD_DIR="$ROOT_DIR/build/classes"
DIST_DIR="$ROOT_DIR/dist"

# Nom du jar: argument 1 si fourni, sinon app.jar.
JAR_NAME="${1:-app.jar}"
JAR_PATH="$DIST_DIR/$JAR_NAME"

# Verifie que le dossier source existe.
if [[ ! -d "$SRC_DIR" ]]; then
  echo "Erreur: dossier src introuvable: $SRC_DIR"
  exit 1
fi

# Recupere toutes les sources Java dans un tableau.
mapfile -t JAVA_FILES < <(find "$SRC_DIR" -type f -name "*.java")
# Echec explicite s'il n'y a rien a compiler.
if [[ ${#JAVA_FILES[@]} -eq 0 ]]; then
  echo "Erreur: aucune classe Java trouvee dans $SRC_DIR"
  exit 1
fi

# Nettoie l'ancien build puis recree les dossiers de sortie.
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR" "$DIST_DIR"

# Compile avec classpath si des jars existent dans lib/, sinon compilation simple.
if compgen -G "$LIB_DIR/*.jar" > /dev/null; then
  echo "Compilation avec dependances de $LIB_DIR"
  javac -cp "$LIB_DIR/*" -d "$BUILD_DIR" "${JAVA_FILES[@]}"
else
  echo "Compilation sans dependances externes"
  javac -d "$BUILD_DIR" "${JAVA_FILES[@]}"
fi

# Cree le jar final a partir des .class compiles.
jar cf "$JAR_PATH" -C "$BUILD_DIR" .
echo "JAR genere: $JAR_PATH"