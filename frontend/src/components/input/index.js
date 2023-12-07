export function Input({
                                  type = 'text',
                                  className = '',
                                  labelClassName = '',
                                  before: BeforeIcon = null,
                                  after: AfterIcon = null,
                                  label = '',
                                  errors = false,
                                  variant = '',
                                  ...props
                              }) {
    let inputStyles
    let labelStyles

    switch (variant) {
        case 'primary':
            inputStyles = `bg-teal-500 bg-opacity-20
                      focus:bg-yellow-300 focus:bg-opacity-20 focus:text-yellow-900 focus:ring focus:ring-yellow-500`
            labelStyles = 'focus-within:text-yellow-900'
            break
        case 'inverse':
            inputStyles = `bg-yellow-300 bg-opacity-20
                      focus:bg-teal-500 focus:bg-opacity-20 focus:text-teal-900 focus:ring focus:ring-teal-500`
            labelStyles = 'focus-within:text-teal-900'
            break
        default:
            inputStyles = ''
            labelStyles = ''
            break
    }

    return <label className = {`block text-sm font-bold space-y-2 ${labelStyles} ${labelClassName}`}>
        { label !== '' ? <span>{label}</span> : null }
        <p className = { `relative w-full transition
                          ${errors ? 'ring ring-red-500' : ''}`
        }>
            { BeforeIcon !== null ? <BeforeIcon className = 'absolute top-4 left-0 w-4 mx-4'/> : null }
            <input type = { type }
                   className = {`rounded outline-none h-12 w-full font-medium
                                 ${ BeforeIcon !== null ? 'pl-12' : 'pl-4'}
                                 ${ AfterIcon !== null ? 'pr-12' : 'pr-4'}
                                 ${ inputStyles }
                                 ${ className }`}
                   { ...props }
            />
            { AfterIcon !== null ? <AfterIcon className = 'absolute top-4 left-0 w-4 mx-4'/> : null }
        </p>
    </label>

}