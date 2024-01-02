import {Shell, TODO, Link, Separator} from '../../components';
import {useComments, useFriendship, useUser} from "../../hooks";
import { ArrowCircleLeftOutline as Back, XOutline as Cancel, CheckOutline as OK } from '@graywolfai/react-heroicons'
import {useState} from "react";




export default function Profile() {
    const user = useUser().user;
    return (
        <Shell>
            <img style = {{ height: '36rem' }}
                 src = { `${user.picture}` }
                 alt = { `${user.name} backdrop` }
                 className = 'absolute top-2 left-0 right-0 w-full object-cover filter blur transform scale-105' />

            <Link variant = 'primary'
                  className = 'rounded-full absolute text-white top-4 left-8 flex items-center pl-2 pr-4 py-2 gap-4'
                  to = '/'
            >
                <Back className = 'w-8 h-8'/>
                <span>Volver</span>
            </Link>



            <div className = 'mx-auto w-full max-w-screen-2xl p-8'>
                <Header user = { user } />
                <SeccionAmigos user = { user }/>
            </div>


        </Shell>
    );
}




function Header({ user }) {
    return <header className = 'mt-64 relative flex items-end pb-8 mb-8'>
        <img style = {{ aspectRatio: '2/3' }}
             src = { `${ user.picture }` }
             alt = { `${ user.name } poster` }
             className = 'w-64 rounded-lg shadow-xl z-20' />
        <hgroup className = 'flex-1'>
            <h1 className = {`bg-black bg-opacity-50 backdrop-filter backdrop-blur 
                                          text-right text-white text-6xl font-bold
                                          p-8`}>
                { user.name }
            </h1>
            <Tagline user = { user } />
        </hgroup>
    </header>
}

function Tagline({ user }) {
    if (user) {
        return (
            <div className="flex justify-between items-center">
                <div className="flex items-center">
                    <p className="text-3xl font-semibold text-black px-8 py-4">
                        {user.birthday?.day}/{user.birthday?.month}/{user.birthday?.year}
                    </p>
                </div>
                <div className="flex items-center">
                    <p className="text-3xl font-semibold text-black px-8 py-4">
                        {user.country}
                    </p>
                </div>
                <p className="text-3xl font-semibold text-black px-8 py-4">
                    {user.email}
                </p>
            </div>
        );
    } else {
        return <span className="block text-3xl font-semibold py-4">&nbsp;</span>;
    }
}


function SeccionAmigos({ user }) {
    const { friendship, eliminarAmistad, anhadirAmigo } = useFriendship( user.id )

    return (
        <>
            <h1 className='mt-16 font-bold text-2xl'>Añadir Amigos</h1>
            <Separator />
            <AgregarAmigos anhadirAmigo={anhadirAmigo} />
            <h1 className='mt-16 font-bold text-2xl'>Amigos</h1>
            <Separator />

            {friendship && friendship.length > 0 ? (
                <div className='grid gap-4 grid-cols-3'>
                    {friendship.map(solicitud => (
                        <Amistad
                            amistad={solicitud}
                            eliminarAmistad={eliminarAmistad}
                            key={solicitud.friendship.id}
                        />
                    ))}
                </div>
            ) : (
                <p>No has agregado a ningún amigo.</p>
            )}
        </>
    );
}




function Amistad({ amistad, eliminarAmistad }) {
    const handleCancela = (e) => {
        // se llama al delete de amistades
        eliminarAmistad(localStorage.getItem('userID'), `${amistad.user.id}`);
    };

    return (
        <>
            {amistad.friendship.confirmed === true ? (
                <div
                    className='flex items-center  p-4 border'
                    style={{ boxShadow: '0px 0px 5px 4px rgba(214,214,214,1)' }}
                >
                    <img
                        style={{ height: '80px' }}
                        src={`${amistad.user.picture}`}
                        alt={`${amistad.user.email} poster`}
                        className='w-24 rounded-full'
                    />
                    <div className='flex-1 flex flex-col items-center mr-4'>
                        <h1 style={{ fontSize: '32px' }} className='w-full text-center font-bold '>
                            {amistad.user.name}
                        </h1>
                        <div className='flex items-center gap-8 pt-4 pb-4'>
                            <h2 className='w-44 text-center italic'>
                                Sois amigos desde el {amistad.friendship.since.day}/
                                {amistad.friendship.since.month}/{amistad.friendship.since.year}
                            </h2>
                            <Cancel className='w-8 h-8 cursor-pointer' onClick={handleCancela} />
                        </div>
                    </div>
                </div>
            ) : null}
        </>
    );
}


function AgregarAmigos({ anhadirAmigo }) {
    const [emailToAdd, setEmailToAdd] = useState('');
    const [nameToAdd, setNameToAdd] = useState('');

    const handleEnviarSolicitud = () => {
        if (/^\S+@\S+\.\S+$/.test(emailToAdd)) {
            anhadirAmigo(localStorage.getItem('userID'),nameToAdd,emailToAdd)
                .then((response) => {
                    console.log("Solicitud de amistad enviada:", response);
                    setEmailToAdd('');
                    setNameToAdd('');
                })
                .catch((error) => {
                    console.error("Error al enviar solicitud de amistad:", error);
                });
        } else {
            console.log("Email no válido")
        }
    };

    return (
        <div className="flex items-center gap-3">
            <input
                type="text"
                placeholder="Email del amigo"
                value={emailToAdd}
                onChange={(e) => setEmailToAdd(e.target.value)}
                className="border border-gray-300 p-2 rounded-md"
            />
            <input
                type="text"
                placeholder="Nombre del amigo"
                value={nameToAdd}
                onChange={(e) => setNameToAdd(e.target.value)}
                className="border border-gray-300 p-2 rounded-md"
            />
            <button
                onClick={handleEnviarSolicitud}
                className="bg-red-500 text-white px-4 py-2 rounded-md"
            >
                Añadir amigo
            </button>
        </div>
    );
}




