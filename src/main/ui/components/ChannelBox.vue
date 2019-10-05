<template>
  <div>
    <b-form-group>
      <label for="name">Name</label>
      <b-form-input id="name" :value="name" autofocus @input="$emit('update:name', $event)" />
    </b-form-group>
    <b-form-group>
      <b-button
        v-b-modal.new-channel
        :disabled="name.trim() === ''"
        size="sm"
        variant="link"
        class="float-right"
      >
        Create new
      </b-button>
      <label for="channel">
        Channel
      </label>
      <b-form-select
        id="channel"
        :disabled="name.trim() === ''"
        :value="channel"
        :options="channels"
        @input="$emit('update:channel', $event); $emit('channel-change', $event)"
      />
    </b-form-group>

    <b-modal
      id="new-channel"
      hide-header
      hide-footer
    >
      <b-form @submit.prevent.stop="createChannel">
        <b-form-group>
          <label for="new-channel">Channel Name</label>
          <b-form-input id="new-channel" v-model="newChannel" autofocus />
        </b-form-group>
        <b-form-group>
          <b-button
            type="submit"
            variant="primary"
            class="float-right"
            :disabled="!newChannelValid"
          >
            Create
          </b-button>
        </b-form-group>
      </b-form>
    </b-modal>
  </div>
</template>

<script>
export default {
  props: {
    name: {
      type: String,
      default: ''
    },
    channel: {
      type: String,
      default: null
    },
    channels: {
      type: Array,
      default: () => ([])
    }
  },
  data () {
    return {
      newChannel: ''
    }
  },
  computed: {
    newChannelValid () {
      return this.newChannel.trim() !== ''
    }
  },
  methods: {
    createChannel () {
      if (!this.newChannelValid) {
        return
      }
      this.$emit('new-channel', this.newChannel)
      this.$bvModal.hide('new-channel')
    }
  }
}
</script>
