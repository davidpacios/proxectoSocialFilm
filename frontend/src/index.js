import React from 'react'
import ReactDOM from 'react-dom'
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom'
import ReactFontLoader from 'react-font-loader'

import { SecuredApp, SecuredRoute } from './context'

import Login from './pages/login'
import Register from './pages/registry'
import Movies from './pages/movies'
import Movie from './pages/movie'
import EditMovie from './pages/edit-movie'
import CreateMovie from './pages/create-movie'
import Profile from './pages/profile'
import Friends from './pages/friends'
import NotFound from './pages/not-found'

import './styles.css'

ReactDOM.render(
    <React.StrictMode>
        <SecuredApp>
            <ReactFontLoader url = 'https://fonts.googleapis.com/css2?family=Quicksand:wght@300;400;500;600;700&display=swap' />
            <ReactFontLoader url = 'https://fonts.googleapis.com/css2?family=Modak&display=swap' />
            <ReactFontLoader url = 'https://fonts.googleapis.com/css2?family=Courier+Prime&display=swap' />
            <Router>
                <Switch>
                    <Route exact path = '/login'>
                        <Login />
                    </Route>
                    <Route exact path = '/register'>
                        <Register />
                    </Route>
                    <SecuredRoute exact path = '/movies/new' >
                        <CreateMovie />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/movies/:id/edit' >
                        <EditMovie />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/movies/:id' >
                        <Movie />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/movies'>
                        <Movies />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/profile'>
                        <Profile />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/friends'>
                        <Friends />
                    </SecuredRoute>
                    <SecuredRoute exact path = '/404'>
                        <NotFound />
                    </SecuredRoute>
                    <Redirect exact from = '/' to = '/movies' />
                    <Redirect exact to = '/404' />
                </Switch>
            </Router>
        </SecuredApp>
    </React.StrictMode>,
    document.getElementById('root')
);
