export const ACTIVITY_TYPES = {
    WORK: 'WORK',
    BREAK: 'BREAK'
}

export const ACTIVITY_TYPE_DICT = {
    [ACTIVITY_TYPES.WORK]: 'Работа',
    [ACTIVITY_TYPES.BREAK]: 'Перерыв'
}

export const ACTIVITY_COLORS = {
    [ACTIVITY_TYPES.WORK]: 'primary',
    [ACTIVITY_TYPES.BREAK]: 'success'
}

export const ACTIVITY_TYPE_OPTIONS = [
    { title: ACTIVITY_TYPE_DICT[ACTIVITY_TYPES.WORK], value: ACTIVITY_TYPES.WORK },
    { title: ACTIVITY_TYPE_DICT[ACTIVITY_TYPES.BREAK], value: ACTIVITY_TYPES.BREAK }
]

