<script setup>
import {computed} from 'vue'
import {formatTime} from '../utils/timeFormatter.js'

const props = defineProps({
  intervals: {
    type: Array,
    required: true,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  },
  headers: {
    type: Array,
    required: true
  },
  totalItems: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    required: true
  },
  itemsPerPage: {
    type: Number,
    required: true
  },
  sortBy: {
    type: Array,
    required: true
  },
  activityTypeDict: {
    type: Object,
    required: true
  },
  activityColors: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:page', 'update:itemsPerPage', 'update:sortBy', 'update:options', 'update:error'])

const pageModel = computed({
  get: () => props.page,
  set: (value) => emit('update:page', value)
})

const itemsPerPageModel = computed({
  get: () => props.itemsPerPage,
  set: (value) => emit('update:itemsPerPage', value)
})

const sortByModel = computed({
  get: () => props.sortBy,
  set: (value) => emit('update:sortBy', value)
})

const handleUpdateOptions = (options) => {
  emit('update:options', options)
}

const handleCloseError = () => {
  emit('update:error', null)
}
</script>

<template>
  <v-container fluid>
    <v-alert
        v-if="error"
        type="error"
        variant="tonal"
        class="mb-4"
        closable
        @click:close="handleCloseError"
    >
      {{ error }}
    </v-alert>

    <v-data-table-server
        v-model:page="pageModel"
        v-model:items-per-page="itemsPerPageModel"
        v-model:sort-by="sortByModel"
        :headers="headers"
        :items="intervals"
        :items-length="totalItems"
        :loading="loading"
        @update:options="handleUpdateOptions"
        density="comfortable"
        class="elevation-1"
        :items-per-page-options="[
        { value: 10, title: '10' },
        { value: 25, title: '25' },
        { value: 50, title: '50' },
        { value: 100, title: '100' }
      ]"
        :show-current-page="true"
        items-per-page-text="Записей на странице:"
    >
      <template #item.start="{ item }">
        <span class="font-mono">{{ item.start }}</span>
        <span class="text-caption text-grey ml-2">{{ formatTime(item.start) }}</span>
      </template>

      <template #item.end="{ item }">
        <span class="font-mono">{{ item.end }}</span>
        <span class="text-caption text-grey ml-2">{{ formatTime(item.end) }}</span>
      </template>

      <template #item.type="{ item }">
        <v-chip :color="activityColors[item.type] || 'grey'" size="small" variant="flat">
          {{ activityTypeDict[item.type] || item.type }}
        </v-chip>
      </template>

      <template #no-data>
        <div class="text-center py-12">
          <v-icon size="64" color="grey-lighten-1" class="mb-4">
            mdi-timer-off
          </v-icon>
          <div class="text-h6 text-grey">Нет данных об интервалах</div>
          <div class="text-caption text-grey mt-2">
            Добавьте первый интервал с помощью кнопки выше
          </div>
        </div>
      </template>

      <template #loading>
        <v-skeleton-loader type="table-row@10"/>
      </template>

    </v-data-table-server>
  </v-container>
</template>