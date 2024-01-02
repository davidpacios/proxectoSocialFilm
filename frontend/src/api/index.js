import DATA from './data'
let __instance = null

export default class API {
    #token = sessionStorage.getItem('token') || null

    static instance() {
        if (__instance == null)
            __instance = new API()

        return __instance
    }

    async login(email, pass) {
        const login = await fetch(`http://localhost:8080/login`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({email: email, password: pass})
        })

        const token = login.headers.get('Authentication')
        const userID = login.headers.get('UserID');

        if (token !== null) {
            localStorage.setItem('userID', userID)
            localStorage.setItem('token', token)
            this.#token = token
            return true
        } else {
            return false
        }
    }

    async logout() {
        this.#token = null
        localStorage.clear()

        return true
    }
/*
    async findMovies(
        {
            filter: { genre = '', title = '', status = '' } = { genre: '', title: '', status: '' },
            sort,
            pagination: { page, size  } = { page: 0, size: 10000 }
        } = {
            filter: { genre: '', title: '', status: '' },
            sort: {},
            pagination: { page: 0, size:10000 }
        }
    ) {
        try {
            let queryString = ``;
            //si page y size no son undefined, se añaden a la query
            if (page !== undefined) {
                queryString += `page=${page}`;
            }
            if (size !== undefined) {
                queryString += `&size=${size}`;
            }

            if (title) {
                queryString += `&title=${encodeURIComponent(title)}`;
            }
            if (genre) {
                queryString += `&genre=${encodeURIComponent(genre)}`;
            }
            if (status) {
                queryString += `&status=${encodeURIComponent(status)}`;
            }
            if (sort) {
                queryString += `&sort=${encodeURIComponent(sort)}`;

            }

            console.log(`http://localhost:8080/movies?${queryString}`)

            const response = await fetch(`http://localhost:8080/movies?${queryString}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
            });

            if (response.ok) {
                return await response.json();
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al obtener películas:', errorData);
                throw new Error('Error al obtener películas');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }*/

    async findMovies(
        {
            filter: {genre = '', title = '', status = ''} = {genre: '', title: '', status: ''},
            sort,
            pagination: {page = 0, size = 7} = {page: 0, size: 7}
        } = {
            filter: {genre: '', title: '', status: ''},
            sort: {},
            pagination: {page: 0, size: 7}
        }
    ) {
        return new Promise(resolve => {
            const filtered = DATA.movies
                ?.filter(movie => movie.title.toLowerCase().includes(title.toLowerCase() || ''))
                ?.filter(movie => genre !== '' ? movie.genres.map(genre => genre.toLowerCase()).includes(genre.toLowerCase()) : true)
                ?.filter(movie => movie.status.toLowerCase().includes(status.toLowerCase() || ''))

            const data = {
                content: filtered?.slice(size * page, size * page + size),
                pagination: {
                    hasNext: size * page + size < filtered.length,
                    hasPrevious: page > 0
                }
            }

            resolve(data)
        })
    }


    async findMovie(id) {
        const response = await fetch(`http://localhost:8080/movies/${id}`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        return await response.json();
    }

    async findUser(id) {

        const response = await fetch(`http://localhost:8080/users/${id}`, {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        return await response.json();


    }

    async findComments(
        {
            filter: {movieId = '', userId = ''} = {movieId: '', userId: ''},
            pagination: {page = 0, size = 10, sort = 'id'} = {page: 0, size: 10, sort: 'id'}
        }
    ) {
        try {
            let queryString = '';
            const pathArray = window.location.pathname.split('/');
            const movieIdIndex = pathArray.indexOf('movies') + 1; // Index siguiente al de 'movies'
            movieId = movieIdIndex < pathArray.length ? pathArray[movieIdIndex] : '';
            if (movieId) {
                queryString += `movieId=${movieId}&`;
            }
            if (userId) {
                queryString += `userId=${userId}&`;
            }


            queryString += `page=${page}&size=${size}&sort=${sort}`;
            console.log(`http://localhost:8080/comments?${queryString}`)

            const response = await fetch(`http://localhost:8080/comments?${queryString}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
            });

            if (response.ok) {
                return await response.json();
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al obtener comentarios:', errorData);
                throw new Error('Error al obtener comentarios');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }

    async createComment(comment) {
        try {
            const response = await fetch('http://localhost:8080/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(comment),
            });

            if (response.ok) {
                const createdComment = await response.json();
                console.log('Comentario creado:', createdComment);
                return createdComment;
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al crear el comentario:', errorData);
                throw new Error('Error al crear el comentario');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }

    async createUser(user) {
        console.log(user)
        try {
            const response = await fetch('http://localhost:8080/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(user),
            });

            if (response.ok) {
                const createdUser = await response.json();
                console.log('Usuario creado:', createdUser);
                return createdUser;
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al crear el usuario:', errorData);
                throw new Error('Error al crear el usuario');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }

    async updateUser(id, user) {
        try {
            // Crear un array de objetos en el formato esperado
            const patchBody = Object.keys(user).map(key => {
                return {
                    op: "replace",
                    path: `/${key}`,
                    value: user[key]
                };
            });
            const response = await fetch(`http://localhost:8080/users/${id}`, {
                method: 'PATCH', // O el método que corresponda para actualizar usuarios en tu API
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(patchBody),
            });
            console.log(patchBody)


            if (response.ok) {
                const updatedUser = await response.json();
                console.log('Usuario actualizado:', updatedUser);
                return updatedUser;
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al actualizar el usuario:', errorData);
                throw new Error('Error al actualizar el usuario');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }


    //se quiere hacer un update de la pelicula mediante llamada Patch de la api
    //aquí se recibe toda la información de la pelicula pero en el patch es una coleccion de lo que se quiere updatear
    async updateMovieFilm(id, infoMovie) {
        try {
            // Crear un array de objetos en el formato esperado
            const patchBody = Object.keys(infoMovie).map(key => {
                return {
                    op: "replace",
                    path: `/${key}`,
                    value: infoMovie[key]
                };
            });

            const response = await fetch(`http://localhost:8080/movies/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(patchBody),
            });
            console.log(patchBody)

            if (response.ok) {
                const updatedMovie = await response.json();
                console.log('Pelicula actualizada:', updatedMovie);
                return updatedMovie;
            } else {
                // Manejar errores si la respuesta no es exitosa
                const errorData = await response.json();
                console.error('Error al actualizar la pelicula:', errorData);
                throw new Error('Error al actualizar la pelicula');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            throw error; // Puedes manejar el error según tus necesidades
        }
    }



    async eliminarAmistad(userID, friendID) {
        let url = `http://localhost:8080/users/${userID}/friend/${friendID}`;
        try {
            const deleteFriend = await fetch(url, {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
            });

            // Verificar si la respuesta tiene contenido antes de intentar parsearla
            if (!deleteFriend.ok) {
                throw new Error(`Error al eliminar amistad: ${deleteFriend.statusText}`);
            }

            // Verificar si hay contenido en la respuesta antes de intentar parsearla
            const responseText = await deleteFriend.text();
            if (!responseText) {
                return null; // O manejar el caso donde no hay contenido
            }

            return JSON.parse(responseText);
        } catch (error) {
            console.error('Error al eliminar amistad:', error);
            throw error;
        }
    }

    async searchFriends(user) {
        let url = `http://localhost:8080/users/${user}/friends/`
        const friends = await fetch(url,{
            method: "GET",
            headers: { 'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('token')}})
        let finalFriends = await friends.json()
        return await finalFriends
    }


    //añadir amigo sabiendo que la url es /users/{id}/friend y hay que poner tu id de usuario y de body el name y el email del amigo
    async anhadirAmigo(userID, name, email) {
        let url = `http://localhost:8080/users/${userID}/friend`;
        try {
            const addFriend = await fetch(url, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({ email: email,name: name})
            });
            console.log(JSON.stringify({ email: email,name: name}))


            // Verificar si la respuesta tiene contenido antes de intentar parsearla
            if (!addFriend.ok) {
                throw new Error(`Error al añadir amigo: ${addFriend.statusText}`);
            }

            // Verificar si hay contenido en la respuesta antes de intentar parsearla
            const responseText = await addFriend.text();
            if (!responseText) {
                return null; // O manejar el caso donde no hay contenido
            }

            return JSON.parse(responseText);
        } catch (error) {
            console.error('Error al añadir amigo:', error);
            throw error;
        }
    }
}