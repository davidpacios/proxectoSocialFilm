import { useEffect, useReducer } from 'react'
import { Link } from 'react-router-dom'
import { EyeOutline as ViewMore } from '@graywolfai/react-heroicons'

import { useMovies } from '../../hooks'

export function Carousel({query = {}}) {
    const { content = [] } = useMovies(query)

    const [{activeIndex}, dispatch] = useReducer(
        (state, action) => {
            switch(action.type) {
                case 'next': return { activeIndex: ((state.activeIndex + 1) % content.length) }
                case 'prev': return { activeIndex: ((state.activeIndex - 1) % content.length) }
                case 'pause': return { activeIndex: state.activeIndex }
                case 'set': return  { activeIndex: action.payload }
                default: throw new Error('Invalid action')
            }
        },
        { activeIndex: 0 },
        () => ({ activeIndex: 0 })
    )

    useEffect(() => {
        const listener = setInterval(() => dispatch({type: 'next'}), 5000)
        return () => clearInterval(listener)
    }, [])

    return <section className = 'relative bg-black'>
        <ul className = 'w-full relative overflow-hidden'
            style = {{ height: '36rem' }}>
            {
                content?.map((movie, idx) =>
                    <Element key = { movie.id } movie = {movie} active = { activeIndex } index = { idx } total = { content.length }/>
                )
            }
        </ul>
        <ul className = 'w-full absolute flex space-x-2 bottom-0 h-12 z-20 items-center justify-center'>
            {
                content?.map((movie, idx) =>
                    <li key = { idx }
                        className = {`block rounded-full bg-white h-2 cursor-pointer transition-all duration-1000 shadow
                                      hover:bg-gradient-to-br hover:from-pink-500 hover:via-red-500 hover:to-yellow-500
                                      ${idx === activeIndex ? 'w-6' : 'w-2'}`}
                        onClick = { () => dispatch({type: 'set', payload: idx})}
                    />
                )
            }
        </ul>
        <span className = 'block absolute bottom-0 z-20 h-1 w-full bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500'/>
    </section>
}

function Element({movie, className = '', active, index, total}) {
    const classes = idx => {
        if(idx === active) {
            return 'z-20'
        } else if (((idx + 1) % total) === active) {
            return '-translate-x-full z-10'
        } else {
            return 'translate-x-full'
        }
    }
    const backdrop = movie => {
        const backdrop = movie?.resources?.find(res => res?.type === 'BACKDROP')?.url
        const poster = movie?.resources?.find(res => res?.type === 'POSTER')?.url

        return backdrop ? backdrop : poster
    }
    const poster = movie => movie?.resources?.find(res => res?.type === 'POSTER')?.url

    return <li className = {`absolute w-full h-full left-0 right-0 bg-black
                             duration-1000 transition-transform ease-out transform
                             ${classes(index)}
                             ${className}`}>
        <Link to = { `/movies/${movie.id}` }
              className = 'group flex relative mx-auto w-full h-full max-w-screen-2xl select-none cursor-pointer'>
            <header className = {`relative w-full max-w-md h-full p-8 flex flex-col z-20
                                  text-white bg-black bg-opacity-60 backdrop-filter backdrop-blur`}>
                <ul className = 'mb-4 gap-2 block w-full text-sm flex flex-row flex-wrap'>
                    {
                        movie.genres.map(genre =>
                            <li key = { genre }
                                className = {`px-3 leading-5 font-bold text-white rounded-full text-xs pointer-events-none whitespace-nowrap
                                            bg-gradient-to-tl from-pink-500 via-red-500 to-yellow-500 shadow`}>
                                {genre}
                            </li>
                        )
                    }
                </ul>
                <div className = 'overflow-hidden'>
                    <img className = 'float-left pointer-events-none object-contain rounded-md overflow-hidden mb-4 mr-4 mt-2 w-48'
                         style = {{ aspectRatio: '2/3'}}
                         alt = { `${movie.title} poster` }
                         src = { poster(movie) }/>
                    <h1 className = 'text-3xl font-bold mb-4'>
                        {movie.title}
                    </h1>
                    <span className = 'italic flex-1 overflow-hidden'>
                        {movie.overview}
                    </span>
                </div>
            </header>
            <div className = {`relative h-full flex-1 flex justify-center items-center z-20 
                               transform translate-x-full transition ease-in-out duration-500
                               backdrop-filter backdrop-blur 
                               group-hover:translate-x-0`}>
                <span className= 'absolute top-0 left-0 bottom-0 right-0 bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500 opacity-20'/>
                <span className = 'relative shadow block rounded-full p-2 w-24 h-24 bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500'>
                    <ViewMore className = 'text-white w-full h-full' />
                </span>
            </div>
            <img className = 'absolute top-0 left-0 right-0 bottom-0 pointer-events-none h-full w-full object-cover z-10'
                 alt = { `${movie.title} backdrop` }
                 src = { backdrop(movie) }/>
        </Link>
    </li>
}
