import StudentStatistics from '@/models/dashboard/StudentStatistics';
import QuizStatistics from '@/models/dashboard/QuizStatistics';

export default class TeacherDashboard {
  id!: number;
  studentStats: StudentStatistics[] = [];
  quizStats: QuizStatistics[] = [];

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
    }
  }
}