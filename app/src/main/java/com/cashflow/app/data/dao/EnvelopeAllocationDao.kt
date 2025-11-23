package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.EnvelopeAllocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface EnvelopeAllocationDao {
    @Query("SELECT * FROM envelope_allocations WHERE envelopeId = :envelopeId ORDER BY periodStart DESC")
    fun getAllocationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeAllocationEntity>>

    @Query("SELECT * FROM envelope_allocations WHERE envelopeId = :envelopeId AND periodStart <= :date AND periodEnd >= :date LIMIT 1")
    suspend fun getAllocationForEnvelopeAndDate(envelopeId: Long, date: LocalDate): EnvelopeAllocationEntity?

    @Query("SELECT * FROM envelope_allocations WHERE envelopeId = :envelopeId AND periodStart <= :date AND periodEnd >= :date")
    suspend fun getAllocationForPeriod(envelopeId: Long, date: LocalDate): EnvelopeAllocationEntity?

    @Query("SELECT * FROM envelope_allocations WHERE id = :id")
    suspend fun getAllocationById(id: Long): EnvelopeAllocationEntity?

    @Query("SELECT * FROM envelope_allocations WHERE envelopeId = :envelopeId AND periodStart >= :startDate AND periodEnd <= :endDate ORDER BY periodStart")
    fun getAllocationsInRange(envelopeId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<EnvelopeAllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: EnvelopeAllocationEntity): Long

    @Update
    suspend fun updateAllocation(allocation: EnvelopeAllocationEntity)

    @Delete
    suspend fun deleteAllocation(allocation: EnvelopeAllocationEntity)
    
    @Query("DELETE FROM envelope_allocations WHERE envelopeId = :envelopeId")
    suspend fun deleteAllocationsForEnvelope(envelopeId: Long)
    
    @Query("DELETE FROM envelope_allocations")
    suspend fun deleteAllAllocations()
}
