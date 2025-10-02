package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.TimetableRequest;
import com.vijay.User_Master.dto.TimetableResponse;
import com.vijay.User_Master.dto.TimetableStatistics;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.entity.Subject;
import com.vijay.User_Master.entity.Timetable;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.repository.SubjectRepository;
import com.vijay.User_Master.repository.TimetableRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.TimetableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Timetable management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final WorkerRepository workerRepository;

    @Override
    public TimetableResponse createTimetable(TimetableRequest request, Long ownerId) {
        log.info("Creating timetable entry for class: {} and teacher: {} on {}", 
                request.getClassId(), request.getTeacherId(), request.getDayOfWeek());
        
        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Validate subject exists and belongs to owner
        Subject subject = subjectRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getSubjectId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Subject not found or does not belong to owner"));
        
        // Validate class exists and belongs to owner
        SchoolClass schoolClass = schoolClassRepository.findByIdAndOwner_IdAndIsDeletedFalse(request.getClassId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Class not found or does not belong to owner"));
        
        // Validate teacher exists and belongs to owner
        Worker teacher = workerRepository.findById(request.getTeacherId())
                .filter(w -> w.getOwner() != null && w.getOwner().getId().equals(ownerId) && !w.isDeleted())
                .orElseThrow(() -> new RuntimeException("Teacher not found or does not belong to owner"));
        
        // Check for time conflicts
        List<Timetable> conflicts = timetableRepository.findOwnerTeacherTimeConflicts(
                ownerId, request.getTeacherId(), request.getDayOfWeek(), 
                request.getStartTime(), request.getEndTime());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time conflict detected for teacher at " + 
                    request.getStartTime() + " - " + request.getEndTime() + " on " + request.getDayOfWeek());
        }
        
        Timetable timetable = Timetable.builder()
                .schoolClass(schoolClass)
                .subject(subject)
                .teacher(teacher)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .periodNumber(request.getPeriodNumber())
                .roomNumber(request.getRoomNumber())
                .periodType(request.getPeriodType())
                .academicYear(request.getAcademicYear())
                .semester(request.getSemester())
                .status(request.getStatus())
                .notes(request.getNotes())
                .isRecurring(request.isRecurring())
                .owner(owner)
                .isDeleted(false)
                .build();
        
        Timetable savedTimetable = timetableRepository.save(timetable);
        log.info("Timetable entry created successfully with ID: {}", savedTimetable.getId());
        
        return convertToResponse(savedTimetable);
    }

    @Override
    public TimetableResponse updateTimetable(Long id, TimetableRequest request, Long ownerId) {
        log.info("Updating timetable entry: {} for owner: {}", id, ownerId);
        
        Timetable timetable = timetableRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Timetable entry not found"));
        
        // Update fields
        timetable.setDayOfWeek(request.getDayOfWeek());
        timetable.setStartTime(request.getStartTime());
        timetable.setEndTime(request.getEndTime());
        timetable.setPeriodNumber(request.getPeriodNumber());
        timetable.setRoomNumber(request.getRoomNumber());
        timetable.setPeriodType(request.getPeriodType());
        timetable.setAcademicYear(request.getAcademicYear());
        timetable.setSemester(request.getSemester());
        timetable.setStatus(request.getStatus());
        timetable.setNotes(request.getNotes());
        timetable.setRecurring(request.isRecurring());
        
        Timetable updatedTimetable = timetableRepository.save(timetable);
        log.info("Timetable entry updated successfully");
        
        return convertToResponse(updatedTimetable);
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableResponse getTimetableById(Long id, Long ownerId) {
        log.info("Getting timetable entry: {} for owner: {}", id, ownerId);
        
        Timetable timetable = timetableRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Timetable entry not found"));
        
        return convertToResponse(timetable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableResponse> getAllTimetables(Long ownerId, Pageable pageable) {
        log.info("Getting all timetable entries for owner: {}", ownerId);
        
        Page<Timetable> timetables = timetableRepository.findByOwner_IdAndIsDeletedFalse(ownerId, pageable);
        return timetables.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByClass(Long classId, Long ownerId) {
        log.info("Getting timetable entries for class: {} and owner: {}", classId, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(ownerId, classId);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByClassAndDay(Long classId, Timetable.DayOfWeek dayOfWeek, Long ownerId) {
        log.info("Getting timetable entries for class: {} and day: {} for owner: {}", classId, dayOfWeek, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(ownerId, classId);
        return timetables.stream()
                .filter(t -> t.getDayOfWeek() == dayOfWeek)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByTeacher(Long teacherId, Long ownerId) {
        log.info("Getting timetable entries for teacher: {} and owner: {}", teacherId, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndTeacher_IdAndIsDeletedFalse(ownerId, teacherId);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByTeacherAndDay(Long teacherId, Timetable.DayOfWeek dayOfWeek, Long ownerId) {
        log.info("Getting timetable entries for teacher: {} and day: {} for owner: {}", teacherId, dayOfWeek, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndTeacher_IdAndIsDeletedFalse(ownerId, teacherId);
        return timetables.stream()
                .filter(t -> t.getDayOfWeek() == dayOfWeek)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesBySubject(Long subjectId, Long ownerId) {
        log.info("Getting timetable entries for subject: {} and owner: {}", subjectId, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndSubject_IdAndIsDeletedFalse(ownerId, subjectId);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByDay(Timetable.DayOfWeek dayOfWeek, Long ownerId) {
        log.info("Getting timetable entries for day: {} and owner: {}", dayOfWeek, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndDayOfWeekAndIsDeletedFalse(ownerId, dayOfWeek);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableResponse> getTimetablesByStatus(Timetable.TimetableStatus status, Long ownerId, Pageable pageable) {
        log.info("Getting timetable entries with status: {} for owner: {}", status, ownerId);
        
        Page<Timetable> timetables = timetableRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, status, pageable);
        return timetables.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByAcademicYearAndSemester(String academicYear, String semester, Long ownerId) {
        log.info("Getting timetable entries for academic year: {} and semester: {} for owner: {}", academicYear, semester, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByAcademicYearAndSemesterAndIsDeletedFalse(academicYear, semester);
        return timetables.stream()
                .filter(t -> t.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> checkTeacherTimeConflicts(Long teacherId, Timetable.DayOfWeek dayOfWeek, 
                                                           LocalTime startTime, LocalTime endTime, Long ownerId) {
        log.info("Checking time conflicts for teacher: {} on {} at {}-{} for owner: {}", 
                teacherId, dayOfWeek, startTime, endTime, ownerId);
        
        List<Timetable> conflicts = timetableRepository.findOwnerTeacherTimeConflicts(
                ownerId, teacherId, dayOfWeek, startTime, endTime);
        return conflicts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetablesByRoom(String roomNumber, Long ownerId) {
        log.info("Getting timetable entries for room: {} and owner: {}", roomNumber, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByRoomNumberAndIsDeletedFalse(roomNumber);
        return timetables.stream()
                .filter(t -> t.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableResponse> searchTimetables(String keyword, Long ownerId, Pageable pageable) {
        log.info("Searching timetable entries with keyword: {} for owner: {}", keyword, ownerId);
        
        Page<Timetable> timetables = timetableRepository.searchByOwner(ownerId, keyword, pageable);
        return timetables.map(this::convertToResponse);
    }

    @Override
    public void deleteTimetable(Long id, Long ownerId) {
        log.info("Deleting timetable entry: {} for owner: {}", id, ownerId);
        
        Timetable timetable = timetableRepository.findByIdAndOwner_IdAndIsDeletedFalse(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Timetable entry not found"));
        
        timetable.setDeleted(true);
        timetableRepository.save(timetable);
        
        log.info("Timetable entry deleted successfully");
    }

    @Override
    public void restoreTimetable(Long id, Long ownerId) {
        log.info("Restoring timetable entry: {} for owner: {}", id, ownerId);
        
        Timetable timetable = timetableRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Timetable entry not found"));
        
        timetable.setDeleted(false);
        timetableRepository.save(timetable);
        
        log.info("Timetable entry restored successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableStatistics getTimetableStatistics(Long ownerId) {
        log.info("Getting timetable statistics for owner: {}", ownerId);
        
        long totalPeriods = timetableRepository.countByOwner_IdAndIsDeletedFalse(ownerId);
        
        // Count by status
        long activePeriods = timetableRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Timetable.TimetableStatus.ACTIVE, Pageable.unpaged()).getTotalElements();
        long inactivePeriods = timetableRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Timetable.TimetableStatus.INACTIVE, Pageable.unpaged()).getTotalElements();
        long temporaryPeriods = timetableRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Timetable.TimetableStatus.TEMPORARY, Pageable.unpaged()).getTotalElements();
        long cancelledPeriods = timetableRepository.findByOwner_IdAndStatusAndIsDeletedFalse(ownerId, Timetable.TimetableStatus.CANCELLED, Pageable.unpaged()).getTotalElements();
        
        // Count by period type
        List<Timetable> allTimetables = timetableRepository.findByOwner_IdAndIsDeletedFalse(ownerId, Pageable.unpaged()).getContent();
        long lecturePeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.LECTURE).count();
        long practicalPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.PRACTICAL).count();
        long labPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.LAB).count();
        long breakPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.BREAK).count();
        long lunchPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.LUNCH).count();
        long sportsPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.SPORTS).count();
        long assemblyPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.ASSEMBLY).count();
        long libraryPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.LIBRARY).count();
        long studyHallPeriods = allTimetables.stream().filter(t -> t.getPeriodType() == Timetable.PeriodType.STUDY_HALL).count();
        
        // Count by day
        long mondayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.MONDAY).count();
        long tuesdayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.TUESDAY).count();
        long wednesdayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.WEDNESDAY).count();
        long thursdayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.THURSDAY).count();
        long fridayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.FRIDAY).count();
        long saturdayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.SATURDAY).count();
        long sundayPeriods = allTimetables.stream().filter(t -> t.getDayOfWeek() == Timetable.DayOfWeek.SUNDAY).count();
        
        // Count recurring vs non-recurring
        long recurringPeriods = allTimetables.stream().filter(Timetable::isRecurring).count();
        long nonRecurringPeriods = allTimetables.stream().filter(t -> !t.isRecurring()).count();
        
        // Count unique entities
        long totalTeachersScheduled = allTimetables.stream().map(t -> t.getTeacher().getId()).distinct().count();
        long totalClassesScheduled = allTimetables.stream().map(t -> t.getSchoolClass().getId()).distinct().count();
        long totalSubjectsScheduled = allTimetables.stream().map(t -> t.getSubject().getId()).distinct().count();
        
        return TimetableStatistics.builder()
                .totalPeriods(totalPeriods)
                .activePeriods(activePeriods)
                .inactivePeriods(inactivePeriods)
                .temporaryPeriods(temporaryPeriods)
                .cancelledPeriods(cancelledPeriods)
                .lecturePeriods(lecturePeriods)
                .practicalPeriods(practicalPeriods)
                .labPeriods(labPeriods)
                .breakPeriods(breakPeriods)
                .lunchPeriods(lunchPeriods)
                .sportsPeriods(sportsPeriods)
                .assemblyPeriods(assemblyPeriods)
                .libraryPeriods(libraryPeriods)
                .studyHallPeriods(studyHallPeriods)
                .mondayPeriods(mondayPeriods)
                .tuesdayPeriods(tuesdayPeriods)
                .wednesdayPeriods(wednesdayPeriods)
                .thursdayPeriods(thursdayPeriods)
                .fridayPeriods(fridayPeriods)
                .saturdayPeriods(saturdayPeriods)
                .sundayPeriods(sundayPeriods)
                .recurringPeriods(recurringPeriods)
                .nonRecurringPeriods(nonRecurringPeriods)
                .totalTeachersScheduled(totalTeachersScheduled)
                .totalClassesScheduled(totalClassesScheduled)
                .totalSubjectsScheduled(totalSubjectsScheduled)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getWeeklyTimetableForClass(Long classId, Long ownerId) {
        log.info("Getting weekly timetable for class: {} and owner: {}", classId, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndSchoolClass_IdAndIsDeletedFalse(ownerId, classId);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getWeeklyTimetableForTeacher(Long teacherId, Long ownerId) {
        log.info("Getting weekly timetable for teacher: {} and owner: {}", teacherId, ownerId);
        
        List<Timetable> timetables = timetableRepository.findByOwner_IdAndTeacher_IdAndIsDeletedFalse(ownerId, teacherId);
        return timetables.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private TimetableResponse convertToResponse(Timetable timetable) {
        Duration duration = Duration.between(timetable.getStartTime(), timetable.getEndTime());
        long minutes = duration.toMinutes();
        String durationStr = String.format("%d:%02d", minutes / 60, minutes % 60);
        
        return TimetableResponse.builder()
                .id(timetable.getId())
                .classId(timetable.getSchoolClass().getId())
                .className(timetable.getSchoolClass().getClassName())
                .subjectId(timetable.getSubject().getId())
                .subjectName(timetable.getSubject().getSubjectName())
                .teacherId(timetable.getTeacher().getId())
                .teacherName(timetable.getTeacher().getName())
                .dayOfWeek(timetable.getDayOfWeek())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .periodNumber(timetable.getPeriodNumber())
                .roomNumber(timetable.getRoomNumber())
                .periodType(timetable.getPeriodType())
                .academicYear(timetable.getAcademicYear())
                .semester(timetable.getSemester())
                .status(timetable.getStatus())
                .notes(timetable.getNotes())
                .isRecurring(timetable.isRecurring())
                .isDeleted(timetable.isDeleted())
                .createdOn(timetable.getCreatedOn())
                .updatedOn(timetable.getUpdatedOn())
                .dayDisplay(timetable.getDayOfWeek().name())
                .timeDisplay(timetable.getStartTime() + " - " + timetable.getEndTime())
                .periodTypeDisplay(timetable.getPeriodType().name().replace("_", " "))
                .statusDisplay(timetable.getStatus().name())
                .duration(durationStr)
                .ownerName(timetable.getOwner().getName())
                .build();
    }
}
