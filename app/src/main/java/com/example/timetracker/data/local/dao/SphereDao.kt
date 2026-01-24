// data/local/dao/SphereDao.kt
package com.example.timetracker.data.local.dao

import androidx.room.*
import com.example.timetracker.data.local.entities.SphereEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SphereDao {
    @Query("SELECT * FROM spheres")
    fun getAllSpheres(): Flow<List<SphereEntity>>

    @Query("SELECT * FROM spheres ORDER BY sphere ASC")
    fun getAllSpheresByName(): Flow<List<SphereEntity>>

    // Получение сфер с названиями (не null)
    @Query("SELECT * FROM spheres WHERE sphere IS NOT NULL ORDER BY sphere ASC")
    fun getNamedSpheres(): Flow<List<SphereEntity>>

    // Получение сфер без названий
//    @Query("SELECT * FROM spheres WHERE sphere IS NULL ORDER BY lastUsedAt DESC")
//    fun getUnnamedSpheres(): Flow<List<SphereEntity>>

    // Получение одной сферы
    @Query("SELECT * FROM spheres WHERE id = :id")
    suspend fun getSphereById(id: Long): SphereEntity?

    // Поиск по цвету
    @Query("SELECT * FROM spheres WHERE color = :color")
    suspend fun getSpheresByColor(color: Long): List<SphereEntity>

    // Проверка существования пары color-sphere
    @Query("SELECT COUNT(*) FROM spheres WHERE color = :color AND sphere = :sphere")
    suspend fun existsByColorAndSphere(color: Long, sphere: String?): Int

    // Поиск по цвету и сфере
    @Query("SELECT * FROM spheres WHERE color = :color AND sphere = :sphere LIMIT 1")
    suspend fun findByColorAndSphere(color: Long, sphere: String?): SphereEntity?

    // Базовые операции
    @Insert(onConflict = OnConflictStrategy.ABORT) // Вызовет ошибку при дубликате
    suspend fun insert(sphere: SphereEntity): Long

    @Update
    suspend fun update(sphere: SphereEntity)

    @Delete
    suspend fun delete(sphere: SphereEntity)

    // Добавление названия к существующей сфере (по цвету)
    @Query("UPDATE spheres SET sphere = :sphereName WHERE id = :id")
    suspend fun updateSphereName(id: Long, sphereName: String)
}