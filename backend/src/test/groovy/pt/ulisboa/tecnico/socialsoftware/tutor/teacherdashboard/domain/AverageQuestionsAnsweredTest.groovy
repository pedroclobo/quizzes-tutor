package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class AverageQuestionsAnsweredTest extends SpockTest {
    def teacherDashboard
    def questionStats
    def course
    def options

    def setup() {
        createExternalCourseAndExecution()
        course = externalCourseExecution.getCourse()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)

        options = new ArrayList<OptionDto>()
        options.add(optionDto)
    }

    def "create an empty QuestionStats"() {
        expect: "the average number of unique questions answered by student is 0"
        questionStats.getAverageQuestionsAnswered() == 0f
    }

    def "create an empty QuestionStats and update it"() {
        when: "the questionStats is updated"
        questionStats.update()

        then: "the average number of unique questions answered by student is 0"
        questionStats.getAverageQuestionsAnswered() == 0f
    }

    def "there are no students in the course execution"() {
        given: "a question"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        questionDto.getQuestionDetailsDto().setOptions(options)
        def question = new Question(course, questionDto)
        questionRepository.save(question)

        when: "a student not enrolled in the course execution answers the question"
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question, new Student(USER_2_NAME, false)))
        questionStats.update()

        then: "the average number of unique questions answered by student is 0"
        questionStats.getAverageQuestionsAnswered() == 0f
    }

    def "add questions answered by multiple students to a course execution"() {
        given: "two questions and two students"
        def questionDto1 = new QuestionDto()
        questionDto1.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto1.setTitle(QUESTION_1_TITLE)
        questionDto1.setContent(QUESTION_1_CONTENT)
        questionDto1.setStatus(Question.Status.SUBMITTED.name())
        questionDto1.getQuestionDetailsDto().setOptions(options)
        def question1 = new Question(course, questionDto1)
        questionRepository.save(question1)

        def questionDto2 = new QuestionDto()
        questionDto2.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto2.setTitle(QUESTION_2_TITLE)
        questionDto2.setContent(QUESTION_2_CONTENT)
        questionDto2.setStatus(Question.Status.SUBMITTED.name())
        questionDto2.getQuestionDetailsDto().setOptions(options)
        def question2 = new Question(course, questionDto2)
        questionRepository.save(question2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "the questions are answered"
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question1, student1))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question1, student2))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question2, student2))

        questionStats.update()

        then: "the average number of unique questions answered by student is 1.5"
        questionStats.getAverageQuestionsAnswered() == 1.5f
    }

    def "add questions answered by all students to a course execution"() {
        given: "two questions and two students"
        def questionDto1 = new QuestionDto()
        questionDto1.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto1.setTitle(QUESTION_1_TITLE)
        questionDto1.setContent(QUESTION_1_CONTENT)
        questionDto1.setStatus(Question.Status.SUBMITTED.name())
        questionDto1.getQuestionDetailsDto().setOptions(options)
        def question1 = new Question(course, questionDto1)
        questionRepository.save(question1)

        def questionDto2 = new QuestionDto()
        questionDto2.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto2.setTitle(QUESTION_2_TITLE)
        questionDto2.setContent(QUESTION_2_CONTENT)
        questionDto2.setStatus(Question.Status.SUBMITTED.name())
        questionDto2.getQuestionDetailsDto().setOptions(options)
        def question2 = new Question(course, questionDto2)
        questionRepository.save(question2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "each student answers all the quizzes"
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question1, student1))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question2, student1))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question1, student2))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question2, student2))

        questionStats.update()

        then: "the average number of unique questions answered by student is 2"
        questionStats.getAverageQuestionsAnswered() == 2f
    }

    def "add questions to a course execution where not all students have answered a question"() {
        given: "two quizzes and two students"
        def questionDto1 = new QuestionDto()
        questionDto1.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto1.setTitle(QUESTION_1_TITLE)
        questionDto1.setContent(QUESTION_1_CONTENT)
        questionDto1.setStatus(Question.Status.SUBMITTED.name())
        questionDto1.getQuestionDetailsDto().setOptions(options)
        def question1 = new Question(course, questionDto1)
        questionRepository.save(question1)

        def questionDto2 = new QuestionDto()
        questionDto2.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto2.setTitle(QUESTION_2_TITLE)
        questionDto2.setContent(QUESTION_2_CONTENT)
        questionDto2.setStatus(Question.Status.SUBMITTED.name())
        questionDto2.getQuestionDetailsDto().setOptions(options)
        def question2 = new Question(course, questionDto2)
        questionRepository.save(question2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "each student answers all the quizzes"
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question1, student1))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question2, student1))

        questionStats.update()

        then: "the average number of unique questions answered by student is 1"
        questionStats.getAverageQuestionsAnswered() == 1f
    }

    def "add questions to a course execution where a student submites multiple answers"() {
        given: "a question and two students"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        questionDto.getQuestionDetailsDto().setOptions(options)
        def question = new Question(course, questionDto)
        questionRepository.save(question)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "the first student answers the question and the second student answers it three times"
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question, student1))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question, student2))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question, student2))
        externalCourseExecution.addQuestionSubmission(new QuestionSubmission(externalCourseExecution, question, student2))

        questionStats.update()

        then: "the average number of unique questions answered by student is 1"
        questionStats.getAverageQuestionsAnswered() == 1f
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
