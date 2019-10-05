import EventBus from 'vertx3-eventbus-client'

export const state = () => ({
  eventBus: null,
  connected: false,
  channels: [],
  messages: [],
  handlers: {},
  current: {
    sender: '',
    channel: ''
  }
})

export const mutations = {
  setCurrentChannel (state, channel) {
    state.current.channel = channel
  },
  setCurrentSender (state, sender) {
    state.current.sender = sender
  },
  setEventBus (state, eb) {
    state.eventBus = eb
  },
  setConnected (state, connected) {
    state.connected = connected
  },
  setChannels (state, channels) {
    state.channels.length = 0
    state.channels.push('', ...channels)
  },
  addMessage (state, { channel, message }) {
    const messages = state.messages
    message.channel = channel
    messages.push(message)
  }
}

export const actions = {
  connect ({ commit, dispatch }) {
    const eb = new EventBus('/eventbus')
    return new Promise((resolve) => {
      eb.onopen = () => {
        commit('setConnected', true)
        commit('setEventBus', eb)
        dispatch('registerHandlers', eb)
        resolve(true)
      }
    })
  },
  connectToChannel ({ commit, state }, channel) {
    if (channel === '') {
      return
    }
    const eb = state.eventBus
    const chatChannel = 'chat.messages.' + channel
    if (eb.handlers.hasOwnProperty(chatChannel)) {
      return
    }
    eb.registerHandler(chatChannel, (err, msg) => {
      if (err) {
        throw err
      }
      const message = JSON.parse(msg.body)
      commit('addMessage', { channel, message })
    })
  },
  getChannels ({ state }) {
    const eb = state.eventBus
    return new Promise((resolve, reject) => {
      eb.send('chat.get-channels', {}, (err, msg) => {
        if (err) {
          reject(err)
        }
        resolve(JSON.parse(msg.body))
      })
    })
  },
  getMessages ({ commit, state }, channel) {
    const eb = state.eventBus
    return new Promise((resolve, reject) => {
      eb.send('chat.get-messages', {}, { channel }, (err, msg) => {
        if (err) {
          reject(err)
        }
        const messages = JSON.parse(msg.body)
        for (const message of messages) {
          commit('addMessage', { message, channel })
        }
        resolve(messages)
      })
    })
  },
  registerHandlers ({ commit }, eb) {
    eb.registerHandler('chat.channels', (err, msg) => {
      if (err) {
        throw err
      }
      const body = JSON.parse(msg.body)
      commit('setChannels', body)
    })
  },
  sendMessage ({ state }, { content = '', sender = '', channel = '' }) {
    if (content.trim() === '' || sender.trim() === '' || channel.trim() === '') {
      return
    }
    const eb = state.eventBus
    return new Promise((resolve, reject) => {
      eb.send('chat.send-messages', { content, sender }, { channel }, (err, msg) => {
        if (err) {
          reject(err)
        }
        resolve(msg)
      })
    })
  }
}
