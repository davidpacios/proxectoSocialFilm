import {Link, Separator, Shell} from '../../components'
import {useParams} from "react-router-dom";
import { useMovie} from "../../hooks";
import {ArrowCircleLeftOutline as Back, PencilAltOutline as Edit} from "@graywolfai/react-heroicons";
import Disney from "../movie/icons/disney_plus.png";
import Play from "../movie/icons/google_play.png";
import HBO from "../movie/icons/hbo.png";
import ITunes from "../movie/icons/itunes.png";
import Netflix from "../movie/icons/netflix.png";
import Prime from "../movie/icons/prime_video.png";
import Youtube from "../movie/icons/youtube.png";
import ReactPlayer from "react-player";
import {useEffect, useState} from "react";
import updateMovieFilm from "../../api/";
import API from "../../api/";


const backdrop = movie => {
    const backdrop = movie?.resources?.find(res => res?.type === 'BACKDROP')?.url
    const poster = movie?.resources?.find(res => res?.type === 'POSTER')?.url

    return backdrop ? backdrop : poster
}
const poster = movie => movie?.resources?.find(res => res?.type === 'POSTER')?.url


export default function EditMovie() {
    const { id } = useParams()
    const movie = useMovie(id)

    const [editedMovie, setMovie] = useState(movie);

    // useEffect para actualizar editedUser cuando user cambie
    useEffect(() => {
        setMovie({
            title: movie.title || '',
            overview: movie.overview || '',
            crew: movie.crew || '',
        });
    }, [movie]);

// Función para actualizar la película
    const updateMovie = (e) => {
        // Manejar cambios en campos específicos del crew
        const { name, value } = e.target;
        if (name.startsWith("crew")) {
            const id = name.split("/")[1]; //recuperar el id del crew
            //recuperar el indice del crew
            const index = movie.crew.findIndex((crew) => crew.id === id);
            setMovie((prevEditedMovie) => {
                const newCrew = [...prevEditedMovie.crew];
                newCrew[index].name = e.target.value;
                return {
                    ...prevEditedMovie,
                    crew: newCrew,
                };
            });
        } else {
            const { name, value } = e.target;
            setMovie((prevEditedMovie) => ({
                ...prevEditedMovie,
                [name]: value,
            }));
        }
    };

    const handleGuardarCambios = async () => {
        try {
            // Lógica para guardar los cambios
            console.log('Guardando cambios:', editedMovie);
            await API.instance().updateMovieFilm(movie.id, editedMovie);
            console.log('Cambios guardados correctamente.');
            window.history.back();
        } catch (error) {
            console.error('Error al guardar los cambios:', error);
        }
    };

    return <Shell>
        <img style = {{ height: '36rem' }}
             src = { backdrop(movie) }
             alt = { `${movie.title} backdrop` }
             className = 'absolute top-2 left-0 right-0 w-full object-cover filter blur transform scale-105' />

        <Link variant = 'primary'
              className = 'rounded-full absolute text-white top-4 left-8 flex items-center pl-2 pr-4 py-2 gap-4'
              to = {`/movies/${id}`}
        >
            <Back className = 'w-8 h-8'/>
            <span>Volver</span>
        </Link>


        <div className = 'mx-auto w-full max-w-screen-2xl p-8'>
            <Header movie={movie} editedMovie={editedMovie} updateMovie={updateMovie} />
            <Info movie = { movie } editedMovie={editedMovie} updateMovie={updateMovie} />
            <View movie = { movie }/>
            <Cast movie = { movie } editedMovie={editedMovie} updateMovie={updateMovie}/>
        </div>

        <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <button
                style={{
                    padding: '12px 24px',
                    borderRadius: '8px',
                    backgroundColor: '#FF5733',
                    color: 'white',
                    fontSize: '16px',
                    fontWeight: 'bold',
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'background-color 0.3s',
                    marginRight: '10px', // Espaciado entre botones
                }}
                onClick={() => {
                    window.history.back();
                }}
            >
                Cancelar
            </button>
            <button
                style={{
                    padding: '12px 24px',
                    borderRadius: '8px',
                    backgroundColor: '#4CAF50',
                    color: 'black',
                    fontSize: '16px',
                    fontWeight: 'bold',
                    border: 'none',
                    cursor: 'pointer',
                    transition: 'background-color 0.3s',
                }}
                onClick={handleGuardarCambios}
            >
                Guardar Cambios
            </button>
        </div>



    </Shell>
}
function Header({ movie, editedMovie, updateMovie }) {

    return (
        <header className="mt-64 relative flex items-end pb-8 mb-8">
            <img
                style={{ aspectRatio: '2/3' }}
                src={poster(movie)}
                alt={`${movie.title} poster`}
                className="w-64 rounded-lg shadow-xl z-20"
            />
            <hgroup className="flex-1">
                <textarea
                    className={`bg-black bg-opacity-50 backdrop-filter backdrop-blur 
              text-left text-white text-6xl font-bold p-8 ml-4 
              placeholder-gray-800 placeholder-opacity-50 w-full h-32`}
                    value={editedMovie.title}
                    name={'title'}
                    onChange={(e) => updateMovie(e)}
                    placeholder={movie.title}
                />
                <Tagline movie={movie} />
            </hgroup>
        </header>
    );
}


function Info({ movie, editedMovie, updateMovie }) {
    return <div className = 'grid grid-cols-5 gap-4'>
        <div className = 'col-span-4'>
            <h2 className = 'font-bold text-2xl text-white bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500 p-4 shadow'>
                Argumento
            </h2>
            <textarea
                className="pt-8 p-4 w-full h-40 bg-black bg-opacity-50 backdrop-filter backdrop-blur text-white font-normal"
                value={editedMovie.overview}
                name={'overview'}
                onChange={(e) => updateMovie(e)}
                placeholder={movie.overview}
            />

        </div>
        <div className = 'text-right'>
            <dl className = 'space-y-2'>
                <CrewMember movie = { movie } job = 'Director' label = 'Dirección' editedMovie={editedMovie} updateMovie={updateMovie}/>
                <CrewMember movie = { movie } job = 'Producer' label = 'Producción' editedMovie={editedMovie} updateMovie={updateMovie}/>
                <CrewMember movie = { movie } job = 'Screenplay' label = 'Guión' editedMovie={editedMovie} updateMovie={updateMovie}/>
                <CrewMember movie = { movie } job = 'Original Music Composer' label = 'Banda sonora' editedMovie={editedMovie} updateMovie={updateMovie}/>
            </dl>
        </div>
    </div>
}

function View({ movie }) {
    return <div className = 'flex gap-4 mt-8'>
        <div className = 'w-80 z-10'>
            <Links movie = { movie } />
        </div>
        <div style = {{
            aspectRatio: '16/9'
        }}
             className = 'flex-1 ml-8 mt-8 bg-pattern-2 flex items-center justify-center z-20'>
            <Trailer movie = { movie } />
        </div>
    </div>
}

function Cast({ movie }) {
    return <>
        <h2 className = 'mt-16 font-bold text-2xl'>Reparto principal</h2>
        <Separator />
        <ul className = 'w-full grid grid-cols-10 gap-2 overflow-hidden'>
            {
                movie?.cast?.slice(0, 10).map(person => <CastMember key = { person.name } person = { person }/>)
            }
        </ul>
    </>
}
function Tagline({ movie }) {
    if(movie.tagline) {
        return <q className={`block text-3xl font-semibold text-black italic w-full px-8 py-4 text-right`}>
            {movie.tagline}
        </q>
    } else {
        return <span className = 'block text-3xl font-semibold py-4'>&nbsp;</span>
    }
}
function CrewMember({ movie, job, label, editedMovie, updateMovie }) {
    const people= movie?.crew?.filter(p => p.job === job) || []
    if(editedMovie.crew && editedMovie.crew.length > 0) {
        const peopleEdit = editedMovie.crew.filter(p => p.job === job) || []
        if(peopleEdit.length > 0) {
            peopleEdit.forEach((p, index) => {
                people[index] = p
            })
        }
    }

    if(people.length === 0) return null
    return (
        <div>
            <dt className='font-bold text-sm'>{label}</dt>
            {people.map((p, index) => (
                <input
                    type="text"
                    value={p.name}
                    name={`crew/${p.id}`}
                    onChange={(e) => {updateMovie(e);}}
                    className="text-sm block text-black p-2 border border-gray-300 rounded mt-2"
                    key={`${job}/${p.id}`}
                />
            ))}
        </div>
    );
}
function Links({ movie }) {
    const resources = movie?.resources?.filter(r => !['POSTER', 'BACKDROP', 'TRAILER'].includes(r.type))
    let links

    if(resources?.length === 0) {
        links = <span className = 'block p-8 text-center bg-gray-300 font-bold'>
            No se han encontrado enlaces!
        </span>
    } else {
        links = <ul className = 'space-y-4'>
            {
                resources?.map(r => <PlatformLink key = { r.type } type = { r.type } url = { r.url } />)
            }
        </ul>
    }


    return <>
        <h2 className = 'font-bold text-2xl'>Ver ahora</h2>
        <Separator />
        { links }
    </>
}
function CastMember({ person }) {
    return <li className = 'overflow-hidden'>
        <img src = { person?.picture }
             alt = { `${person.name} profile` }
             className = 'w-full object-top object-cover rounded shadow'
             style = {{ aspectRatio: '2/3' }}/>
        <span className = 'font-bold block'> { person?.name } </span>
        <span className = 'text-sm block'> { person?.character } </span>
    </li>
}

function PlatformLink({ type = '', url = '', ...props }) {
    switch (type) {
        case 'DISNEY_PLUS':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { Disney } alt = 'Disney+ logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en
                </span>
            </a>
        case 'GOOGLE_PLAY':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { Play } alt = 'Google Play logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en Google Play
                </span>
            </a>
        case 'HBO':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { HBO } alt = 'HBO logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en HBO
                </span>
            </a>
        case 'ITUNES':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { ITunes } alt = 'iTunes logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en iTunes
                </span>
            </a>
        case 'NETFLIX':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { Netflix } alt = 'Netflix logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en Netflix
                </span>
            </a>
        case 'PRIME_VIDEO':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { Prime } alt = 'Prime Video logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en Prime Video
                </span>
            </a>
        case 'YOUTUBE':
            return <a target = '_blank'
                      rel = 'noreferrer'
                      href = { url }
                      className = {`flex items-center space-x-2 overflow-hidden h-16 w-full bg-white
                                    transform transition duration-200 
                                    hover:translate-x-8 hover:scale-105`}>
                <img src = { Youtube } alt = 'YouTube logo'
                     className = 'rounded-lg w-16 h-16'
                />
                <span className = 'font-bold'>
                    Reproducir en YouTube
                </span>
            </a>
        default: return null
    }
}
function Trailer({ movie, ...props }) {
    const trailer = movie?.resources?.find(r => r.type === 'TRAILER')

    if(trailer)
        return <ReactPlayer url = { trailer.url } { ...props } />
    else
        return <span className = 'text-white text-xl font-semibold p-8 backdrop-filter backdrop-blur bg-red-500 bg-opacity-30'>No se han encontrado trailers!</span>
}