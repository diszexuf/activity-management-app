export function formatTime(seconds) {
    if (seconds === null || seconds === '' || seconds === undefined) {
        return ''
    }

    const secs = Number(seconds)
    if (isNaN(secs)) {
        return ''
    }

    const hours = Math.floor(secs / 3600)
    const minutes = Math.floor((secs % 3600) / 60)
    const sec = secs % 60

    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
}

