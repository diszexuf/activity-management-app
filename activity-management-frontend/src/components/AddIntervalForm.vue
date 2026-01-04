<script setup>
import {ref, computed, watch} from 'vue'
import { formatTime } from '../utils/timeFormatter.js'
import { ACTIVITY_TYPE_OPTIONS } from '../constants/activityTypes.js'

const props = defineProps({
  open: Boolean,
  loading: Boolean,
  error: String
})

const emit = defineEmits(['submit', 'update:open'])

const formRef = ref(null)
const form = ref({
  start: null,
  end: null,
  type: ''
})

const startRules = [
  v => v !== null && v !== '' || 'Обязательное поле',
  v => Number.isInteger(Number(v)) || 'Должно быть целым числом',
  v => v >= 0 || 'Минимум 0',
  v => v <= 86400 || 'Максимум 86400',
  v => {
    if (form.value.end !== null && form.value.end !== '') {
      return v < form.value.end || 'Начало должно быть меньше конца'
    }
    return true
  }
]

const endRules = [
  v => v !== null && v !== '' || 'Обязательное поле',
  v => Number.isInteger(Number(v)) || 'Должно быть целым числом',
  v => v >= 0 || 'Минимум 0',
  v => v <= 86400 || 'Максимум 86400',
  v => {
    if (form.value.start !== null && form.value.start !== '') {
      return v > form.value.start || 'Конец должен быть больше начала'
    }
    return true
  }
]

const typeRules = [
  v => !!v || 'Выберите тип активности'
]

const startTimeFormatted = computed(() => formatTime(form.value.start))
const endTimeFormatted = computed(() => formatTime(form.value.end))

const submit = async () => {
  const {valid} = await formRef.value.validate()

  if (!valid) return

  emit('submit', {
    start: Number(form.value.start),
    end: Number(form.value.end),
    type: form.value.type
  })
}

const reset = () => {
  form.value = {
    start: null,
    end: null,
    type: ''
  }
  formRef.value?.resetValidation()
}

const close = () => {
  emit('update:open', false)
  reset()
}

defineExpose({reset})

watch(() => props.open, (isOpen) => {
  if (!isOpen) {
    reset()
  }
})
</script>

<template>
  <v-dialog :model-value="open" max-width="600" @update:model-value="$emit('update:open', $event)">
    <template #activator="{ props: activatorProps }">
      <slot name="activator" :props="activatorProps">
        <v-btn v-bind="activatorProps" color="primary" prepend-icon="mdi-plus">Добавить интервал</v-btn>
      </slot>
    </template>

    <v-card :loading="loading">
      <v-card-title class="text-h5">Добавление активности</v-card-title>

      <v-card-text>
        <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable>
          {{ error }}
        </v-alert>

        <v-form ref="formRef" @submit.prevent="submit">
          <v-text-field
              v-model.number="form.start"
              label="Начало (секунды)"
              type="number"
              :rules="startRules"
              :hint="startTimeFormatted ? `Время: ${startTimeFormatted}` : 'От 0 до 86400 секунд'"
              persistent-hint
              class="mb-2"
          />

          <v-text-field
              v-model.number="form.end"
              label="Конец (секунды)"
              type="number"
              :rules="endRules"
              :hint="endTimeFormatted ? `Время: ${endTimeFormatted}` : 'От 0 до 86400 секунд'"
              persistent-hint
              class="mb-2"
          />

          <v-select
              v-model="form.type"
              :items="ACTIVITY_TYPE_OPTIONS"
              item-title="title"
              item-value="value"
              label="Тип активности"
              :rules="typeRules"
              class="mb-2"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <slot name="actions" :submit="submit" :close="close">
          <v-btn @click="close" variant="text" :disabled="loading">Отмена</v-btn>
          <v-btn @click="submit" color="primary" variant="flat" :loading="loading">Добавить</v-btn>
        </slot>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>