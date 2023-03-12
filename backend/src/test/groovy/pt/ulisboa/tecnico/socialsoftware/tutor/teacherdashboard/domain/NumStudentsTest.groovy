package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import java.time.LocalDateTime

@DataJpaTest
class NumStudentsTest extends SpockTest {
    def student1
    def student2
    def courseExecution
    def teacher

    def setup() {
        student1 = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        teacher = new Teacher(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(student1)
        userRepository.save(student2)
        userRepository.save(teacher)
        courseExecution = new CourseExecution()
        teacher.getAuthUser().setLastAccess(LocalDateTime.now())
    }

    def "Test student stats entity numStudents and testing set method for said attribute"() {
        given:
        def dashboard = new TeacherDashboard(courseExecution, teacher)
        def studentStats = new StudentStats(courseExecution, dashboard)
        def board1 = new StudentDashboard(courseExecution, student1)
        def board2 = new StudentDashboard(courseExecution, student2)


        when:
        board1.numberOfStudentAnswers = 8
        board1.numberOfCorrectStudentAnswers = 6
        board2.numberOfStudentAnswers = 8
        board2.numberOfCorrectStudentAnswers = 6
        studentStats.update()
        studentStats.setNumStudents(2)

        then:
        studentStats.getNumStudents() == 2
        studentStats.toString().equals("StudentStats{" +
                "id=" + studentStats.getId() +
                ", courseExecution=" + studentStats.getCourseExecution() +
                ", teacherDashboard=" + studentStats.getTeacherDashboard() +
                ", numStudents=" + studentStats.getNumStudents() +
                ", numMore75CorrectQuestions " + studentStats.getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes" + studentStats.getNumAtLeast3Quizzes() +
                '}')

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
