package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

@DataJpaTest
class CreateQuizStatsTest extends SpockTest {
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def "create an empty quizStats"() {
        given: " a quizStats"
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)

        expect:
        quizStatsRepository.count() == 1L

        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        result.toString() == "QuizStats{" + "id=" + result.getId() +
                ", courseExecution=" + result.getCourseExecution() +
                ", teacherDashboard=" + result.getTeacherDashboard() +
                ", numQuizzes=" + result.getNumQuizzes() +
                ", uniqueQuizzesSolved=" + result.getUniqueQuizzesSolved() +
                ", averageQuizzesSolved=" +result.getAverageQuizzesSolved() +
                '}'
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
