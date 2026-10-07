FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
FROM nginx:alpine
ENV PORT=80 BACKEND_HOST=backend BACKEND_PORT=8080
ENV NGINX_ENVSUBST_FILTER="^(PORT|BACKEND_HOST|BACKEND_PORT|DNS_RESOLVER)$"
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY docker/15-proxy-env.envsh /docker-entrypoint.d/15-proxy-env.envsh
RUN sed -i 's/\r$//' /docker-entrypoint.d/15-proxy-env.envsh && chmod +x /docker-entrypoint.d/15-proxy-env.envsh
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
