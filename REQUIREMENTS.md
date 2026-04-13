# Reader Collection — Software Requirements (Gherkin Format)

**Versión:** 2.9.0  
**Fecha:** 2025-01-11  
**Plataformas:** Android · iOS · Web
**Estándar:** Gherkin (BDD)

---

## Índice de Módulos

| Módulo | Código | Features |
|---|---|---|
| Autenticación | AUTH | US-AUTH-001, US-AUTH-002, US-AUTH-003, US-AUTH-004 |
| Gestión de libros | BOOK | US-BOOK-001 … US-BOOK-008 |
| Búsqueda de libros | SRCH | US-SRCH-001 |
| Estadísticas | STAT | US-STAT-001, US-STAT-002, US-STAT-003 |
| Amigos | FRND | US-FRND-001, US-FRND-002, US-FRND-003 |
| Ajustes | SETT | US-SETT-001 |
| Sincronización | SYNC | US-SYNC-001, US-SYNC-002 |
| Personalización | DISP | US-DISP-001, US-DISP-002, US-DISP-003 |

---

## AUTH — Autenticación

---

### US-AUTH-001 · Inicio de sesión

```gherkin
Feature: US-AUTH-001 Inicio de sesión
  Como usuario registrado
  Quiero iniciar sesión con mi nombre de usuario y contraseña
  Para acceder a mi colección personal de libros

  Background:
    Given que el usuario se encuentra en la pantalla de inicio de sesión

  Scenario: Validación en tiempo real de campos incorrectos
    When el usuario introduce un nombre de usuario con formato inválido
    And el usuario introduce una contraseña que no cumple los requisitos mínimos
    Then se muestran mensajes de error debajo de los campos correspondientes
    And el botón de inicio de sesión permanece deshabilitado

  Scenario: Botón de inicio de sesión habilitado con datos válidos
    When el usuario introduce un nombre de usuario válido
    And el usuario introduce una contraseña válida
    Then el botón de inicio de sesión se habilita

  Scenario: Indicador de carga durante el proceso de autenticación
    Given que el usuario ha introducido credenciales con formato válido
    When el usuario pulsa el botón de inicio de sesión
    Then se muestra un indicador de carga mientras se procesa la petición

  Scenario: Inicio de sesión exitoso
    Given que el usuario ha introducido credenciales correctas
    When el usuario pulsa el botón de inicio de sesión
    Then la aplicación carga la colección del usuario desde el servidor
    And navega a la pantalla principal de libros

  Scenario: Credenciales incorrectas
    Given que el usuario ha introducido credenciales incorrectas con formato válido
    When el usuario pulsa el botón de inicio de sesión
    Then se muestra un mensaje de error indicando que las credenciales son incorrectas

  Scenario: Error de servidor durante el inicio de sesión
    Given que el servidor no está disponible
    When el usuario pulsa el botón de inicio de sesión con credenciales válidas
    Then se muestra un mensaje de error genérico de servidor

  Scenario: Pre-relleno del campo de usuario
    Given que el usuario ha iniciado sesión anteriormente con el usuario "lector42"
    When el usuario abre la pantalla de inicio de sesión
    Then el campo de nombre de usuario muestra "lector42" pre-rellenado
```

---

### US-AUTH-002 · Registro de nueva cuenta

```gherkin
Feature: US-AUTH-002 Registro de nueva cuenta
  Como nuevo usuario
  Quiero crear una cuenta con nombre de usuario y contraseña
  Para empezar a utilizar la aplicación con mi propia colección

  Background:
    Given que el usuario se encuentra en la pantalla de registro

  Scenario: Validación de campo de nombre de usuario inválido
    When el usuario introduce un nombre de usuario con formato inválido
    Then se muestra un mensaje de error en el campo de nombre de usuario
    And el botón de registro permanece deshabilitado

  Scenario: Validación de contraseña que no cumple los requisitos
    When el usuario introduce una contraseña que no cumple los requisitos mínimos
    Then se muestra un mensaje de error en el campo de contraseña
    And el botón de registro permanece deshabilitado

  Scenario: Contraseña y confirmación no coinciden
    When el usuario introduce una contraseña válida
    And el usuario introduce una confirmación de contraseña diferente
    Then se muestra el mensaje de error "Las contraseñas no coinciden"
    And el botón de registro permanece deshabilitado

  Scenario: Registro exitoso
    Given que el usuario introduce datos de registro válidos y únicos
    When el usuario pulsa el botón de registro
    Then se muestra un indicador de carga
    And la cuenta se crea correctamente
    And el usuario inicia sesión automáticamente
    And la aplicación navega a la pantalla principal

  Scenario: Nombre de usuario ya registrado
    Given que el usuario introduce un nombre de usuario que ya existe en el sistema
    When el usuario pulsa el botón de registro
    Then se muestra un mensaje de error indicando que el usuario ya existe

  Scenario: Error de servidor durante el registro
    Given que el servidor no está disponible
    When el usuario pulsa el botón de registro con datos válidos
    Then se muestra un mensaje de error genérico de servidor
```

---

### US-AUTH-003 · Cierre de sesión

```gherkin
Feature: US-AUTH-003 Cierre de sesión
  Como usuario autenticado
  Quiero poder cerrar mi sesión
  Para proteger mis datos y permitir que otro usuario acceda desde el mismo dispositivo

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de ajustes

  Scenario: Acceso a la opción de cerrar sesión
    When el usuario accede a la pantalla de ajustes
    Then la opción "Cerrar sesión" es visible

  Scenario: Confirmación requerida antes de cerrar sesión
    When el usuario pulsa la opción "Cerrar sesión"
    Then se muestra un diálogo de confirmación

  Scenario: Cierre de sesión confirmado
    Given que se muestra el diálogo de confirmación
    When el usuario confirma el cierre de sesión
    Then se muestra un indicador de carga
    And los datos locales del usuario se eliminan de la base de datos local
    And la aplicación navega a la pantalla de inicio de sesión

  Scenario: Cierre de sesión cancelado
    Given que se muestra el diálogo de confirmación
    When el usuario cancela el cierre de sesión
    Then el diálogo se cierra
    And el usuario permanece en la pantalla de ajustes autenticado

---

### US-AUTH-004 · Gestión de perfil y cuenta

```gherkin
Feature: US-AUTH-004 Gestión de perfil y cuenta
  Como usuario autenticado
  Quiero gestionar los datos de mi perfil y las opciones de mi cuenta
  Para mantener mi información actualizada y controlar mi privacidad

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de cuenta

  Scenario: Visualización de datos actuales
    When la pantalla termina de cargar
    Then se muestra el nombre de usuario y el email actual

  Scenario: Actualización de datos de perfil
    When el usuario modifica su email
    And pulsa el botón de guardar
    Then los cambios se persisten en el servidor
    And se muestra un mensaje de éxito

  Scenario: Cambio de visibilidad del perfil
    When el usuario activa o desactiva el toggle de "Perfil público"
    Then la preferencia de privacidad se actualiza inmediatamente
    And otros usuarios podrán o no encontrar su biblioteca según la elección

  Scenario: Eliminación de cuenta
    When el usuario pulsa la opción "Eliminar cuenta"
    And confirma la acción en el diálogo de seguridad
    Then la cuenta se elimina permanentemente del sistema
    And el usuario es redirigido a la pantalla de inicio de sesión
```

---
```

---

## BOOK — Gestión de Libros

---

### US-BOOK-001 · Visualización del catálogo personal

```gherkin
Feature: US-BOOK-001 Visualización del catálogo personal
  Como usuario autenticado
  Quiero ver todos mis libros organizados por estado
  Para tener una visión general de mi colección

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla principal de libros

  Scenario: Se muestran las secciones de libros por estado
    When la pantalla termina de cargar
    Then se muestran las secciones "Pendientes", "Leyendo" y "Leídos"
    And cada sección muestra el número de libros que contiene

  Scenario: Cada libro muestra su información básica
    Given que el usuario tiene libros en su colección
    When la pantalla termina de cargar
    Then cada libro muestra su portada, título y autor

  Scenario: Filtrado por texto de búsqueda
    Given que el usuario tiene libros en su colección
    When el usuario introduce el texto "Tolkien" en el campo de búsqueda
    Then sólo se muestran los libros cuyo título o autor contiene "Tolkien"

  Scenario: Resultado vacío al buscar sin coincidencias
    When el usuario introduce un texto de búsqueda sin coincidencias
    Then la lista aparece vacía

  Scenario: Ordenación de libros por un criterio
    When el usuario abre el selector de ordenación
    And selecciona "Valoración" como criterio de ordenación
    Then los libros se reordenan por valoración

  Scenario: Cambio entre orden ascendente y descendente
    Given que el criterio de ordenación activo es "Título"
    When el usuario selecciona el orden descendente
    Then los libros se muestran en orden alfabético inverso por título

  Scenario: Navegación a la lista completa de un estado
    When el usuario pulsa sobre la sección "Leídos"
    Then se navega a la pantalla de lista de libros filtrada por estado "Leído"
```

---

### US-BOOK-002 · Visualización de lista de libros por estado o filtro

```gherkin
Feature: US-BOOK-002 Visualización de lista de libros por estado o filtro
  Como usuario autenticado
  Quiero ver la lista completa de mis libros filtrada por estado, año, mes, autor, formato o género
  Para explorar mi colección de forma segmentada

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de lista de libros

  Scenario: Se muestra el subtítulo con los filtros activos
    Given que la lista está filtrada por el año "2024" y el género "Fantasía"
    When la pantalla termina de cargar
    Then el subtítulo de la pantalla muestra "2024, Fantasía"

  Scenario: Cambio de criterio de ordenación en la lista
    When el usuario abre el selector de ordenación
    And selecciona "Número de páginas" como criterio
    Then los libros se reordenan por número de páginas

  Scenario: Activación del modo arrastrar y soltar en pendientes
    Given que la lista muestra libros pendientes
    When el usuario activa el modo de reordenación
    Then los indicadores de arrastre son visibles en cada elemento de la lista

  Scenario: Reordenación de libros pendientes por prioridad
    Given que el modo de arrastrar y soltar está activo
    When el usuario arrastra el libro "A" a la posición del libro "B"
    Then "A" ocupa la posición de "B" en la lista
    And el nuevo orden se persiste en la base de datos

  Scenario: Desactivación del modo arrastrar y soltar
    Given que el modo de arrastrar y soltar está activo
    When el usuario desactiva el modo de reordenación
    Then los indicadores de arrastre desaparecen
```

---

### US-BOOK-003 · Detalle de un libro

```gherkin
Feature: US-BOOK-003 Detalle de un libro
  Como usuario autenticado
  Quiero consultar el detalle completo de un libro de mi colección
  Para revisar toda su información

  Background:
    Given que el usuario está autenticado
    And ha navegado al detalle de un libro de su colección

  Scenario: Se muestra la información bibliográfica del libro
    When la pantalla termina de cargar
    Then se muestra la portada, título, subtítulo, autor o autores, editorial, fecha de publicación e ISBN

  Scenario: Se muestra el contenido descriptivo del libro
    When la pantalla termina de cargar
    Then se muestra la descripción del libro
    And se muestra el resumen personal escrito por el usuario

  Scenario: Se muestra el estado y fecha de lectura
    Given que el libro tiene estado "Leído" y fecha de lectura "15/03/2024"
    When la pantalla termina de cargar
    Then se muestra el estado "Leído"
    And se muestra la fecha de lectura "15/03/2024"

  Scenario: Se muestra la valoración del libro
    When la pantalla termina de cargar
    Then se muestra la puntuación media global del libro
    And se muestra la puntuación personal del usuario

  Scenario: Se muestra el formato y género del libro
    When la pantalla termina de cargar
    Then se muestran el número de páginas, el formato y el género o categoría del libro
```

---

### US-BOOK-004 · Edición de un libro

```gherkin
Feature: US-BOOK-004 Edición de un libro
  Como usuario autenticado
  Quiero editar la información de un libro de mi colección
  Para corregir o actualizar sus datos, estado, valoración o notas personales

  Background:
    Given que el usuario está autenticado
    And ha navegado al detalle de un libro de su colección

  Scenario: Activación del modo de edición
    When el usuario pulsa el botón de editar
    Then los campos del detalle del libro se vuelven editables

  Scenario: Modificación de datos del libro
    Given que el modo de edición está activo
    When el usuario modifica el campo "Resumen personal" con texto nuevo
    And el usuario pulsa el botón de guardar
    Then el nuevo valor del resumen personal se persiste localmente y en el servidor

  Scenario: Cancelación de edición restaura los valores anteriores
    Given que el modo de edición está activo
    When el usuario modifica el título del libro
    And el usuario pulsa el botón de cancelar
    Then el título vuelve al valor original
    And el modo de edición se desactiva

  Scenario: Fecha de lectura asignada automáticamente al marcar como leído
    Given que el libro no tiene fecha de lectura asignada
    And el modo de edición está activo
    When el usuario cambia el estado del libro a "Leído"
    And pulsa guardar
    Then la fecha de lectura se asigna automáticamente con la fecha actual

  Scenario: Cambio de portada del libro
    Given que el modo de edición está activo
    When el usuario pulsa sobre la imagen de portada
    And selecciona una nueva imagen desde la galería del dispositivo
    Then la nueva portada se muestra en el detalle del libro

  Scenario: Confirmación visual tras guardar cambios
    Given que el modo de edición está activo y los datos son válidos
    When el usuario pulsa el botón de guardar
    Then se muestra un mensaje de confirmación de que los cambios se guardaron correctamente
```

---

### US-BOOK-005 · Añadir libro desde búsqueda

```gherkin
Feature: US-BOOK-005 Añadir libro desde búsqueda
  Como usuario autenticado
  Quiero añadir un libro encontrado en el catálogo externo a mi colección
  Para registrarlo con mi estado y valoración personal

  Background:
    Given que el usuario está autenticado
    And ha navegado al detalle de un libro procedente de la búsqueda externa
    And el libro no está guardado en su colección

  Scenario: Añadir un libro nuevo a la colección
    When el usuario asigna un estado al libro y pulsa guardar
    Then el libro se añade a la colección del usuario
    And la pantalla refleja que el libro ya está guardado

  Scenario: Prioridad asignada automáticamente al añadir en estado pendiente
    When el usuario guarda el libro con estado "Pendiente"
    Then el libro recibe automáticamente la última prioridad disponible en la lista de pendientes

  Scenario: El libro aparece en el listado correspondiente
    Given que el usuario ha guardado el libro con estado "Leyendo"
    When el usuario navega a la sección "Leyendo" del catálogo
    Then el libro recién añadido aparece en esa sección
```

---

### US-BOOK-006 · Eliminar un libro de la colección

```gherkin
Feature: US-BOOK-006 Eliminar un libro de la colección
  Como usuario autenticado
  Quiero eliminar un libro de mi colección
  Para mantener mi biblioteca actualizada y sin entradas no deseadas

  Background:
    Given que el usuario está autenticado
    And ha navegado al detalle de un libro guardado en su colección

  Scenario: Confirmación requerida antes de eliminar
    When el usuario pulsa el botón de eliminar
    Then se muestra un diálogo de confirmación

  Scenario: Eliminación confirmada del libro
    Given que se muestra el diálogo de confirmación de eliminación
    When el usuario confirma la eliminación
    Then el libro desaparece de la colección del usuario
    And se muestra un mensaje informativo confirmando la eliminación
    And la pantalla queda en modo de edición, permitiendo volver a añadir el libro

  Scenario: Cancelar eliminación mantiene el libro en la colección
    Given que se muestra el diálogo de confirmación de eliminación
    When el usuario cancela la eliminación
    Then el libro permanece en la colección
    And el diálogo se cierra
```

---

### US-BOOK-007 · Cambio de estado de un libro desde la lista

```gherkin
Feature: US-BOOK-007 Cambio de estado de un libro desde la lista
  Como usuario autenticado
  Quiero cambiar el estado de un libro directamente desde el listado principal
  Para actualizar mi progreso de forma rápida sin entrar al detalle

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla principal de libros

  Scenario: Cambio de estado a "Leído" con asignación automática de fecha
    Given que el libro "Dune" no tiene fecha de lectura y su estado es "Leyendo"
    When el usuario cambia el estado de "Dune" a "Leído" desde la lista
    Then el estado de "Dune" pasa a "Leído"
    And se le asigna automáticamente la fecha de lectura con la fecha actual

  Scenario: Cambio de estado a "Pendiente" con asignación automática de prioridad
    Given que el libro "1984" no tiene prioridad asignada
    When el usuario cambia el estado de "1984" a "Pendiente" desde la lista
    Then el libro "1984" recibe la última prioridad disponible en la lista de pendientes

  Scenario: El cambio de estado se persiste en la base de datos
    When el usuario cambia el estado de cualquier libro
    Then el nuevo estado queda almacenado en la base de datos local
```

---

### US-BOOK-008 · Reordenación de prioridad de libros pendientes

```gherkin
Feature: US-BOOK-008 Reordenación de prioridad de libros pendientes
  Como usuario autenticado
  Quiero definir el orden de prioridad de mis libros pendientes
  Para saber cuál es el siguiente libro que quiero leer

  Background:
    Given que el usuario está autenticado
    And tiene libros en estado "Pendiente"

  Scenario: Los libros pendientes se muestran ordenados por prioridad
    When el usuario accede a la sección "Pendientes"
    Then los libros aparecen en orden de prioridad ascendente

  Scenario: Intercambio de prioridad desde la pantalla principal
    Given que el usuario se encuentra en la pantalla principal de libros
    When el usuario arrastra el libro "A" sobre el libro "B" en la sección de pendientes
    Then "A" toma la prioridad de "B" y viceversa
    And el cambio se persiste en la base de datos

  Scenario: Reordenación detallada en la lista de pendientes
    Given que el usuario se encuentra en la pantalla de lista de libros pendientes con drag & drop activo
    When el usuario arrastra un libro a una nueva posición
    Then el libro ocupa la nueva posición en la lista
    And todos los valores de prioridad se actualizan correctamente y se persisten
```

---

## SRCH — Búsqueda de Libros

---

### US-SRCH-001 · Búsqueda de libros en catálogo externo

```gherkin
Feature: US-SRCH-001 Búsqueda de libros en catálogo externo
  Como usuario autenticado
  Quiero buscar libros en el catálogo de Google Books
  Para encontrar nuevos títulos y añadirlos a mi colección

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de búsqueda

  Scenario: Búsqueda por título con resultados
    Given que el filtro de búsqueda activo es "Título"
    When el usuario introduce "El señor de los anillos" en el campo de búsqueda
    Then se muestra un indicador de carga
    And se muestra una lista de resultados con portada, título y autor

  Scenario: Búsqueda por autor con resultados
    Given que el filtro de búsqueda activo es "Autor"
    When el usuario introduce "Tolkien" en el campo de búsqueda
    Then se muestran libros cuyo autor coincide con "Tolkien"

  Scenario: Cambio de filtro de búsqueda
    When el usuario selecciona el filtro "Autor"
    Then el filtro activo cambia a "Autor"
    And la búsqueda siguiente se realizará usando ese criterio

  Scenario: Carga de más resultados al llegar al final de la lista
    Given que se han mostrado los primeros resultados de una búsqueda
    When el usuario hace scroll hasta el final de la lista
    Then se cargan automáticamente más resultados de la página siguiente

  Scenario: Búsqueda sin resultados
    When el usuario introduce un término de búsqueda sin coincidencias en el catálogo
    Then se muestra la lista vacía sin resultados

  Scenario: Error de red durante la búsqueda
    Given que no hay conexión a internet
    When el usuario realiza una búsqueda
    Then se muestra un mensaje de error indicando que la búsqueda falló

  Scenario: Navegación al detalle de un resultado
    Given que se muestran resultados de búsqueda
    When el usuario pulsa sobre un libro de la lista
    Then se navega a la pantalla de detalle de ese libro
```

---

## STAT — Estadísticas

---

### US-STAT-001 · Visualización de estadísticas de lectura

```gherkin
Feature: US-STAT-001 Visualización de estadísticas de lectura
  Como usuario autenticado
  Quiero consultar estadísticas sobre mis lecturas
  Para conocer mis hábitos y progreso lector a lo largo del tiempo

  Background:
    Given que el usuario está autenticado
    And tiene al menos un libro en estado "Leído"
    And se encuentra en la pantalla de estadísticas

  Scenario: Se muestra el total de libros leídos
    When la pantalla termina de cargar
    Then se muestra el número total de libros leídos

  Scenario: Se muestran libros por año
    When la pantalla termina de cargar
    Then se muestra un gráfico con los libros leídos agrupados por año

  Scenario: Se muestran libros por mes
    When la pantalla termina de cargar
    Then se muestra un gráfico con los libros leídos agrupados por mes del año

  Scenario: Se muestran los autores más leídos
    When la pantalla termina de cargar
    Then se muestra un gráfico con los 5 autores más leídos

  Scenario: Se muestran el libro más corto y el más largo
    When la pantalla termina de cargar
    Then se muestra el libro con menos páginas de la colección leída
    And se muestra el libro con más páginas de la colección leída

  Scenario: Se muestra la distribución por formato
    When la pantalla termina de cargar
    Then se muestra un gráfico de distribución de libros por formato (físico, digital, audiolibro)

  Scenario: Se muestra la distribución por género
    When la pantalla termina de cargar
    Then se muestra un gráfico con los 5 géneros más frecuentes

  Scenario: Estado vacío sin libros leídos
    Given que el usuario no tiene libros en estado "Leído"
    When el usuario accede a la pantalla de estadísticas
    Then se muestra un estado vacío informando de que aún no hay datos

  Scenario: Navegación a lista desde una entrada del gráfico de años
    Given que se muestra el gráfico de libros por año
    When el usuario pulsa sobre la entrada del año "2023"
    Then se navega a la lista de libros filtrada por el año 2023
```

---

### US-STAT-002 · Exportación de datos a fichero

```gherkin
Feature: US-STAT-002 Exportación de datos a fichero
  Como usuario autenticado
  Quiero exportar mis datos de libros a un fichero local
  Para tener una copia de seguridad o poder compartirla externamente

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de estadísticas

  Scenario: Confirmación requerida antes de exportar
    When el usuario pulsa la opción de exportar datos
    Then se muestra un diálogo de confirmación

  Scenario: Exportación exitosa
    Given que se muestra el diálogo de confirmación de exportación
    When el usuario confirma la exportación
    Then se genera el fichero de datos
    And se muestra un mensaje de confirmación de que el fichero fue creado
    And el fichero queda disponible en el dispositivo

  Scenario: Error durante la exportación
    Given que ocurre un error interno al generar el fichero
    When el usuario confirma la exportación
    Then se muestra un mensaje de error descriptivo
```

---

### US-STAT-003 · Importación de datos desde fichero

```gherkin
Feature: US-STAT-003 Importación de datos desde fichero
  Como usuario autenticado
  Quiero importar datos de libros desde un fichero local
  Para restaurar o migrar mi colección

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de estadísticas

  Scenario: Importación exitosa de un fichero válido
    When el usuario selecciona un fichero de datos válido desde el dispositivo
    Then los datos del fichero se importan a la colección del usuario
    And se muestra un mensaje de confirmación de que los datos fueron importados

  Scenario: Fichero con formato incorrecto
    When el usuario selecciona un fichero con formato inválido
    Then se muestra un mensaje de error indicando que el formato del fichero es incorrecto
    And la colección no se modifica
```

---

## FRND — Amigos

---

### US-FRND-001 · Búsqueda y solicitud de amistad

```gherkin
Feature: US-FRND-001 Búsqueda y solicitud de amistad
  Como usuario autenticado
  Quiero buscar a otros usuarios por nombre de usuario y enviarles una solicitud de amistad
  Para conectarme con mis amigos y poder ver su colección

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de añadir amigos

  Scenario: Búsqueda de usuario existente
    When el usuario introduce el nombre de usuario "amigo123" en el campo de búsqueda
    Then se muestra un indicador de carga durante la búsqueda
    And se muestra el perfil del usuario "amigo123" con un botón para enviar solicitud

  Scenario: Búsqueda de usuario inexistente
    When el usuario introduce un nombre de usuario que no existe en el sistema
    Then la lista de resultados aparece vacía

  Scenario: Envío de solicitud de amistad
    Given que se muestra el perfil del usuario "amigo123"
    When el usuario pulsa el botón de enviar solicitud
    Then se muestra un indicador de carga durante el envío
    And el estado del usuario "amigo123" refleja que la solicitud está pendiente

  Scenario: Error de servidor al enviar solicitud
    Given que el servidor no está disponible
    When el usuario pulsa el botón de enviar solicitud
    Then se muestra un mensaje de error genérico de servidor

  Scenario: Búsqueda con campo vacío
    When el usuario borra el contenido del campo de búsqueda
    Then la lista de resultados aparece vacía sin realizar petición al servidor
```

---

### US-FRND-002 · Gestión de amigos y solicitudes recibidas

```gherkin
Feature: US-FRND-002 Gestión de amigos y solicitudes recibidas
  Como usuario autenticado
  Quiero ver y gestionar mis amigos y las solicitudes de amistad pendientes
  Para controlar con quién comparto mi colección

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de amigos

  Scenario: Se muestran amigos y solicitudes pendientes
    When la pantalla termina de cargar
    Then se muestra la lista de amigos actuales
    And se muestran las solicitudes de amistad recibidas pendientes de respuesta

  Scenario: Aceptar solicitud de amistad
    Given que hay una solicitud de amistad de "lector99"
    When el usuario pulsa "Aceptar" en la solicitud de "lector99"
    Then "lector99" pasa al estado "Amigo" en la lista
    And se muestra un mensaje de confirmación

  Scenario: Rechazar solicitud de amistad
    Given que hay una solicitud de amistad de "lector99"
    When el usuario pulsa "Rechazar" en la solicitud de "lector99"
    Then la solicitud de "lector99" desaparece de la lista
    And se muestra un mensaje de confirmación

  Scenario: Eliminar un amigo desde la lista
    Given que "lector77" aparece en la lista de amigos
    When el usuario pulsa la opción de eliminar a "lector77"
    Then "lector77" desaparece de la lista de amigos
    And se muestra un mensaje de confirmación

  Scenario: Error al gestionar una solicitud o amigo
    Given que el servidor devuelve un error
    When el usuario intenta aceptar, rechazar o eliminar un amigo
    Then se muestra un mensaje de error descriptivo
```

---

### US-FRND-003 · Visualización de la biblioteca de un amigo

```gherkin
Feature: US-FRND-003 Visualización de la biblioteca de un amigo
  Como usuario autenticado
  Quiero ver la colección de libros de uno de mis amigos
  Para descubrir qué está leyendo o qué ha leído

  Background:
    Given que el usuario está autenticado
    And tiene al menos un amigo en su lista

  Scenario: Acceso al perfil del amigo desde la lista
    When el usuario pulsa sobre el amigo "lector42" en la lista de amigos
    Then se navega a la pantalla de detalle del amigo "lector42"
    And se muestra un indicador de carga mientras se obtienen los datos

  Scenario: Se muestra la información del amigo y su colección
    Given que se ha cargado el detalle del amigo "lector42"
    Then se muestra el nombre de usuario "lector42"
    And se muestra la lista de libros leídos de "lector42"

  Scenario: Navegación al detalle de un libro del amigo
    Given que se muestra la colección de libros del amigo
    When el usuario pulsa sobre un libro de la lista
    Then se navega al detalle de ese libro

  Scenario: Añadir un libro del amigo a la propia colección
    Given que se muestra el detalle de un libro del amigo que el usuario no tiene guardado
    When el usuario pulsa añadir a su colección
    Then el libro se añade a la colección del usuario

  Scenario: Eliminar al amigo desde su pantalla de detalle
    Given que se muestra el detalle del amigo "lector42"
    When el usuario pulsa la opción de eliminar amigo
    Then se muestra un diálogo de confirmación
    And al confirmar, "lector42" se elimina de la lista de amigos
    And se muestra un mensaje de confirmación

  Scenario: Error al cargar datos del amigo
    Given que el servidor devuelve un error al obtener los datos del amigo
    When se intenta cargar la pantalla de detalle del amigo
    Then se muestra un mensaje de error descriptivo
```

---

## SETT — Ajustes

---

### US-SETT-001 · Acceso al menú de ajustes

```gherkin
Feature: US-SETT-001 Acceso al menú de ajustes
  Como usuario autenticado
  Quiero acceder a un menú de configuración centralizado
  Para gestionar mi cuenta, amigos, sincronización y preferencias de visualización

  Background:
    Given que el usuario está autenticado

  Scenario: La sección de ajustes es accesible desde la navegación principal
    When el usuario pulsa la pestaña de ajustes en la barra de navegación inferior
    Then se muestra la pantalla de ajustes

  Scenario: Se muestran todas las opciones del menú de ajustes
    When la pantalla de ajustes termina de cargar
    Then se muestran las opciones: "Cuenta", "Amigos", "Sincronización de datos", "Ajustes de visualización" y "Cerrar sesión"

  Scenario: Se muestra la versión de la aplicación
    When la pantalla de ajustes termina de cargar
    Then se muestra la versión actual de la aplicación
```

---

## SYNC — Sincronización de Datos

---

### US-SYNC-001 · Sincronización manual de datos

```gherkin
Feature: US-SYNC-001 Sincronización manual de datos
  Como usuario autenticado
  Quiero sincronizar manualmente mis datos con el servidor remoto
  Para asegurar que mi colección está actualizada en todos mis dispositivos

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de sincronización de datos

  Scenario: Confirmación requerida antes de sincronizar
    When el usuario pulsa el botón de sincronizar datos
    Then se muestra un diálogo de confirmación

  Scenario: Sincronización exitosa
    Given que se muestra el diálogo de confirmación de sincronización
    When el usuario confirma la sincronización
    Then se muestra un indicador de carga durante el proceso
    And la sincronización se completa correctamente
    And se muestra un mensaje de confirmación

  Scenario: Error de servidor durante la sincronización
    Given que el servidor no está disponible
    When el usuario confirma la sincronización
    Then se muestra un mensaje de error indicando que la sincronización falló
```

---

### US-SYNC-002 · Sincronización automática de datos

```gherkin
Feature: US-SYNC-002 Sincronización automática de datos
  Como usuario autenticado
  Quiero activar la sincronización automática de mis datos
  Para no tener que sincronizar manualmente cada vez que hago cambios

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de sincronización de datos

  Scenario: Estado del toggle refleja la configuración guardada
    Given que la sincronización automática está activada
    When el usuario abre la pantalla de sincronización
    Then el toggle de sincronización automática aparece activado

  Scenario: Activar la sincronización automática
    Given que la sincronización automática está desactivada
    When el usuario activa el toggle de sincronización automática
    Then la preferencia se persiste
    And la sincronización automática queda habilitada para futuras sesiones

  Scenario: Desactivar la sincronización automática
    Given que la sincronización automática está activada
    When el usuario desactiva el toggle de sincronización automática
    Then la preferencia se persiste
    And la sincronización automática queda deshabilitada para futuras sesiones
```

---

## DISP — Personalización de Visualización

---

### US-DISP-001 · Configuración del idioma de la aplicación

```gherkin
Feature: US-DISP-001 Configuración del idioma de la aplicación
  Como usuario autenticado
  Quiero seleccionar el idioma de la aplicación
  Para utilizarla en mi idioma preferido

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de ajustes de visualización

  Scenario: El idioma actual se muestra seleccionado al abrir la pantalla
    Given que el idioma configurado es "Español"
    When la pantalla de ajustes de visualización termina de cargar
    Then la opción "Español" aparece seleccionada

  Scenario: Cambio de idioma y guardado
    When el usuario selecciona "English" como idioma
    And pulsa el botón de guardar
    Then la preferencia de idioma "English" se persiste en las preferencias del usuario

  Scenario: El cambio de idioma tiene efecto en los textos
    Given que el usuario ha guardado el idioma "English"
    When la aplicación aplica el nuevo idioma
    Then los textos de la interfaz se muestran en inglés
```

---

### US-DISP-002 · Configuración del criterio de ordenación por defecto

```gherkin
Feature: US-DISP-002 Configuración del criterio de ordenación por defecto
  Como usuario autenticado
  Quiero definir el criterio de ordenación por defecto para mis listas de libros
  Para que mis preferencias se apliquen automáticamente sin tener que configurarlo cada vez

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de ajustes de visualización

  Scenario: Los valores actuales de ordenación se muestran al cargar la pantalla
    Given que el criterio guardado es "Valoración" en orden descendente
    When la pantalla termina de cargar
    Then "Valoración" aparece seleccionado como criterio de ordenación
    And el orden descendente aparece seleccionado

  Scenario: Cambio del criterio de ordenación por defecto
    When el usuario selecciona "Título" como criterio de ordenación
    And selecciona "Ascendente" como dirección
    And pulsa guardar
    Then las preferencias de ordenación se persisten

  Scenario: Reinicio de la navegación tras guardar cambios de ordenación
    When el usuario modifica el criterio de ordenación y pulsa guardar
    Then la aplicación reinicia la sesión de navegación para aplicar el nuevo criterio a los listados
```

---

### US-DISP-003 · Configuración del tema visual

```gherkin
Feature: US-DISP-003 Configuración del tema visual
  Como usuario autenticado
  Quiero elegir el tema visual de la aplicación (claro, oscuro o sistema)
  Para adaptar la apariencia a mis preferencias o condiciones de uso

  Background:
    Given que el usuario está autenticado
    And se encuentra en la pantalla de ajustes de visualización

  Scenario: El tema actual se muestra seleccionado al cargar la pantalla
    Given que el tema configurado es "Oscuro"
    When la pantalla de ajustes de visualización termina de cargar
    Then la opción "Oscuro" aparece seleccionada

  Scenario: Cambio al tema claro y guardado
    When el usuario selecciona "Claro" como tema
    And pulsa el botón de guardar
    Then la preferencia de tema "Claro" se persiste
    And el tema de la aplicación se actualiza

  Scenario: Cambio al modo según el sistema
    When el usuario selecciona "Según el sistema" como tema
    And pulsa el botón de guardar
    Then la aplicación adopta el tema que tenga configurado el sistema operativo del dispositivo
```

---

## Requisitos No Funcionales

```gherkin
Feature: RNF-01 Tiempo de carga del catálogo personal
  Scenario: La lista de libros se muestra en tiempo razonable
    Given que el usuario tiene libros en su colección
    And la conexión a internet es normal
    When el usuario accede a la pantalla principal de libros
    Then el listado se muestra en menos de 2 segundos

Feature: RNF-02 Tiempo de respuesta de búsqueda externa
  Scenario: Los resultados de búsqueda llegan en tiempo razonable
    Given que el usuario realiza una búsqueda en el catálogo externo
    And la conexión a internet es normal
    When la búsqueda se envía al servidor
    Then los resultados se muestran en menos de 3 segundos

Feature: RNF-04 Compatibilidad multiplataforma
  Scenario: La aplicación funciona en Android e iOS
    Given que la aplicación está instalada en un dispositivo Android con API >= 26
    Then la aplicación se ejecuta correctamente
    Given que la aplicación está instalada en un dispositivo iOS con versión >= 16
    Then la aplicación se ejecuta correctamente

Feature: RNF-08 Retroalimentación visual en operaciones asíncronas
  Scenario: Se muestra indicador de carga en toda operación asíncrona
    When el usuario realiza cualquier operación que requiera comunicación con el servidor
    Then se muestra un indicador de carga durante la operación
    And se muestra un mensaje de éxito o error al finalizar

Feature: RNF-09 Validación en tiempo real de formularios
  Scenario: Los errores de formulario se muestran antes del envío
    When el usuario introduce datos inválidos en un formulario
    Then se muestran los mensajes de error en los campos correspondientes
    And el botón de envío permanece deshabilitado hasta que todos los campos sean válidos

Feature: RNF-11 Gestión de pérdida de conexión
  Scenario: La aplicación no se cierra ante fallos de red
    Given que el dispositivo pierde la conexión a internet
    When el usuario intenta realizar una operación que requiere red
    Then la aplicación muestra un mensaje de error informativo
    And la aplicación permanece estable y utilizable en modo local
```

---

## Trazabilidad

| Feature | Pantalla | ViewModel | Estado |
|---|---|---|---|
| US-AUTH-001 | `LoginScreen` | `LoginViewModel` | ✅ Implementado |
| US-AUTH-002 | `RegisterScreen` | `RegisterViewModel` | ✅ Implementado |
| US-AUTH-003 | `SettingsScreen` | `SettingsViewModel` | ✅ Implementado |
| US-AUTH-004 | `AccountScreen` | `AccountViewModel` | ✅ Implementado |
| US-BOOK-001 | `BooksScreen` | `BooksViewModel` | ✅ Implementado |
| US-BOOK-002 | `BookListScreen` | `BookListViewModel` | ✅ Implementado |
| US-BOOK-003 | `BookDetailScreen` | `BookDetailViewModel` | ✅ Implementado |
| US-BOOK-004 | `BookDetailScreen` | `BookDetailViewModel` | ✅ Implementado |
| US-BOOK-005 | `BookDetailScreen` | `BookDetailViewModel` | ✅ Implementado |
| US-BOOK-006 | `BookDetailScreen` | `BookDetailViewModel` | ✅ Implementado |
| US-BOOK-007 | `BooksScreen` | `BooksViewModel` | ✅ Implementado |
| US-BOOK-008 | `BooksScreen` / `BookListScreen` | `BooksViewModel` / `BookListViewModel` | ✅ Implementado |
| US-SRCH-001 | `SearchScreen` | `SearchViewModel` | ✅ Implementado |
| US-STAT-001 | `StatisticsScreen` | `StatisticsViewModel` | ✅ Implementado |
| US-STAT-002 | `StatisticsScreen` | `StatisticsViewModel` | ✅ Implementado |
| US-STAT-003 | `StatisticsScreen` | `StatisticsViewModel` | ✅ Implementado |
| US-FRND-001 | `AddFriendsScreen` | `AddFriendsViewModel` | ✅ Implementado |
| US-FRND-002 | `FriendsScreen` | `FriendsViewModel` | ✅ Implementado |
| US-FRND-003 | `FriendDetailScreen` | `FriendDetailViewModel` | ✅ Implementado |
| US-SETT-001 | `SettingsScreen` | `SettingsViewModel` | ✅ Implementado |
| US-SYNC-001 | `DataSyncScreen` | `DataSyncViewModel` | ✅ Implementado |
| US-SYNC-002 | `DataSyncScreen` | `DataSyncViewModel` | ✅ Implementado |
| US-DISP-001 | `DisplaySettingsScreen` | `DisplaySettingsViewModel` | ✅ Implementado |
| US-DISP-002 | `DisplaySettingsScreen` | `DisplaySettingsViewModel` | ✅ Implementado |
| US-DISP-003 | `DisplaySettingsScreen` | `DisplaySettingsViewModel` | ✅ Implementado |
