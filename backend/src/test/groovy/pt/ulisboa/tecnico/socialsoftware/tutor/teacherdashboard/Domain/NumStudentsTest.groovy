package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution

@DataJpaTest
class NumStudentsTest extends SpockTest {
    def student1
    def student2
    def courseExecution;

    def setup() {
        student1 = new Student(USER_1_NAME, false)
        student2 = new Student(USER_2_NAME, false)
        userRepository.save(student1)
        userRepository.save(student2)
        courseExecution = new CourseExecution()
    }

    def "Test student stats entity numStudents and testing set method for said attribute"() {
        given:
        def teacher = new Teacher()
        def dashboard = new TeacherDashboard(courseExecution, teacher)
        def studentStats = new StudentStats(dashboard, courseExecution)
        def board1 = new StudentDashboard(courseExecution, student1)
        def board2 = new StudentDashboard(courseExecution, student2)
        board1.numberOfStudentAnswers = 8
        board1.numberOfCorrectStudentAnswers = 6
        board2.numberOfStudentAnswers = 8
        board2.numberOfCorrectStudentAnswers = 6


        when:
        studentStats.update()

        then:
        studentStats.getNumStudents() == 0

        when:
        courseExecution.addUser(student1)
        studentStats.update()

        then:
        studentStats.getNumStudents() == 1

        when:
        courseExecution.addUser(student2)
        studentStats.update()

        then:
        studentStats.getNumStudents() == 2
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
