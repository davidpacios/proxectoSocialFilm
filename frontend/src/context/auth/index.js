import React, {useContext, useState} from 'react'
import {Redirect, Route} from 'react-router-dom'
import API from '../../api'

const AuthenticationContext = React.createContext({
    isAuthenticated: false,
    errors: false,
    login: () => {},
    logout: () => {},
    reset: () => {}
})
const client = API.instance()

function SecuredApp({children}) {

    const [isAuthenticated, setIsAuthenticated] = useState(JSON.parse(localStorage.getItem('authenticated') || false))
    const [errors, setErrors] = useState(false)

    const reset = async () => {
        localStorage.setItem('authenticated', JSON.stringify(false))
        setErrors(false)
        setIsAuthenticated(false)
    }
    const login = async (user, pass) => {
        const loginSuccessful = await client.login(user, pass)
        localStorage.setItem('authenticated', JSON.stringify(loginSuccessful))
        setIsAuthenticated(loginSuccessful)
        setErrors(!loginSuccessful)
    }
    const logout = async () => {
        await client.logout()
        await reset()
    }

    const context = { isAuthenticated, login, logout, errors, reset }

    return <AuthenticationContext.Provider value = { context } >
        { children }
    </AuthenticationContext.Provider>

}

function SecuredRoute({children, ...props}) {

    const {isAuthenticated} = useContext(AuthenticationContext)
    return isAuthenticated ? <Route {...props}>{children}</Route> : <Redirect to = '/login' />

}

export { AuthenticationContext, SecuredApp, SecuredRoute }
