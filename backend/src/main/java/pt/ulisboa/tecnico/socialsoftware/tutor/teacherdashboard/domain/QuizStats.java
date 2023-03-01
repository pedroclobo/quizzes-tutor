package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
public class QuizStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numQuizzes;
    private int uniqueQuizzesSolved;
    private float averageQuizzesSolved;

    public QuizStats() {
    }

    public QuizStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public void remove() {
        teacherDashboard.getQuizStats().remove(this);
        teacherDashboard = null;
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

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addQuizStats(this);
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getUniqueQuizzesSolved() {
        return uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void updateNumQuizzes() {
        setNumQuizzes(courseExecution.getNumberOfQuizzes());
    }

    public void updateUniqueQuizzesSolved() {
        setUniqueQuizzesSolved((int) courseExecution.getQuizzes()
                               .stream()
                               .filter(quiz -> !quiz.getQuizAnswers().isEmpty())
                               .count());
    }

    public void updateAverageQuizzesSolved() {
        int numAnswers = 0;
        Set<Student> students = courseExecution.getStudents();
        Set<Quiz> uniqueQuizzes = new HashSet<>();

        for (Student student : students) {
            Set<QuizAnswer> quizAnswers = student.getQuizAnswers();
            for (QuizAnswer quizAnswer : quizAnswers) {
                Quiz quiz = quizAnswer.getQuiz();
                if (!uniqueQuizzes.contains(quiz)) {
                    uniqueQuizzes.add(quiz);
                }
            }
            numAnswers += uniqueQuizzes.size();
            uniqueQuizzes.clear();
        }

        int numStudents = students.size();

        if (numStudents == 0) {
            setAverageQuizzesSolved(0);
        } else {
            setAverageQuizzesSolved((float) numAnswers / numStudents);
        }
    }

    public void update() {
        updateNumQuizzes();
        updateUniqueQuizzesSolved();
		updateAverageQuizzesSolved();
    }

    @Override
    public String toString() {
        return "QuizStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numQuizzes=" + numQuizzes +
                ", uniqueQuizzesSolved=" + uniqueQuizzesSolved +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                '}';
    }

}
