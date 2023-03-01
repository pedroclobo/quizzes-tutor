package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;

@Entity
public class StudentStats implements DomainEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private int numStudents;

    private int numMore75CorrectQuestions;

    private int numAtleast3Quizzes;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    public StudentStats() {}

    public StudentStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        this.teacherDashboard = teacherDashboard;
        this.courseExecution = courseExecution;
        this.numAtleast3Quizzes = 0;
        this.numMore75CorrectQuestions = 0;
        this.numStudents = 0;
    }

    public Integer getId() {
        return id;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void setNumMore75CorrectQuestions(int newNumMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = newNumMore75CorrectQuestions;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumAtleast3Quizzes(int numAtleast3Quizzes) {
        this.numAtleast3Quizzes = numAtleast3Quizzes;
    }

    public int getNumAtleast3Quizzes() {
        return numAtleast3Quizzes;
    }

    public void update() {
        setNumStudents(courseExecution.getNumberOfActiveStudents());
        
        setNumMore75CorrectQuestions((int) courseExecution.getStudents()
                .stream()
                .filter(student -> student.getCourseExecutionDashboard(courseExecution).getNumberOfStudentAnswers() > 0 &&
                (100 * student.getCourseExecutionDashboard(courseExecution).getNumberOfCorrectStudentAnswers()) / 
                student.getCourseExecutionDashboard(courseExecution).getNumberOfStudentAnswers() >= 75)
                .count());
    
        setNumAtleast3Quizzes((int) courseExecution.getStudents().stream()
            .filter(student -> student.getCourseExecutionDashboard(courseExecution).getNumberOfStudentQuizzes() >= 3)
            .count());
    }

    public void accept(Visitor visitor) {
         // Only used for XML generation
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + getId() +
                ", courseExecution=" + getCourseExecution() +
                ", teacherDashboard=" + getTeacherDashboard() +
                ", numStudents=" + getNumStudents() +
                ", numMore75CorrectQuestions " + getNumMore75CorrectQuestions() +
                ", numAtleast3Quizzes" + getNumAtleast3Quizzes() +
                '}';
    }
}