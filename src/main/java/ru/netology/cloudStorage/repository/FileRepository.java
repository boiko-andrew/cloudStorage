package ru.netology.cloudStorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.netology.cloudStorage.entity.File;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query(value = "select * from files f where f.user_id = ?1 order by f.id asc limit ?2",
            nativeQuery = true)
    List<File> findFilesByUserIdWithLimit(Long userId, int limit);

    @Query(value = "select id from files f where f.user_id = ?1 and f.name = ?2",
            nativeQuery = true)
    List<Long> findFilesByUserIdAndName(Long userId, String name);

    @Query(value = "select * from files f where f.id = ?1 limit 1",
            nativeQuery = true)
    File findFilesById(Long fileId);

    @Query(value = "select * from files", nativeQuery = true)
    List<File> findAllFiles();
}