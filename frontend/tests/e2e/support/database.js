function dbPasswordCommand(password) {
  if (Cypress.platform === 'win32') {
    return `set PGPASSWORD=${password}&& `;
  } else {
    return `PGPASSWORD=${password} `;
  }
}

function dbCommand(command) {
  return cy.exec(
    dbPasswordCommand(Cypress.env('psql_db_password')) +
      `psql -d ${Cypress.env('psql_db_name')} ` +
      `-U ${Cypress.env('psql_db_username')} ` +
      `-h ${Cypress.env('psql_db_host')} ` +
      `-p ${Cypress.env('psql_db_port')} ` +
      `-c "${command.replace(/\r?\n/g, ' ')}"`
  );
}

Cypress.Commands.add('beforeEachTournament', () => {
  dbCommand(`
      WITH tmpCourse as (SELECT ce.course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')      
        ,insert1 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (1, 0, 'AVAILABLE', 'test1', (select course_execution_id from tmpCourse)))
        ,insert2 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (2, 0, 'AVAILABLE', 'test2', (select course_execution_id from tmpCourse)))
        ,insert3 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (100, 1))
        ,insert4 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (101, 2))
        ,insert5 as (INSERT INTO topics (id, name, course_id) VALUES (82, 'Software Architecture', (select course_id from tmpCourse)))
        ,insert6 as (INSERT INTO topics (id, name, course_id) VALUES (83, 'Web Application', (select course_id from tmpCourse)))
        ,insert7 as (INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (82, 100))
        ,insert8 as (INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (83, 101))
        ,insert9 as (INSERT INTO questions (id, title, content, status, course_id, creation_date) VALUES (1389, 'test', 'Question?', 'AVAILABLE', (select course_id from tmpCourse), current_timestamp))
        ,insert10 as (INSERT INTO question_details (id, question_type, question_id) VALUES (1000, 'multiple_choice', 1389))
        INSERT INTO topics_questions (topics_id, questions_id) VALUES (82, 1389);
    `);

  for (let content in [0, 1, 2, 3]) {
    let correct = content === '0' ? 't' : 'f';
    dbCommand(`
      INSERT INTO options(content, correct, question_details_id, sequence) VALUES ('${content}', '${correct}', 1000, ${content});
      `);
  }
});

Cypress.Commands.add('cleanTestTopics', () => {
  dbCommand(`
        DELETE FROM topics
        WHERE name like 'CY%'
    `);
});

Cypress.Commands.add('cleanTestCourses', () => {
  dbCommand(`
    delete from users_course_executions where course_executions_id in (select id from course_executions where acronym like 'TEST-%');
    delete from course_executions where acronym like 'TEST-%';
    `);
});

Cypress.Commands.add('updateTournamentStartTime', () => {
  dbCommand(`
        UPDATE tournaments SET start_time = '2020-07-16 07:57:00';
    `);
});

Cypress.Commands.add('afterEachTournament', () => {
  dbCommand(`
         DELETE FROM tournaments_topics WHERE topics_id = 82;
         DELETE FROM tournaments_topics WHERE topics_id = 83;
         DELETE FROM topics_topic_conjunctions WHERE topics_id = 82;
         DELETE FROM topics_topic_conjunctions WHERE topics_id = 83;
         DELETE FROM topic_conjunctions WHERE id = 100;
         DELETE FROM topic_conjunctions WHERE id = 101;
         DELETE FROM assessments WHERE id = 1;
         DELETE FROM assessments WHERE id = 2;
         DELETE FROM topics_questions WHERE questions_id = 1389;
         DELETE FROM topics WHERE id = 82;
         DELETE FROM topics WHERE id = 83;
         DELETE FROM question_answers USING quiz_questions WHERE quiz_questions.id = question_answers.quiz_question_id AND quiz_questions.question_id = 1389;
         DELETE FROM quiz_questions WHERE question_id = 1389;
         DELETE FROM options WHERE question_details_id = 1000;
         DELETE FROM question_details WHERE id = 1000;
         DELETE FROM questions WHERE id = 1389;
         DELETE FROM tournaments_participants;
         DELETE FROM tournaments; 
         ALTER SEQUENCE tournaments_id_seq RESTART WITH 1;
         UPDATE tournaments SET id=nextval('tournaments_id_seq');
    `);
});

Cypress.Commands.add('addQuestionSubmission', (title, submissionStatus) => {
  dbCommand(`
    WITH course as (SELECT ce.course_id as course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')     
    , quest AS (
      INSERT INTO questions (title, content, status, course_id, creation_date) 
      VALUES ('${title}', 'Question?', 'SUBMITTED', (select course_id from course), current_timestamp) RETURNING id
      )
    INSERT INTO question_submissions (status, question_id, submitter_id, course_execution_id) 
    VALUES ('${submissionStatus}', (SELECT id from quest), (select id from users where name = 'Demo Student'), (select course_execution_id from course));`);

  //add options
  for (let content in [0, 1, 2, 3]) {
    let correct = content === '0' ? 't' : 'f';
    dbCommand(
      `WITH quest AS (SELECT id FROM questions WHERE title='${title}' limit 1),
      quest_details as (INSERT INTO question_details (question_type, question_id) VALUES ('multiple_choice', (SELECT id FROM quest)) RETURNING id)
      INSERT INTO options(content, correct, question_details_id, sequence) 
      VALUES ('${content}', '${correct}', (SELECT id FROM quest_details), ${content});`
    );
  }
});

Cypress.Commands.add('removeQuestionSubmission', (hasReviews = false) => {
  if (hasReviews) {
    dbCommand(`WITH rev AS (DELETE FROM reviews WHERE id IN (SELECT max(id) FROM reviews) RETURNING question_submission_id)
                      , sub AS (DELETE FROM question_submissions WHERE id IN (SELECT * FROM rev) RETURNING question_id) 
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                        DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
  } else {
    dbCommand(`WITH sub AS (DELETE FROM question_submissions WHERE id IN (SELECT max(id) FROM question_submissions) RETURNING question_id)
                      , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM sub JOIN question_details qd on qd.question_id = sub.question_id)) 
                      , det AS (DELETE FROM question_details WHERE question_id in (SELECT * FROM sub))
                    DELETE FROM questions WHERE id IN (SELECT * FROM sub);`);
  }
});

Cypress.Commands.add('cleanMultipleChoiceQuestionsByName', (questionName) => {
  dbCommand(`WITH toDelete AS (SELECT qt.id as question_id FROM questions qt JOIN question_details qd ON qd.question_id = qt.id and qd.question_type='multiple_choice' where title like '%${questionName}%')
                  , opt AS (DELETE FROM options WHERE question_details_id IN (SELECT qd.id FROM toDelete JOIN question_details qd on qd.question_id = toDelete.question_id)) 
                  , det AS (DELETE FROM question_details WHERE question_id in (SELECT question_id FROM toDelete))
                DELETE FROM questions WHERE id IN (SELECT question_id FROM toDelete);`);
});

Cypress.Commands.add('cleanCodeFillInQuestionsByName', (questionName) => {
  dbCommand(`WITH toDelete AS (SELECT qt.id as question_id FROM questions qt JOIN question_details qd ON qd.question_id = qt.id and qd.question_type='code_fill_in' where title like '%${questionName}%')
                , fillToDelete AS (SELECT id FROM  code_fill_in_spot WHERE question_details_id IN (SELECT qd.id FROM toDelete JOIN question_details qd on qd.question_id = toDelete.question_id))
                , opt AS (DELETE FROM  code_fill_in_options WHERE code_fill_in_id IN (SELECT id FROM fillToDelete))
                , fill AS (DELETE FROM  code_fill_in_spot WHERE id IN (SELECT id FROM fillToDelete)) 
                , det AS (DELETE FROM question_details WHERE question_id in (SELECT question_id FROM toDelete))
              DELETE FROM questions WHERE id IN (SELECT question_id FROM toDelete);`);
});

Cypress.Commands.add('createWeeklyScore', () => {
  dbCommand(`WITH courseExecutionId as (SELECT ce.id as course_execution_id FROM course_executions ce WHERE acronym = 'DemoCourse')
        , demoStudentId as (SELECT u.id as users_id FROM users u WHERE name = 'Demo Student')
        , dashboardId as (SELECT d.id as student_dashboard_id FROM student_dashboard d WHERE student_id = (select users_id from demoStudentId) AND course_execution_id = (select course_execution_id from courseExecutionId))
       INSERT INTO weekly_score(closed, quizzes_answered, questions_answered, questions_uniquely_answered, percentage_correct, improved_correct_answers, week, student_dashboard_id) VALUES (true, 3, 10, 50, 9, 8, '2022-02-02', (select student_dashboard_id from dashboardId))
      `);
});

Cypress.Commands.add('deleteWeeklyScores', () => {
  dbCommand(`
         UPDATE student_dashboard SET last_check_weekly_scores = NULL;
         DELETE FROM weekly_score;
    `);
});

Cypress.Commands.add('deleteFailedAnswers', () => {
  dbCommand(`
         UPDATE student_dashboard SET last_check_failed_answers = NULL;
         DELETE FROM failed_answer;
    `);
});

Cypress.Commands.add('addTopicAndAssessment', () => {
  dbCommand(`
      WITH tmpCourse as (SELECT ce.course_id, ce.id as course_execution_id FROM courses c JOIN course_executions ce on ce.course_id = c.id WHERE name = 'Demo Course')      
        ,insert1 as (INSERT INTO assessments (id, sequence, status, title, course_execution_id) VALUES (1, 0, 'AVAILABLE', 'assessment one', (select course_execution_id from tmpCourse)))
        ,insert2 as (INSERT INTO topic_conjunctions (id, assessment_id) VALUES (100, 1))
        ,insert3 as (INSERT INTO topics (id, name, course_id) VALUES (82, 'Software Architecture', (select course_id from tmpCourse)))
        INSERT INTO topics_topic_conjunctions (topics_id, topic_conjunctions_id) VALUES (82, 100);
    `);
});

Cypress.Commands.add('deleteDifficultQuestions', () => {
  dbCommand(`
         DELETE FROM difficult_question;
    `);
});

Cypress.Commands.add('deleteQuestionsAndAnswers', () => {
  dbCommand(`
         DELETE FROM replies;
         DELETE FROM discussions;
         DELETE FROM answer_details;
         DELETE FROM question_answers;
         DELETE FROM quiz_answers;
         DELETE FROM quiz_questions;
         DELETE FROM quizzes;
         DELETE FROM topics_topic_conjunctions;
         DELETE FROM topic_conjunctions;
         DELETE FROM topics_questions;
         DELETE FROM assessments;
         DELETE FROM options;
         DELETE FROM question_details;
         DELETE FROM questions;
         DELETE FROM topics;
    `);
});

const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

Cypress.Commands.add('getDemoCourseExecutionId', () => {
  cy.task('queryDatabase', {
    query: "SELECT id FROM course_executions WHERE acronym = 'DemoCourse'",
    credentials: credentials,
  });
});

Cypress.Commands.add('createCourseExecutionOnDemoCourse', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO course_executions (id, academic_term, acronym, end_date, status, type, course_id)
              SELECT id+1, '${academicTerm}', acronym, end_date, status, type, course_id
              FROM course_executions
              WHERE acronym = 'DemoCourse'
              AND id=(SELECT max(id) FROM course_executions)`,
    credentials: credentials,
  });
});

Cypress.Commands.add('addCourseExecutionToDemoTeacher', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO users_course_executions (users_id, course_executions_id)
              SELECT u.id, ce.id
              FROM users u, course_executions ce
              WHERE u.name = 'Demo Teacher'
              AND ce.academic_term = '${academicTerm}'`,
    credentials: credentials,
  });
});

Cypress.Commands.add('createAndAddCourseExecutionToDemoTeacher', (academicTerm) => {
  cy.createCourseExecutionOnDemoCourse(academicTerm);
  cy.addCourseExecutionToDemoTeacher(academicTerm);
});

Cypress.Commands.add('removeCourseExecutionFromDemoCourse', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM course_executions
            WHERE academic_term = '${academicTerm}'`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeCourseExecutionFromDemoTeacher', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM users_course_executions
              WHERE course_executions_id = (
                SELECT id
                FROM course_executions
                WHERE academic_term = '${academicTerm}'
              )
              AND users_id = (
                SELECT id
                FROM users
                WHERE name = 'Demo Teacher'
              )`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeCourseExecution', (academicTerm) => {
  cy.removeCourseExecutionFromDemoTeacher(academicTerm);
  cy.removeCourseExecutionFromDemoCourse(academicTerm);
});

Cypress.Commands.add('removeTeacherDashboardFromDemoTeacher', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM teacher_dashboard
              WHERE course_execution_id = (
                SELECT id
                FROM course_executions
                WHERE academic_term = '${academicTerm}'
              )
              AND teacher_id = (
                SELECT id
                FROM users
                WHERE name = 'Demo Teacher'
              )`,
    credentials: credentials,
  });
});

Cypress.Commands.add('createAndAddQuizToCourseExecution', (quizTitle, academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO quizzes (id, title, type, course_execution_id)
              SELECT COALESCE(MAX(id), 0) + 1, '${quizTitle}', 'IN_CLASS', (
                SELECT id
                FROM course_executions
                WHERE academic_term = '${academicTerm}'
              )
              FROM quizzes`,
    credentials: credentials,
  });
});

Cypress.Commands.add('deleteQuizzesFromCourseExecution', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM quizzes
              WHERE course_execution_id = (
               SELECT id FROM course_executions
               WHERE academic_term = '${academicTerm}'
		      )`,
    credentials: credentials,
  });
});

Cypress.Commands.add('createStudent', (studentName) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO users (user_type, id, admin, creation_date, name, role)
              SELECT 'student', COALESCE(MAX(id), 0) + 1, false, NOW(), '${studentName}', 'STUDENT'
              FROM users`,
    credentials: credentials,
  });
});

Cypress.Commands.add('addStudentToCourseExecution', (academicTerm) => {
  cy.task('queryDatabase', {
    query: `INSERT into users_course_executions (users_id, course_executions_id)
            VALUES ((SELECT MAX(id) FROM users), (SELECT id FROM course_executions WHERE academic_term = '${academicTerm}'))`,
    credentials: credentials,
  });
});

Cypress.Commands.add('createAndAddStudentToCourseExecution', (studentName, academicTerm) => {
  cy.createStudent(studentName);
  cy.addStudentToCourseExecution(academicTerm);
});

Cypress.Commands.add('removeStudentFromCourseExecutions', (studentName) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM users_course_executions
              WHERE users_id = (SELECT id FROM users WHERE name = '${studentName}')`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeStudentFromUsers', (studentName) => {
  cy.task('queryDatabase', {
    query: `DELETE FROM users WHERE id = (SELECT id FROM users WHERE name = '${studentName}')`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeStudent', (studentName) => {
  cy.removeStudentFromCourseExecutions(studentName);
  cy.removeStudentFromUsers(studentName);
});

Cypress.Commands.add('createAndAddQuestionToDemoCourse', () => {
  cy.task('queryDatabase', {
    query: `INSERT into questions (id, number_of_answers, number_of_correct, status, title, course_id)
              SELECT COALESCE(MAX(id), 0) + 1, 5, 4, 'AVAILABLE', CONCAT('Question ', MAX(id) + 1), (SELECT id FROM courses WHERE name = 'Demo Course' AND type = 'TECNICO')
              FROM questions`,
    credentials: credentials,
  });
});

Cypress.Commands.add('addQuestionToQuiz', (quizTitle) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO quiz_questions (id, question_id, quiz_id)
              SELECT COALESCE(MAX(id), 0) + 1, (SELECT MAX(id) FROM questions), (SELECT id FROM quizzes WHERE title='${quizTitle}')
              FROM quiz_questions`,
    credentials: credentials,
  });
});

Cypress.Commands.add('createAndAddQuestionToQuiz', (quizTitle) => {
  cy.createAndAddQuestionToDemoCourse();
  cy.addQuestionToQuiz(quizTitle);
});

Cypress.Commands.add('removeQuestionsFromQuizzes', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM quiz_questions",
    credentials: credentials,
  });
});

Cypress.Commands.add('removeQuestions', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM questions",
    credentials: credentials,
  });
});

Cypress.Commands.add('removeAllQuestions', () => {
  cy.removeQuestionsFromQuizzes();
  cy.removeQuestions();
});

Cypress.Commands.add('addQuestionAnswer', (quizAnswerId, quizQuestionId) => {
  cy.task('queryDatabase', {
    query: `INSERT INTO question_answers (id, quiz_answer_id, quiz_question_id)
              SELECT COALESCE(MAX(id), 0) + 1, ${quizAnswerId}, ${quizQuestionId}
              FROM question_answers`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeAllQuestionAnswers', (quizAnswerId, quizQuestionId) => {
  cy.task('queryDatabase', {
    query: "DELETE FROM question_answers",
    credentials: credentials,
  });
});

Cypress.Commands.add('createAndAddQuizAnswer', (quizTitle, studentName) => {
  cy.task('queryDatabase', {
    query: `INSERT into quiz_answers (id, completed, quiz_id, user_id)
              SELECT COALESCE(MAX(id), 0) + 1, true, (SELECT id FROM quizzes WHERE title='${quizTitle}'), (SELECT id FROM users WHERE name='${studentName}')
              FROM quiz_answers`,
    credentials: credentials,
  });
});

Cypress.Commands.add('removeAllQuizAnswers', (quizId, userId) => {
  cy.task('queryDatabase', {
    query: "DELETE FROM quiz_answers",
    credentials: credentials,
  });
});
