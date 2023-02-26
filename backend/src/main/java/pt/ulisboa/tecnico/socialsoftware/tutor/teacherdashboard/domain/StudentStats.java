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

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    public StudentStats(){}

    public StudentStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution){
        this.teacherDashboard = teacherDashboard;
        this.courseExecution = courseExecution;
    }

    public Integer getId(){
        return id;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents){
        this.numStudents = numStudents;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard){
        this.teacherDashboard = teacherDashboard;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution){
        this.courseExecution = courseExecution;
    }

    public void setNumMore75CorrectQuestions(int newNumMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = newNumMore75CorrectQuestions;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }


    public void update() {
        setNumStudents(courseExecution.getStudents().size());
        setNumMore75CorrectQuestions((int) courseExecution.getStudents()
                .stream()
                .filter(student -> student.studentDashboard.getNumberOfCorrectStudentAnswers() / student.studentDashboard.getNumberOfStudentAnswers() >= 75)
                .count());
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numStudents=" + numStudents +
                '}';
    }
}