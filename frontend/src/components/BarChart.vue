<template>
  <Bar :chartData="data" :chartOptions="options" />
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { Bar } from 'vue-chartjs/legacy';
@Component({
  components: {
    Bar,
  },
})
export default class BarChart extends Vue {
  @Prop() readonly labels!: string[];
  @Prop() readonly datasetLabels!: string[];
  @Prop() readonly datasetData!: number[][];
  data = {};
  options = {
    responsive: true,
  };
  created() {
    ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)
	this.updateData();
  }
  updateData() {
    this.data = {
    labels: this.labels,
      datasets: [
        {
          label: this.datasetLabels[0],
          backgroundColor: '#c0392b',
          data: this.datasetData[0],
        },
        {
          label: this.datasetLabels[1],
          backgroundColor: '#2980b9',
          data: this.datasetData[1],
        },
        {
          label: this.datasetLabels[2],
          backgroundColor: '#1abc9c',
          data: this.datasetData[2],
        }
      ],
    }
  }
}
</script>

<style scoped lang="scss" />
