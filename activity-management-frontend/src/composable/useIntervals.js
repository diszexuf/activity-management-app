import {computed, ref} from 'vue'
import intervalsService from "../service/intervalsService.js";

export function useIntervals() {
    const intervals = ref([])
    const loading = ref(false)
    const error = ref(null)
    const dialogOpen = ref(false)

    const fetchIntervals = async () => {
        loading.value = true
        error.value = null
        try {
            intervals.value = await intervalsService.getAllIntervals()
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки данных'
        } finally {
            loading.value = false
        }
    }

    const addInterval = async (intervalData) => {
        try {
            const newInterval = await intervalsService.createInterval(intervalData)
            intervals.value = [...intervals.value, newInterval]
            dialogOpen.value = false
            return newInterval
        } catch (e) {
            throw e.response?.data?.message ? new Error(e.response.data.message) : e
        }
    }

    const openDialog = () => {
        dialogOpen.value = true
    }

    const closeDialog = () => {
        dialogOpen.value = false
    }

    return {
        intervals,
        loading,
        error,
        dialogOpen,
        fetchIntervals,
        addInterval,
        openDialog,
        closeDialog,
        isEmpty: computed(() => intervals.value.length === 0)
    }
}