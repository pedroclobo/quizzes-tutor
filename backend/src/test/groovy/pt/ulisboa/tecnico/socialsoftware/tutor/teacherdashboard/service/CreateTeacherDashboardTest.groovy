package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import spock.lang.Unroll

@DataJpaTest
    class CreateTeacherDashboardTest extends SpockTest {
    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def "create an empty dashboard"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        when: "a dashboard is created"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacher().getId() == teacher.getId()

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(result)
    }

    def "cannot create multiple dashboards for a teacher on a course execution"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        and: "an empty dashboard for the teacher"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        when: "a second dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "there is only one dashboard"
        teacherDashboardRepository.count() == 1L

        and: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_ALREADY_HAS_DASHBOARD
    }

    def "cannot create a dashboard for a user that does not belong to the course execution"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot create a dashboard with courseExecutionId=#courseExecutionId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecutionId, teacher.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        where:
        courseExecutionId << [0, 100]
    }

    @Unroll
    def "cannot create a dashboard with teacherId=#teacherId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacherId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        where:
        teacherId << [0, 100]
    }

    def "create a dashboard with the teacher associated to one course execution"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "the associated statistics have been created"
        def result = teacherDashboardRepository.findAll().get(0)

        result.getQuizStats().size() == 1
        result.getStudentStats().size() == 1
        result.getQuestionStats().size() == 1

        result.getQuizStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getStudentStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuestionStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
    }

    def "create a dashboard with the teacher associated to two course executions"() {
        given: "a teacher in two course executions"
        def courseExecution2 = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution2)

        teacher.addCourse(externalCourseExecution)
        teacher.addCourse(courseExecution2)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "the associated statistics have been created"
        def result = teacherDashboardRepository.findAll().get(0)

        result.getQuizStats().size() == 2
        result.getStudentStats().size() == 2
        result.getQuestionStats().size() == 2

        result.getQuizStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getStudentStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuestionStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()

        result.getQuizStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getStudentStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getQuestionStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
    }

    def "create a dashboard with the teacher associated with three course executions"() {
        given: "a teacher in three course executions"
        def courseExecution2 = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution2)

        def courseExecution3 = new CourseExecution(externalCourse, COURSE_3_ACRONYM, COURSE_3_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution3)

        teacher.addCourse(externalCourseExecution)
        teacher.addCourse(courseExecution2)
        teacher.addCourse(courseExecution3)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "the associated statistics have been created"
        def result = teacherDashboardRepository.findAll().get(0)

        result.getQuizStats().size() == 3
        result.getQuizStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuizStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getQuizStats().get(2).getCourseExecution().getId() == courseExecution3.getId()

        result.getStudentStats().size() == 3
        result.getStudentStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getStudentStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getStudentStats().get(2).getCourseExecution().getId() == courseExecution3.getId()

        result.getQuestionStats().size() == 3
        result.getQuestionStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuestionStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getQuestionStats().get(2).getCourseExecution().getId() == courseExecution3.getId()
    }

    def "create a dashboard with the teacher associated with five course executions"() {
        given: "a teacher in five course executions"
        def courseExecution2 = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution2)

        def courseExecution3 = new CourseExecution(externalCourse, COURSE_3_ACRONYM, COURSE_3_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution3)

        def courseExecution4 = new CourseExecution(externalCourse, COURSE_4_ACRONYM, COURSE_4_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution4)

        def courseExecution5 = new CourseExecution(externalCourse, COURSE_5_ACRONYM, COURSE_5_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution5)

        teacher.addCourse(externalCourseExecution)
        teacher.addCourse(courseExecution2)
        teacher.addCourse(courseExecution3)
        teacher.addCourse(courseExecution4)
        teacher.addCourse(courseExecution5)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "the associated statistics have been created"
        def result = teacherDashboardRepository.findAll().get(0)

        result.getQuizStats().size() == 3
        result.getQuizStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuizStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getQuizStats().get(2).getCourseExecution().getId() == courseExecution3.getId()

        result.getStudentStats().size() == 3
        result.getStudentStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getStudentStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getStudentStats().get(2).getCourseExecution().getId() == courseExecution3.getId()

        result.getQuestionStats().size() == 3
        result.getQuestionStats().get(0).getCourseExecution().getId() == externalCourseExecution.getId()
        result.getQuestionStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        result.getQuestionStats().get(2).getCourseExecution().getId() == courseExecution3.getId()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
