# Docker and Containers Fundamentals — Real, Executed Demo

Backs [Docker and Containers Fundamentals](../../syllabus/14-devops-containers/docker-and-containers-fundamentals.md) (T-2208). A real image, built and run with the actual Docker Engine (29.6.2) — no simulation.

## Reproduce

```bash
javac app/Server.java
docker build -t docker-fundamentals-demo:1.0 .

# With port mapping -- reachable from the host
docker run -d --name docker-fundamentals-demo -p 8080:8080 docker-fundamentals-demo:1.0
curl -s http://localhost:8080/
docker logs docker-fundamentals-demo
docker stop docker-fundamentals-demo && docker rm docker-fundamentals-demo

# Without port mapping -- the real isolation proof
docker run -d --name docker-fundamentals-demo-noport docker-fundamentals-demo:1.0
curl -s --max-time 2 http://localhost:8080/         # fails: connection refused
docker exec docker-fundamentals-demo-noport curl -s http://localhost:8080/  # succeeds
docker stop docker-fundamentals-demo-noport && docker rm docker-fundamentals-demo-noport
```

`docker-transcript.txt` reproduces both real runs. The second half is the actual point of this demo: the container is genuinely running and serving on port 8080 the whole time — proven by `docker exec` reaching it from inside the container's own network namespace — but the host has no route to it without an explicit `-p` mapping. This is real, observed container network isolation, not a description of it.
