import {Shell, TODO, Link, Separator} from '../../components';
import {useComments, useUser} from "../../hooks";
import { StarSolid } from "@graywolfai/react-heroicons";


import {ArrowCircleLeftOutline as Back, PencilAltOutline as Edit} from "@graywolfai/react-heroicons";
import {useEffect, useState} from "react";
import API from "../../api";


export default function EditProfile() {
    const user = useUser().user;

    const [editedUser, setEditedUser] = useState({
        name: user.name || '',
        picture: user.picture || '',
        country: user.country || '',

    });

    const handleUserChange = (e) => {
        const { name, value } = e.target;
        setEditedUser((prevEditedUser) => ({
            ...prevEditedUser,
            [name]: value,
        }));
    };

    // useEffect para actualizar editedUser cuando user cambie
    useEffect(() => {
        setEditedUser({
            name: user.name || '',
            picture: user.picture || '',
            country: user.country || '',
        });
    }, [user]);

    const handleGuardarCambios = async () => {
        try {
            // Lógica para guardar los cambios
            console.log('Guardando cambios:', editedUser);
            await API.instance().updateUser(user.id, editedUser);
            console.log('Cambios guardados correctamente.');
            window.history.back();
        } catch (error) {
            console.error('Error al guardar los cambios:', error);
        }
    }

    return (
        <Shell>
            <img style = {{ height: '36rem' }}
                 src = { `${user.picture}` }
                 alt = { `${user.name} backdrop` }
                 className = 'absolute top-2 left-0 right-0 w-full object-cover filter blur transform scale-105' />

            <Link variant = 'primary'
                  className = 'rounded-full absolute text-white top-4 left-8 flex items-center pl-2 pr-4 py-2 gap-4'
                  to = '/profile'
            >
                <Back className = 'w-8 h-8'/>
                <span>Volver</span>
            </Link>

            <div className = 'mx-auto w-full max-w-screen-2xl p-8'>
                <Header user = { user } editedUser={editedUser} updateUser={handleUserChange} />
                <p className = 'text-2xl font-bold'>Para actualizar tu perfil simplemente coloca tu cursor encima de ella y
                    modificala a tu antojo. ¡No olvides guardar los cambios!</p>
                <p className = 'text-2xl font-bold'>No podrás modificar tu dirección de correo electrónico ni tu fecha de nacimiento</p>
                <Separator />
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
    );
}

function Header({ user, editedUser, updateUser }) {

    return <header className = 'mt-64 relative flex items-end pb-8 mb-8'>
        <div className="flex flex-col items-center">
            <img style = {{ aspectRatio: '2/3' }}
                 src = { `${ user.picture }` }
                 alt = { `${ user.name } poster` }
                 className = 'w-64 rounded-lg shadow-xl z-20' />
            <textarea
                className={`bg-black bg-opacity-50 backdrop-filter backdrop-blur 
                  text-center text-white text-3xl font-bold outline-none`}
                placeholder={user.picture || 'picture'}
                value={editedUser.picture}
                name={'picture'}
                onChange={(e) => { updateUser(e); }}
            />
        </div>
        <hgroup className = 'flex-1'>
            <textarea
                className={`bg-black bg-opacity-50 backdrop-filter backdrop-blur 
              text-left text-white text-6xl font-bold
              p-8 outline-none w-full h-32`}
                placeholder={user.name}
                value={editedUser.name}
                name={'name'}
                onChange={(e) => { updateUser(e); }}
            />
            <Tagline user = { user} editedUser={ editedUser} updateUser={updateUser} />
        </hgroup>

    </header>
}

function Tagline({ user, updateUser }) {
    if (user) {
        return (
            <div className="flex justify-between items-center">
                <div className="flex items-center">
                    <p className="text-3xl font-semibold text-black px-8 py-4">
                        {user.birthday?.day}/{user.birthday?.month}/{user.birthday?.year}
                    </p>
                </div>
                <div className="flex items-center">
                    <textarea className={`bg-black bg-opacity-50 backdrop-filter backdrop-blur 
                  text-center outline-none text-3xl font-semibold text-black px-8 py-4`}
                        placeholder={user.country || 'country'}
                        value={user.country}
                        name={'country'}
                        onChange={(e) => {
                            updateUser(e);}}>

                    </textarea>
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
