export function Button({
                                   type = 'button',
                                   children = '',
                                   variant = '',
                                   className = '',
                                   iconSize = 'w-4 h-4',
                                   before: BeforeIcon = null,
                                   after: AfterIcon = null,
                                   disabled = false,
                                   ...props}) {
    let styles

    switch (variant) {
        case 'plain-primary':
            styles = `shadow bg-black
                  focus:bg-gradient-to-br focus:ring-pink-500 focus:bg-gradient-to-br focus:from-pink-500 focus:via-red-500 focus:to-yellow-500
                  hover:bg-gradient-to-br hover:bg-gradient-to-br hover:from-pink-500 hover:via-red-500 hover:to-yellow-500`
            break
        case 'plain-secondary':
            styles = `shadow bg-black
                  focus:bg-gradient-to-br focus:ring-teal-500 focus:from-green-400 focus:via-teal-500 focus:to-blue-500
                  hover:bg-gradient-to-br hover:from-green-400 hover:via-teal-500 hover:to-blue-500`
            break
        case 'primary':
            styles = `shadow bg-gradient-to-br from-pink-500 via-red-500 to-yellow-500
                  focus:ring-teal-500 focus:from-green-400 focus:via-teal-500 focus:to-blue-500
                  hover:from-green-400 hover:via-teal-500 hover:to-blue-500`
            break
        case 'secondary':
            styles = `shadow bg-gradient-to-br from-green-400 via-teal-500 to-blue-500
                  focus:ring-pink-500 focus:bg-gradient-to-br focus:from-pink-500 focus:via-red-500 focus:to-yellow-500
                  hover:bg-gradient-to-br hover:from-pink-500 hover:via-red-500 hover:to-yellow-500`
            break
        default:
            styles = 'bg-transparent cursor-pointer'
            break
    }

    return <button
        disabled = { disabled }
        type = {type || 'button'}
        className = { `font-semibold relative p-4 text-white rounded transition flex justify-center items-center
                       focus:outline-none focus:ring focus:ring-offset-2 space-x-2
                       ${styles}
                       ${className}
                       ${disabled ? 'invisible' : '' }` }
        { ...props }
    >
        { BeforeIcon !== null ? <BeforeIcon className = { iconSize } /> : null }
        <span className = 'pointer-events-none'>{ children }</span>
        { AfterIcon !== null ? <AfterIcon className = { iconSize } /> : null }
    </button>
}