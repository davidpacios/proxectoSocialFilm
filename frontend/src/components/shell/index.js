import { useContext, useState } from 'react'
import { useHotkeys } from 'react-hotkeys-hook'
import { Link } from 'react-router-dom'
import {
    ChevronUpOutline as Close,
    LogoutOutline as Logout,
    UserGroupOutline as Friends,
    UserOutline as Profile,
    SearchOutline as Search,
    DocumentAddOutline as New } from '@graywolfai/react-heroicons'
import { Menu, Transition } from '@headlessui/react'
import { useDebouncedCallback as useDebounce } from 'use-debounce'

import { AuthenticationContext } from '../../context'
import { Logo, Button, Input, MovieList } from '../'
import { useUser } from '../../hooks'

function tryParse(query) {
    try{
        if(query?.startsWith('?') && query?.length > 1) {
            const parts = query.slice(1)
                ?.split(';')
                ?.map(part => part?.trim())
                ?.map(part => part?.split('=')) || {}

            return Object.fromEntries(parts)
        } else {
            return {title: query}
        }
    }catch(e) {
        return {title: query}
    }
}

export function Shell({children, className = ''}) {
    const [ query, setQuery ] = useState('')
    const [ focused, setFocused ] = useState(false)
    const debouncedSetQuery = useDebounce(setQuery, 500)

    const form = () => document?.getElementById("searchForm")
    const input = () => form()?.querySelector("input[name=search]")
    const close = () => {
        blur()
        setQuery('')
    }
    const blur = () => {
        setFocused(false)
        form()?.querySelectorAll("*")?.forEach(elem => elem?.blur())
    }
    const focus = () => {
        setFocused(true)
        input()?.focus()
    }

    useHotkeys('ctrl+q', focus)

    return <section className = 'overflow-hidden min-h-screen'>
        <header className = {`fixed bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500
                              w-full h-16 flex justify-start space-x-12 items-center shadow px-4 z-50`}
                onClick = { evt => evt.target !== input() && blur() }
        >
            <Link to = '/'>
                <Logo className = 'text-4xl ' inverse/>
            </Link>
            <form autoComplete = 'off' className = 'max-w-lg group flex-1 relative overflow-hidden' id = 'searchForm'>
                <Input name = 'search'
                       type = 'text'
                       placeholder = 'Puedes efectuar busquedas avanzadas con ?'
                       labelClassName = 'text-gray-400 rounded-full focus-within:text-yellow-600'
                       className = 'rounded-full bg-white shadow-inner placeholder-gray-400 font-medium'
                       before = { Search }
                       onFocus = { focus }
                       onChange = { evt => debouncedSetQuery(evt.target.value?.trimStart()) }
                       onKeyUp = { evt => evt.key === 'Escape' && close() }
                />
                <button className = {`absolute right-4 top-3 bottom-3 flex items-center px-2 pt-px 
                                    rounded border-2 border-gray-300 cursor-pointer bg-white transform transition -translate-y-10
                                    font-mono text-sm text-gray-300 font-semibold uppercase tracking-wide
                                    group-focus-within:translate-y-0`}
                        type = 'button'
                        onClick = { blur }
                >
                    esc
                </button>
                <button className = {`absolute right-4 top-3 bottom-3 flex items-center px-2 pt-px 
                                    rounded border-2 border-gray-300 cursor-pointer bg-white transform transition
                                    font-mono text-sm text-gray-300 font-semibold uppercase tracking-wide
                                    group-focus-within:translate-y-10`}
                        type = 'button'
                        onClick = { focus }
                >
                    ctrl + q
                </button>
            </form>
            <nav className = 'h-full flex flex-1 items-center justify-end'>
                <AppMenu />
            </nav>
        </header>
        <aside className = {`fixed top-16 left-0 w-full h-screen z-40
                             shadow-md bg-black bg-opacity-70 transition-transform transform 
                             ${focused ? 'translate-y-0' : '-translate-y-full'}`}
               onClick = { evt => evt.target === evt.currentTarget && blur() }
        >
            <div className = 'w-full bg-white'>
                <MovieList className = 'bg-white pt-8' itemClassName = 'hover:translate-y-4' query = { { filter: tryParse(query) } } />
            </div>
            <footer className = {`w-full flex bg-white justify-center p-2 cursor-pointer transition transform hover:-translate-y-2` }>
                <Button type = 'reset' onClick = { close } className = 'w-full' form = 'searchForm'>
                    <Close className = 'text-yellow-600 w-8 h-8 pointer-events-none' />
                </Button>
            </footer>
        </aside>
        <main className = {`relative mt-16 overflow-x-hidden w-full min-h-full flex-1 max-w-full ${className}`}>
            { children }
        </main>
        <footer className = 'bg-black px-16 py-12 flex justify-between items-center'>
            <Logo className = 'text-6xl' logoSize = 'w-12 h-12' inverse/>
            <span className = 'text-white'>&copy; 2021 watch.it</span>
        </footer>
    </section>
}

function AppMenu() {
    const { logout } = useContext(AuthenticationContext)
    const { user: { name = '', email = '', picture = '' } } = useUser()

    return <Menu>
        { ({open}) => <>
            <Menu.Button className = {`flex flex-row justify-end items-center h-12 space-x-4 overflow-hidden focus:outline-none`}>
                    <span className = 'flex flex-col text-white text-right'>
                        <span className='text-sm font-bold'>{ name } </span>
                        <span className='text-xs font-bold'>{ email }</span>
                    </span>

                <img className = 'h-12 w-12 rounded-full shadow'
                     alt = 'profile icon'
                     src = { picture } />
            </Menu.Button>
            <Transition show = { open }
                        className = 'absolute right-4 top-0 pt-2 transform-gpu duration-500 ease-spring'
                        enter = 'transition-transform'
                        enterFrom = '-translate-y-full'
                        enterTo = 'translate-y-0'
                        leave = 'transition-transform'
                        leaveFrom = 'translate-y-0'
                        leaveTo = '-translate-y-full'>
                <Menu.Items static className = {`shadow mt-16 w-40 bg-white rounded border-gray-500 outline-none divide-y divide-gray-200`}>
                    <div className = 'py-1'>
                        <Menu.Item>
                            <Link className = {`flex items-center w-full h-10 text-sm leading-5 text-left transition text-gray-700 transition-all group
                                        hover:bg-gray-100 hover:text-gray-900`}
                                  to = '/profile'>
                                <span className = 'block bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 w-0 h-full group-hover:w-2'/>
                                <Profile className = 'h-4 w-4 mx-4 text-gray-500'/>
                                <span>Ver perfil</span>
                            </Link>
                        </Menu.Item>
                        <Menu.Item>
                            <Link className = {`flex items-center w-full h-10 text-sm leading-5 text-left transition text-gray-700 transition-all group
                                        hover:bg-gray-100 hover:text-gray-900`}
                                  to = '/friends'>
                                <span className = 'block bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 w-0 h-full group-hover:w-2'/>
                                <Friends className = 'h-4 w-4 mx-4 text-gray-500'/>
                                <span>Amigos</span>
                            </Link>
                        </Menu.Item>
                    </div>
                    <div className = 'py-1'>
                        <Menu.Item>
                            <Link className = {`flex items-center w-full h-10 text-sm leading-5 text-left transition text-gray-700 transition-all group
                                        hover:bg-gray-100 hover:text-gray-900`}
                                  to = '/movies/new'>
                                <span className = 'block bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 w-0 h-full group-hover:w-2'/>
                                <New className = 'h-4 w-4 mx-4 text-gray-500'/>
                                <span>Nueva película</span>
                            </Link>
                        </Menu.Item>
                    </div>
                    <div className = 'py-1'>
                        <Menu.Item>
                            <button className = {`flex items-center w-full h-10 text-sm leading-5 text-left transition text-gray-700 transition-all group
                                        hover:bg-gray-100 hover:text-gray-900`}
                                    onClick = { logout }>
                                <span className = 'block bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500 w-0 h-full group-hover:w-2'/>
                                <Logout className = 'h-4 w-4 mx-4 text-gray-500'/>
                                <span>Salir</span>
                            </button>
                        </Menu.Item>
                    </div>
                </Menu.Items>
            </Transition>
        </>
        }

    </Menu>
}