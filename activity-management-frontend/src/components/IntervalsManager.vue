<script setup>
import {ref, watch, onMounted} from 'vue'
import AddIntervalForm from './AddIntervalForm.vue'
import IntervalsTable from './IntervalsTable.vue'
import {useIntervals} from '../composable/useIntervals.js'

const {
  intervals, loading, error, dialogOpen, totalItems, page, itemsPerPage, sortBy, headers,
  fetchIntervals, addInterval, loadItems, openDialog, closeDialog, clearError, ACTIVITY_TYPE_DICT, ACTIVITY_COLORS,
} = useIntervals()

const formLoading = ref(false)
const formError = ref(null)
const snackbar = ref(false)
const timeout = ref(2000)

onMounted(() => { fetchIntervals() })

const handleAddInterval = async (intervalData) => {
  formLoading.value = true
  formError.value = null
  try {
    await addInterval(intervalData)
    snackbar.value = true
    dialogOpen.value = false
    formError.value = null
  } catch (err) {
    formError.value = err.message
  } finally {
    formLoading.value = false
  }
}

watch(() => dialogOpen.value, (isOpen) => {
  if (isOpen) {
    formError.value = null
  }
})

defineExpose({openDialog, closeDialog,})
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-6">
      <div>
        <h1 class="text-h4">Управление активностями</h1>
      </div>

      <AddIntervalForm
          v-model:open="dialogOpen"
          :loading="formLoading"
          :error="formError"
          @submit="handleAddInterval"
      />
    </div>

    <IntervalsTable
        :intervals="intervals"
        :loading="loading"
        :error="error"
        :headers="headers"
        :total-items="totalItems"
        v-model:page="page"
        v-model:items-per-page="itemsPerPage"
        v-model:sort-by="sortBy"
        :activity-type-dict="ACTIVITY_TYPE_DICT"
        :activity-colors="ACTIVITY_COLORS"
        @update:options="loadItems"
        @update:error="clearError"
    />

    <v-snackbar v-model="snackbar" :timeout="timeout">Активность успешно добавлена!</v-snackbar>
  </v-container>
</template>