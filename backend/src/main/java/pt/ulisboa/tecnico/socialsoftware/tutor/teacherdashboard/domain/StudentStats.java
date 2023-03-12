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

    private int numAtLeast3Quizzes;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    public StudentStats() {}

    public StudentStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setTeacherDashboard(teacherDashboard);
        this.courseExecution = courseExecution;
        this.numAtLeast3Quizzes = 0;
        this.numMore75CorrectQuestions = 0;
        this.numStudents = 0;
    }

    public void remove() {
        teacherDashboard.getStudentStats().remove(this);
        teacherDashboard = null;
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
        this.teacherDashboard.addStudentStats(this);
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

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public int getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void update() {
        setNumStudents(courseExecution.getNumberOfActiveStudents());
        
        setNumMore75CorrectQuestions((int) courseExecution.getStudents()
                .stream()
                .filter(student -> student.getCourseExecutionDashboard(courseExecution).getNumberOfStudentAnswers() > 0 &&
                (100 * student.getCourseExecutionDashboard(courseExecution).getNumberOfCorrectStudentAnswers()) / 
                student.getCourseExecutionDashboard(courseExecution).getNumberOfStudentAnswers() >= 75)
                .count());
    
        setNumAtLeast3Quizzes((int) courseExecution.getStudents().stream()
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
                ", numAtLeast3Quizzes" + getNumAtLeast3Quizzes() +
                '}';
    }
}