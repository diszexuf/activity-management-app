<script setup>
import {computed} from 'vue'

const props = defineProps({
  intervals: {
    type: Array,
    required: true,
    default: () => []
  },
  loading: Boolean,
  error: String
})

const ACTIVITY_TYPE_DICT = {
  'WORK': 'Работа',
  'BREAK': 'Перерыв',
}

const ACTIVITY_COLORS = {
  'WORK': 'primary',
  'BREAK': 'success'
}

const headers = computed(() => [
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
])

defineExpose({
  headers,
  ACTIVITY_TYPE_DICT,
  ACTIVITY_COLORS
})
</script>

<template>
  <v-container fluid>
    <v-alert v-if="error" type="error" variant="tonal" class="mb-4">{{ error }}</v-alert>

    <v-skeleton-loader v-if="loading" type="table-thead, table-tbody" class="mb-4"/>

    <v-data-table v-else
                  :headers="headers"
                  :items="intervals"
                  :items-per-page="10"
                  density="comfortable"
                  class="elevation-1">

      <template #[`item.type`]="{ item }">
        <v-chip
            :color="ACTIVITY_COLORS[item.type] || 'grey'"
            size="small"
            variant="flat">
          {{ ACTIVITY_TYPE_DICT[item.type] || item.type }}
        </v-chip>
      </template>

      <template #no-data>
        <div class="text-center py-8 text-grey">
          <v-icon size="large" class="mb-2">mdi-timer-off</v-icon>
          <div>Нет данных об интервалах</div>
        </div>
      </template>

    </v-data-table>
  </v-container>
</template>