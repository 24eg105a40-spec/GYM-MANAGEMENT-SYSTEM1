package com.gymmanagement;

import com.gymmanagement.dto.*;
import com.gymmanagement.entity.*;
import com.gymmanagement.enums.MembershipStatus;
import com.gymmanagement.enums.Role;
import com.gymmanagement.enums.SessionStatus;
import com.gymmanagement.exception.BadRequestException;
import com.gymmanagement.exception.ConflictException;
import com.gymmanagement.repository.MembershipPlanRepository;
import com.gymmanagement.repository.UserRepository;
import com.gymmanagement.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class SmartGymApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipPlanService planService;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerAssignmentService assignmentService;

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private TrainingSessionService sessionService;

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Test
    @DisplayName("Context loads and initial admin exists")
    void contextLoads() {
        assertTrue(userRepository.existsByEmail("admin@gym.com"));
    }

    @Test
    @DisplayName("Member registration creates MEMBER role and prevents duplicate email")
    void testMemberRegistrationAndDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "John Doe", "john.doe@test.com", "9876543210", "Password@123");

        UserResponse response = authService.registerMember(request);
        assertNotNull(response.getId());
        assertEquals("John Doe", response.getFullName());
        assertEquals(Role.MEMBER, response.getRole());

        // Attempt duplicate registration
        assertThrows(ConflictException.class, () -> authService.registerMember(request));
    }

    @Test
    @DisplayName("Login with valid credentials returns JWT token")
    void testLoginSuccess() {
        RegisterRequest registerReq = new RegisterRequest(
                "Jane Smith", "jane.smith@test.com", "9876543211", "Password@123");
        authService.registerMember(registerReq);

        LoginRequest loginReq = new LoginRequest("jane.smith@test.com", "Password@123");
        AuthResponse authResponse = authService.login(loginReq);

        assertNotNull(authResponse.getToken());
        assertEquals("jane.smith@test.com", authResponse.getEmail());
        assertEquals(Role.MEMBER, authResponse.getRole());
    }

    @Test
    @DisplayName("Retrieve active membership plans")
    void testGetMembershipPlans() {
        List<MembershipPlanResponse> plans = planService.getAllPlans(true);
        assertFalse(plans.isEmpty());
        assertTrue(plans.stream().anyMatch(p -> p.getName().equalsIgnoreCase("Monthly")));
    }

    @Test
    @DisplayName("Attendance requires active membership and prevents duplicate daily check-in")
    void testAttendanceValidationAndDuplicatePrevention() {
        // 1. Create a member
        RegisterRequest reg = new RegisterRequest("Fitness Fan", "fan@test.com", "9876543212", "Pass@123");
        UserResponse member = authService.registerMember(reg);

        // Check-in without active membership should throw BadRequestException
        assertThrows(BadRequestException.class, () -> attendanceService.checkIn(member.getId()));

        // 2. Request and activate a membership
        MembershipPlan plan = planRepository.findAll().get(0);
        MembershipRequestDto memReq = new MembershipRequestDto(plan.getId(), LocalDate.now());
        MembershipResponseDto memResp = membershipService.createMembershipRequest(member.getId(), memReq);
        assertEquals(MembershipStatus.PENDING, memResp.getStatus());

        // Activate membership
        membershipService.activateMembership(memResp.getId());

        // 3. Now check-in should succeed
        AttendanceResponseDto attResp = attendanceService.checkIn(member.getId());
        assertNotNull(attResp.getId());
        assertEquals(LocalDate.now(), attResp.getAttendanceDate());

        // 4. Duplicate check-in on same day should throw ConflictException
        assertThrows(ConflictException.class, () -> attendanceService.checkIn(member.getId()));
    }

    @Test
    @DisplayName("Trainer assignment and workout plan authorization")
    void testTrainerAssignmentAndWorkoutPlan() {
        // 1. Register Member
        UserResponse member = authService.registerMember(
                new RegisterRequest("Workout Guy", "workout@test.com", "9876543213", "Pass@123"));

        // 2. Get seeded Trainer
        List<TrainerProfileResponse> trainers = trainerService.getAllTrainers(true);
        assertFalse(trainers.isEmpty());
        TrainerProfileResponse trainer = trainers.get(0);

        // 3. Admin assigns trainer to member
        TrainerAssignmentRequest assignReq = new TrainerAssignmentRequest();
        assignReq.setMemberId(member.getId());
        assignReq.setTrainerId(trainer.getId());
        assignReq.setNotes("Hypertrophy focus");
        TrainerAssignmentResponse assignment = assignmentService.assignTrainer(assignReq);
        assertTrue(assignment.isActive());

        // 4. Assigned trainer creates workout plan
        WorkoutPlanRequest planReq = new WorkoutPlanRequest();
        planReq.setMemberId(member.getId());
        planReq.setPlanName("4-Day Upper Lower Split");
        planReq.setDescription("Focus on compound lifts");
        planReq.setStartDate(LocalDate.now());

        WorkoutExerciseRequest ex1 = new WorkoutExerciseRequest();
        ex1.setExerciseName("Barbell Bench Press");
        ex1.setTargetMuscleGroup("Chest");
        ex1.setSets(4);
        ex1.setRepetitions(8);
        ex1.setRestSeconds(90);
        ex1.setExerciseOrder(1);
        planReq.setExercises(List.of(ex1));

        WorkoutPlanResponse createdPlan = workoutPlanService.createWorkoutPlan(trainer.getUserId(), planReq);
        assertNotNull(createdPlan.getId());
        assertEquals(1, createdPlan.getExercises().size());
    }

    @Test
    @DisplayName("Training session booking past date prevention and slot creation")
    void testTrainingSessionBookingValidation() {
        UserResponse member = authService.registerMember(
                new RegisterRequest("Session Member", "sess@test.com", "9876543214", "Pass@123"));

        List<TrainerProfileResponse> trainers = trainerService.getAllTrainers(true);
        TrainerProfileResponse trainer = trainers.get(0);

        // Attempt booking in the past should throw BadRequestException
        TrainingSessionBookingRequest pastReq = new TrainingSessionBookingRequest();
        pastReq.setTrainerId(trainer.getId());
        pastReq.setSessionDate(LocalDate.now().minusDays(1));
        pastReq.setStartTime(LocalTime.of(10, 0));
        pastReq.setEndTime(LocalTime.of(11, 0));
        assertThrows(BadRequestException.class, () -> sessionService.bookSession(member.getId(), pastReq));

        // Booking valid future session
        TrainingSessionBookingRequest validReq = new TrainingSessionBookingRequest();
        validReq.setTrainerId(trainer.getId());
        validReq.setSessionDate(LocalDate.now().plusDays(2));
        validReq.setStartTime(LocalTime.of(14, 0));
        validReq.setEndTime(LocalTime.of(15, 0));
        validReq.setPurpose("Form check");

        TrainingSessionResponse session = sessionService.bookSession(member.getId(), validReq);
        assertNotNull(session.getId());
        assertEquals(SessionStatus.PENDING, session.getStatus());
    }

    @Test
    @DisplayName("Admin dashboard stats return real non-null figures")
    void testAdminDashboardStats() {
        AdminDashboardStatsDto stats = adminDashboardService.getAdminDashboardStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalPlans() > 0);
        assertTrue(stats.getTotalActiveTrainers() > 0);
        assertNotNull(stats.getTotalVerifiedRevenue());
    }
}
