package com.polyparrot.teacherservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polyparrot.teacherservice.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
	
	List<Teacher> findByTeachingLanguages_Name(String name);
	
	List<Teacher> findBySpokenLanguages_Name(String name);
	
	@Query("""
		    SELECT DISTINCT t FROM Teacher t
		    JOIN t.teachingLanguages tl
		    JOIN t.spokenLanguages sl
		    WHERE (:minPrice IS NULL OR t.pricePerHour >= :minPrice)
		    AND (:maxPrice IS NULL OR t.pricePerHour <= :maxPrice)
		    AND (CAST(:teachingLanguage AS string) IS NULL OR LOWER(tl.name) = LOWER(CAST(:teachingLanguage AS string)))
		    AND (CAST(:spokenLanguage AS string) IS NULL OR LOWER(sl.name) = LOWER(CAST(:spokenLanguage AS string)))
		    """)
		List<Teacher> searchTeachers(
		    @Param("minPrice") Double minPrice,
		    @Param("maxPrice") Double maxPrice,
		    @Param("teachingLanguage") String teachingLanguage,
		    @Param("spokenLanguage") String spokenLanguage
		);
}