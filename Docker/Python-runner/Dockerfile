FROM python:3.11-slim

# Create non-root user
RUN useradd -m runner

WORKDIR /app

# Switch to non-root user
USER runner

# Disable Python cache
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# ENTRYPOINT defines the executable
ENTRYPOINT ["python"]
