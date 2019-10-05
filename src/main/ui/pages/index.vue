<template>
  <div v-if="connected" style="margin-top: 70px">
    <b-navbar fixed="top" variant="dark" type="dark">
      <b-container>
        <b-navbar-brand>Simcat</b-navbar-brand>
      </b-container>
    </b-navbar>
    <b-container>
      <b-row>
        <b-col sm="12">
          <channel-box
            :channels="channels"
            :name="current.sender"
            :channel="current.channel"
            @update:name="setCurrentSender"
            @update:channel="setCurrentChannel"
            @channel-change="connectAndGetMessage"
            @new-channel="getMessages($event).then(m => setCurrentChannel($event))"
          />
        </b-col>
      </b-row>
      <b-row>
        <b-col sm="12" :style="{ height: (window.height - 300) + 'px' }">
          <div ref="message-box" class="overflow-auto" style="height: 100%">
            <message-box
              v-for="(m, index) in currentMessages"
              :key="'message-' + index"
              :message="m.content"
              :sender="m.sender"
              :op="m.sender === current.sender"
              class="col-sm-9 mb-1"
              body-class="py-1 px-0"
            />
          </div>
        </b-col>
      </b-row>
    </b-container>
    <div class="fixed-bottom">
      <b-container>
        <b-form @submit.prevent="submitMessage()">
          <b-form-group>
            <b-input-group>
              <b-form-input v-model="content" placeholder="Message..." class="my-6" :disabled="current.channel === '' || !senderValid" />
              <b-input-group-append>
                <b-button variant="primary" type="submit" :disabled="current.channel === '' || !senderValid">
                  Send
                </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b-form>
      </b-container>
    </div>
  </div>
</template>

<script>
import { mapActions, mapMutations, mapState } from 'vuex'
import ChannelBox from '~/components/ChannelBox'
import MessageBox from '~/components/MessageBox'

export default {
  components: {
    ChannelBox,
    MessageBox
  },
  data () {
    return {
      content: '',
      window: {
        width: 0,
        height: 0
      }
    }
  },
  computed: {
    currentMessages () {
      return this.messages.filter(m => m.channel === this.current.channel)
    },
    senderValid () {
      return this.current.sender.trim() !== ''
    },
    ...mapState({
      current: state => state.current,
      connected: state => state.connected,
      channels: state => state.channels,
      messages: state => state.messages
    })
  },
  watch: {
    currentMessages () {
      setTimeout(this.scrollDown, 100)
    }
  },
  async mounted () {
    await this.connect()
    await this.getChannels()
  },
  beforeMount () {
    window.addEventListener('resize', this.handleResize)
    this.handleResize()
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    async connectAndGetMessage (channel) {
      await this.connectToChannel(channel)
      if (this.currentMessages.length < 1 && channel !== '') {
        await this.getMessages(channel)
      }
      this.scrollDown()
    },
    scrollDown () {
      const messageBox = this.$refs['message-box']
      messageBox.scrollTop = messageBox.scrollHeight
    },
    async submitMessage () {
      await this.sendMessage({ content: this.content, sender: this.current.sender, channel: this.current.channel })
      this.content = ''
    },
    handleResize () {
      this.window.width = window.innerWidth
      this.window.height = window.innerHeight
    },
    ...mapMutations([ 'setCurrentChannel', 'setCurrentSender' ]),
    ...mapActions([ 'connect', 'connectToChannel', 'getChannels', 'getMessages', 'sendMessage' ])
  }
}
</script>

<style>
</style>
