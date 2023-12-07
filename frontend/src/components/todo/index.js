export function TODO({children}) {
    return <div className = 'p-4 w-full rounded bg-yellow-400 text-yellow-800 flex flex-col'>
        <span className = 'text-xs font-bold uppercase underline mb-4'>TODO:</span>
        <p>{children}</p>
    </div>
}