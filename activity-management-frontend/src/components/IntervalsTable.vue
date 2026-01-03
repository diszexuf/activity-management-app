<script setup>
import { useIntervals } from '../composable/useIntervals.js'

const {
  intervals,
  loading,
  error,
  headers,
  totalItems,
  page,
  itemsPerPage,
  sortBy,
  loadItems,
  ACTIVITY_TYPE_DICT,
  ACTIVITY_COLORS,
} = useIntervals()

const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
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
        @click:close="error = null"
    >
      {{ error }}
    </v-alert>

    <v-data-table-server
        v-model:page="page"
        v-model:items-per-page="itemsPerPage"
        v-model:sort-by="sortBy"
        :headers="headers"
        :items="intervals"
        :items-length="totalItems"
        :loading="loading"
        @update:options="loadItems"
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
        <span class="text-caption text-grey ml-2">{{ formatTime(item.end)}}</span>
      </template>

      <template #item.type="{ item }">
        <v-chip
            :color="ACTIVITY_COLORS[item.type] || 'grey'"
            size="small"
            variant="flat"
        >
          {{ ACTIVITY_TYPE_DICT[item.type] || item.type }}
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
        <v-skeleton-loader type="table-row@10" />
      </template>

    </v-data-table-server>
  </v-container>
</template>