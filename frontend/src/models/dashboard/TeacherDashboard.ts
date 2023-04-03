import StudentStatistics from '@/models/dashboard/StudentStatistics';

export default class TeacherDashboard {
  id!: number;
  studentStats: StudentStatistics[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;

      if (jsonObj.studentStats) {
        this.studentStats = jsonObj.studentStats.map(
          (studentStats: StudentStatistics) => new StudentStatistics(studentStats)
        );
      }
    }
  }
}