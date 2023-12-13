import DATA from './data'

let __instance = null

export default class API {
    #token = sessionStorage.getItem('token') || null

    static instance() {
        if(__instance == null)
            __instance = new API()

        return __instance
    }

    async login(email, pass) {
        const login = await fetch(`http://localhost:8080/login`,{
            method: "POST",
            headers: { 'Content-Type': 'application/json'},
            body: JSON.stringify({ email: email, password: pass })
        })

        const token = login.headers.get('Authentication')
        const userID = login.headers.get('UserID');

        if(token !== null) {
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
    async findMovies(
        {
            filter: { genre = '', title = '', status = '' } = { genre : '', title : '', status : '' },
            sort,
            pagination: {page = 0, size = 7} = { page: 0, size: 7 }
        } = {
            filter: { genre : '', title : '', status : '' },
            sort: {},
            pagination: { page: 0, size: 7 }
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
            filter: { movieId = '', userId = '' } = { movieId: '', userId: '' },
            pagination: { page = 0, size = 10, sort = 'id' } = { page: 0, size: 10, sort: 'id' }
        }
    ) {
        try {
            let queryString = '';

            const pathArray = window.location.pathname.split('/');
            const movieIdIndex = pathArray.indexOf('movies') + 1; // Index siguiente al de 'movies'
            movieId = movieIdIndex < pathArray.length ? pathArray[movieIdIndex] : '';
            //movieId = '716354' //Esta pelicula tiene comentarios //TODO: PRuebas
            queryString += `movieId=${movieId}&`;
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
        console.log (user)
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
        console.log(user)
    }
}