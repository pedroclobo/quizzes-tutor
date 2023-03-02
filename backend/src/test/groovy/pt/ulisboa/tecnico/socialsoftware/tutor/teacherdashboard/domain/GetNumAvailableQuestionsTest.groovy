package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class GetNumAvailableQuestionsTest extends SpockTest {
    def teacherDashboard
    def questionStats

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)
    }

    def "create an empty QuestionStats"() {
        expect: "the QuestionStats to be created with the correct attribute values"
        questionStats != null
        questionStats.getTeacherDashboard() == teacherDashboard
        questionStats.getCourseExecution() == externalCourseExecution
        questionStats.getNumAvailable() == 0
    }

    def "set and get the number of available questions"() {
        given: "a QuestionStats with 0 available questions"
        questionStats.setNumAvailable(0)

        when: "the number of available questions is updated to 10"
        questionStats.setNumAvailable(10)

        then: "the number of available questions is updated"
        questionStats.getNumAvailable() == 10
    }

    def "create an empty QuestionStats and update it"() {
        when: "the questionStats is updated"
        questionStats.update()

        then: "the number of questions is 0"
        questionStats.getNumAvailable() == 0
    }

    def "add disabled question to course execution"() {
        given: "a course execution and a course with no questions and a disabled question"
        def course = externalCourseExecution.getCourse()
        def question = new Question()

        when: "a disabled question is added to the course and the questionStats is updated"
        course.addQuestion(question)
        questionStats.update()

        then: "the number of questions is 0"
        questionStats.numAvailable == 0
    }

    def "add available question to course execution"() {
        given: "a course execution and a course with no questions and a available question"
        def course = externalCourseExecution.getCourse()
        def question = new Question()
        question.setStatus(Question.Status.AVAILABLE)

        when: "a question is added to the course and the questionStats is updated"
        course.addQuestion(question)
        questionStats.update()

        then: "the number of questions is 1"
        questionStats.numAvailable == 1
    }

    def "change question status to available"() {
        given: "a course execution and a course with no questions and a disabled question"
        def course = externalCourseExecution.getCourse()
        def question = new Question()
        course.addQuestion(question)
        questionStats.update()

        when: "the question status is changed to available and the questionStats is updated"
        question.setStatus(Question.Status.AVAILABLE)
        questionStats.update()

        then: "the number of questions is 1"
        questionStats.numAvailable == 1
    }

    def "change question status to disabled"() {
        given: "a course execution and a course with no questions and a available question"
        def course = externalCourseExecution.getCourse()
        def question = new Question()
        question.setStatus(Question.Status.AVAILABLE)
        course.addQuestion(question)
        questionStats.update()

        when: "the question status is changed to disabled and the questionStats is updated"
        question.setStatus(Question.Status.DISABLED)
        questionStats.update()

        then: "the number of questions is 0"
        questionStats.numAvailable == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
