<template>
  <b-alert :variant="op ? 'primary' : 'secondary'" :class="(op ? 'offset-sm-3' : '') + ' col-sm-9 mb-1 py-1 px-2'" show>
    <div>
      <strong v-text="sender" />
    </div>
    <div v-text="message" />
  </b-alert>
</template>

<script>
export default {
  props: {
    sender: {
      type: String,
      default: null
    },
    message: {
      type: String,
      default: null
    },
    op: {
      type: Boolean,
      default: false
    }
  }
}
</script>
