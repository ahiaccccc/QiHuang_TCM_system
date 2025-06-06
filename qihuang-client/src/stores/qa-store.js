import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/apis/qa-apis'
import { EventSourcePolyfill } from 'event-source-polyfill'
import { getProfileAPI, updateProfileAPI, uploadAvatarAPI } from '@/apis/user'
// const userStore = useUserStore()
// const userId = userStore.userInfo?.userId || null

export const useQAClassicStore = defineStore('qaclassic', () => {
    const profile = ref({
    username: '',
    userId: '',
    email: '',
    avatar: '',
    })  
    // State
    const classic = ref({})
    const sessions = ref([])
    const currentMessages = ref([])
    const currentSessionId = ref(null)
    const newMessage = ref('')
    const quote = ref('')
    const sending = ref(false)
    const loadingMessage = ref({})
    const renamingSession = ref(null)
    const renamingTitle = ref('')
    const bookid = ref(0)

    const loadProfile = async () => {
  try {
    const response = await getProfileAPI()
    if (response.code === 200) {
      profile.value = response.data
    
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}
    // Actions
    const fetchClassic = async (id) => {
        classic.value = await api.getClassic(id)
    }

    const fetchSessions = async (classicId, userId) => {
        await loadProfile() // 确保在获取会话前加载用户信息
        userId = profile.value.userId || null
        sessions.value = await api.getSessions(classicId, userId)
    }

    const loadSession = async (sessionId) => {
        currentSessionId.value = sessionId
        currentMessages.value = await api.getMessages(sessionId)
    }

    const startNewSession = (initialQuestion = '') => {
        currentSessionId.value = null
        currentMessages.value = []
        newMessage.value = initialQuestion
        quote.value = ''
    }

    const loadingStates = ref({
  sending: false,
  editing: new Map(),
  regenerating: new Map()
})




// 修改后的sendMessage方法
const sendMessage = async (classicId) => {
    if (!newMessage.value.trim() || sending.value) return;
    sending.value = true;
    const userContent = quote.value + newMessage.value;

    try {
       // 创建临时用户消息（先使用临时ID）
        const tempUserMsg = {
            id: 'temp-' + Date.now(), // 临时ID
            role: 'user',
            content: userContent,
            createdAt: new Date().toISOString()
        };
        currentMessages.value.push(tempUserMsg);
        const userId = profile.value.userId || null;
        let sessionId = currentSessionId.value;
        if (!sessionId) {
            const session = await api.createSession(classicId, userId, userContent);
            sessionId = session.id;
            currentSessionId.value = sessionId;
            await fetchSessions(classicId);
        }

        const token = localStorage.getItem('token')
        const response = await fetch(`/api/qa/stream-messages`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ sessionId, content: userContent, role: 'user', parentId: null })
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let aiContent = '';
        let serverAIId = null;
        let currentEvent = null;
        let realUserId = null; // 新增真实用户ID存储

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
                if (line.startsWith('event:')) {
                    currentEvent = line.replace('event:', '').trim();
                    continue;
                }

                if (line.startsWith('data:')) {
                    const data = line.replace('data:', '').trim();

                    // 处理 USER_MSG 事件（新增）
                    if (currentEvent === 'USER_MSG') {
                        realUserId = data;
                        // 更新临时消息为真实ID
                        currentMessages.value = currentMessages.value.map(msg => 
                            msg.id === tempUserMsg.id ? { ...msg, id: realUserId } : msg
                        );
                        currentEvent = null;
                        continue;
                    }

                    // 处理 START 事件
                    if (currentEvent === 'START') {
                        serverAIId = data;
                        currentMessages.value.push({
                            id: serverAIId,
                            role: 'assistant',
                            content: '',
                            createdAt: new Date().toISOString(),
                            streaming: true
                        });
                        currentEvent = null;
                        continue;
                    }

                    // 处理普通内容（MESSAGE 或未指定 event）
                    if (currentEvent === null) {
                        aiContent += data;
                        currentMessages.value = currentMessages.value.map(msg =>
                            msg.id === serverAIId ? { ...msg, content: aiContent } : msg
                        );
                    }

                    // 处理 COMPLETE 事件（结束）
                    if (currentEvent === 'COMPLETE') {
                        currentMessages.value = currentMessages.value.map(msg =>
                            msg.id === serverAIId ? { ...msg, streaming: false } : msg
                        );
                    }
                }
            }
        }

        currentMessages.value = currentMessages.value.map(msg =>
            msg.id === serverAIId ? { ...msg, streaming: false } : msg
        );
        await fetchSessions(classic.value.id);

    } catch (error) {
        if (error.name === 'AbortError') {
      currentMessages.value.push({
        role: 'system',
        content: '已停止生成',
        error: true
      })
    } else {
        console.error('发送失败:', error);
        currentMessages.value.push({
            role: 'system',
            content: '请求失败: ' + error.message,
            error: true
        });
    }
    } finally {
        sending.value = false;
        newMessage.value = '';
        quote.value = '';
    }
};



    const deleteSession = async (sessionId) => {
        await api.deleteSession(sessionId)
        sessions.value = sessions.value.filter(s => s.id !== sessionId)
        if (currentSessionId.value === sessionId) {
            currentSessionId.value = null
            currentMessages.value = []
        }
    }

    const deleteMessage = async (messageId) => {
        await api.deleteMessage(messageId)
        currentMessages.value = currentMessages.value.filter(m => m.id !== messageId)
    }

const editMessage = async (messageId, newContent) => {
    try {
        loadingStates.value.editing.set(messageId, true);
        currentMessages.value.push({
            content: newContent,
            role: 'user',
            createdAt: new Date().toISOString()
        });
        console.log('编辑消息:', messageId, newContent);
        const token = localStorage.getItem('token')
        const response = await fetch(`/api/qa/stream-edit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ messageId, newContent })
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let aiContent = '';
        let serverAIId = null;
        let currentEvent = null;

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

           for (const line of lines) {
  if (line.startsWith('event:')) {
    currentEvent = line.replace('event:', '').trim();
    continue;
  }

  if (line.startsWith('data:')) {
    const data = line.replace('data:', '').trim();

    // 处理 START 事件
    if (currentEvent === 'START') {
      serverAIId = data;
      currentMessages.value.push({
        id: serverAIId,
        role: 'assistant',
        content: '',
        createdAt: new Date().toISOString(),
        streaming: true
      });
      currentEvent = null; // 重置事件状态
      //console.log('START event received, serverAIId:', serverAIId);
      continue;
    }

    // 处理普通内容（MESSAGE 或未指定 event）
    if (currentEvent === null) {
      aiContent += data;
      currentMessages.value = currentMessages.value.map(msg =>
        msg.id === serverAIId ? { ...msg, content: aiContent } : msg
      );
    }

    // 处理 COMPLETE 事件（结束）
    if (currentEvent === 'COMPLETE') {
      currentMessages.value = currentMessages.value.map(msg =>
        msg.id === serverAIId ? { ...msg, streaming: false } : msg
      );
    }
  }
}
        }
        currentMessages.value = currentMessages.value.map(msg =>
            msg.id === serverAIId ? { ...msg, streaming: false } : msg
        );
        await fetchSessions(classic.value.id);

    } catch (error) {
        console.error('编辑失败:', error);
        currentMessages.value.push({
            role: 'system',
            content: '编辑失败: ' + error.message,
            error: true
        });
    } finally {
        loadingStates.value.editing.delete(messageId);
    }
};


// 修改 regenerate 方法
const regenerate = async (messageId) => {
    try {
        loadingMessage.value[messageId] = true;
        
        // 找到原消息
        const originalMsgIndex = currentMessages.value.findIndex(msg => msg.id === messageId);
        if (originalMsgIndex === -1) {
            throw new Error('消息未找到');
        }
        
        // 重置原消息状态
        currentMessages.value[originalMsgIndex].content = '';
        currentMessages.value[originalMsgIndex].streaming = true;
        currentMessages.value[originalMsgIndex].error = false;

        const token = localStorage.getItem('token')
        const response = await fetch(`/api/qa/stream-regenerate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ messageId })
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let currentEvent = null;

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
                if (line.startsWith('event:')) {
                    currentEvent = line.replace('event:', '').trim();
                    continue;
                }

                if (line.startsWith('data:')) {
                    const data = line.replace('data:', '').trim();

                    // START 事件 - 确认原消息ID
                    if (currentEvent === 'START') {
                        if (data !== messageId.toString()) {
                            console.warn('消息ID不匹配', data, messageId);
                        }
                        currentEvent = null;
                        continue;
                    }

                    // 普通内容 - 更新原消息
                    if (currentEvent === null) {
                        currentMessages.value[originalMsgIndex].content += data;
                    }

                    // 完成事件
                    if (currentEvent === 'COMPLETE') {
                        currentMessages.value[originalMsgIndex].streaming = false;
                    }
                }
            }
        }

        // 确保结束状态
        currentMessages.value[originalMsgIndex].streaming = false;
        await fetchSessions(classic.value.id);
    } catch (error) {
        console.error('重新生成失败:', error);
        const originalMsgIndex = currentMessages.value.findIndex(msg => msg.id === messageId);
        if (originalMsgIndex !== -1) {
            currentMessages.value[originalMsgIndex].content = '重新生成失败: ' + error.message;
            currentMessages.value[originalMsgIndex].streaming = false;
            currentMessages.value[originalMsgIndex].error = true;
        }
    } finally {
        loadingMessage.value[messageId] = false;
    }
};

    const rate = async (messageId, feedback) => {
        await api.sendFeedback(messageId, feedback)
    }

    const startRename = (session) => {
        renamingSession.value = session.id
        renamingTitle.value = session.title
    }

    const saveRename = async () => {
        if (!renamingTitle.value.trim()) return
        try {
            await api.renameSession(renamingSession.value, renamingTitle.value)
            const idx = sessions.value.findIndex(s => s.id === renamingSession.value)
            if (idx !== -1) sessions.value[idx].title = renamingTitle.value
            renamingSession.value = null
            renamingTitle.value = ''
        } catch (error) {
            alert('重命名失败: ' + error.message)
        }
    }

    const cancelRename = () => {
        renamingSession.value = null
        renamingTitle.value = ''
    }

    return {
        // State
        classic,
        sessions,
        currentMessages,
        currentSessionId,
        newMessage,
        quote,
        sending,
        loadingMessage,
        renamingSession,
        renamingTitle,
        bookid,
        
        // Actions
        fetchClassic,
        fetchSessions,
        loadSession,
        startNewSession,
        sendMessage,
        deleteSession,
        deleteMessage,
        editMessage,
        regenerate,
        rate,
        startRename,
        saveRename,
        cancelRename
    }
})