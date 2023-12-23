import {Shell, TODO, Link, Separator} from '../../components';
import {useComments, useUser} from "../../hooks";
import { StarSolid } from "@graywolfai/react-heroicons";


import {ArrowCircleLeftOutline as Back, PencilAltOutline as Edit} from "@graywolfai/react-heroicons";


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

            <Link variant = 'primary'
                  className = 'rounded-full absolute text-white top-4 right-8 flex items-center px-2 py-2 gap-4'
                  to = {`/profile/edit`}
            >
                <Edit className = 'w-8 h-8'/>
            </Link>

            <div className = 'mx-auto w-full max-w-screen-2xl p-8'>
                <Header user = { user } />
                <UltimosComentarios/>
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


function UltimosComentarios() {
    return <>
        <h1 className = 'mt-16 font-bold text-2xl'>Últimos Comentarios</h1>
        <Separator />
        <Comments />

    </>
}


function RatingStars({ rating }) {
    const maxStars = 5;
    const filledStars = Math.round(rating);
    return (
        <div className="flex items-center text-yellow-500">
            {Array.from({ length: maxStars }, (_, index) => (
                <StarSolid
                    key={index}
                    className={`w-6 h-6 ${index < filledStars ? "fill-current" : ""}`}
                />
            ))}
        </div>
    );
}

function Comments() {
    const user = useUser().user;
    const { comments } = useComments({ filter: { userId: user.id } });
    return (
        <div className="bg-white p-8">
            {comments.totalElements > 0 ? (
                <div className="comments-list flex flex-wrap -mx-4">
                    {comments.content.map((comment) => (
                        <div
                            key={comment.id}
                            className="w-screen px-4 mb-8"
                        >
                            <div className="max-w bg-white border border-black shadow-2xl rounded-md p-4">
                                <div className="flex justify-between mb-2">
                                    <p className="text-gray-700 font-semibold">
                                        {comment.movie.title}
                                    </p>
                                    <RatingStars rating={comment.rating} />
                                </div>
                                <div className="comment-details">
                                    <p className="text-gray-800">Comentario: {comment.comment}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <p className="text-gray-700">No hay comentarios disponibles.</p>
            )}
        </div>
    );
}
