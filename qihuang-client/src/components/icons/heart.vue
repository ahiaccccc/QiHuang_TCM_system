<template>
  <svg
    :class="['heart', className]"
    fill="none"
    height="10"
    viewBox="0 0 10 10"
    width="10"
    xmlns="http://www.w3.org/2000/svg"
  >
    <g class="g" clip-path="url(#clip0_419_218)">
      <path
        class="path"
        d="M8.68355 1.92086C8.47073 1.70795 8.21805 1.53905 7.93994 1.42381C7.66184 1.30858 7.36375 1.24927 7.06271 1.24927C6.76168 1.24927 6.46359 1.30858 6.18548 1.42381C5.90737 1.53905 5.65469 1.70795 5.44188 1.92086L5.00021 2.36253L4.55855 1.92086C4.12867 1.49099 3.54564 1.24949 2.93771 1.24949C2.32978 1.24949 1.74675 1.49099 1.31688 1.92086C0.887007 2.35073 0.645508 2.93376 0.645508 3.54169C0.645508 4.14963 0.887007 4.73266 1.31688 5.16253L5.00021 8.84586L8.68355 5.16253C8.89646 4.94971 9.06536 4.69703 9.18059 4.41893C9.29583 4.14082 9.35514 3.84273 9.35514 3.54169C9.35514 3.24066 9.29583 2.94257 9.18059 2.66446C9.06536 2.38636 8.89646 2.13368 8.68355 1.92086Z"
        stroke="white"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="0.8"
      />
    </g>

    <defs class="defs">
      <clipPath class="clip-path" id="clip0_419_218">
        <rect class="rect" fill="white" height="10" width="10" />
      </clipPath>
    </defs>
  </svg>
</template>

<script>
export default {
  name: 'HeartIcon',
  props: {
    className: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.heart {
  display: inline-block;
  vertical-align: middle;
}

/* Optional: Add transition for hover effects */
.path {
  transition: stroke 0.2s ease;
}
</style>
