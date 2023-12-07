import { useEffect, useReducer } from 'react'
import { Redirect } from 'react-router-dom'

import { Shell, Button } from '../../components'

import { ReactComponent as Image } from './not-found.svg'

export default function NotFound() {
    const [ timeout, dispatch ] = useReducer(
        (state, action) => {
            switch(action) {
                case 'tick' : return state - 1
                case 'end' : return 0
                default: throw new Error('invalid action')
            }
        },
        15,
        arg => arg
    )

    useEffect(() => {
        const listener = setInterval(() => {
            dispatch('tick')
        }, 1000)
        return () => clearInterval(listener)
    }, [])

    if(timeout === 0) {
        return <Redirect to = '/' />
    } else {
        return <Shell className = 'p-4 flex flex-col items-center justify-center bg-pattern-1'>
            <h1 className = 'bg-black text-white p-4 font-bold text-7xl transform -skew-x-12 -rotate-3'>
                404
            </h1>
            <Image className = 'w-1/2 h-96 transform -rotate-3'/>
            <h2 className = 'bg-black text-white p-4 font-bold text-2xl transform -skew-x-12 -rotate-3'>
                La ruta a la que intentas acceder no existe!
            </h2>
            <Button className = 'transform -skew-x-12 -rotate-3 mt-4'
                    variant = 'plain-primary'
                    onClick = { () => dispatch('end') }>
                Serás redirigido automáticamente en { timeout }
                <br />
                Redirigir ahora
            </Button>
        </Shell>
    }
}