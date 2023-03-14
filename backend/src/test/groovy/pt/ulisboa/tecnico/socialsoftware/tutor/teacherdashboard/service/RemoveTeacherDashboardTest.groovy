package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import spock.lang.Unroll

@DataJpaTest
class RemoveTeacherDashboardTest extends SpockTest {

    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def createTeacherDashboard() {
        def dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(dashboard)
        return dashboard
    }

    def createCourseExecution(Course course, String academicTerm) {
        def courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, academicTerm, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)

        return courseExecution
    }

    def "remove a dashboard"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboard()

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0L
        teacher.getDashboards().size() == 0
    }

    def "cannot remove a dashboard twice"() {
        given: "a removed dashboard"
        def dashboard = createTeacherDashboard()
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        when: "the dashboard is removed for the second time"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
    }

    @Unroll
    def "cannot remove a dashboard that doesn't exist with the dashboardId=#dashboardId"() {
        when: "an incorrect dashboard id is removed"
        teacherDashboardService.removeTeacherDashboard(dashboardId)

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }

    def "the dashboard is removed from the teacher"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboard()

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed from the teacher"
        teacher.getDashboards().size() == 0
    }

    def "the associated statistics are deleted"() {
        given: "a teacher in three course executions"
        def courseExecution1 = createCourseExecution(externalCourse, "1º Semestre 2021/2022")
        def courseExecution2 = createCourseExecution(externalCourse, "1º Semestre 2020/2021")
        def courseExecution3 = createCourseExecution(externalCourse, "1º Semestre 2019/2020")

        teacher.addCourse(courseExecution1)
        teacher.addCourse(courseExecution2)
        teacher.addCourse(courseExecution3)

        when: "a dashboard is created and than removed"
        def result = teacherDashboardService.createTeacherDashboard(courseExecution1.getId(), teacher.getId())

        teacherDashboardService.removeTeacherDashboard(result.getId())

        then: "the associated statistics are deleted"
        questionStatsRepository.findAll().size() == 0
        quizStatsRepository.findAll().size() == 0
        studentStatsRepository.findAll().size() == 0   
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
