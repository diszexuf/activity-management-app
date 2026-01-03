<script setup>
import {ref} from 'vue'
import AddIntervalForm from './AddIntervalForm.vue'
import IntervalsTable from './IntervalsTable.vue'
import {useIntervals} from '../composable/useIntervals.js'

const {
  dialogOpen,
  addInterval,
  openDialog,
  closeDialog,
} = useIntervals()

const formLoading = ref(false)
const formError = ref(null)
const snackbar = ref(false)
const timeout = ref(2000)

const handleAddInterval = async (intervalData) => {
  formLoading.value = true
  formError.value = null
  try {
    await addInterval(intervalData)
    snackbar.value = true
    dialogOpen.value = false
  } catch (err) {
    formError.value = err.message
  } finally {
    formLoading.value = false
  }
}

defineExpose({
  openDialog,
  closeDialog,
})
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

    <IntervalsTable/>

    <v-snackbar v-model="snackbar" :timeout="timeout">Активность успешно добавлена!</v-snackbar>

  </v-container>
</template>