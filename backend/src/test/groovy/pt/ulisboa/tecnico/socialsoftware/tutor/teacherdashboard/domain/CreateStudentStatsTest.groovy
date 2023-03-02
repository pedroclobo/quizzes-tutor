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
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser


@DataJpaTest
class CreateStudentStatsTest extends SpockTest {
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def "create an empty studentStats"() {
        given: 
        def studentStats = new StudentStats(teacherDashboard, externalCourseExecution)
        studentStatsRepository.save(studentStats)

        expect:
        studentStatsRepository.count() == 1L

        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        result.toString() == "StudentStats{" +
                "id=" + result.getId() +
                ", courseExecution=" + result.getCourseExecution() +
                ", teacherDashboard=" + result.getTeacherDashboard() +
                ", numStudents=" + result.getNumStudents() +
                ", numMore75CorrectQuestions " + result.getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes" + result.getNumAtLeast3Quizzes() +
                '}'
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}