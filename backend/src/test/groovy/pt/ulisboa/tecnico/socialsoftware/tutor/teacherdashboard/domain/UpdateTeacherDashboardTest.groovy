package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

@DataJpaTest
class UpdateTeacherDashboardTest extends SpockTest {
    def teacherDashboard
    def quizStats
    def studentStats
    def questionStats

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)

        studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats)

        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)
    }

    def "update empty teacher dashboard"() {
        when: "the teacher dashboard is updated"
        teacherDashboard.update()

        then:
        teacherDashboard.getQuizStats().get(0) == quizStats
        quizStats.getNumQuizzes() == 0
        quizStats.getUniqueQuizzesSolved() == 0
        quizStats.getAverageQuizzesSolved() == 0

        teacherDashboard.getStudentStats().get(0) == studentStats
        studentStats.getNumStudents() == 0
        studentStats.getNumMore75CorrectQuestions() == 0
        studentStats.getNumAtLeast3Quizzes() == 0

        teacherDashboard.getQuestionStats().get(0) == questionStats
        questionStats.getNumAvailable() == 0
        questionStats.getUniqueQuestionsAnswered() == 0
        questionStats.getAverageQuestionsAnswered() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
