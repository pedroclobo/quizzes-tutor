package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;


@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany(mappedBy = "teacherDashboard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<QuestionStats> questionStats = new ArrayList<>();

    @OneToMany(mappedBy = "teacherDashboard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StudentStats> studentStats = new ArrayList<>();

    @OneToMany(mappedBy = "teacherDashboard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<QuizStats> quizStats = new ArrayList<>();

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public List<QuestionStats> getQuestionStats() {
        return questionStats;
    }

    public void addQuestionStats(QuestionStats questionStats) {
        this.questionStats.add(questionStats);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
        studentStats = null;
        quizStats = null;
        questionStats = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public void addStudentStats(StudentStats studentStats) {
        this.studentStats.add(studentStats);
    }

    public List<StudentStats> getStudentStats() {
        return studentStats;
    }

    public List<QuizStats> getQuizStats() {
        return quizStats;
    }

    public void addQuizStats(QuizStats quizStats) {
        this.quizStats.add(quizStats);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update() {
        for (QuizStats quizStats : quizStats) {
            quizStats.update();
        }
        for (StudentStats studentStats : studentStats) {
            studentStats.update();
        }
        for (QuestionStats questionStat : questionStats) {
            questionStat.update();
        }
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

}
