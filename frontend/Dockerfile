# Build stage
FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install || npm install || npm install

COPY . .
# Build with development configuration to avoid internet-dependent optimizations
RUN npm run build -- --configuration development

# Runtime stage
FROM nginx:stable-alpine

# Create temporary directory for nginx
RUN mkdir -p /tmp/nginx /var/cache/nginx/client_temp

# Copy built app
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Configure nginx for non-root user
RUN printf 'worker_processes auto;\nerror_log /dev/stdout info;\npid /tmp/nginx.pid;\n\nevents {\n    worker_connections 1024;\n}\n\nhttp {\n    include /etc/nginx/mime.types;\n    default_type application/octet-stream;\n    log_format main "$remote_addr - $remote_user [$time_local] \\"$request\\" $status $body_bytes_sent \\"$http_referer\\" \\"$http_user_agent\\" \\"$http_x_forwarded_for\\"";\n    access_log /dev/stdout main;\n    sendfile on;\n    tcp_nopush on;\n    tcp_nodelay on;\n    keepalive_timeout 65;\n    types_hash_max_size 2048;\n    client_body_temp_path /tmp/nginx/client_body;\n    proxy_temp_path /tmp/nginx/proxy;\n    fastcgi_temp_path /tmp/nginx/fastcgi;\n    uwsgi_temp_path /tmp/nginx/uwsgi;\n    scgi_temp_path /tmp/nginx/scgi;\n    include /etc/nginx/conf.d/*.conf;\n}\n' > /etc/nginx/nginx.conf

# Create site configuration
RUN printf 'server {\n    listen 4200;\n    server_name localhost;\n    root /usr/share/nginx/html;\n    index index.html;\n\n    location / {\n        try_files $uri $uri/ /index.html;\n    }\n\n    add_header X-Frame-Options "SAMEORIGIN" always;\n    add_header X-Content-Type-Options "nosniff" always;\n    add_header X-XSS-Protection "1; mode=block" always;\n}\n' > /etc/nginx/conf.d/default.conf

# Set proper ownership and permissions
RUN chown -R nginx:nginx /usr/share/nginx/html /var/cache/nginx /tmp/nginx /etc/nginx \
    && chmod -R 755 /tmp/nginx

# Switch to non-root user
USER nginx

EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]
