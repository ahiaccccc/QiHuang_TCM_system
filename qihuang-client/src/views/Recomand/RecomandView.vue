<template>
  <Navi
    :avatar="getAvatarUrl(profile.avatar) || defaultAvatar"
    :nickname="profile.username"
  />
  <div class="health-recommendations">
    <n-space vertical>
      <n-card
        title="养生建议查询"
        size="small"
      >
        <n-space>
          <n-select
            v-model:value="selectedClass"
            :options="classOptions"
            placeholder="选择类别"
            style="width: 200px"
            clearable
          />
          <n-select
            v-model:value="selectedType"
            :options="typeOptions"
            placeholder="选择子类别"
            style="width: 200px"
            clearable
          />
        </n-space>
      </n-card>

      <n-data-table
        :columns="columns"
        :data="filteredData"
        :bordered="false"
        striped
        :single-line="false"
      />
    </n-space>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { NDataTable, NSelect, NSpace, NCard } from 'naive-ui'

// Data loading
const data = ref([])
const loadData = async () => {
  try {
    const response = await import('../../assets/data.json')
    console.log('Data loaded:', response)
    data.value = response.default || response
  } catch (error) {
    console.error('Failed to load data:', error)
  }
}

onMounted(async () => {
  loadData()
  await loadProfile()
})

const columns = [
  {
    title: '建议名称',
    key: 'title',
    width: 180,
  },
  {
    title: '详细内容',
    key: 'describe',
  },
  {
    title: '类别',
    key: 'class_id',
    render: (row) => {
      const classNames = {
        1: '节气养生',
        2: '年龄段养生',
        3: '体质养生',
      }
      return classNames[row.class_id] || row.class_id
    },
  },
  {
    title: '子类别',
    key: 'type',
  },
]

const selectedClass = ref(null)
const selectedType = ref(null)

const classOptions = [
  { label: '节气养生', value: 1 },
  { label: '年龄段养生', value: 2 },
  { label: '体质养生', value: 3 },
]

const typeOptions = computed(() => {
  const types = new Set()
  data.value.forEach((item) => {
    if (!selectedClass.value || item.class_id === selectedClass.value) {
      types.add(item.type)
    }
  })
  return Array.from(types).map((type) => ({ label: type, value: type }))
})

const filteredData = computed(() => {
  return data.value.filter((item) => {
    const classMatch = !selectedClass.value || item.class_id === selectedClass.value
    const typeMatch = !selectedType.value || item.type === selectedType.value
    return classMatch && typeMatch
  })
})

// ---------------- 用户信息 ----------------
import Navi from '../components/NaviHomeView.vue'
import { getProfileAPI } from '@/apis/user'
import defaultAvatar from '../../assets/images/defaultAvatar.png'
const getAvatarUrl = (avatar) => {
  return avatar ? `http://localhost:8080${avatar}` : null
}
const profile = ref({
  username: '',
  userId: '',
  email: '',
  avatar: '',
})
const loadProfile = async () => {
  try {
    const response = await getProfileAPI()
    if (response.code === 200) {
      profile.value = response.data
      // console.log('个人信息加载成功:', profile.value)
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.health-recommendations {
  padding: 7%;
  background-color: #e6f2f2;
  min-height: 100vh;

  :deep(.n-data-table) {
    .n-data-table-tr--striped {
      background-color: #f0f7f7;
    }

    .n-data-table-tr {
      &:hover {
        background-color: #d9ecec;
      }
    }
  }
}
</style>