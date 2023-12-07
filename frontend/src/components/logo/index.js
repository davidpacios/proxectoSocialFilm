import { FilmOutline as Film } from '@graywolfai/react-heroicons'

export function Logo({className = '', logoSize = 'w-8 h-8', inverse, ...props}) {
    return <span className = {`flex justify-center items-center 
                               tracking-tight text-transparent whitespace-nowrap font-logo select-none 
                               ${inverse ? 'text-white' : 'bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500 bg-clip-text'}
                               ${className}`}
                 {...props} >
        <Film className = {`transform rotate-6 mr-4
                                   ${ logoSize } 
                                   ${ inverse ? 'text-white' : 'text-red-500' }`}
        />
        watch.it
    </span>
}