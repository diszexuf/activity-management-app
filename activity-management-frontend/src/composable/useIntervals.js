import { computed, ref, watch } from 'vue'
import intervalsService from '../service/intervalsService.js'
import { ACTIVITY_TYPE_DICT, ACTIVITY_COLORS } from '../constants/activityTypes.js'

const STORAGE_KEY = 'intervals_table_settings'

const getSavedSettings = () => {
    try {
        const settings = localStorage.getItem(STORAGE_KEY)
        if (settings) {
            return JSON.parse(settings)
        }
    } catch (e) {
        console.error('Error loading settings:', e)
    }
    return {
        page: 1,
        itemsPerPage: 10,
        sortBy: [{ key: 'start', order: 'asc' }]
    }
}

const savedSettings = getSavedSettings()

export function useIntervals() {
    const intervals = ref([])
    const loading = ref(false)
    const error = ref(null)
    const dialogOpen = ref(false)
    const totalElements = ref(0)

    const page = ref(savedSettings.page)
    const itemsPerPage = ref(savedSettings.itemsPerPage)
    const sortBy = ref(savedSettings.sortBy)

    const saveSettings = () => {
        const settings = {
            page: page.value,
            itemsPerPage: itemsPerPage.value,
            sortBy: sortBy.value
        }
        localStorage.setItem(STORAGE_KEY, JSON.stringify(settings))
    }

    watch([page, itemsPerPage, sortBy], saveSettings, { deep: true })

    const fetchIntervals = async () => {
        loading.value = true
        error.value = null

        try {
            const apiPage = Math.max(0, page.value - 1)
            const apiSort = sortBy.value?.length > 0
                ? `${sortBy.value[0].key},${sortBy.value[0].order}`
                : 'start,asc'

            const response = await intervalsService.getAllIntervals(apiPage, itemsPerPage.value, apiSort)

            intervals.value = response.intervals || []
            totalElements.value = response.totalElements || 0
        } catch (e) {
            console.error('Error fetching intervals:', e)
            error.value = e.response?.data?.message || 'Ошибка загрузки данных'
            intervals.value = []
            totalElements.value = 0
        } finally {
            loading.value = false
        }
    }

    const addInterval = async (intervalData) => {
        try {
            await intervalsService.createInterval(intervalData)
            await fetchIntervals()
            dialogOpen.value = false
        } catch (e) {
            console.error('Error adding interval:', e)
            const message = e.response?.data?.message || 'Ошибка при создании интервала'
            throw new Error(message)
        }
    }

    const loadItems = async ({ page: newPage, itemsPerPage: newItemsPerPage, sortBy: newSortBy }) => {
        page.value = newPage
        itemsPerPage.value = newItemsPerPage
        sortBy.value = newSortBy

        await fetchIntervals()
    }

    const openDialog = () => {
        dialogOpen.value = true
    }

    const closeDialog = () => {
        dialogOpen.value = false
    }

    const clearError = () => {
        error.value = null
    }

    const totalPages = computed(() => {
        if (totalElements.value <= 0 || itemsPerPage.value <= 0) return 1
        return Math.ceil(totalElements.value / itemsPerPage.value)
    })

    const isEmpty = computed(() => intervals.value.length === 0)

    const currentRange = computed(() => {
        if (intervals.value.length === 0) return '0-0'
        const start = (page.value - 1) * itemsPerPage.value + 1
        const end = start + intervals.value.length - 1
        return `${start}-${end}`
    })

    return {
        intervals,
        loading,
        error,
        dialogOpen,
        totalItems: totalElements,
        page,
        itemsPerPage,
        sortBy,
        fetchIntervals,
        addInterval,
        loadItems,
        openDialog,
        closeDialog,
        clearError,
        totalPages,
        isEmpty,
        currentRange,
        headers: [
            {
                title: 'Начало (сек)',
                key: 'start',
                sortable: true,
            },
            {
                title: 'Конец (сек)',
                key: 'end',
                sortable: true,
            },
            {
                title: 'Тип',
                key: 'type',
                sortable: true
            }
        ],
        ACTIVITY_TYPE_DICT,
        ACTIVITY_COLORS
    }
}