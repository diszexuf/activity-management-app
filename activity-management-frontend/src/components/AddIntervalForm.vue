<script setup>
import {ref, computed} from 'vue'

const props = defineProps({
  open: Boolean,
  loading: Boolean,
  error: String
})

const emit = defineEmits(['submit', 'update:open'])

const form = ref({
  start: 0,
  end: 0,
  type: ''
})

const activityTypes = [
  {title: 'Работа', value: 'WORK'},
  {title: 'Перерыв', value: 'BREAK'}
]

const isValid = computed(() => {
  const {start, end, type} = form.value
  return (
      start < end &&
      start >= 0 &&
      end <= 86400 &&
      type !== ''
  )
})

const validationErrors = computed(() => {
  const errors = []
  if (form.value.start >= form.value.end) {
    errors.push('Начало должно быть раньше конца')
  }
  if (form.value.start < 0 || form.value.end > 86400) {
    errors.push('Время должно быть от 0 до 86400 секунд')
  }
  if (!form.value.type) {
    errors.push('Выберите тип активности')
  }
  return errors
})

const submit = () => {
  if (!isValid.value) return

  emit('submit', {
    start: Number(form.value.start),
    end: Number(form.value.end),
    type: form.value.type
  })
}

const reset = () => {
  form.value = {start: 0, end: 0, type: ''}
}

const close = () => {
  emit('update:open', false)
  reset()
}

defineExpose({reset})
</script>

<template>
  <v-dialog
      :model-value="open"
      max-width="500"
      @update:model-value="$emit('update:open', $event)"
      @after-leave="reset"
  >
    <template #activator="{ props: activatorProps }">
      <slot name="activator" :props="activatorProps">
        <v-btn
            v-bind="activatorProps"
            color="primary"
            variant="outlined"
            prepend-icon="mdi-plus"
            class="mb-4"
        >
          Добавить интервал
        </v-btn>
      </slot>
    </template>

    <v-card :loading="loading">
      <v-card-title>Добавление активности</v-card-title>

      <v-card-text>
        <v-alert
            v-if="error"
            type="error"
            variant="tonal"
            class="mb-4"
        >
          {{ error }}
        </v-alert>

        <v-alert
            v-if="validationErrors.length > 0"
            type="warning"
            variant="tonal"
            class="mb-4"
        >
          <ul class="mb-0 pl-4">
            <li v-for="err in validationErrors" :key="err">
              {{ err }}
            </li>
          </ul>
        </v-alert>

        <v-form @submit.prevent="submit">
          <v-number-input
              v-model="form.start"
              label="Начало (секунды)"
              :min="0"
              :max="86400"
              class="mb-3"
          />

          <v-number-input
              v-model="form.end"
              label="Конец (секунды)"
              :min="0"
              :max="86400"
              class="mb-3"
          />

          <v-select
              v-model="form.type"
              :items="activityTypes"
              item-title="title"
              item-value="value"
              label="Тип активности"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <slot name="actions" :submit="submit" :close="close" :isValid="isValid">
          <v-btn @click="close" color="grey" variant="text">Отмена</v-btn>
          <v-btn @click="submit" :disabled="!isValid" color="primary" variant="flat">Добавить</v-btn>
        </slot>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>