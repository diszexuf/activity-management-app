import {Configuration, IntervalsApi} from './../api/generated'

const apiBasePath = import.meta.env.VITE_API_BASE_URL || '/api/v1'

const configuration = new Configuration({
    basePath: apiBasePath
})

const intervalsApi = new IntervalsApi(configuration)

export const intervalsService = {
    async getAllIntervals(page, size, sort) {
        try {
            const response = await intervalsApi.getAllIntervals(page, size, sort)
            return response.data
        } catch (e) {
            console.error('Ошибка при получении интервалов', e)
            throw e
        }
    },

    async createInterval(intervalData) {
        try {
            const response = await intervalsApi.createInterval(intervalData);
            return response.data
        } catch (e) {
            console.error('Ошибка при создании интервала', e)
            throw e
        }
    }
}

export default intervalsService