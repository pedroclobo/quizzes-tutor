<template>
  <div class="container">
    <h2 v-if="teacherDashboard != null">Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="numStudents" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStats[0].numStudents" />
        </div>
        <div class="project-name">
          <p>Number of Students</p>
        </div>
      </div>

      <div class="items">
        <div ref="numMore75CorrectQuestions" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStats[0].numMore75CorrectQuestions" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 75% Questions</p>
        </div>
      </div>

      <div class="items">
        <div ref="numAtLeast3Quizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.studentStats[0].numAtLeast3Quizzes" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 3 Quizzes</p>
        </div>
      </div>

      <div class="items">
        <div ref="numQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].numQuizzes" />
        </div>
        <div class="project-name">
          <p>Number of Quizzes</p>
        </div>
      </div>

      <div class="items">
        <div ref="numUniqueAnsweredQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes" />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique)</p>
        </div>
      </div>

      <div class="items">
        <div ref="averageQuizzesSolved" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].averageQuizzesSolved" />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique, Average Per Student)</p>
        </div>
      </div>

      <div class="items">
        <div ref="numQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].numAvailable" />
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>

      <div class="items">
        <div ref="numUniqueAnsweredQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].answeredQuestionsUnique" />
        </div>
        <div class="project-name">
          <p>Number of Questions Solved (Unique)</p>
        </div>
      </div>

      <div class="items">
        <div ref="averageQuizzesSolved" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].averageQuestionsAnswered" />
        </div>
        <div class="project-name">
          <p>Number of Questions Correctly Solved (Unique, Average Per Student)</p>
        </div>
      </div>
    </div>

    <h2 v-if="teacherDashboard != null && quizLabels.length > 1">Comparison with previous course executions</h2>
    <div v-if="teacherDashboard != null && quizLabels.length > 1" class="chart-container">
      <div class="bar-chart">
          <bar-chart :datasetLabels="quizDatasetLabels"
                     :labels="quizLabels"
                     :datasetData="quizDatasetData"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import BarChart from '@/components/BarChart.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';

@Component({
  components: { AnimatedNumber, BarChart },
})

export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  quizDatasetLabels: string[] = ['Quizzes: Total Available', 'Quizzes: Solved (Unique)', 'Quizzes: Solved (Unique, Average Per Student)'];
  quizLabels: number[] | null = null;
  quizDatasetData: number[][] | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();

      this.quizLabels = [];
      this.quizDatasetData = [[], [], []];

	  if (this.teacherDashboard.quizStats[0]) {
        this.quizLabels.unshift(this.teacherDashboard.quizStats[0].courseExecutionYear);
		this.quizDatasetData[0].unshift(this.teacherDashboard.quizStats[0].numQuizzes);
		this.quizDatasetData[1].unshift(this.teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes);
		this.quizDatasetData[2].unshift(this.teacherDashboard.quizStats[0].averageQuizzesSolved);
	  }
	  if (this.teacherDashboard.quizStats[1]) {
        this.quizLabels.unshift(this.teacherDashboard.quizStats[1].courseExecutionYear);
		this.quizDatasetData[0].unshift(this.teacherDashboard.quizStats[1].numQuizzes);
		this.quizDatasetData[1].unshift(this.teacherDashboard.quizStats[1].numUniqueAnsweredQuizzes);
		this.quizDatasetData[2].unshift(this.teacherDashboard.quizStats[1].averageQuizzesSolved);
	  }
	  if (this.teacherDashboard.quizStats[2]) {
        this.quizLabels.unshift(this.teacherDashboard.quizStats[2].courseExecutionYear);
		this.quizDatasetData[0].unshift(this.teacherDashboard.quizStats[2].numQuizzes);
		this.quizDatasetData[1].unshift(this.teacherDashboard.quizStats[2].numUniqueAnsweredQuizzes);
		this.quizDatasetData[2].unshift(this.teacherDashboard.quizStats[2].averageQuizzesSolved);
	  }

    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}

</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.chart-container {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.90);
    justify-content: center;
    width: 800px;
    height: 400px;
    margin: 20px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
