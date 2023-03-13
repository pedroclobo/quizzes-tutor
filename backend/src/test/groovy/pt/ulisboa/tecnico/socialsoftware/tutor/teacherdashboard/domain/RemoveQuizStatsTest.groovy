package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

@DataJpaTest
class RemoveQuizStatsTest extends SpockTest {
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def "remove a quizStats"() {
        given: " a quizStats"
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)

        when: "the quizStats is removed"
        quizStats.remove()

        then: "the teacher dashboard and the course execution don't have any QuizStats"
        teacherDashboard.getQuizStats().isEmpty()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
