package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

@DataJpaTest
class CreateQuestionStatsTest extends SpockTest {
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def "create an empty questionStats"() {
        given: "a questionStats"
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)

        expect:
        questionStatsRepository.count() == 1L

        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        result.toString() == "QuestionStats{" + 
                "id=" + result.getId() +
                ", courseExecution=" + result.getCourseExecution() +
                ", teacherDashboard=" + result.getTeacherDashboard() +
                ", numAvailable=" + result.getNumAvailable() +
                ", uniqueQuestionsAnswered=" + result.getUniqueQuestionsAnswered() +
                ", averageQuestionsAnswered=" + result.getAverageQuestionsAnswered() +
                '}'
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}