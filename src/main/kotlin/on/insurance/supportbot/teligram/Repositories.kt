package uz.demo.demolesson

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import javax.persistence.EntityManager
import javax.transaction.Transactional

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun trash(id: Long): T
    fun trashList(ids: List<Long>): List<T>
    fun findByIdNotDeleted(id: Long): T?
    fun findAllNotDeleted(pageable: Pageable): Page<T>
    fun findAllNotDeleted(): List<T>
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {
    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    @Transactional
    override fun trash(id: Long) = save(findById(id).get().apply { deleted = true })
    override fun findAllNotDeleted(pageable: Pageable) = findAll(isNotDeletedSpecification, pageable)
    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun trashList(ids: List<Long>): List<T> = ids.map { trash(it) }
    override fun findByIdNotDeleted(id: Long): T? =
        findById(id).orElseGet { null }?.run { if (!this.deleted) this else null }
}

interface UniversityRepository : BaseRepository<University> {
    fun existsByName(name: String): Boolean

    @Query(
        """ select f.name as facultyName ,avg(m.mark) as  avgMark from mark m
 inner join student s on m.student_id = s.id
 inner join groups g on s.group_id = g.id
 inner join faculty f on f.id = g.faculty_id
 inner join university u on u.id = f.university_id
where u.id=:id and m.deleted=false and s.deleted=false and g.deleted=false and f.deleted=false and u.deleted=false
group by f.name
order by avg(m.mark) desc""", nativeQuery = true
    )
    fun facultyListWithAvgMark(id: Long): List<BestFaculty>

}

interface FacultyRepository : BaseRepository<Faculty> {
    fun existsByName(name: String): Boolean
}

interface StudentRepository : BaseRepository<Student> {

    @Query(
        """  select s.first_name as firstName, s.last_name as lastName,
         g.name as groupName,f.name as facultyName  from student s
  join groups g on g.id = s.group_id
  join  faculty  f on f.id= g.faculty_id
 where s.id =:studentId""", nativeQuery = true
    )
    fun studentInfo(studentId: Long): List<StudentInfo>

    @Query(
        """select sub.name as subjectName from student s
inner join groups g on s.group_id = g.id
inner join journal j on g.id = j.group_id
inner join journal_page jp on j.id = jp.journal_id
inner join subject sub on sub.id = jp.subject_id
where s.id=:studentId""", nativeQuery = true
    )
    fun subjectList(studentId: Long): List<StudentSubject>

}

interface GroupRepository : BaseRepository<Group> {
    @Query(
        value = """ select g.name as groupName ,count(s.id) as studentCount from groups g 
             inner join faculty f on  f.id =g.faculty_id 
                 inner join  student s on s.group_id=g.id 
                 where g.faculty_id=:facultyId
                group by g.name""", nativeQuery = true
    )
    fun countStudent(facultyId: Long): List<CountOfStudent>


}

interface JournalRepository : BaseRepository<Journal> {}
interface JournalPageRepository : BaseRepository<JournalPage>
interface SubjectRepository : BaseRepository<Subject> {
    fun existsByName(name: String): Boolean

}

interface MarkRepository : BaseRepository<Mark> {

    @Query(
        value = """select s.first_name as firstName,avg(m.mark) as avgMark from mark m
    inner join student s on m.student_id = s.id
    inner join groups g on s.group_id = g.id
    where group_id=:groupId
    group by s.first_name
    order by avg(m.mark) desc""", nativeQuery = true
    )
    fun studentMarks(groupId: Long): List<StudentMark>

}