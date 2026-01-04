FROM node:20-alpine AS build
RUN apk update && apk add --no-cache openjdk17-jre-headless
WORKDIR /app
COPY activity-management-frontend/package*.json ./
RUN npm ci
COPY activity-management-frontend/ .
COPY openapi.yaml /openapi.yaml
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY activity-management-frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
