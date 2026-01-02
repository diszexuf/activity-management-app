<script setup>
import {onMounted, ref} from 'vue'
import AddIntervalForm from './AddIntervalForm.vue'
import IntervalsTable from './IntervalsTable.vue'
import {useIntervals} from "../composable/useIntervals.js";

const {
  intervals,
  loading,
  error,
  dialogOpen,
  fetchIntervals,
  addInterval,
  openDialog,
  closeDialog
} = useIntervals()

const formLoading = ref(false)
const formError = ref(null)

const handleAddInterval = async (intervalData) => {
  formLoading.value = true
  formError.value = null

  try {
    await addInterval(intervalData)
  } catch (err) {
    formError.value = err.message
    throw err
  } finally {
    formLoading.value = false
  }
}

onMounted(() => {
  fetchIntervals()
})


defineExpose({
  openDialog,
  closeDialog,
})
</script>

<template>
  <v-container>
    <h1 class="text-h4 mb-4">Активности</h1>

    <div class="intervals-manager">
      <div class="d-flex justify-space-between align-center mb-6">
        <div class="d-flex gap-2">
          <AddIntervalForm
              :open="dialogOpen"
              :loading="formLoading"
              :error="formError"
              @update:open="dialogOpen = $event"
              @submit="handleAddInterval"/>
        </div>
      </div>

      <IntervalsTable
          :intervals="intervals"
          :loading="loading"
          :error="error"/>

    </div>
  </v-container>
</template>

<style scoped>

</style>