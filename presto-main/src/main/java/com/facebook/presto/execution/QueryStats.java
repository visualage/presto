/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.execution;

import com.facebook.presto.operator.BlockedReason;
import com.facebook.presto.operator.OperatorStats;
import com.facebook.presto.operator.TableWriterOperator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.airlift.units.DataSize;
import io.airlift.units.Duration;
import org.joda.time.DateTime;

import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static io.airlift.units.DataSize.succinctBytes;
import static io.airlift.units.Duration.succinctNanos;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class QueryStats
{
    private final DateTime createTime;

    private final DateTime executionStartTime;
    private final DateTime lastHeartbeat;
    private final DateTime endTime;

    private final Duration elapsedTime;
    private final Duration queuedTime;
    private final Duration analysisTime;
    private final Duration distributedPlanningTime;
    private final Duration totalPlanningTime;
    private final Duration finishingTime;

    private final int totalTasks;
    private final int runningTasks;
    private final int completedTasks;

    private final int totalDrivers;
    private final int queuedDrivers;
    private final int runningDrivers;
    private final int blockedDrivers;
    private final int completedDrivers;

    private final double cumulativeMemory;
    private final DataSize totalMemoryReservation;
    private final DataSize peakMemoryReservation;

    private final boolean scheduled;
    private final Duration totalScheduledTime;
    private final Duration totalCpuTime;
    private final Duration totalUserTime;
    private final Duration totalBlockedTime;
    private final boolean fullyBlocked;
    private final Set<BlockedReason> blockedReasons;

    private final DataSize rawInputDataSize;
    private final long rawInputPositions;

    private final DataSize processedInputDataSize;
    private final long processedInputPositions;

    private final DataSize outputDataSize;
    private final long outputPositions;

    private final DataSize physicalWrittenDataSize;

    private final List<OperatorStats> operatorSummaries;

    @VisibleForTesting
    public QueryStats()
    {
        this.createTime = null;
        this.executionStartTime = null;
        this.lastHeartbeat = null;
        this.endTime = null;
        this.elapsedTime = null;
        this.queuedTime = null;
        this.analysisTime = null;
        this.distributedPlanningTime = null;
        this.totalPlanningTime = null;
        this.finishingTime = null;
        this.totalTasks = 0;
        this.runningTasks = 0;
        this.blockedDrivers = 0;
        this.completedTasks = 0;
        this.totalDrivers = 0;
        this.queuedDrivers = 0;
        this.runningDrivers = 0;
        this.completedDrivers = 0;
        this.cumulativeMemory = 0.0;
        this.totalMemoryReservation = null;
        this.peakMemoryReservation = null;
        this.scheduled = false;
        this.totalScheduledTime = null;
        this.totalCpuTime = null;
        this.totalUserTime = null;
        this.totalBlockedTime = null;
        this.fullyBlocked = false;
        this.blockedReasons = ImmutableSet.of();
        this.rawInputDataSize = null;
        this.rawInputPositions = 0;
        this.processedInputDataSize = null;
        this.processedInputPositions = 0;
        this.outputDataSize = null;
        this.outputPositions = 0;
        this.physicalWrittenDataSize = null;
        this.operatorSummaries = null;
    }

    @JsonCreator
    public QueryStats(
            @JsonProperty("createTime") DateTime createTime,
            @JsonProperty("executionStartTime") DateTime executionStartTime,
            @JsonProperty("lastHeartbeat") DateTime lastHeartbeat,
            @JsonProperty("endTime") DateTime endTime,

            @JsonProperty("elapsedTime") Duration elapsedTime,
            @JsonProperty("queuedTime") Duration queuedTime,
            @JsonProperty("analysisTime") Duration analysisTime,
            @JsonProperty("distributedPlanningTime") Duration distributedPlanningTime,
            @JsonProperty("totalPlanningTime") Duration totalPlanningTime,
            @JsonProperty("finishingTime") Duration finishingTime,

            @JsonProperty("totalTasks") int totalTasks,
            @JsonProperty("runningTasks") int runningTasks,
            @JsonProperty("completedTasks") int completedTasks,

            @JsonProperty("totalDrivers") int totalDrivers,
            @JsonProperty("queuedDrivers") int queuedDrivers,
            @JsonProperty("runningDrivers") int runningDrivers,
            @JsonProperty("blockedDrivers") int blockedDrivers,
            @JsonProperty("completedDrivers") int completedDrivers,

            @JsonProperty("cumulativeMemory") double cumulativeMemory,
            @JsonProperty("totalMemoryReservation") DataSize totalMemoryReservation,
            @JsonProperty("peakMemoryReservation") DataSize peakMemoryReservation,

            @JsonProperty("scheduled") boolean scheduled,
            @JsonProperty("totalScheduledTime") Duration totalScheduledTime,
            @JsonProperty("totalCpuTime") Duration totalCpuTime,
            @JsonProperty("totalUserTime") Duration totalUserTime,
            @JsonProperty("totalBlockedTime") Duration totalBlockedTime,
            @JsonProperty("fullyBlocked") boolean fullyBlocked,
            @JsonProperty("blockedReasons") Set<BlockedReason> blockedReasons,

            @JsonProperty("rawInputDataSize") DataSize rawInputDataSize,
            @JsonProperty("rawInputPositions") long rawInputPositions,

            @JsonProperty("processedInputDataSize") DataSize processedInputDataSize,
            @JsonProperty("processedInputPositions") long processedInputPositions,

            @JsonProperty("outputDataSize") DataSize outputDataSize,
            @JsonProperty("outputPositions") long outputPositions,

            @JsonProperty("physicalWrittenDataSize") DataSize physicalWrittenDataSize,

            @JsonProperty("operatorSummaries") List<OperatorStats> operatorSummaries)
    {
        this.createTime = requireNonNull(createTime, "createTime is null");
        this.executionStartTime = executionStartTime;
        this.lastHeartbeat = requireNonNull(lastHeartbeat, "lastHeartbeat is null");
        this.endTime = endTime;

        this.elapsedTime = elapsedTime;
        this.queuedTime = queuedTime;
        this.analysisTime = analysisTime;
        this.distributedPlanningTime = distributedPlanningTime;
        this.totalPlanningTime = totalPlanningTime;
        this.finishingTime = finishingTime;

        checkArgument(totalTasks >= 0, "totalTasks is negative");
        this.totalTasks = totalTasks;
        checkArgument(runningTasks >= 0, "runningTasks is negative");
        this.runningTasks = runningTasks;
        checkArgument(completedTasks >= 0, "completedTasks is negative");
        this.completedTasks = completedTasks;

        checkArgument(totalDrivers >= 0, "totalDrivers is negative");
        this.totalDrivers = totalDrivers;
        checkArgument(queuedDrivers >= 0, "queuedDrivers is negative");
        this.queuedDrivers = queuedDrivers;
        checkArgument(runningDrivers >= 0, "runningDrivers is negative");
        this.runningDrivers = runningDrivers;
        checkArgument(blockedDrivers >= 0, "blockedDrivers is negative");
        this.blockedDrivers = blockedDrivers;
        checkArgument(completedDrivers >= 0, "completedDrivers is negative");
        this.completedDrivers = completedDrivers;

        this.cumulativeMemory = requireNonNull(cumulativeMemory, "cumulativeMemory is null");
        this.totalMemoryReservation = requireNonNull(totalMemoryReservation, "totalMemoryReservation is null");
        this.peakMemoryReservation = requireNonNull(peakMemoryReservation, "peakMemoryReservation is null");
        this.scheduled = scheduled;
        this.totalScheduledTime = requireNonNull(totalScheduledTime, "totalScheduledTime is null");
        this.totalCpuTime = requireNonNull(totalCpuTime, "totalCpuTime is null");
        this.totalUserTime = requireNonNull(totalUserTime, "totalUserTime is null");
        this.totalBlockedTime = requireNonNull(totalBlockedTime, "totalBlockedTime is null");
        this.fullyBlocked = fullyBlocked;
        this.blockedReasons = ImmutableSet.copyOf(requireNonNull(blockedReasons, "blockedReasons is null"));

        this.rawInputDataSize = requireNonNull(rawInputDataSize, "rawInputDataSize is null");
        checkArgument(rawInputPositions >= 0, "rawInputPositions is negative");
        this.rawInputPositions = rawInputPositions;

        this.processedInputDataSize = requireNonNull(processedInputDataSize, "processedInputDataSize is null");
        checkArgument(processedInputPositions >= 0, "processedInputPositions is negative");
        this.processedInputPositions = processedInputPositions;

        this.outputDataSize = requireNonNull(outputDataSize, "outputDataSize is null");
        checkArgument(outputPositions >= 0, "outputPositions is negative");
        this.outputPositions = outputPositions;

        this.physicalWrittenDataSize = requireNonNull(physicalWrittenDataSize, "physicalWrittenDataSize is null");

        this.operatorSummaries = ImmutableList.copyOf(requireNonNull(operatorSummaries, "operatorSummaries is null"));
    }

    @JsonProperty
    public DateTime getCreateTime()
    {
        return createTime;
    }

    @JsonProperty
    public DateTime getExecutionStartTime()
    {
        return executionStartTime;
    }

    @JsonProperty
    public DateTime getLastHeartbeat()
    {
        return lastHeartbeat;
    }

    @JsonProperty
    public DateTime getEndTime()
    {
        return endTime;
    }

    @JsonProperty
    public Duration getElapsedTime()
    {
        return elapsedTime;
    }

    @JsonProperty
    public Duration getQueuedTime()
    {
        if (queuedTime == null) {
            // counter-intuitively, this means that the query is still queued
            return elapsedTime;
        }
        return queuedTime;
    }

    @JsonProperty
    public Duration getExecutionTime()
    {
        if (queuedTime == null) {
            // counter-intuitively, this means that the query is still queued
            return new Duration(0, NANOSECONDS);
        }
        return succinctNanos((long) elapsedTime.getValue(NANOSECONDS) - (long) queuedTime.getValue(NANOSECONDS));
    }

    @JsonProperty
    public Duration getAnalysisTime()
    {
        return analysisTime;
    }

    @JsonProperty
    public Duration getDistributedPlanningTime()
    {
        return distributedPlanningTime;
    }

    @JsonProperty
    public Duration getTotalPlanningTime()
    {
        return totalPlanningTime;
    }

    @JsonProperty
    public Duration getFinishingTime()
    {
        return finishingTime;
    }

    @JsonProperty
    public int getTotalTasks()
    {
        return totalTasks;
    }

    @JsonProperty
    public int getRunningTasks()
    {
        return runningTasks;
    }

    @JsonProperty
    public int getCompletedTasks()
    {
        return completedTasks;
    }

    @JsonProperty
    public int getTotalDrivers()
    {
        return totalDrivers;
    }

    @JsonProperty
    public int getQueuedDrivers()
    {
        return queuedDrivers;
    }

    @JsonProperty
    public int getRunningDrivers()
    {
        return runningDrivers;
    }

    @JsonProperty
    public int getBlockedDrivers()
    {
        return blockedDrivers;
    }

    @JsonProperty
    public int getCompletedDrivers()
    {
        return completedDrivers;
    }

    @JsonProperty
    public double getCumulativeMemory()
    {
        return cumulativeMemory;
    }

    @JsonProperty
    public DataSize getTotalMemoryReservation()
    {
        return totalMemoryReservation;
    }

    @JsonProperty
    public DataSize getPeakMemoryReservation()
    {
        return peakMemoryReservation;
    }

    @JsonProperty
    public boolean isScheduled()
    {
        return scheduled;
    }

    @JsonProperty
    public Duration getTotalScheduledTime()
    {
        return totalScheduledTime;
    }

    @JsonProperty
    public Duration getTotalCpuTime()
    {
        return totalCpuTime;
    }

    @JsonProperty
    public Duration getTotalUserTime()
    {
        return totalUserTime;
    }

    @JsonProperty
    public Duration getTotalBlockedTime()
    {
        return totalBlockedTime;
    }

    @JsonProperty
    public boolean isFullyBlocked()
    {
        return fullyBlocked;
    }

    @JsonProperty
    public Set<BlockedReason> getBlockedReasons()
    {
        return blockedReasons;
    }

    @JsonProperty
    public DataSize getRawInputDataSize()
    {
        return rawInputDataSize;
    }

    @JsonProperty
    public long getRawInputPositions()
    {
        return rawInputPositions;
    }

    @JsonProperty
    public DataSize getProcessedInputDataSize()
    {
        return processedInputDataSize;
    }

    @JsonProperty
    public long getProcessedInputPositions()
    {
        return processedInputPositions;
    }

    @JsonProperty
    public DataSize getOutputDataSize()
    {
        return outputDataSize;
    }

    @JsonProperty
    public long getOutputPositions()
    {
        return outputPositions;
    }

    @JsonProperty
    public DataSize getPhysicalWrittenDataSize()
    {
        return physicalWrittenDataSize;
    }

    @JsonProperty
    public long getWrittenPositions()
    {
        return operatorSummaries.stream()
                .filter(stats -> stats.getOperatorType().equals(TableWriterOperator.class.getSimpleName()))
                .mapToLong(stats -> stats.getInputPositions())
                .sum();
    }

    @JsonProperty
    public DataSize getLogicalWrittenDataSize()
    {
        return succinctBytes(
                operatorSummaries.stream()
                        .filter(stats -> stats.getOperatorType().equals(TableWriterOperator.class.getSimpleName()))
                        .mapToLong(stats -> stats.getInputDataSize().toBytes())
                        .sum());
    }

    @JsonProperty
    public List<OperatorStats> getOperatorSummaries()
    {
        return operatorSummaries;
    }

    @JsonProperty
    public OptionalDouble getProgressPercentage()
    {
        if (!scheduled || totalDrivers == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(min(100, (completedDrivers * 100.0) / totalDrivers));
    }
}
