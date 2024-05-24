package onTrackService;
import org.junit.jupiter.api.Test;
import onTrackService.OnTrackService.*;
import static org.junit.jupiter.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;


public class OnTrackTests {
	private OnTrackService.TaskService taskService;
	private OnTrackService.TutoringService tutoringService;
	private OnTrackService.StudyGroupService studyGroupService;
	private OnTrackService.ProgressReportService progressReportService;
	private OnTrackService.NotificationService notificationService;



	@BeforeEach
	public void setup() {
		taskService = new OnTrackService.TaskService();
		tutoringService = new OnTrackService.TutoringService();
		studyGroupService = new OnTrackService.StudyGroupService();
		progressReportService = new OnTrackService.ProgressReportService();
		notificationService = new OnTrackService.NotificationService();
		
	}
	
	@Test
	public void testStudentIdentity() {
	String studentId = "222469478";
	assertNotNull("Student ID is 222469478", studentId);
	}
	@Test
	public void testStudentName() {
	String studentName = "Siddharth";
	assertNotNull("Student name is Siddharth", studentName);
	}

	 // Task Collaboration Test Cases
	  
	@Test
    public void testCreateTask_ValidInput() {
        Task task = taskService.createTask("Task Title", "Task Description", "student1");
        assertNotNull(task);
        assertEquals("Task Title", task.getTitle());
        assertEquals("Task Description", task.getDescription());
        assertEquals("student1", task.getCreator());
    }

    @Test
    public void testCreateTask_EmptyTitleAndDescription() {
        Task task = taskService.createTask("", "", "student1");
        assertNotNull(task);
        assertEquals("", task.getTitle());
        assertEquals("", task.getDescription());
    }

    @Test
    public void testAddCollaborator_ValidInput() {
        Task task = taskService.createTask("Task Title", "Task Description", "student1");
        int taskId = task.getId();
        boolean success = taskService.addCollaborator(taskId, "student2");
        assertTrue(success);
        assertTrue(task.getCollaborators().contains("student2"));
    }

    @Test
    public void testAddCollaborator_InvalidTaskId() {
        boolean success = taskService.addCollaborator(-1, "student2");
        assertFalse(success);
    }

    @Test
    public void testSubmitTask_ValidInput() {
        Task task = taskService.createTask("Task Title", "Task Description", "student1");
        int taskId = task.getId();
        boolean success = taskService.submitTask(taskId, "student1", "Submission Content");
        assertTrue(success);
        assertEquals("Submission Content", task.getSubmission("student1"));
    }

    @Test
    public void testSubmitTask_InvalidTaskId() {
        boolean success = taskService.submitTask(-1, "student1", "Submission Content");
        assertFalse(success);
    }

    @Test
    public void testSendMessage_ValidInput() {
        Task task = taskService.createTask("Task Title", "Task Description", "student1");
        int taskId = task.getId();
        boolean success = taskService.sendMessage(taskId, "student1", "tutor1", "Hello");
        assertTrue(success);
        List<String> messages = task.getMessages();
        assertFalse(messages.isEmpty());
        assertEquals("student1 (to tutor1): Hello", messages.get(0));
    }

    @Test
    public void testSendMessage_InvalidTaskId() {
        boolean success = taskService.sendMessage(-1, "student1", "tutor1", "Hello");
        assertFalse(success);
    }

    @Test
    public void testGetTaskMessages_ValidInput() {
        Task task = taskService.createTask("Task Title", "Task Description", "student1");
        int taskId = task.getId();
        taskService.sendMessage(taskId, "student1", "tutor1", "Hello");
        taskService.sendMessage(taskId, "tutor1", "student1", "Hi");
        List<String> messages = taskService.getTaskMessages(taskId);
        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
    }

    @Test
    public void testGetTaskMessages_InvalidTaskId() {
        List<String> messages = taskService.getTaskMessages(-1);
        assertTrue(messages.isEmpty());
    }

    // Tutoring Service Tests
    @Test
    public void testScheduleSession_ValidInput() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sessionDate = sdf.parse("2024-05-10");
        Session session = tutoringService.scheduleSession("tutor1", "student1", sessionDate, "10:00 AM");
        assertNotNull(session);
        assertEquals("tutor1", session.getTutor());
        assertEquals("student1", session.getStudent());
    }

    // Study Group Service Tests
    @Test
    public void testCreateStudyGroup_ValidInput() {
        StudyGroup group = studyGroupService.createStudyGroup("Math Study Group", "student1");
        assertNotNull(group);
        assertEquals("Math Study Group", group.getGroupName());
        assertEquals("student1", group.getCreator());
        assertTrue(group.getMembers().contains("student1"));
    }

    @Test
    public void testJoinStudyGroup_ValidInput() {
        StudyGroup group = studyGroupService.createStudyGroup("Math Study Group", "student1");
        int groupId = group.getId();
        boolean success = studyGroupService.joinStudyGroup("student2", groupId);
        assertTrue(success);
        assertTrue(group.getMembers().contains("student2"));
    }

    @Test
    public void testJoinStudyGroup_InvalidGroupId() {
        boolean success = studyGroupService.joinStudyGroup("student2", -1);
        assertFalse(success);
    }

    // Progress Report Service Tests
    @Test
    public void testGenerateReport_ValidInput() {
        ProgressReport report = progressReportService.generateReport("student1");
        assertNotNull(report);
        assertEquals("student1", report.getStudent());
        assertEquals(90, report.getAverageScore());
        assertEquals(20, report.getTasksCompleted());
    }

    // Notification Service Tests
    @Test
    public void testNotifyStudentOnTaskUpdate_ValidInput() {
        notificationService.notifyStudentOnTaskUpdate("student1", 1, "New feedback available");
        List<String> notifications = notificationService.getNotifications("student1");
        assertFalse(notifications.isEmpty());
        assertEquals("Task 1: New feedback available", notifications.get(0));
    }

    @Test
    public void testNotifyTutorOnTaskSubmission_ValidInput() {
        notificationService.notifyTutorOnTaskSubmission("tutor1", 1, "student1");
        List<String> notifications = notificationService.getNotifications("tutor1");
        assertFalse(notifications.isEmpty());
        assertEquals("Task 1 submitted by student1", notifications.get(0));
    }
}




