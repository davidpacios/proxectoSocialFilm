import {useContext, useState} from 'react'
import { Redirect, useHistory } from 'react-router-dom'

import { AuthenticationContext } from '../../context'

import {Logo, Link, Input, Button} from '../../components'
import { useUser } from '../../hooks'
import { AtSymbolOutline, FingerPrintOutline, UserOutline, CalendarOutline } from '@graywolfai/react-heroicons'

export default function Login() {
    const history = useHistory()
    const { isAuthenticated } = useContext(AuthenticationContext)
    const { create } = useUser()
    const [ errors, setErrors ] = useState(false)


    const submit = async (event) => {
        event.preventDefault()
        const data = new FormData(event.target)
        const birthday = data.get('birthday')

        try {
            const [day, month, year] = birthday.split(/[^0-9]/).map(part => Number.parseInt(part))
            const date = new Date(year, month - 1, day)

            debugger

            if(date.getDate() !== day || date.getMonth() !== month - 1 || date.getFullYear() !== year) {
                setErrors(true)
            } else {
                await create({
                    email: data.get('email'),
                    name: data.get('name'),
                    password: data.get('password'),
                    birthday: {
                        day,
                        month,
                        year
                    }
                })

                history.push('/')
            }
        } catch (err) {
            setErrors(true)
        }
    }

    const reset = () => {
        setErrors(false)
    }

    if (isAuthenticated)
        return <Redirect to='/' />
    else
        return <main className = 'w-screen h-screen grid place-items-center content-center bg-pattern-1' >
            <form className = 'bg-white rounded shadow p-8 flex flex-col text-teal-900'
                  onSubmit = { submit }
                  autoComplete = 'off'>
                <Logo className = 'text-6xl mb-8' logoSize = 'w-12 h-12'/>
                <Input type = 'email'
                       name = 'user'
                       label = 'Email'
                       labelClassName = 'mb-4'
                       errors = { errors }
                       onClick = { reset }
                       before = { AtSymbolOutline }
                       variant = 'primary'
                />
                <Input type = 'text'
                       name = 'name'
                       label = 'Nombre'
                       labelClassName = 'mb-4'
                       errors = { errors }
                       onClick = { reset }
                       before = { UserOutline }
                       variant = 'primary'
                />
                <Input type = 'text'
                       name = 'birthday'
                       label = 'Fecha de nacimiento'
                       labelClassName = 'mb-4'
                       errors = { errors }
                       onClick = { reset }
                       before = { CalendarOutline }
                       variant = 'primary'
                       pattern = '[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}'
                />
                <Input type = 'password'
                       name = 'password'
                       label = 'Contraseña'
                       labelClassName = 'mb-8'
                       errors = { errors }
                       onClick = { reset }
                       before = { FingerPrintOutline }
                       variant = 'primary'
                />
                <Button className = 'mt-8' type = 'submit' variant = 'secondary'>Registrar</Button>
            </form>
            <Link to='login' variant = 'plain-secondary'>Iniciar sesión</Link>
        </main>
}