---
title: "Nordstrom — Senior Software Engineer, Backend (Java/Spring/AWS/Kubernetes, Remote)"
document_type: company-prep-guide
domain: interview-playbook
status: draft
last_updated: 2026-09-03
related:
  - ../../handbook/system-design/idempotency.md
  - ../../handbook/databases/optimistic-vs-pessimistic-locking.md
  - ../../syllabus/02-java/collections/concurrenthashmap-internals.md
  - ../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md
  - ../../handbook/system-design/resilience-patterns.md
  - ../../handbook/databases/connection-pooling-and-sizing.md
  - ../../handbook/cloud/aws-core-services-for-backend-engineers.md
  - ../../handbook/system-design/distributed-transactions-saga-and-outbox.md
---

# Nordstrom — Senior Software Engineer, Backend (Java/Spring/AWS/Kubernetes, Remote)

Guía de preparación puntual para esta entrevista específica, no un capítulo canónico del handbook. Cada sección enlaza al capítulo canónico correspondiente (donde ya existe una explicación completa, ejemplos y preguntas de entrevista) y agrega solo lo que ese capítulo no cubría todavía — verificado por lectura directa de cada archivo, no asumido. Ver `CLAUDE.md`'s Canonical Content Ownership: esta guía no duplica contenido ya canónico, lo referencia.

**Auditoría de los 8 temas que pediste** (2026-09-03): 6 de 8 ya estaban cubiertos en el handbook con profundidad razonable; se agregó contenido nuevo en 5 capítulos canónicos para cerrar matices reales que faltaban (marcados 🆕 abajo). Ningún tema estaba completamente ausente.

## Crítico

### 1. Idempotency Key pattern (REST + Kafka)

**Ya cubierto en profundidad:** [Idempotency at System Edges](../../handbook/system-design/idempotency.md) (T-809, IWI 7.85) — mecanismo de 3 partes (key/storage/TTL), por qué el `UNIQUE` constraint de la base de datos coordina las escrituras concurrentes en vez de un lock de aplicación, comportamiento del cliente ante una respuesta ambigua, y una reproducción real y medida de dos requests concurrentes con la misma key (ambas devuelven el mismo resultado, un solo cargo real ejecutado).

🆕 **Lo que agregué:** el capítulo ya mostraba el patrón correcto (`INSERT` + capturar la violación de unicidad), pero nunca nombraba explícitamente el patrón inseguro como contraste. Agregué una sección nueva ("Check before write" versus "insert/upsert by key") que nombra ambas formas:

- **Check-before-write (inseguro):** `SELECT ... WHERE key = ?`, si no existe, procede. Dos requests concurrentes pueden pasar el check antes de que cualquiera haga el insert — la clásica race de leer-luego-escribir.
- **Insert/upsert por key (correcto):** el `INSERT` (o `INSERT ... ON CONFLICT`) mismo es el check, atómicamente, vía el unique constraint.

También agregué un puente explícito hacia Kafka: el mismo mecanismo aplica a un consumer que puede recibir el mismo mensaje más de una vez (at-least-once delivery) — la "key" es el ID del evento (o `topic-partition-offset`), y el "write" es el mismo insert/upsert-por-key, idealmente en la misma transacción que el efecto secundario real. El lado productor/broker (idempotent producer, `acks=all`) vive en [Kafka Delivery Semantics and Exactly-Once](../../handbook/kafka/delivery-semantics-and-exactly-once.md) — son mitades complementarias del mismo garantía end-to-end, no técnicas competidoras.

### 2. Atomic conditional UPDATE para race conditions de inventario

🆕 **Gap real, cerrado.** No existía como patrón propio — solo estaban `@Version` (optimistic locking) y `SELECT ... FOR UPDATE` (pessimistic locking). Agregué esta tercera técnica directamente a [Optimistic vs. Pessimistic Locking](../../handbook/databases/optimistic-vs-pessimistic-locking.md) como "A third technique: the atomic conditional UPDATE":

```sql
UPDATE inventory
SET stock = stock - 1
WHERE product_id = ? AND stock > 0;
```

```java
int rowsAffected = jdbcTemplate.update(
    "UPDATE inventory SET stock = stock - 1 WHERE product_id = ? AND stock > 0",
    productId
);
if (rowsAffected == 0) {
    throw new OutOfStockException(productId);
}
```

**Por qué evita la race que sí existe con `SELECT` + `UPDATE` separados:** con dos sentencias, dos requests concurrentes pueden ambas leer `stock = 1`, ambas pasar el check en código de aplicación, y ambas decrementar — resultado `stock = -1`. Con una sola sentencia, la base de datos evalúa el `WHERE` y ejecuta el decremento como una sola operación atómica contra el valor *actual* de la fila, no contra el valor que la aplicación leyó antes. Un segundo request concurrente contra la misma fila (ya en 0) simplemente falla su propio `WHERE stock > 0` y actualiza 0 filas — **revisar `rowsAffected` es obligatorio**; ignorarlo silenciosamente reintroduce el mismo bug de overselling, solo que movido a código de aplicación que no lo revisó.

**Límite de la técnica:** solo sirve cuando la regla de negocio se reduce a una actualización aritmética condicional de una sola fila. Si el invariante involucra varios campos o varias filas, se necesita `@Version` o un lock pesimista — esta técnica es más barata, no un reemplazo universal. Tabla comparativa completa (incluyendo esta tercera columna) en el capítulo.

### 3. HashMap vs ConcurrentHashMap: el infinite loop bajo resize concurrente

🆕 **Gap real, cerrado.** [ConcurrentHashMap Internals](../../syllabus/02-java/collections/concurrenthashmap-internals.md) (T-205, IWI 6.65) ya cubría la corrupción silenciosa (entries perdidas, tamaño final incorrecto) bajo escritura concurrente, pero no el mecanismo específico del infinite loop. Agregué la explicación precisa:

- **JDK 7 y anteriores:** el resize de `HashMap` reescribe cada bucket usando una lista enlazada, y el algoritmo de resize **invertía el orden** de esa lista al moverla al nuevo array. Si dos threads disparan un resize concurrentemente sin sincronización, el rewiring parcial de un thread puede interlazarse con el del otro, produciendo un nodo cuyo `next` apunta hacia atrás en la misma lista — un ciclo. Cualquier `get()`/`put()` posterior que recorra ese bucket queda en loop infinito, un core al 100% para siempre.
- **Por qué NO es OOM ni el GC:** la memoria del JVM está bien, el GC no hace nada malo — el proceso está atascado recorriendo una lista circular, no reteniendo memoria de más ni pausando por colección de basura. Esta distinción es exactamente la que suelen confundir en entrevista.
- **JDK 8+:** el resize fue rediseñado (split lo/hi sin invertir orden), así que este mecanismo específico **ya no puede ocurrir**. Pero `HashMap` en JDK 8+ **sigue sin ser thread-safe** — la corrupción silenciosa/pérdida de entries sigue siendo real, por una causa distinta (dos threads escribiendo el mismo bucket slot).
- **Punto clave para la entrevista:** nombrar el mecanismo correcto según la generación de JDK que se está discutiendo — presentar el infinite loop como "lo que pasa" en un JDK moderno es la respuesta incorrecta; ahí la respuesta correcta es corrupción silenciosa.

### 4. Connection/thread pool exhaustion + Circuit Breaker

**Ya bien cubierto por separado:** [Connection Pooling and Sizing (HikariCP)](../../handbook/databases/connection-pooling-and-sizing.md) (T-607) tiene un escenario de producción real y medido sobre sizing de pool; [Resilience Patterns](../../handbook/system-design/resilience-patterns.md) (T-515, IWI 7.60) cubre circuit breaker de 3 estados, bulkhead, timeouts y jitter en profundidad.

🆕 **Lo que agregué:** los dos capítulos no conectaban explícitamente el escenario exacto que pediste — un dependency downstream lento saturando un pool de tamaño fijo y afectando requests *no relacionados*. Agregué esa narrativa unificada a la sección de bulkhead en Resilience Patterns:

Un pool HikariCP compartido (`maximumPoolSize: 20`) sirve dos endpoints no relacionados (ej. consulta de orden y cancelación de orden). Si cancelación llama a un downstream lento (procesador de pagos) mientras retiene su conexión prestada, cada request de cancelación en vuelo retiene una conexión durante toda la latencia del downstream. Con suficientes requests concurrentes de cancelación, las 20 conexiones se agotan — y el endpoint de consulta de orden, que nunca toca ese downstream, también empieza a fallar por timeout de conexión, porque no quedan conexiones libres en el pool compartido.

**Por qué el Circuit Breaker lo resuelve:** al abrirse el breaker (Resilience4j) sobre la llamada al downstream lento, las requests de cancelación fallan rápido *sin llegar a pedir una conexión del pool* — el pool deja de ser drenado por requests esperando a una dependencia ya conocida como no saludable. El breaker no arregla el downstream; evita que el problema de una dependencia se propague a un recurso compartido del que dependen requests no relacionados — la justificación clásica para combinar circuit breaker con bulkhead isolation.

### 5. Checklist mental de troubleshooting: "p95 alto, CPU normal"

Los componentes individuales ya están cubiertos por separado y en profundidad; esta es una síntesis nueva de un flujo de diagnóstico ordenado, cada paso enlazando al capítulo canónico correspondiente:

1. **Logs** — buscar excepciones, timeouts o reintentos alrededor de la ventana de tiempo donde p95 subió. Si los logs están limpios, el problema probablemente no es un error explícito sino latencia genuina en alguna dependencia. Ver [Logging, Metrics, Tracing, and OpenTelemetry](../../handbook/performance/logging-metrics-tracing-and-opentelemetry.md).
2. **Métricas** — confirmar que es realmente cola alta (p95/p99), no un problema de medición. Revisar el mecanismo de medición mismo: un load test o dashboard con *coordinated omission* puede subestimar la cola real. Ver [Percentiles, Tail Latency, and Coordinated Omission](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md).
3. **Traces distribuidos** — con CPU normal, la latencia casi siempre está en un span de espera (I/O, downstream), no de cómputo. Un `traceId` compartido reconstruye exactamente en qué servicio y en qué operación se acumula el tiempo. Ver el mismo capítulo de logging/metrics/tracing — la propagación de contexto de trace es el mecanismo que hace esto posible.
4. **Connection/thread pools** — revisar si hay requests esperando por una conexión o un thread disponible (pool exhaustion), no por trabajo real. Ver [Connection Pooling and Sizing](../../handbook/databases/connection-pooling-and-sizing.md) y [Executors and Thread Pool Sizing](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md).
5. **Dependencias downstream** — si el pool está sano pero la latencia persiste, el downstream mismo es lento (no caído — CPU normal descarta un problema local de cómputo). Ver [Resilience Patterns](../../handbook/system-design/resilience-patterns.md) (timeout selection es una decisión de percentil, no una adivinanza) y [Distributed Systems Failure Modes](../../handbook/system-design/distributed-systems-failure-modes.md).

**Por qué este orden:** cada paso descarta una categoría de causa antes de pasar a la siguiente — CPU normal ya descarta cómputo local, así que el orden va de la señal más barata de revisar (logs) a la más cara (trazar un downstream específico), sin saltar directo a "debe ser el downstream" sin antes confirmar que no es el pool.

## Importante

### 6. AWS para desplegar microservicios Java (nivel conceptual)

**Ya bien cubierto:** [AWS Core Services for Backend Engineers](../../handbook/cloud/aws-core-services-for-backend-engineers.md) (T-1006) — EC2/ECS/EKS/Lambda como espectro de ownership operacional vs. conveniencia, RDS vs. DynamoDB con el mismo método de storage-selection del programa, SQS (punto-a-punto) vs. SNS (pub/sub) y el patrón combinado "SNS fan-out a múltiples SQS".

🆕 **Lo que agregué:** ALB y Auto Scaling no estaban mencionados en ningún capítulo del handbook. Agregué una sección nueva ("Traffic distribution and elasticity"):

- **ALB (Application Load Balancer):** Layer-7 — puede rutear por path/host header y terminar TLS, a diferencia de un Network Load Balancer (Layer-4). El mismo rol conceptual que cubre [Load Balancing, Service Discovery, and Health Checking](../../handbook/system-design/load-balancing-service-discovery-and-health-checking.md), con AWS administrando el balanceador.
- **Auto Scaling:** un Auto Scaling Group (EC2), Service Auto Scaling (ECS), o un HorizontalPodAutoscaler (EKS) — agrega o quita instancias/tasks/pods según una métrica (CPU, request-count), en vez de dimensionar una vez para el pico y dejarlo así.
- **Cómo se componen:** el target group del ALB se actualiza automáticamente conforme Auto Scaling agrega o quita instancias — un scale-out es invisible para el caller, que sigue golpeando el mismo endpoint del ALB mientras el pool de targets saludables detrás crece o se achica.

### 7. Saga pattern (choreography vs orchestration)

**Ya bien cubierto, sin gap.** [Distributed Transactions: Saga and Outbox](../../handbook/system-design/distributed-transactions-saga-and-outbox.md) (T-618, IWI 7.65) distingue claramente:

- **Orchestration:** un coordinador central llama explícitamente a cada servicio e invoca compensaciones ante fallo — más fácil de debuggear, pero el coordinador es una dependencia estructural para cada paso.
- **Choreography:** cada servicio reacciona a eventos del paso anterior y emite el suyo — sin coordinador central, pero el flujo es implícito y genuinamente más difícil de trazar.

Incluye tabla de trade-offs, por qué una acción compensatoria es una operación de negocio hacia adelante (no un `ROLLBACK`), y por qué el outbox pattern suele vivir *dentro* de un paso del Saga (no son técnicas competidoras). No requirió agregado — está a nivel de profundidad Senior/Staff ya.

## Nice to have

### 8. Comandos básicos de kubectl para debugging de pods

🆕 **Gap parcial, cerrado.** Los comandos aparecían mencionados de pasada dentro de narrativas de incidentes (`kubectl get pods`, `kubectl describe pod` en [Kubernetes Resource Limits, Probes, and JVM Sizing](../../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md)), pero no como referencia práctica dedicada. Agregué una sección nueva a [Kubernetes Objects, Scheduling, and Networking](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md) ("A Practical kubectl Debugging Workflow"):

1. **`kubectl get pods`** — primer comando siempre. `STATUS` (`CrashLoopBackOff`, `Pending`, `ImagePullBackOff`) y `READY` (`0/1` con `Running` significa que el contenedor corre pero falló su readiness probe — distinción que se suele confundir).
2. **`kubectl describe pod <name>`** — el comando más denso en información. `Last State: Terminated, Reason: OOMKilled, Exit Code: 137` significa que el kernel mató el contenedor por exceder su límite de memoria — un kill de infraestructura sin ninguna señal a nivel de aplicación, así que los logs de la app no van a mostrar nada útil. La sección `Events` es el mejor lugar para ver un `FailedScheduling` o un probe fallando, antes de que el pod sea matado por eso.
3. **`kubectl logs <pod>`** para el contenedor actual; **`kubectl logs <pod> --previous`** específicamente para ver los logs de la instancia *anterior* del contenedor después de un restart — los logs actuales empiezan vacíos justo después de un crash-and-restart.
4. **`kubectl exec -it <pod> -- /bin/sh`** para una shell interactiva dentro del contenedor corriendo — verificar si un archivo de config realmente llegó donde se esperaba, si un host downstream es alcanzable desde el namespace de red del contenedor, o inspeccionar el estado vivo de una JVM.

**Por qué ese orden importa:** `get pods` filtra qué pod mirar, `describe` explica *por qué* (usualmente el camino más rápido a la causa raíz real), `logs`/`logs --previous` muestra qué hacía la aplicación al momento de fallar, y `exec` es el último recurso para inspeccionar estado que no aparece en ninguno de los anteriores. Saltar directo a `exec` sin antes leer `Events` en `describe pod` es el patrón de pérdida de tiempo más común en una ronda de debugging en vivo.

## Resumen de cambios hechos al handbook (2026-09-03)

| Archivo | Qué se agregó |
|---|---|
| [`handbook/system-design/idempotency.md`](../../handbook/system-design/idempotency.md) | "Check before write" vs "insert/upsert by key" nombrados explícitamente; puente hacia idempotencia en consumers de Kafka |
| [`handbook/databases/optimistic-vs-pessimistic-locking.md`](../../handbook/databases/optimistic-vs-pessimistic-locking.md) | Tercera técnica: atomic conditional UPDATE, con código SQL/Java y tabla comparativa actualizada |
| [`syllabus/02-java/collections/concurrenthashmap-internals.md`](../../syllabus/02-java/collections/concurrenthashmap-internals.md) | Mecanismo del infinite loop en JDK 7 durante resize concurrente, y por qué JDK 8+ ya no lo reproduce (pero sigue sin ser thread-safe) |
| [`handbook/system-design/resilience-patterns.md`](../../handbook/system-design/resilience-patterns.md) | Narrativa unificada: pool HikariCP compartido + downstream lento + circuit breaker como fix |
| [`handbook/cloud/aws-core-services-for-backend-engineers.md`](../../handbook/cloud/aws-core-services-for-backend-engineers.md) | ALB y Auto Scaling (ASG/ECS/HPA) como sección nueva |
| [`handbook/cloud/kubernetes-objects-scheduling-and-networking.md`](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md) | Flujo práctico de debugging con kubectl (4 comandos, en orden) |

**Nota de honestidad:** los snippets de código nuevos en esta guía (atomic UPDATE) son ilustrativos y sintácticamente correctos, pero no fueron ejecutados como parte de una demo real con base de datos — a diferencia del resto del contenido de este repositorio, que sí exige evidencia ejecutada. Si querés una demo real y medida del atomic-UPDATE pattern (dos threads compitiendo por el mismo `stock`), decímelo y la construyo con el mismo rigor que el resto del handbook.
