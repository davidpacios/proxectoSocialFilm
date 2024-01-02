import { useEffect, useState } from 'react'

import API from '../api'

export function useMovies(query = {}) {
    const [data, setData] = useState({ content: [], pagination: { hasNext: false, hasPrevious: false }})
    const queryString = JSON.stringify(query)

    useEffect(() => {
        API.instance()
            .findMovies(JSON.parse(queryString))
            .then(setData)
    }, [queryString])

    return data
}

export function useMovie(id = '') {
    const [data, setData] = useState({})

    useEffect(() => {
        API.instance()
            .findMovie(id)
            .then(setData)
    }, [id])

    return data
}

export function useUser(id = null) {
    const [data, setData] = useState([])
    const userId = id === null ? localStorage.getItem('userID') : id

    useEffect(() => {
        API.instance()
            .findUser(userId)
            .then(user => {
                setData(user)
            })
    }, [userId])

    const create = user => API.instance()
            .createUser(user)
            .then(user => setData(user))

    const update = user => API.instance()
            .updateUser(id, user)
            .then(user => setData(user))

    return {
        user: data,
        create,
        update
    }
}

export function useComments(query = {
    filter: { } ,
    pagination: {  }
}){
    const [data, setData] = useState({ content: [], pagination: { hasNext: false, hasPrevious: false }})
    const queryString = JSON.stringify(query)
    useEffect(() => {
        if (query && query.filter && query.filter.userId) {
            API.instance()
                .findComments(JSON.parse(queryString))
                .then(setData)
                .catch(error => console.error("Error al obtener comentarios:", error));
        }
    }, [queryString])

    const create = comment => {
        API.instance()
            .createComment(comment)
            .then( () => {
                API.instance()
                    .findComments(query)
                    .then(setData)
            })
    }

    return {
        comments: data,
        createComment: create
    }
}

export function useFriendship(user = null, amigo = null) {
    const [data, setData] = useState([]);
    const [reload, setReload] = useState(false); // Nuevo estado para recargar

    useEffect(() => {
        if (amigo === null || reload) {
            API.instance()
                .searchFriends(user)
                .then((data) => {
                    setData(data);
                    console.log("datos hook: ", data);
                    setReload(false); // Reiniciar el estado de recarga después de actualizar los datos
                })
                .catch((error) =>
                    console.error('Error al obtener amigos:', error)
                );
        }
    }, [user, reload]); // Incluye reload como dependencia del efecto

    const searchFriends = (user) =>
        API.instance()
            .searchFriends(user)
            .then((data) => {
                setData(data);
            });

    const eliminarAmistad = async (userid, friendid) => {
        try {
            const response = await API.instance().eliminarAmistad(userid, friendid);
            setData(response);
            setReload(true); // Establecer el estado de recarga después de eliminar un amigo
            return response;
        } catch (error) {
            console.error('Error al eliminar amistad:', error);
            throw error;
        }
    };

    const anhadirAmigo = async (userid, nombreAmigo, emailAmigo) => {
        try {
            const response = await API.instance().anhadirAmigo(userid, nombreAmigo, emailAmigo);
            setData(response);
            setReload(true); // Establecer el estado de recarga después de añadir un amigo
            return response;
        } catch (error) {
            console.error('Error al añadir amigo:', error);
            throw error;
        }
    };

    return {
        friendship: data,
        searchFriends,
        eliminarAmistad,
        anhadirAmigo
    };
}