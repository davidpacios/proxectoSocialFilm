# proyectoSocialFilm
### Usuarios
|                <!-- -->                 | <!-- -->        |
|:---------------------------------------:|:---------------:|
|        Obter un usuario concreto        | ✔️ |
| Obter unha listaxe con tódolos usuarios |    |
|            Crear un usuario             | ✔️ |
|           Eliminar un usuario           | ✔️ |
|          Modificar un usuario           | ✔️ |
|            Engadir un amigo             | ✔️ |
|            Eliminar un amigo            | ✔️ |

### Películas
| <!-- -->      | <!-- -->        |
|:-------------:|:---------------:|
| Obter unha película | ✔️ |
| Obter tódalas películas |  |
| Crear unha nova película | ✔️ |
| Modificar unha película | ✔️ |
| Eliminar unha película  | ✔️ |

### Comentarios
| <!-- -->      | <!-- -->        |
|:-------------:|:---------------:|
| Obter os comentarios dunha película | ✔️ |
| Obter os comentarios dun usuario | ✔️ |
| Engadir un novo comentario a unha película | ✔️ |
| Modificar un comentario | ✔️ |
| Eliminar un comentario  | ✔️ |

### Metodos HTTP y URLs para cada caracteristica
| Característica                                | Método HTTP | URL                              |
|----------------------------------------------|-------------|----------------------------------|
| Obter un usuario concreto | GET | /users/{id}                      |
| Obter unha listaxe con tódolos usuarios| GET  | ️/users                          |
| Crear un usuario | POST | /users/                          |
| Eliminar un usuario | DELETE | /users/{id}                      |
| Modificar un usuario | PATCH | /users/{id}                      |
| Engadir un amigo | POST | /users/{id}/friend               |
| Eliminar un amigo | DELETE | users/{userId}/friend/{friendId} |
| Obter unha película | GET  | /movies/{id}                     |
| Obter tódalas películas | GET  | /movies️                               |
| Crear unha nova película | POST  | /movies/                         |
| Modificar unha película | PATCH | /movies/{id}                     |
| Eliminar unha película | DELETE | /movies/{id}                     |
| Obter os comentarios dunha película | GET  | ️/comments/movie/{movieId}       |
| Obter os comentarios dun usuario | GET  | ️/comments/user/{userId}         |
| Engadir un novo comentario a unha película | POST | ️/comments/                      |
| Modificar un comentario | PATCH | /comments/{id}                   |
| Eliminar un comentario | DELETE | /comments/{id}                   |



