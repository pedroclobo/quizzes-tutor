package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission

@DataJpaTest
class UniqueQuestionsAnsweredTest extends SpockTest {
    def student1
    def student2
    def questionStats

    def setup() {
        createExternalCourseAndExecution()

        student1 = new Student(USER_1_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        student2 = new Student(USER_2_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        def teacher = new Teacher(USER_3_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
    }

    def "get 0 questions answered"() {
        when: "a question has no answers"
        questionStats.update()

        then: "the number of questions answered is 0"
        questionStats.getUniqueQuestionsAnswered() == 0
    }

    def "get 1 question answered by students"() {
        given: "a question and 2 submissions by 2 students"
        def question = new Question()
        def submission1 = new QuestionSubmission(externalCourseExecution, question, student1)
        def submission2 = new QuestionSubmission(externalCourseExecution, question, student2)

        when: "a question gets an answer"
        externalCourseExecution.addQuestionSubmission(submission1)
        questionStats.update()

        then: "the number of questions answered is 1"
        questionStats.getUniqueQuestionsAnswered() == 1

        when: "a question gets 2 answers by the same student"
        externalCourseExecution.addQuestionSubmission(submission1)
        questionStats.update()

        then: "the number of questions answered remains 1"
        questionStats.getUniqueQuestionsAnswered() == 1

        when: "a question gets answered by a different students"
        externalCourseExecution.addQuestionSubmission(submission2)
        questionStats.update()

        then: "the number of questions answered remains 1 again"
        questionStats.getUniqueQuestionsAnswered() == 1
    }

    def "get 2 questions answered by students"() {
        given: "two questions"
        def question1 = new Question()
        def question2 = new Question()

        when: "two questions are answered by the same student"
        externalCourseExecution.addQuestionSubmission(
            new QuestionSubmission(externalCourseExecution, question1, student1))
        externalCourseExecution.addQuestionSubmission(
            new QuestionSubmission(externalCourseExecution, question2, student1))
        questionStats.update()

        then: "the number of questions answered is 2"
        questionStats.getUniqueQuestionsAnswered() == 2

        when: "the first question is also answered by another student"
        externalCourseExecution.addQuestionSubmission(
            new QuestionSubmission(externalCourseExecution, question1, student2))
        questionStats.update()

        then: "the number of questions answered remains 2"
        questionStats.getUniqueQuestionsAnswered() == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}