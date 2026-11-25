FROM nginx:alpine

COPY index.php /usr/share/nginx/html/index.html
COPY assets /usr/share/nginx/html/assets
COPY css /usr/share/nginx/html/css
COPY img /usr/share/nginx/html/img
COPY js /usr/share/nginx/html/js
COPY deploy/default.conf.template /etc/nginx/templates/default.conf.template
COPY deploy/40-api-config.sh /docker-entrypoint.d/40-api-config.sh
RUN chmod +x /docker-entrypoint.d/40-api-config.sh

ENV PORT=8080
EXPOSE 8080
