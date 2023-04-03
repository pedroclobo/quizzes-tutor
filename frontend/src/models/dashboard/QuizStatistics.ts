export default class QuizStatistics {
  numQuizzes!: number;
  numUniqueAnsweredQuizzes!: number;
  averageQuizzesSolved!: number;
  courseExecutionYear!: number;

  constructor(jsonObj?: QuizStatistics) {
    if (jsonObj) {
      this.numQuizzes = jsonObj.numQuizzes;
      this.numUniqueAnsweredQuizzes = jsonObj.numUniqueAnsweredQuizzes;
      this.averageQuizzesSolved = jsonObj.averageQuizzesSolved;
      this.courseExecutionYear = jsonObj.courseExecutionYear;
    }
  }
}
