import StudentStatistics from '@/models/dashboard/StudentStatistics';
import QuizStatistics from '@/models/dashboard/QuizStatistics';
import QuestionStatistics from '@/models/dashboard/QuestionStatistics';

export default class TeacherDashboard {
  id!: number;
  studentStats: StudentStatistics[] = [];
  quizStats: QuizStatistics[] = [];
  questionStats: QuestionStatistics[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;

      if (jsonObj.studentStats) {
        this.studentStats = jsonObj.studentStats.map(
          (studentStats: StudentStatistics) => new StudentStatistics(studentStats)
        );
      }
      if (jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStatistics) => new QuizStatistics(quizStats)
        );
      }
      if (jsonObj.questionStats) {
        this.questionStats = jsonObj.questionStats.map(
          (questionStats: QuestionStatistics) => new QuestionStatistics(questionStats)
        );
      }
    }
  }
}