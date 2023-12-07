import { useContext, useState } from 'react'
import { Redirect } from 'react-router-dom'
import { AtSymbolOutline, FingerPrintOutline } from '@graywolfai/react-heroicons'

import { AuthenticationContext } from '../../context'

import { Logo, Button, Input, Link } from '../../components'

export default function Login() {
    const { login, isAuthenticated, errors, reset } = useContext(AuthenticationContext)
    const [user, setUser] = useState('')
    const [pass, setPass] = useState('')

    const submit = async (event) => {
        event.preventDefault()
        await login(user, pass)
    }

    if(isAuthenticated)
        return <Redirect to = '/' />
    else
        return <main className = 'w-screen h-screen flex flex-col items-center justify-center bg-pattern-1' >
            <form className = 'bg-white rounded p-8 flex flex-col shadow-md text-teal-900'
                  onSubmit = { submit }
                  autoComplete = 'off'>
                <Logo className = 'text-6xl mb-8' logoSize = 'w-12 h-12'/>
                <Input type = 'email'
                       name = 'user'
                       label = 'Usuario'
                       labelClassName = 'mb-4'
                       before = { AtSymbolOutline }
                       errors = { errors }
                       onClick = { reset }
                       variant = 'primary'
                       value = { user }
                       onChange = { evt => setUser(evt.target.value) }
                />
                <Input type = 'password'
                       name = 'password'
                       label = 'Contraseña'
                       labelClassName = 'mb-8'
                       before = { FingerPrintOutline }
                       errors = { errors }
                       onClick = { reset }
                       variant = 'primary'
                       value = { pass }
                       onChange = { evt => setPass(evt.target.value) }
                />
                <Button className = 'mt-8' type = 'submit' variant = 'secondary'>Entrar</Button>
            </form>
            <Link to='register' variant = 'plain-secondary' >Crear nueva cuenta</Link>
        </main>
}