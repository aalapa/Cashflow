package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.EnvelopeTransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeTransferDao {
    @Query("SELECT * FROM envelope_transfers WHERE fromEnvelopeId = :envelopeId OR toEnvelopeId = :envelopeId ORDER BY date DESC")
    fun getTransfersForEnvelope(envelopeId: Long): Flow<List<EnvelopeTransferEntity>>

    @Query("SELECT * FROM envelope_transfers ORDER BY date DESC")
    fun getAllTransfers(): Flow<List<EnvelopeTransferEntity>>

    @Query("SELECT * FROM envelope_transfers WHERE id = :id")
    suspend fun getTransferById(id: Long): EnvelopeTransferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: EnvelopeTransferEntity): Long

    @Delete
    suspend fun deleteTransfer(transfer: EnvelopeTransferEntity)
    
    @Query("DELETE FROM envelope_transfers")
    suspend fun deleteAllTransfers()
}
