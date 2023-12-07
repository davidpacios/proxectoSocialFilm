import { Link as RouterLink } from 'react-router-dom'

export function Link({className = '', to, children, variant = 'primary', ...props}) {
    let styles = ''

    switch (variant) {
        case 'plain-primary':
            styles = `text-transparent bg-clip-text
                      bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500
                      hover:from-green-500 hover:to-blue-500 hover:via-teal-500
                      focus:from-green-500 focus:to-blue-500 focus:via-teal-500`
            break
        case 'plain-secondary':
            styles = `text-transparent bg-clip-text
                      bg-gradient-to-br from-green-500 to-blue-500 via-teal-500
                      hover:from-pink-500 hover:to-yellow-500 hover:via-red-500
                      focus:from-pink-500 focus:to-yellow-500 focus:via-red-500`
            break;
        case 'primary':
            styles = `text-white 
                      bg-gradient-to-br from-pink-500 to-yellow-500 via-red-500
                      hover:from-green-500 hover:to-blue-500 hover:via-teal-500
                      focus:from-green-500 focus:to-blue-500 focus:via-teal-500`
            break
        case 'secondary':
            styles = `text-white 
                      bg-gradient-to-br from-green-500 to-blue-500 via-teal-500
                      hover:from-pink-500 hover:to-yellow-500 hover:via-red-500
                      focus:from-pink-500 focus:to-yellow-500 focus:via-red-500`
            break;
        default: throw new Error('variant not allowed')
    }

    return <RouterLink className = { `font-bold text-sm mt-4 no-underline outline-none 
                                      ${styles}
                                      ${className}`}
                 to = {to}
                 {...props}>
        {children}
    </RouterLink>
}