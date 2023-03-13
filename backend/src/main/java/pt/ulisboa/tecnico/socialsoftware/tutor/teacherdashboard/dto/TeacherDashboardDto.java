package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.*;
import java.util.List;


public class TeacherDashboardDto {

    private Integer id;
    private Integer numberOfStudents;
    private List<Integer> numOfStudents;
    private List<Integer> numMore75CorrectQuestions;
    private List<Integer> numAtLeast3Quizzes;
    private List<Integer> numQuestionsAvailable;
    private List<Integer> uniqueQuestionsAnswered;
    private List<Float> averageQuestionsAnswered;
    private List<Integer> numQuizzes;
    private List<Integer> uniqueQuizzesSolved;
    private List<Float> averageQuizzesSolved;
    
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        // Number of Students from StudentStats
        this.numOfStudents = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumStudents)
        .collect(Collectors.toList());
        // Number of students who got more than 75% of questions correct from StudentStats
        this.numMore75CorrectQuestions = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumMore75CorrectQuestions)
        .collect(Collectors.toList());
        // Number of students who did at least 3 quizzes from StudentStats
        this.numAtLeast3Quizzes = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumAtLeast3Quizzes)
        .collect(Collectors.toList());

        this.numQuestionsAvailable = teacherDashboard.getQuestionStats().stream()
        .map(QuestionStats::getNumAvailable)
        .collect(Collectors.toList());

        this.uniqueQuestionsAnswered = teacherDashboard.getQuestionStats().stream()
        .map(QuestionStats::getUniqueQuestionsAnswered)
        .collect(Collectors.toList());

        this.averageQuestionsAnswered = teacherDashboard.getQuestionStats().stream()
        .map(QuestionStats::getAverageQuestionsAnswered)
        .collect(Collectors.toList());

        this.numQuizzes = teacherDashboard.getQuizStats().stream()
        .map(QuizStats::getNumQuizzes)
        .collect(Collectors.toList());

        this.uniqueQuizzesSolved = teacherDashboard.getQuizStats().stream()
        .map(QuizStats::getUniqueQuizzesSolved)
        .collect(Collectors.toList());

        this.averageQuizzesSolved = teacherDashboard.getQuizStats().stream()
        .map(QuizStats::getAverageQuizzesSolved)
        .collect(Collectors.toList());

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public List<Integer> getNumOfStudents() {
        return numOfStudents;
    }

    public void setNumOfStudents(List<Integer> numOfStudents) {
        this.numOfStudents = numOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public void setNumMore75CorrectQuestions(List<Integer> newNumMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = newNumMore75CorrectQuestions;
    }

    public List<Integer> getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumAtLeast3Quizzes(List<Integer> numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public List<Integer> getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public List<Integer> getNumAQuestionsAvailable() {
        return numQuestionsAvailable;
    }

    public void setNumAvailableQuestions(List<Integer> numQuestionsAvailable) {
        this.numQuestionsAvailable = numQuestionsAvailable;
    }

    public List<Integer> getUniqueQuestionsAnswered() {
        return uniqueQuestionsAnswered;
    }

    public void setUniqueQuestionsAnswered(List<Integer> uniqueQuestionsAnswered) {
        this.uniqueQuestionsAnswered = uniqueQuestionsAnswered;
    }

    public List<Float> getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(List<Float> averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public List<Integer> getNumQuizzes() {
        return numQuizzes;
    }

    public List<Integer> getUniqueQuizzesSolved() {
        return uniqueQuizzesSolved;
    }

    public List<Float> getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setNumQuizzes(List<Integer> numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public void setUniqueQuizzesSolved(List<Integer> uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public void setAverageQuizzesSolved(List<Float> averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                ", numOfStudents=" + this.getNumOfStudents() +
                ", numMore75CorrectQuestions=" + this.getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes=" + this.getNumAtLeast3Quizzes() +
                ", numQuestionsAvailable=" + this.getNumAQuestionsAvailable() +
                ", uniqueQuestionsAnswered=" + this.getUniqueQuestionsAnswered() +
                ", averageQuestionsAnswered=" + this.getAverageQuestionsAnswered() +
                ", numQuizzes=" + this.getNumQuizzes() +
                ", uniqueQuizzesSolved=" + this.getUniqueQuizzesSolved() +
                ", averageQuizzesSolved=" + this.getAverageQuizzesSolved() +
                "}";
    }
}
