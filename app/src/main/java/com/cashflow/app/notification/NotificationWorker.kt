package com.cashflow.app.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cashflow.app.di.AppModule
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = AppModule.provideRepository(applicationContext)
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        
        // Check for upcoming bills
        val bills = repository.getAllActiveBills().first()
        val upcomingBills = bills.filter { bill ->
            val daysUntilDue = (bill.startDate.toEpochDays() - today.toEpochDays()).toInt()
            daysUntilDue in 0..bill.reminderDaysBefore
        }

        if (upcomingBills.isNotEmpty()) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            upcomingBills.forEach { bill ->
                val notification = NotificationCompat.Builder(applicationContext, "cashflow_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Upcoming Bill: ${bill.name}")
                    .setContentText("Due in ${bill.reminderDaysBefore} days: $${bill.amount}")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                
                notificationManager.notify(bill.id.toInt(), notification)
            }
        }

        return Result.success()
    }
}

