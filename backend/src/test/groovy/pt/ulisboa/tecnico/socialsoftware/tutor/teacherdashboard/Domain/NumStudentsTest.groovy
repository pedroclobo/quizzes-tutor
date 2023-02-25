package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

@DataJpaTest
class NumStudentsTest extends SpockTest {
    def student1
    def student2

    def setup() {
        student1 = new Student(USER_1_NAME, false)
        student2 = new Student(USER_2_NAME, false)
        userRepository.save(student1)
        userRepository.save(student2)
    }

    def "Test student stats entity numStudents and testing set method for said attribute"() {
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
        courseExecution.addUser(student1)
        studentStats.update()

        then:
        studentStats.numberOfStudents == 1

        when:
        courseExecution.addUser(student2)
        studentStats.update()

        then:
        studentStats.numberOfStudents == 2
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
