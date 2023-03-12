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
class NumMore75CorrectQuestionsTest extends SpockTest {
    def student1
    def student2
    def student3

    def setup() {
        student1 = new Student(USER_1_NAME, false)
        student2 = new Student(USER_2_NAME, false)
        student3 = new Student(USER_3_NAME, false)
        userRepository.save(student1)
        userRepository.save(student2)
        userRepository.save(student3)
    }

    def "Test student stats entity getNumMore75CorrectQuestions and testing set method for said attribute"() {
        given:
        def courseExecution = new CourseExecution()
        def teacher = new Teacher()
        def teacherDashboard = new TeacherDashboard(courseExecution, teacher)
        def studentStats = new StudentStats(courseExecution, teacherDashboard)

        when:
        courseExecution.addUser(student1)
        def board1 = new StudentDashboard(courseExecution, student1)
        board1.numberOfStudentAnswers = 8
        board1.numberOfCorrectStudentAnswers = 6
        studentStats.update()

        then:
        studentStats.getNumMore75CorrectQuestions() == 1
        studentStats.getId() != 0

        when:
        courseExecution.addUser(student2)
        def board2 = new StudentDashboard(courseExecution, student2)
        board2.numberOfStudentAnswers = 12
        board2.numberOfCorrectStudentAnswers = 9
        studentStats.update()

        then:
        studentStats.getNumMore75CorrectQuestions() == 2

        when:
        courseExecution.addUser(student3)
        def board3 = new StudentDashboard(courseExecution, student3)
        board3.numberOfStudentAnswers = 5
        board3.numberOfCorrectStudentAnswers = 2
        studentStats.update()

        then:
        studentStats.getNumMore75CorrectQuestions() == 2
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}