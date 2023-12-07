import { Shell, MovieList, Carousel } from '../../components'

export default function Movies() {
    return <Shell>
        <Carousel query = {{sort: { releaseDate: 'DESC'}, pagination: { size: 7 }}} />

        <section className = 'bg-pattern-1'>
            <MovieList title = 'Destacadas'
                       paginationVariant = 'inverse'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-2'>
            <MovieList title = 'Acción'
                       paginationVariant = 'plain-secondary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-black p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'genre': 'acción'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-1'>
            <MovieList title = 'Animación'
                       paginationVariant = 'primary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'genre': 'animación'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-2'>
            <MovieList title = 'Ciencia ficción'
                       paginationVariant = 'plain-secondary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-black p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'genre': 'ciencia ficción'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-1'>
            <MovieList title = 'Documentales'
                       paginationVariant = 'primary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'genre': 'documental'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-2'>
            <MovieList title = 'Fantasía'
                       paginationVariant = 'plain-secondary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-black p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'genre': 'fantasía'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>

        <section className = 'bg-pattern-1'>
            <MovieList title = 'De LEGO'
                       paginationVariant = 'primary'
                       className = 'pt-6 pb-12 max-w-screen-2xl'
                       titleClassName = 'select-none bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 p-2 uppercase text-white inline-block transform -skew-x-12 -rotate-3 translate-y-4'
                       query = {{filter: {'title': 'LEGO'}, sort: { 'releaseDate': 'ASC' }, pagination: { size: 7 }}}
            />
        </section>
    </Shell>
}