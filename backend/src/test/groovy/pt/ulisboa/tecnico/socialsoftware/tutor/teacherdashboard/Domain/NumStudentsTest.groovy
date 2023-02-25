package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

class StudentStatsSpec extends Specification {

    def "Test student stats entity"() {
        given:
        def courseExecution = new CourseExecution()
        def teacher = new Teacher()
        def dashboard = new TeacherDashboard(courseExecution, teacher)
        def studentStats = new StudentStats(dashboard, courseExecution)

        when:
        studentStats.update()

        then:
        studentStats.numberOfStudents == 0

        when:
        courseExecution.addStudent(new Student())
        studentStats.update()

        then:
        studentStats.numberOfStudents == 1

        when:
        courseExecution.addStudent(new Student())
        studentStats.update()

        then:
        studentStats.numberOfStudents == 2
    }
}
