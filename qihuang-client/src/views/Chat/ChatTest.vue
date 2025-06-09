<template>
  <div class="chat-container">
    <div class="sidebar">
      <div class="sidebar-content">
        <h3>新建会话</h3>
        <button
          class="new-conversation-btn"
          @click="startNewConversation"
        >➕ 新建对话</button>

        <h3>应用界面</h3>
        <button
          class="new-conversation-btn"
          @click="showAllApps"
        >📱 全部应用</button>
        <h3>我的收藏</h3>
        <button
          class="new-conversation-btn"
          @click="showCollections"
        >⭐ 我的收藏</button>

        <h3>历史会话</h3>
        <div class="history-section">
          <ul>
            <li
              v-for="(conv, index) in conversations"
              :key="conv.id"
              class="conversation-item"
            >
              <div
                class="conversation-wrapper"
                :class="{ active: currentConversation && currentConversation.id === conv.id }"
              >
                <button
                  @click="loadConversation(conv.id)"
                  class="conversation-btn"
                >
                  {{ conv.title }}
                </button>
                <div
                  class="dropdown-container"
                  @click.stop
                >
                  <button
                    class="ellipsis-btn"
                    @click="toggleDropdown(index)"
                  >⋯</button>
                  <div
                    class="dropdown-menu"
                    v-if="activeDropdown === index"
                  >
                    <button @click="renameConversation(conv.id)">重命名</button>
                    <button @click="deleteConversation(conv.id)">删除</button>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <div
        class="logo-2"
        @click="goToHome"
      >
        <img
          class="logo-3"
          alt="Logo"
          src="https://c.animaapp.com/ma28oshf5ECohh/img/logo-1-1.png"
        />
        <div class="text-wrapper-9">岐黄慧问</div>
      </div>
    </div>
    <!-- 侧边栏 -->

    <!-- 主聊天窗口 -->
    <div class="chat-window">

      <!-- 应用视图 -->
      <div
        v-if="showAppsView"
        class="apps-view"
      >
        <h2>中医资源推荐</h2>
        <div class="apps-grid">
          <div
            v-for="app in apps"
            :key="app.id"
            class="app-card"
            @click="openApp(app.url)"
          >
            <div class="app-icon">
              <img
                :src="app.icon"
                :alt="app.name"
              />
            </div>
            <div class="app-info">
              <h3>{{ app.name }}</h3>
              <p>{{ app.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 收藏视图 -->
      <div
        v-if="showCollectionsView"
        class="collections-view"
      >
        <h2>我的收藏</h2>
        <div class="search-container">
          <input
            v-model="searchQuery"
            @keyup.enter="searchCollections"
            placeholder="输入收藏ID或关键词搜索"
            class="search-input"
          />
          <button
            @click="searchCollections"
            class="search-btn"
          >搜索</button>
          <button
            @click="resetSearch"
            class="reset-btn"
          >重置</button>
        </div>
        <div class="collections-grid">
          <div
            v-for="collection in filteredCollections"
            :key="collection.collectionId"
            class="collection-card"
          >
            <div class="collection-header">
              <span class="collection-id">收藏ID: {{ collection.collectionId }}</span>
            </div>
            <div class="collection-content">
              <div
                class="markdown-body"
                v-html="renderMarkdown(collection.content)"
              ></div>
            </div>
            <div class="collection-footer">
              <div class="collection-actions">
                <span class="collection-date">创建日期：{{ formatDate(collection.createdAt) }}</span>
                <button
                  @click="deleteCollection(collection.collectionId)"
                  class="icon-btn"
                >
                  <img
                    src="@/assets/images/删除.png"
                    alt="delete"
                    class="icon-img"
                  >
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
        v-if="!showAppsView && !showCollectionsView"
        class="chat-messages-container"
      >
        <div
          class="messages"
          ref="messages"
        >
          <!-- 引导语部分 -->
          <div
            v-if="isNewConversation"
            class="welcome-message"
          >
            <div class="welcome-content">
              <h2>欢迎使用岐黄慧问</h2>
              <p>我是您的中医健康助手，可以为您解答以下问题：</p>
              <ul class="suggestion-list">
                <li @click="useSuggestion('中医如何调理失眠？')">中医如何调理失眠？</li>
                <li @click="useSuggestion('请解释一下气血不足的表现')">请解释一下气血不足的表现</li>
                <li @click="useSuggestion('针灸治疗头痛有哪些穴位？')">针灸治疗头痛有哪些穴位？</li>
                <li @click="useSuggestion('推荐一些适合秋季养生的食疗方')">推荐一些适合秋季养生的食疗方</li>

              </ul>
              <p class="tip">点击上方问题可以直接提问，或者在下方的输入框中输入您的问题</p>
            </div>
          </div>

          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="msg.role"
          >

            <div
              v-if="msg.role === 'user'"
              class="user-message-container"
            >
              <div
                class="bubble"
                v-if="editingMessageIndex !== index"
              >
                <strong>你:</strong> {{ msg.content }}
                <div class="message-actions">
                  <button
                    @click="startEditing(index, msg.content)"
                    class="icon-btn"
                  >
                    <img
                      src="@/assets/images/编辑.png"
                      alt="edit"
                      class="icon-img"
                    >
                  </button>
                  <button
                    @click="handleCopy(msg.content)"
                    class="icon-btn"
                  >
                    <img
                      src="@/assets/images/复制.png"
                      alt="copy"
                      class="icon-img"
                    >
                  </button>
                </div>
              </div>

              <!-- 编辑状态 -->
              <div
                class="bubble editing-bubble"
                v-else
              >
                <textarea
                  v-model="editingContent"
                  ref="editTextarea"
                  class="edit-textarea"
                ></textarea>
                <div class="edit-actions">
                  <button
                    @click="saveEdit(index)"
                    class="edit-btn save-btn"
                  >保存</button>
                  <button
                    @click="cancelEdit"
                    class="edit-btn cancel-btn"
                  >取消</button>
                </div>
              </div>
            </div>

            <div
              v-if="msg.role === 'assistant'"
              class="ai-message-container"
            >
              <img
                src="@/assets/images/头像.png"
                alt="AI头像"
                class="ai-avatar"
              >
              <div class="bubble">
                <!-- 翻译结果显示 -->

                <!-- 修改翻译结果显示部分，在language-selector上方添加 -->
                <div
                  v-if="isTranslated(index)"
                  class="translation-result"
                >
                  <div class="translation-header">
                    <span>翻译结果</span>
                    <button
                      @click="clearTranslation(index)"
                      class="translation-close-btn"
                      title="退出翻译"
                    >
                      × 关闭
                    </button>
                  </div>
                  <div class="translation-content">{{ translatedTexts[index].text }}</div>
                </div>

                <!-- 原始内容 -->
                <div v-if="!isTranslated(index)">
                  <!-- 可折叠的思考内容区域 -->
                  <div
                    v-if="hasThinkingContent(msg.content)"
                    class="thinking-section"
                  >
                    <div
                      class="thinking-header"
                      @click="toggleThinking(index)"
                    >
                      <span>深度思考过程</span>
                      <span class="toggle-icon">{{ expandedThinkingIndices.includes(index) ? '−' : '+' }}</span>
                    </div>
                    <div
                      class="thinking-content"
                      v-show="expandedThinkingIndices.includes(index)"
                      v-html="msg.renderedContent ? extractThinkingContent(msg.renderedContent) : renderMarkdown(extractThinkingContent(msg.content))"
                    ></div>
                  </div>
                  <!-- 正式回答内容 -->
                  <div
                    class="markdown-body"
                    v-html="msg.renderedContent ? extractResponseContent(msg.renderedContent) : renderMarkdown(extractResponseContent(msg.content))"
                  ></div>
                </div>

                <div
                  v-if="msg.isStreaming"
                  class="streaming-indicator"
                >
                  <div class="dot-pulse"></div>
                </div>

                <div class="message-actions">
                  <button
                    @click="regenerateResponse(index)"
                    class="icon-btn"
                  >
                    <img
                      src="@/assets/images/重新生成.png"
                      alt="regenerate"
                      class="icon-img"
                    >
                  </button>
                  <button
                    @click="handleCopy(extractResponseContent(msg.content))"
                    class="icon-btn"
                  >
                    <img
                      src="@/assets/images/复制.png"
                      alt="copy"
                      class="icon-img"
                    >
                  </button>
                  <!-- 添加翻译按钮 -->
                  <button
                    @click="translateMessage(index, msg.content)"
                    class="icon-btn"
                    :disabled="translatingIndex === index"
                  >
                    <img
                      v-if="translatingIndex !== index"
                      src="@/assets/images/翻译.png"
                      alt="translate"
                      class="icon-img"
                    >
                    <span
                      v-else
                      class="translating-spinner"
                    >...</span>
                  </button>
                  <button
                    @click="addToCollections(msg.content)"
                    class="icon-btn"
                  >
                    <img
                      src="@/assets/images/收藏.png"
                      alt="favorite"
                      class="icon-img"
                    >
                  </button>
                  <button
                    @click="toggleSpeak(index, msg.content)"
                    class="icon-btn"
                  >
                    <img
                      v-if="currentSpeakingIndex !== index"
                      src="@/assets/images/朗读.png"
                      alt="speak"
                      class="icon-img"
                    >
                    <span
                      v-else
                      class="speaking-indicator"
                    >🔴</span>
                  </button>
                </div>

                <!-- 语言选择下拉菜单 -->
                <div
                  class="language-selector"
                  v-if="isTranslated(index)"
                >
                  <label>翻译为: </label>
                  <select
                    v-model="defaultTargetLanguage"
                    @change="changeTranslationLanguage($event.target.value)"
                  >
                    <option
                      v-for="lang in translationLanguages"
                      :value="lang.code"
                      :key="lang.code"
                    >
                      {{ lang.name }}
                    </option>
                  </select>
                </div>
              </div>
            </div>

          </div>

        </div>

        <div class="input-container">
          <input
            type="file"
            ref="fileInput"
            style="display: none"
            @change="handleFileChange"
          />
          <button
            @click="triggerFileInput"
            class="send-btn"
            style="margin-right: 10px;"
          >📎 上传附件</button>
          <span
            v-if="selectedFile"
            style="font-size: 12px; margin-right: 10px;"
          >{{ selectedFile.name }}</span>
          <button
            v-if="selectedFile"
            @click="cancelFileUpload"
            class="cancel-file-btn"
            style="margin-right: 10px;"
          >
            ✖ 取消
          </button>

          <input
            v-model="userInput"
            @keyup.enter="sendMessage"
            placeholder="您想了解什么······"
            class="user-input"
          />
          <button
            v-if="!isGenerating"
            @click="sendMessage"
            class="icon-send-btn"
          >
            <img
              src="@/assets/images/send.png"
              alt="发送"
              class="send-icon"
            >
          </button>
          <button
            v-else
            @click="stopGeneration"
            class="icon-send-btn stop-btn"
          >
            <img
              src="@/assets/images/停止.png"
              alt="停止"
              class="send-icon"
            >
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import axios from 'axios'
import { marked } from 'marked'
import ChatWebSocket from '@/components/ChatWebSocket.vue' // 根据实际路径导入
import { ElMessage } from 'element-plus'
import likedIcon from '@/assets/images/收藏.png'
import unlikedIcon from '@/assets/images/点赞.png'
import cntcmIcon from '@/assets/images/中国中医药网.jpg'
import WTOIcon from '@/assets/images/世界卫生组织.jpg'
import Header from '@/views/components/NaviHomeView.vue' // 导入Header组件
import { getToken } from '@/utils/auth' // 导入获取token的工具函数
import httpInstance from '@/utils/http' // 假设你的文件路径是 @/utils/http.js

export default {
  components: {
    Header,
    ChatWebSocket,
  },
  data() {
    return {
      messages: [{ role: 'system', content: '你是一个对话助手' }],
      userInput: '',
      forceNew: true,
      conversations: [],
      currentConversation: null,
      activeDropdown: null,
      selectedFile: null,
      expandedThinkingIndices: [], // 存储已展开的思考内容的索引
      isNewConversation: true, // 新增状态标识是否为新会话
      eventSource: null, // SSE连接
      currentStreamingMessage: null, // 当前正在接收的流式消息
      abortController: null, // 用于中断请求
      isGenerating: false, // 是否正在生成回答
      editingMessageIndex: null, // 当前正在编辑的消息索引
      editingContent: '', // 编辑中的内容
      hasSaved: false,
      likedMessages: [], // 存储被点赞的消息索引
      showAppsView: false,
      apps: [
        {
          id: 1,
          name: '中国中医药网',
          description: '国家中医药管理局官方网站',
          url: 'http://www.cntcm.com.cn/',
          icon: cntcmIcon,
        },
        {
          id: 2,
          name: '中医世家',
          description: '中医经典古籍在线阅读',
          url: 'https://www.zysj.com.cn/',
          icon: 'https://www.zysj.com.cn/favicon.ico',
        },
        {
          id: 3,
          name: '39健康网中医频道',
          description: '中医养生保健知识',
          url: 'https://www.39.net/',
          icon: 'https://www.39.net/favicon.ico',
        },
        {
          id: 4,
          name: '世界卫生组织',
          description: '世界卫生组织中医普及',
          url: 'https://www.who.int/health-topics/traditional-medicine',
          icon: WTOIcon,
        },
        {
          id: 5,
          name: '中医宝典',
          description: '中医经典著作在线阅读',
          url: 'https://www.zhongyibaodian.com/',
          icon: 'https://www.zhongyibaodian.com/favicon.ico',
        },
        {
          id: 6,
          name: 'Acupuncture Today',
          description: '针灸学习与交流平台',
          url: 'https://acupuncturetoday.com/',
          icon: 'https://acupuncturetoday.com/favicon.ico',
        },
      ],
      showCollectionsView: false,
      collections: [],
      searchQuery: '',
      allCollections: [], // 存储所有收藏
      filteredCollections: [], // 存储过滤后的收藏

      translatingIndex: null, // 当前正在翻译的消息索引
      translatedTexts: {}, // 存储已翻译的文本 {index: {text: '', from: '', to: ''}}
      translationLanguages: [
        { code: 'zh', name: '中文' },
        { code: 'en', name: '英文' },
        { code: 'ja', name: '日文' },
        { code: 'ko', name: '韩文' },
        { code: 'fr', name: '法文' },
        { code: 'de', name: '德文' },
      ],
      defaultTargetLanguage: 'en', // 默认翻译成英文
      isSpeaking: false, // 是否正在朗读
      speechSynthesis: window.speechSynthesis || null, // 语音合成API
      currentSpeakingIndex: null, // 当前正在朗读的消息索引
      userAvatar: '',
      userNickname: '',
      userInfo: {
        userId: '',
        username: '',
        avatar: '',
      },
    }
  },
  mounted() {
    this.fetchUserProfile()
    this.loadConversations()
    this.initializeExpandedThinking()

    document.addEventListener('click', this.closeDropdown)
  },
  beforeUnmount() {
    this.closeEventSource()
  },
  methods: {
    goToHome() {
      // 使用路由跳转到首页
      this.$router.push('/profile')
    },
    // 在fetchUserProfile方法中确保正确存储用户信息
    async fetchUserProfile() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.$router.push('/login')
          return
        }

        const response = await axios.get('http://localhost:8080/api/user/profile', {
          headers: { Authorization: `Bearer ${token}` },
        })

        if (response.data?.data) {
          // 确保正确存储用户信息
          const userData = response.data.data
          localStorage.setItem('userId', userData.userId)
          localStorage.setItem('username', userData.username)

          // 更新Vuex store
          this.$store.commit('setUser', {
            userId: userData.userId,
            token: token,
            username: userData.username,
          })

          // 更新组件数据
          this.userInfo = {
            userId: userData.userId,
            username: userData.username,
            avatar: userData.avatar || '',
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        if (error.response?.status === 401) {
          this.clearUserData()
          this.$router.push('/login')
        }
      }
    },

    clearUserData() {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      this.$store.commit('clearUser')
      this.userInfo = {
        userId: '',
        username: '',
        avatar: '',
      }
    },

    cancelFileUpload() {
      this.selectedFile = null
      this.$refs.fileInput.value = '' // 清空文件输入
      this.$message.info('已取消文件上传')
    },
    // 朗读消息
    speakMessage(index, content) {
      // 停止当前朗读
      this.stopSpeaking()

      // 提取实际回答内容（去掉思考过程）
      const answerContent = this.extractResponseContent(content)

      // 创建语音合成实例
      const utterance = new SpeechSynthesisUtterance(answerContent)
      utterance.lang = 'zh-CN' // 设置为中文

      // 朗读开始时的回调
      utterance.onstart = () => {
        this.isSpeaking = true
        this.currentSpeakingIndex = index
      }

      // 朗读结束时的回调
      utterance.onend = () => {
        this.isSpeaking = false
        this.currentSpeakingIndex = null
      }

      // 错误处理
      utterance.onerror = (event) => {
        console.error('朗读出错:', event)
        this.isSpeaking = false
        this.currentSpeakingIndex = null
        this.$message.error('朗读失败: ' + event.error)
      }

      // 开始朗读
      this.speechSynthesis.speak(utterance)
    },

    // 停止朗读
    stopSpeaking() {
      if (this.speechSynthesis && this.speechSynthesis.speaking) {
        this.speechSynthesis.cancel()
        this.isSpeaking = false
        this.currentSpeakingIndex = null
      }
    },

    // 切换朗读状态
    toggleSpeak(index, content) {
      if (this.currentSpeakingIndex === index) {
        this.stopSpeaking()
      } else {
        this.speakMessage(index, content)
      }
    },

    // 在组件销毁时停止朗读
    beforeDestroy() {
      this.stopSpeaking()
    },

    async translateMessage(index, content) {
      try {
        if (this.translatingIndex === index) return

        this.translatingIndex = index

        // 提取原始Markdown内容（保留格式）
        const markdownContent = this.extractResponseContent(content)

        // 发送到翻译API
        const response = await axios.get('http://localhost:8080/api/chat/translate', {
          params: {
            text: markdownContent,
            from: 'auto',
            to: this.getBaiduLanguageCode(this.defaultTargetLanguage),
            preserve_markdown: true, // 告诉后端保留Markdown格式
          },
          timeout: 10000,
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })

        if (response.data?.trans_result?.length > 0) {
          // 合并翻译结果，保留Markdown格式
          const translatedMarkdown = response.data.trans_result.map((item) => item.dst).join('\n\n')

          this.translatedTexts = {
            ...this.translatedTexts,
            [index]: {
              text: translatedMarkdown,
              renderedText: this.renderMarkdown(translatedMarkdown), // 预渲染Markdown
              from: this.getLanguageName(response.data.from),
              to: this.getLanguageName(response.data.to),
            },
          }
        }
      } catch (error) {
        console.error('翻译失败:', error)
        this.$message.error('翻译失败: ' + (error.response?.data?.error || error.message))
        delete this.translatedTexts[index]
      } finally {
        this.translatingIndex = null
      }
    },

    // 将前端语言代码映射为百度API支持的语言代码
    getBaiduLanguageCode(langCode) {
      const map = {
        zh: 'zh',
        en: 'en',
        ja: 'jp',
        ko: 'kor',
        fr: 'fra',
        de: 'de',
      }
      return map[langCode] || 'en'
    },

    // 将百度API返回的语言代码转换为可读名称
    getLanguageName(langCode) {
      const map = {
        zh: '中文',
        en: '英文',
        jp: '日文',
        ja: '日文',
        kor: '韩文',
        ko: '韩文',
        fra: '法文',
        fr: '法文',
        de: '德文',
      }
      return map[langCode] || langCode
    },

    // 切换翻译语言
    changeTranslationLanguage(langCode) {
      this.defaultTargetLanguage = langCode
      // 重新翻译所有已翻译的消息
      Object.keys(this.translatedTexts).forEach((index) => {
        const msg = this.messages[index]
        if (msg) {
          this.translateMessage(parseInt(index), msg.content)
        }
      })
    },

    // 判断消息是否已翻译
    isTranslated(index) {
      return !!this.translatedTexts[index]
    },

    // 清除翻译
    clearTranslation(index) {
      const { [index]: _, ...rest } = this.translatedTexts
      this.translatedTexts = rest
    },

    // 显示收藏视图
    showCollections() {
      this.resetViewState()
      this.showCollectionsView = true
      this.loadCollections()
    },
    formatDate(isoString) {
      if (!isoString) return '无日期信息'

      try {
        const date = new Date(isoString)
        if (isNaN(date.getTime())) return '日期格式错误'

        // 格式化为：YYYY-MM-DD HH:MM
        const pad = (num) => num.toString().padStart(2, '0')
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(
          date.getHours()
        )}:${pad(date.getMinutes())}`
      } catch (e) {
        console.error('日期格式化错误:', e)
        return isoString // 如果无法格式化，直接返回原始字符串
      }
    },

    async loadCollections() {
      try {
        const userId = localStorage.getItem('userId')
        console.log('用户ID:', userId)

        if (!userId) {
          console.error('未获取到用户ID')
          return
        }
        const res = await axios.get('http://localhost:8080/api/collections', {
          params: { userId: userId },
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })

        console.log('API返回数据:', res.data)

        if (res.data) {
          this.allCollections = res.data.data
          this.filteredCollections = [...this.allCollections] // 初始显示所有收藏
        } else {
          this.allCollections = []
          this.filteredCollections = []
          console.error('加载收藏失败:', res.data?.message || '未知错误')
        }
      } catch (error) {
        console.error('加载收藏失败', error)
        this.$message.error('加载收藏失败')
        this.allCollections = []
        this.filteredCollections = []
      }
    },

    searchCollections() {
      if (!this.searchQuery.trim()) {
        this.filteredCollections = [...this.allCollections]
        return
      }

      const query = this.searchQuery.toLowerCase().trim()

      // 同时搜索ID和内容
      this.filteredCollections = this.allCollections.filter((collection) => {
        return (
          collection.collectionId.toString().includes(query) ||
          collection.content.toLowerCase().includes(query)
        )
      })

      if (this.filteredCollections.length === 0) {
        this.$message.info('没有找到匹配的收藏')
      }
    },

    resetSearch() {
      this.searchQuery = ''
      this.filteredCollections = [...this.allCollections]
    },

    // 修改deleteCollection方法使用filteredCollections
    async deleteCollection(collectionId) {
      const userId = localStorage.getItem('userId')

      if (!userId) {
        console.error('未获取到用户ID')
        return
      }
      try {
        if (!collectionId) {
          this.$message.error('无效的收藏ID')
          return
        }

        const confirmDelete = confirm('确定要删除这条收藏吗？')
        if (!confirmDelete) return

        const response = await axios.delete(
          `http://localhost:8080/api/collections/${collectionId}`,
          {
            params: { userId: userId },
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
          }
        )

        if (response.data.success) {
          this.loadCollections()
          alert('删除成功')
          this.$message.success('删除收藏成功')
          // 从所有收藏中删除
          this.allCollections = this.allCollections.filter((c) => c.collectionId !== collectionId)
          // 从过滤结果中删除
          this.filteredCollections = this.filteredCollections.filter(
            (c) => c.collectionId !== collectionId
          )
        } else {
          this.$message.error(response.data.message || '删除收藏失败')
        }
      } catch (error) {
        console.error('删除收藏失败', error)
        this.$message.error(error.response?.data?.message || '删除收藏失败')
        this.loadCollections()
      }
    },

    async addToCollections(content) {
      try {
        const userId = localStorage.getItem('userId')
        console.log('用户ID:', userId)

        if (!userId) {
          console.error('未获取到用户ID')
          return
        }

        const confirmAdd = confirm('确定要收藏此条消息吗？')
        if (!confirmAdd) return

        // 提取实际回答内容（去掉思考过程）
        const answerContent = this.extractResponseContent(content)

        const response = await axios.post(
          'http://localhost:8080/api/collections',
          { content: answerContent }, // 请求体
          {
            params: {
              userId: userId, // 查询参数
            },
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
          }
        )

        alert('收藏成功！')
        this.$message.success('收藏成功')
        this.loadCollections()
      } catch (error) {
        console.error('收藏失败:', error)
        this.$message.error('收藏失败: ' + (error.response?.data?.message || error.message))
      }
    },

    // 格式化日期
    formatDate(timestamp) {
      return new Date(timestamp).toLocaleString()
    },

    // 重置视图状态
    resetViewState() {
      this.showAppsView = false
      this.showCollectionsView = false
      this.isNewConversation = false
      this.messages = []
    },
    showAllApps() {
      // 重置所有视图状态
      this.resetViewState()
      // 显示应用视图
      this.showAppsView = true
      // 确保隐藏其他视图
      this.isNewConversation = false
      // 滚动到顶部
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    openApp(url) {
      window.open(url, '_blank')
    },

    toggleLike(index) {
      const currentIndex = this.likedMessages.indexOf(index)
      if (currentIndex === -1) {
        this.likedMessages.push(index)
        this.$message.success('感谢您的点赞！')
      } else {
        this.likedMessages.splice(currentIndex, 1)
      }
    },

    getLikeIcon(index) {
      return this.likedMessages.includes(index) ? likedIcon : unlikedIcon
    },

    refreshMarkdown(index) {
      if (index >= 0 && index < this.messages.length && this.messages[index].role === 'assistant') {
        // 强制重新渲染Markdown
        const msg = this.messages[index]
        msg.renderedContent = this.renderMarkdown(msg.content)
        this.$forceUpdate() // 确保视图更新
        console.log('Markdown已重新渲染')

        ElMessage.success('Markdown已重新渲染') // 修改这里
      }
    },
    startEditing(index, content) {
      this.editingMessageIndex = index
      this.editingContent = content
      this.$nextTick(() => {
        this.$refs.editTextarea[index].focus()
      })
    },

    cancelEdit() {
      this.editingMessageIndex = null
      this.editingContent = ''
    },

    async saveEdit(index) {
      if (!this.editingContent.trim()) {
        this.$message.warning('消息不能为空')
        return
      }

      // 更新消息内容
      this.messages[index].content = this.editingContent
      this.editingMessageIndex = null
      this.editingContent = ''

      // 如果后面有AI回复，询问是否重新生成
      if (index < this.messages.length - 1 && this.messages[index + 1].role === 'assistant') {
        if (confirm('是否要重新生成AI的回复？')) {
          await this.regenerateResponse(index + 1)
        }
      }
    },

    useSuggestion(text) {
      this.userInput = text
      this.$nextTick(() => {
        document.querySelector('.user-input').focus()
      })
    },
    initializeExpandedThinking() {
      this.expandedThinkingIndices = this.messages
        .map((msg, index) => (this.hasThinkingContent(msg.content) ? index : -1))
        .filter((index) => index !== -1)
    },

    // 修改后的toggleThinking方法
    toggleThinking(index) {
      const currentIndex = this.expandedThinkingIndices.indexOf(index)
      if (currentIndex === -1) {
        this.expandedThinkingIndices.push(index)
      } else {
        this.expandedThinkingIndices.splice(currentIndex, 1)
      }
      // 确保数组保持排序，这样新消息不会打乱顺序
      this.expandedThinkingIndices.sort((a, b) => a - b)
    },
    renderMarkdown(content) {
      // 添加空值检查
      if (!content) return ''

      // 更健壮的代码块处理
      const processedContent = content
        .toString() // 确保是字符串
        .replace(/```([\s\S]*?)```/g, '\n```$1```\n')
        .replace(/`([^`]+)`/g, '`$1`')
        .replace(/(^|\n)(#+)/g, '\n$2')

      // 设置marked选项
      marked.setOptions({
        breaks: true,
        gfm: true,
        highlight: (code, lang) => {
          const hljs = require('highlight.js')
          const language = hljs.getLanguage(lang) ? lang : 'plaintext'
          return hljs.highlight(code, { language }).value
        },
      })

      // 更安全的HTML渲染
      const renderer = new marked.Renderer()
      renderer.link = (href, title, text) => {
        return `<a href="${href}" title="${
          title || ''
        }" target="_blank" rel="noopener noreferrer">${text}</a>`
      }

      return marked(processedContent || '', { renderer })
    },

    extractThinkingContent(content) {
      const thinkRegex = /<think>([\s\S]*?)<\/think>/
      const match = content.match(thinkRegex)
      return match ? match[1].trim() : ''
    },

    extractResponseContent(content) {
      // 先移除思考部分
      let result = content.replace(/<think>[\s\S]*?<\/think>\n*/g, '')

      // 处理Markdown格式，保留段落
      result = result
        .replace(/\n{3,}/g, '\n\n') // 多个换行合并为双换行
        .replace(/^\s+|\s+$/g, '') // 去除首尾空白

      return result
    },

    hasThinkingContent(content) {
      return /<think>[\s\S]*?<\/think>/.test(content)
    },

    handleCopy(content) {
      navigator.clipboard
        .writeText(content)
        .then(() => this.$message.success('复制成功'))
        .catch(() => this.$message.error('复制失败'))
    },

    async loadConversations() {
      try {
        const userId = localStorage.getItem('userId')
        console.log('当前用户ID:', userId)

        if (!userId) {
          console.error('未获取到用户ID')
          this.$message.error('请先登录')
          this.$router.push('/login')
          return
        }

        const res = await axios.get('http://localhost:8080/api/chat/conversations', {
          params: { userId: userId },
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })
        this.conversations = res.data.filter((conv) => conv.id)

        // 如果有当前会话但不在列表中，添加到列表
        if (
          this.currentConversation &&
          !this.conversations.some((c) => c.id === this.currentConversation.id)
        ) {
          this.conversations.unshift(this.currentConversation)
        }
      } catch (error) {
        console.error('加载会话失败', error)

        if (error.response?.status === 401) {
          this.$message.error('登录已过期，请重新登录')
          this.$router.push('/login')
        } else {
          this.$message.error('加载会话失败: ' + (error.response?.data?.message || error.message))
        }
        this.conversations = []
      }
    },

    async loadConversation(convId) {
      try {
        const userId = localStorage.getItem('userId')
        console.log('当前用户ID:', userId)

        if (!userId) {
          console.error('未获取到用户ID')
          this.$message.error('请先登录')
          this.$router.push('/login')
          return
        }
        this.resetViewState()
        const conv = this.conversations.find((c) => c.id === convId)
        if (!conv) return

        console.log('加载会话消息:', convId)

        this.currentConversation = conv
        this.isNewConversation = false // 加载已有会话时设置为false
        const res = await axios.get(`http://localhost:8080/api/chat/conversations/${convId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })
        this.messages = res.data.map((msg) => ({
          role: msg.role,
          content: msg.content,
        }))
        this.forceNew = false
        this.initializeExpandedThinking()

        this.scrollToBottom()
      } catch (error) {
        console.error('加载会话消息失败', error)
      }
    },

    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    handleFileChange(event) {
      const file = event.target.files[0]
      // 文件大小限制为10MB
      const maxSize = 10 * 1024 * 1024 // 10MB
      if (file.size > maxSize) {
        this.$message.error('文件大小不能超过10MB')
        this.$refs.fileInput.value = '' // 清空文件输入
        return
      }

      if (file) {
        this.selectedFile = file
      }
    },

    async startNewConversation() {
      try {
        this.resetViewState()
        // 清空当前消息
        this.messages = []
        this.isNewConversation = true

        // 重置当前会话
        this.currentConversation = null
        this.forceNew = true

        // 加载最新对话列表
        await this.loadConversations()

        this.scrollToBottom()
        this.initializeExpandedThinking()
      } catch (error) {
        console.error('创建新会话失败', error)
        this.$message.error('创建新会话失败')
      }
    },

    async sendMessage() {
      if (!this.userInput.trim() && !this.selectedFile) return

      this.isGenerating = true
      this.isNewConversation = false
      const userMsg = {
        role: 'user',
        content: this.userInput,
      }
      this.messages.push(userMsg)
      this.userInput = ''

      try {
        this.abortController = new AbortController()

        const messagesToSend = this.messages.map((msg) => ({
          role: msg.role,
          content: msg.content,
        }))

        // 创建FormData对象来处理文件上传
        const formData = new FormData()
        formData.append('userId', localStorage.getItem('userId'))
        formData.append('messages', JSON.stringify(messagesToSend))
        formData.append('newConversation', this.forceNew || !this.currentConversation)
        if (this.currentConversation?.id) {
          formData.append('conversationId', this.currentConversation.id)
        }
        if (this.selectedFile) {
          formData.append('file', this.selectedFile)
        }

        // 在开始流式响应前先加载一次对话列表
        await this.loadConversations()

        // 使用fetch API发送FormData
        const response = await fetch('http://localhost:8080/api/chat/stream', {
          method: 'POST',
          body: formData,
          signal: this.abortController.signal,
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let rawContent = ''

        // 创建新的AI消息对象
        const newMessage = {
          role: 'assistant',
          content: '',
          isStreaming: true,
          renderedContent: '',
        }
        this.messages.push(newMessage)
        this.currentStreamingMessage = newMessage

        // 在 sendMessage 方法中修改处理逻辑
        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          const chunk = decoder.decode(value, { stream: true })
          buffer += chunk

          // 处理可能的多条消息
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data:') && !line.includes('[DONE]')) {
              const content = line.substring(5).trim()
              if (content) {
                // 添加空格处理逻辑
                let processedContent = content
                if (/[a-zA-Z]$/.test(rawContent) && /^[a-zA-Z]/.test(content)) {
                  processedContent = ' ' + content
                }

                rawContent += processedContent
                // 更新当前流式消息
                if (this.currentStreamingMessage) {
                  this.currentStreamingMessage.content = rawContent
                  this.currentStreamingMessage.renderedContent = this.renderMarkdown(rawContent)
                }
              }
            }
          }

          this.scrollToBottom()
        }

        // 流式响应完成
        this.finalizeStreamingResponse(rawContent)

        if (this.forceNew) {
          this.forceNew = false
        }
      } catch (error) {
        console.error('发送请求失败', error)
        this.handleStreamError(error)
      } finally {
        this.selectedFile = null
        this.$refs.fileInput.value = ''
      }
    },

    async startStreamingResponse(requestData, signal) {
      this.closeEventSource()
      this.hasSaved = false

      // 如果不是重新生成，则创建新消息
      if (!requestData.regenerate) {
        const newMessage = {
          role: 'assistant',
          content: '',
          isStreaming: true,
          renderedContent: '',
        }
        this.messages.push(newMessage)
        this.currentStreamingMessage = newMessage
      }

      try {
        const response = await fetch('http://localhost:8080/api/chat/stream', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(requestData),
          signal,
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let rawContent = ''

        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          const chunk = decoder.decode(value, { stream: true })
          buffer += chunk

          // 处理可能的多条消息
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data:') && !line.includes('[DONE]')) {
              const content = line.substring(5).trim()
              if (content) {
                rawContent += content
                // 确保只更新当前流式消息
                if (this.currentStreamingMessage) {
                  this.currentStreamingMessage.content = rawContent
                  this.currentStreamingMessage.renderedContent = this.renderMarkdown(rawContent)
                }
              }
            }
          }

          this.scrollToBottom()
        }

        // 只有在流式响应正常完成时才保存
        this.finalizeStreamingResponse(rawContent)
      } catch (error) {
        // 中断或其他错误时不保存
        this.handleStreamError(error, rawContent)
      }
    },

    // 修改 stopGeneration 方法
    stopGeneration() {
      if (this.abortController) {
        // 1. 先中断请求
        this.abortController.abort()
        this.isGenerating = false

        // 2. 重置流式消息状态但不保存
        if (this.currentStreamingMessage) {
          this.currentStreamingMessage.isStreaming = false
          this.currentStreamingMessage = null
        }

        // 3. 重置控制器
        this.abortController = null

        // 4. 强制更新视图
        this.$forceUpdate()
      }
    },

    async regenerateResponse(index) {
      try {
        if (!this.currentConversation?.id) return
        if (this.messages[index].role !== 'assistant') {
          console.error('只能重新生成AI消息')
          return
        }

        // // 确保我们只处理最新的AI消息
        // if (index < this.messages.length - 1 && this.messages[index + 1].role === 'assistant') {
        //   console.error('不能重新生成中间的AI消息');
        //   return;
        // }

        // 保存用户原始消息
        const userMessage = this.messages[index - 1]

        // 移除之前的AI回复
        this.messages.splice(index, 1)

        // 创建新的AI消息对象
        const newMessage = {
          role: 'assistant',
          content: '',
          isStreaming: true,
          renderedContent: '',
        }
        this.messages.push(newMessage)
        this.currentStreamingMessage = newMessage
        this.isGenerating = true

        // 准备消息历史（排除之前的AI回复）
        const messagesToSend = this.messages
          .slice(0, index)
          .map((msg) => ({ role: msg.role, content: msg.content }))

        // 创建新的中断控制器
        this.abortController = new AbortController()

        // 创建FormData对象，与发送消息时保持一致
        const formData = new FormData()
        formData.append('userId', localStorage.getItem('userId'))
        formData.append('messages', JSON.stringify(messagesToSend))
        formData.append('newConversation', false)
        formData.append('conversationId', this.currentConversation.id)

        // 使用fetch API发送请求，确保Content-Type由浏览器自动设置
        const response = await fetch('http://localhost:8080/api/chat/stream', {
          method: 'POST',
          body: formData, // 使用FormData而不是JSON
          signal: this.abortController.signal,
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },

          // 不要手动设置Content-Type，浏览器会自动处理
        })

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''
        let rawContent = ''

        // 在 regenerateResponse 方法中找到类似的流式处理部分，添加相同的空格处理逻辑
        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          const chunk = decoder.decode(value, { stream: true })
          buffer += chunk

          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data:') && !line.includes('[DONE]')) {
              const content = line.substring(5).trim()
              if (content) {
                let processedContent = content
                if (/[a-zA-Z]$/.test(rawContent) && /^[a-zA-Z]/.test(content)) {
                  processedContent = ' ' + content
                }

                rawContent += processedContent
                if (this.currentStreamingMessage) {
                  this.currentStreamingMessage.content = rawContent
                  this.currentStreamingMessage.renderedContent = this.renderMarkdown(rawContent)
                }
              }
            }
          }
          this.scrollToBottom()
        }
        // 流式响应完成
        this.finalizeStreamingResponse(rawContent)
      } catch (error) {
        console.error('重新生成失败', error)
        this.handleStreamError(error)
      } finally {
        this.isGenerating = false
        this.abortController = null
      }
    },

    // 修改后的 handleStreamError 方法
    handleStreamError(error) {
      if (error.name === 'AbortError') {
        console.log('请求已被用户中断')
        // 中断错误时不保存内容
        if (this.currentStreamingMessage) {
          this.currentStreamingMessage.isStreaming = false
          this.$forceUpdate()
        }
      } else {
        console.error('流式响应错误:', error)
        // 非中断错误才保存内容
        if (this.currentStreamingMessage) {
          this.currentStreamingMessage.content += '\n\n[错误: ' + error.message + ']'
          this.currentStreamingMessage.renderedContent = this.renderMarkdown(
            this.currentStreamingMessage.content
          )
          this.currentStreamingMessage.isStreaming = false
          this.finalizeStreamingResponse(this.currentStreamingMessage.content)
        }
      }

      this.isGenerating = false
      this.currentStreamingMessage = null
      this.abortController = null
    },

    // 修改 finalizeStreamingResponse 方法
    finalizeStreamingResponse(rawContent) {
      if (!this.currentStreamingMessage) return
      // 在所有英文单词之间添加空格
      const processedContent = rawContent.replace(/([a-zA-Z])([a-zA-Z])/g, '$1 $2')

      this.currentStreamingMessage.content = processedContent
      this.currentStreamingMessage.renderedContent = this.renderMarkdown(processedContent)
      this.currentStreamingMessage.isStreaming = false

      // 确保内容被正确渲染为Markdown
      this.currentStreamingMessage.content = rawContent
      this.currentStreamingMessage.renderedContent = this.renderMarkdown(rawContent)
      this.currentStreamingMessage.isStreaming = false

      // 立即重置状态，避免重复保存
      const tempMessage = this.currentStreamingMessage
      this.currentStreamingMessage = null
      this.isGenerating = false

      // 加载最新的对话列表
      this.loadConversations().then(() => {
        if (!this.currentConversation && this.conversations.length > 0) {
          this.currentConversation = this.conversations[0]
        }
      })
    },

    handleStreamMessage(message) {
      if (message.type === 'start') {
        this.currentStreamingMessage = '' // 开始流式响应时，清空当前消息内容
      } else if (message.type === 'chunk') {
        this.currentStreamingMessage += message.content // 将新的数据块添加到当前消息内容中
      } else if (message.type === 'end') {
        // 流式响应结束，将当前消息内容添加到消息列表中
        this.messages.push({
          role: 'assistant',
          content: this.currentStreamingMessage,
          isStreaming: false,
        })
        this.currentStreamingMessage = '' // 清空当前消息内容
      }
    },

    closeEventSource() {
      if (this.eventSource) {
        this.eventSource.close()
        this.eventSource = null
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messages
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    toggleDropdown(index) {
      this.activeDropdown = this.activeDropdown === index ? null : index
    },

    closeDropdown() {
      this.activeDropdown = null
    },

    async renameConversation(convId) {
      const newTitle = prompt('请输入新的对话标题（1-50个字符）：')

      if (newTitle === null) return // 用户点击取消
      if (!newTitle.trim()) {
        alert('标题不能为空！')
        return
      }

      if (newTitle.length > 50) {
        alert('标题长度不能超过50个字符！')
        return
      }

      try {
        const response = await axios.put(
          `http://localhost:8080/api/chat/conversations/${convId}/rename`,
          {
            title: newTitle,
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
          }
        )

        if (response.status === 200) {
          const index = this.conversations.findIndex((c) => c.id === convId)
          if (index !== -1) {
            this.conversations[index].title = newTitle
          }

          if (this.currentConversation?.id === convId) {
            this.currentConversation.title = newTitle
          }

          this.$message.success('重命名成功！')
        }
      } catch (err) {
        console.error('重命名失败', err)

        const errorMessage = err.response?.data?.message || '请求失败，请检查网络连接'
        this.$message.error(`重命名失败: ${errorMessage}`)
      } finally {
        this.activeDropdown = null
      }
    },

    deleteConversation(convId) {
      // 添加确认对话框
      const confirmDelete = confirm('确定要删除这个会话吗？')
      if (!confirmDelete) return // 用户点击取消

      axios
        .delete(`http://localhost:8080/api/chat/conversations/${convId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        })
        .then((response) => {
          // 你可以根据 API 的返回结构来决定如何处理响应
          if (response && response.status === 200) {
            // 立刻从当前对话列表中移除已删除的会话
            this.conversations = this.conversations.filter((conv) => conv.id !== convId)
            // 若当前会话是被删除的会话，则清空消息和当前会话
            if (this.currentConversation && this.currentConversation.id === convId) {
              this.currentConversation = null
              this.messages = []
              this.isNewConversation = true // 删除会话后显示引导语
            }
          } else {
            // 如果没有成功，则处理 API 返回的错误信息
            console.error('删除失败，未返回成功响应', response)
            const errorMsg = response.data?.message || '无法删除此会话'
            this.$message.error(`删除失败: ${errorMsg}`)
          }
        })
        .catch((error) => {
          console.error('请求错误', error)
          // 尝试访问错误信息
          const errorMsg = error.response?.data?.message || error.message || '请求失败'
          this.$message.error(`删除失败: ${errorMsg}`)
        })
        .finally(() => {
          this.activeDropdown = null
        })
    },
  },
}
</script>


<style scoped>
.ai-message-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
}
.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-top: 5px;
}
.chat-container {
  display: flex;
  height: 97vh;
  width: 99vw;
  margin: 0;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f9f9f9;
  font-family: 'Arial', sans-serif;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.sidebar {
  width: 360px;
  min-width: 360px;
  max-width: 320px;
  background-color: rgba(255, 255, 255, 1);
  padding: 20px;
  border-right: 1px solid #ddd;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.sidebar-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 历史会话区域 - 可滚动 */
.history-section {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee; /* 添加分隔线 */
  margin-bottom: 20px;
}

/* 底部标志区域 - 固定高度 */
.logo-section {
  padding-top: 20px;
  margin-top: auto; /* 确保始终在底部 */
  border-top: 1px solid #eee; /* 添加上分隔线 */
}
.logo-2 {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 20px 0;
}

.logo-3 {
  width: 40px;
  height: 40px;
  margin-bottom: 10px;
}

.text-wrapper-9 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
.new-conversation-btn {
  width: 278px; /* Changed to specified width */
  height: 7%; /* Changed to specified height */
  padding: 10px;
  background-color: rgba(116, 158, 167, 1); /* Changed to specified color */
  color: white; /* Changed text color to white for better contrast */
  border: none;
  border-radius: 69px; /* Changed to specified border radius */
  cursor: pointer;
  margin-top: 10px;
  font-weight: bold; /* Added for better readability */
}

.sidebar h3 {
  margin-top: 0;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.conversation-item {
  margin-bottom: 10px;
  position: relative;
}

.action-buttons {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.icon-img {
  width: 16px;
  height: 16px;
  vertical-align: middle;
}

.loading-dots {
  display: inline-flex;
  align-items: center;
  height: 20px;
}

.loading-dots span {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #666;
  margin: 0 3px;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }

  40% {
    transform: scale(1);
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.icon-btn {
  padding: 4px;
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.icon-btn:hover {
  color: #333;
  transform: scale(1.1);
}

.conversation-wrapper {
  display: flex;
  border: none; /* Removed border */
  border-radius: 5px;
  background-color: white;
  margin-bottom: 8px;
  transition: all 0.2s;
  width: 263px;
  height: 51px;
  gap: 10px;
  padding: 15px 0;
}

.conversation-wrapper.active {
  width: 280px;
  height: 5%;
  margin-left: 16px; /* Instead of left:16px for proper layout */
  border-right: 1px solid #4a90e2;
  border-radius: 57px;
  background-color: rgba(232, 239, 238, 1);
  border: none; /* Remove previous border */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Optional: adds subtle depth */
}
.conversation-wrapper:hover {
  border-color: #4a90e2;
}

.conversation-btn {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  text-align: left;
  word-break: break-word;
  overflow-wrap: break-word;
}

.ellipsis-btn {
  padding: 0 12px;
  border: none;
  background: none; /* Removed background color */
  cursor: pointer;
  font-size: 18px;
  color: #666; /* Changed to match text color */
  border-left: 1px solid #eee; /* Lighter border */
  border-radius: 57px;
}

/* Ellipsis button - active state */
.conversation-wrapper.active .dropdown-container {
  position: relative; /* 改为相对定位 */
  width: auto; /* 改为自动宽度 */
  height: auto; /* 改为自动高度 */
  left: auto; /* 移除固定位置 */
  border-right: none; /* 移除边框 */
  border-radius: 0; /* 移除圆角 */
  overflow: visible; /* 确保内容可见 */
}

.conversation-wrapper.active .ellipsis-btn {
  width: 100%;
  height: 100%;
  background: rgba(53, 86, 90, 1);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  font-size: 18px;
  overflow: visible;
}
.ellipsis-btn:hover {
  background-color: #f0f0f0;
}

.dropdown-container {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 100%;
  background: white;
  border: 1px solid #ccc;
  z-index: 100;
  min-width: 100px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  margin-top: 5px;
}

.dropdown-menu button {
  width: 100%;
  padding: 8px 12px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
}

.dropdown-menu button:hover {
  background-color: #f5f5f5;
}

.sidebar button.active {
  background-color: #d0e6ff;
  border-color: #4a90e2;
  font-weight: bold;
}

.new-conversation-btn:hover {
  background-color: #0688f9;
}

.regenerate-btn {
  margin-top: 8px;
  padding: 4px 8px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.regenerate-btn:hover {
  background-color: #e0e0e0;
}

.message.assistant .bubble {
  position: relative;
}

.message {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start; /* Align items to top */
}

/* Keep user/assistant colors */
.message.user .bubble {
  background-color: white;
  text-align: left;
}

.message.assistant .bubble {
  background-color: white;
  text-align: left;
}

.message.user {
  justify-content: flex-end;
}

.user-message-container {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.message.assistant {
  justify-content: flex-start;
}

.bubble {
  padding: 12px 20px; /* Combined padding: 12px vertical, 20px horizontal */
  border-radius: 30px;
  max-width: 70%;
  min-width: 128px; /* Minimum width */
  word-wrap: break-word;
  font-size: 14px;
  line-height: 1.6;
  display: inline-block; /* Change from flex to inline-block for better text flow */
  position: relative;
  margin: 5px 0; /* Reduced vertical spacing */
  box-sizing: border-box;
}

.bubble pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
}

.bubble code {
  font-family: 'Courier New', monospace;
  background-color: #f5f5f5;
  padding: 2px 4px;
  border-radius: 3px;
}

.bubble blockquote {
  border-left: 3px solid #ddd;
  padding-left: 10px;
  margin-left: 0;
  color: #666;
}

.bubble table {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
}

.bubble table,
.bubble th,
.bubble td {
  border: 1px solid #ddd;
}

.bubble th,
.bubble td {
  padding: 8px;
  text-align: left;
}

.bubble ul,
.bubble ol {
  padding-left: 20px;
  margin: 10px 0;
}

.bubble h1,
.bubble h2,
.bubble h3 {
  margin: 15px 0 10px;
}

.input-container .send-btn[style*='margin-right: 10px'] {
  width: 48px;
  height: 48px;
  border-radius: 60px;
  padding: 10px;
  background: rgba(53, 86, 90, 1);
  box-shadow: 0px 4px 8px 0px rgba(86, 97, 246, 0.25);
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-container .send-btn[style*='margin-right: 10px']:hover {
  background: rgba(42, 76, 80, 1);
  transform: translateY(-1px);
}

/* Adjust input container spacing */

.user-input:focus {
  border-color: #4a90e2;
}

/* 更新发送按钮样式（保持位置不变） */

.send-btn {
  min-width: 70px;
  height: 48px;
  border-radius: 60px;
  padding: 10px 15px;
  background: rgba(53, 86, 90, 1);
  box-shadow: 0px 4px 8px 0px rgba(86, 97, 246, 0.25);
  color: white;
  border: none; /* 确保无边框 */
  cursor: pointer;
}

.icon-send-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(53, 86, 90, 1);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  margin-left: 10px;
  transition: all 0.2s ease;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
}
.icon-send-btn:hover {
  background: rgba(42, 76, 80, 1);
  transform: scale(1.05);
}

.icon-send-btn:active {
  transform: scale(0.95);
}

.send-icon {
  width: 60px;
  height: 60px;
}

/* 更新上传文件按钮样式（保持位置不变） */
.send-btn[style*='margin-right: 10px'] {
  min-width: 80px; /* 改为min-width保持内容自适应 */
  height: 48px;
  border-radius: 60px;
  padding: 10px 15px; /* 保持原有水平padding */
  background: rgba(53, 86, 90, 1);
  box-shadow: 0px 4px 8px 0px rgba(86, 97, 246, 0.25);
  color: white;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  margin-right: 10px; /* 保持原有外边距 */
}

/* 鼠标悬停效果 */
.send-btn:hover,
.send-btn[style*='margin-right: 10px']:hover {
  background: rgba(42, 76, 80, 1);
  transform: translateY(-1px);
}

.user-input {
  flex: 1;
  border: none;
  outline: none;
  padding: 12px 0;
  font-size: 14px;
  background: transparent;
}

.thinking-section {
  background-color: #f8f9fa;
  border-left: 3px solid #6c757d;
  padding: 0;
  margin-bottom: 15px;
  border-radius: 0 5px 5px 0;
  overflow: hidden;
}

.thinking-header {
  padding: 10px 15px;
  font-weight: bold;
  color: #495057;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f1f3f5;
  transition: background-color 0.2s;
}

.thinking-header:hover {
  background-color: #e9ecef;
}

.toggle-icon {
  font-weight: bold;
  font-size: 1.2em;
  width: 20px;
  text-align: center;
}

.thinking-content {
  padding: 10px 15px;
  color: #6c757d;
  font-size: 0.85em;
  line-height: 1.5;
  border-top: 1px solid #e9ecef;
}

/* 新增的欢迎引导语样式 */
.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 20px;
  text-align: center;
}

.welcome-content {
  max-width: 600px;
  padding: 30px;
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-content h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.welcome-content p {
  color: #7f8c8d;
  margin-bottom: 20px;
  line-height: 1.6;
}

.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
  text-align: left;
}

.suggestion-list li {
  padding: 12px 15px;
  margin-bottom: 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid #3498db;
}

.suggestion-list li:hover {
  background-color: #e8f4fc;
  transform: translateX(5px);
}

.tip {
  font-size: 12px;
  color: #95a5a6;
  font-style: italic;
}

.streaming-indicator {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}

.dot-pulse {
  position: relative;
  left: -9999px;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9880ff;
  color: #9880ff;
  box-shadow: 9999px 0 0 -5px #9880ff;
  animation: dotPulse 1.5s infinite linear;
  animation-delay: 0.25s;
}

.dot-pulse::before,
.dot-pulse::after {
  content: '';
  display: inline;
  position: relative;
  top: 0;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9880ff;
  color: #9880ff;
}

.dot-pulse::before {
  box-shadow: 9984px 0 0 -5px #9880ff;
  animation: dotPulseBefore 1.5s infinite linear;
  animation-delay: 0s;
}

.dot-pulse::after {
  box-shadow: 10014px 0 0 -5px #9880ff;
  animation: dotPulseAfter 1.5s infinite linear;
  animation-delay: 0.5s;
}

@keyframes dotPulseBefore {
  0% {
    box-shadow: 9984px 0 0 -5px #9880ff;
  }
  30% {
    box-shadow: 9984px 0 0 2px #9880ff;
  }
  60%,
  100% {
    box-shadow: 9984px 0 0 -5px #9880ff;
  }
}

@keyframes dotPulse {
  0% {
    box-shadow: 9999px 0 0 -5px #9880ff;
  }
  30% {
    box-shadow: 9999px 0 0 2px #9880ff;
  }
  60%,
  100% {
    box-shadow: 9999px 0 0 -5px #9880ff;
  }
}

@keyframes dotPulseAfter {
  0% {
    box-shadow: 10014px 0 0 -5px #9880ff;
  }
  30% {
    box-shadow: 10014px 0 0 2px #9880ff;
  }
  60%,
  100% {
    box-shadow: 10014px 0 0 -5px #9880ff;
  }
}

.stop-btn {
  background-color: #ff4d4f !important;
}

.stop-btn:hover {
  background-color: #ff7875 !important;
}
.user-message-container .message-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 5px;
}

.user-message-container .icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  opacity: 0.5;
  transition: opacity 0.2s;
}

.user-message-container .icon-btn:hover {
  opacity: 1;
}

.user-message-container .icon-img {
  width: 16px;
  height: 16px;
}

/* 在现有的样式后面添加 */

.user-message-container .editing-bubble {
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.edit-textarea {
  width: 80px;
  min-height: 50px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 8px;
  font-family: inherit;
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.edit-btn {
  padding: 4px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.save-btn {
  background-color: #1890ff;
  color: white;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

/* 调整消息操作按钮的间距 */
.message-actions {
  display: flex;
  gap: 8px;
  margin-left: 8px;
}
.like-btn {
  position: relative;
}

.like-btn.liked {
  color: #ff4d4f;
}

.tooltip {
  visibility: hidden;
  width: 60px;
  background-color: #555;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  padding: 5px;
  position: absolute;
  z-index: 1;
  bottom: 125%;
  left: 50%;
  margin-left: -30px;
  opacity: 0;
  transition: opacity 0.3s;
}

.like-btn:hover .tooltip {
  visibility: visible;
  opacity: 1;
}

.like-count {
  font-size: 12px;
  color: #666;
  margin-right: 10px;
}

.message-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #eee;
}
.apps-view {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.apps-view h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.apps-view {
  padding: 30px;
  height: 100%;
  overflow-y: auto;
}

.apps-view h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 24px;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 25px;
  padding: 0 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.app-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 180px;
  justify-content: space-between;
}

.app-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.app-icon {
  width: 60px;
  height: 60px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 15px;
}

.app-icon img {
  width: 70%;
  height: 70%;
  object-fit: contain;
}

.app-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.app-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.app-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.app-footer {
  margin-top: auto;
  padding-top: 15px;
  color: #3498db;
  font-size: 14px;
  font-weight: 500;
}

/* 调整侧边栏按钮样式 */
.sidebar-content button.new-conversation-btn {
  margin-bottom: 10px;
}

.collections-view {
  padding: 20px;
  height: calc(100% - 60px);
  overflow-y: auto;
}

.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px; /* 增加卡片间距 */
  margin-top: 20px;
  padding-bottom: 20px;
}

.collection-card {
  background: #fff;
  border-radius: 12px; /* 增加圆角 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 更柔和的阴影 */
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 300px; /* 固定卡片高度 */
  transition: all 0.3s ease;
}

.collection-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.collection-header {
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.collection-id {
  font-weight: 600;
  color: #555;
}

/* 保持原有卡片滚动条样式不变 */
.collection-content {
  overflow-y: auto;
  max-height: 200px;
  padding-right: 8px;
  margin-bottom: 15px;
  line-height: 1.6;
}
.collection-content::-webkit-scrollbar {
  width: 6px;
}

.collection-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.collection-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.collection-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.collection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.collection-actions {
  display: flex;
  gap: 10px;
}

.collection-date {
  font-size: 0.8em;
  color: #888;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  opacity: 0.7;
  transition: all 0.2s;
}

.icon-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.icon-img {
  width: 16px;
  height: 16px;
}

.collection-content {
  flex-grow: 1;
  overflow: hidden;
  margin-bottom: 10px;
}

.collection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.collection-actions {
  display: flex;
  gap: 8px;
}

.collection-date {
  font-size: 0.8em;
  color: #888;
}
/* 修改 chat-window 布局 */
.chat-window {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: rgba(232, 239, 238, 1);
  position: relative; /* 添加相对定位 */
}

/* 新增聊天消息容器 */
.chat-messages-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

/* 修改 messages 样式 */
.messages {
  flex-grow: 1;
  overflow-y: auto;
  padding-bottom: 80px;
  height: 0; /* 添加这个以确保flex容器内的滚动正常工作 */
}

/* 修改 input-container 样式 */
.input-container {
  position: fixed; /* 改为固定定位 */
  bottom: 20px;
  left: 380px; /* 侧边栏宽度 + 边距 */
  right: 20px;
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background: white;
  border-radius: 30px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

/* 调整应用视图和收藏视图的高度 */
.apps-view,
.collections-view {
  height: 100%;
  overflow-y: auto;
  padding-bottom: 20px;
}
.search-container {
  display: flex;
  gap: 10px;
  margin: 20px 0;
  align-items: center;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 30px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.search-btn,
.reset-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.search-btn {
  background-color: rgba(53, 86, 90, 1);
  color: white;
}

.search-btn:hover {
  background-color: rgba(42, 76, 80, 1);
}

.reset-btn {
  background-color: #f5f5f5;
  color: #666;
}

.reset-btn:hover {
  background-color: #e0e0e0;
}
.collections-view {
  padding: 20px;
  height: calc(100% - 60px);
  overflow-y: auto;
}

.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
  margin-top: 20px;
  padding-bottom: 20px;
}

.collection-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 300px; /* 固定卡片高度 */
  transition: all 0.3s ease;
}

.collection-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.collection-header {
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.collection-id {
  font-weight: 600;
  color: #555;
}

.collection-content {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 15px;
  line-height: 1.6;
  padding-right: 8px; /* 为滚动条留出空间 */
}

/* 自定义滚动条样式 */
.collection-content::-webkit-scrollbar {
  width: 6px;
}

.collection-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.collection-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.collection-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.collection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.collection-actions {
  display: flex;
  gap: 10px;
}

.collection-date {
  font-size: 0.8em;
  color: #888;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  opacity: 0.7;
  transition: all 0.2s;
}

.icon-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.icon-img {
  width: 16px;
  height: 16px;
}

/* 添加到你的样式部分 */
/* 翻译结果样式 */
.translation-result {
  margin: 10px 0;
  padding: 12px;
  background-color: #f8f9fa;
  border: 1px solid #e1e4e8;
  border-radius: 8px;
  position: relative;
}

.translation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #586069;
  font-weight: 500;
}

/* 确保翻译结果中的换行和段落被保留 */
.translation-content {
  white-space: pre-line; /* 保留换行符 */
  line-height: 1.6;
  margin-bottom: 8px;
}

/* 段落间距 */
.translation-content p {
  margin-bottom: 12px;
}

.translation-close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #586069;
  padding: 0;
  line-height: 1;
}

.translation-close-btn:hover {
  color: #0366d6;
}

/* 语言选择器样式 */
.language-selector {
  margin-top: 10px;
  padding: 8px 0;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.language-selector label {
  margin-right: 8px;
  color: #586069;
}

.language-selector select {
  padding: 4px 8px;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  background-color: white;
  font-size: 14px;
  color: #24292e;
}

.language-selector select:focus {
  outline: none;
  border-color: #0366d6;
  box-shadow: 0 0 0 3px rgba(3, 102, 214, 0.1);
}

/* 翻译加载状态 */
.translating-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #0366d6;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 确保翻译内容不会溢出 */
.bubble {
  overflow-wrap: break-word;
  word-wrap: break-word;
  hyphens: auto;
}

/* 朗读按钮样式 */
.speaking-indicator {
  color: #ff4d4f;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

/* 确保语音合成按钮与其他按钮对齐 */
.message-actions .icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
}
.cancel-file-btn {
  padding: 4px 8px;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.cancel-file-btn:hover {
  background-color: #ff7875;
  transform: scale(1.05);
}
.logo-2 {
  cursor: pointer; /* 鼠标悬停时显示手型 */
  transition: transform 0.2s ease; /* 添加平滑过渡效果 */
}

.logo-2:hover {
  transform: scale(1.05); /* 悬停时轻微放大 */
}

.logo-2:active {
  transform: scale(0.98); /* 点击时轻微缩小 */
}
</style>



