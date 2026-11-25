#!/bin/sh
set -eu

if [ -z "${FAST_FANTASY_API_URL:-}" ]; then
  echo "FAST_FANTASY_API_URL must contain the public HTTPS URL of the Java API." >&2
  exit 1
fi

case "$FAST_FANTASY_API_URL" in
  https://*) ;;
  *) echo "FAST_FANTASY_API_URL must use HTTPS." >&2; exit 1 ;;
esac

api_url=${FAST_FANTASY_API_URL%/}
printf 'window.API_BASE_URL = "%s";\n' "$api_url" > /usr/share/nginx/html/js/api-config.js
