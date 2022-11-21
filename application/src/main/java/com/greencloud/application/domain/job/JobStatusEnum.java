package com.greencloud.application.domain.job;

import static java.util.stream.Stream.concat;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum describing what is the current status of the job in the network
 */
public enum JobStatusEnum {

	CREATED,
	PROCESSING,
	ACCEPTED_BY_SERVER,
	ACCEPTED,
	IN_PROGRESS,
	IN_PROGRESS_BACKUP_ENERGY_PLANNED,
	IN_PROGRESS_BACKUP_ENERGY,
	ON_HOLD_TRANSFER,
	ON_HOLD_SOURCE_SHORTAGE_PLANNED,
	ON_HOLD_SOURCE_SHORTAGE,
	ON_HOLD_PLANNED,
	ON_HOLD;

	public static final Set<JobStatusEnum> ACCEPTED_JOB_STATUSES = EnumSet.of(ACCEPTED, IN_PROGRESS,
			IN_PROGRESS_BACKUP_ENERGY_PLANNED, IN_PROGRESS_BACKUP_ENERGY, ON_HOLD_PLANNED, ON_HOLD,
			ON_HOLD_TRANSFER, ON_HOLD_SOURCE_SHORTAGE_PLANNED, ON_HOLD_SOURCE_SHORTAGE);

	public static final Set<JobStatusEnum> ACCEPTED_BY_SERVER_JOB_STATUSES = concat(Stream.of(ACCEPTED_BY_SERVER),
			ACCEPTED_JOB_STATUSES.stream()).collect(
			Collectors.toSet());
	public static final Set<JobStatusEnum> RUNNING_JOB_STATUSES = EnumSet.of(IN_PROGRESS,
			IN_PROGRESS_BACKUP_ENERGY, ON_HOLD, ON_HOLD_TRANSFER, ON_HOLD_SOURCE_SHORTAGE);
	public static final Set<JobStatusEnum> PLANNED_JOB_STATUSES = EnumSet.of(ACCEPTED,
			IN_PROGRESS_BACKUP_ENERGY_PLANNED, ON_HOLD_PLANNED, ON_HOLD_SOURCE_SHORTAGE_PLANNED,
			ON_HOLD_SOURCE_SHORTAGE, ON_HOLD_TRANSFER);
	public static final Set<JobStatusEnum> ACTIVE_JOB_STATUSES = EnumSet.of(IN_PROGRESS,
			IN_PROGRESS_BACKUP_ENERGY, IN_PROGRESS_BACKUP_ENERGY_PLANNED, ACCEPTED);
	public static final Set<JobStatusEnum> JOB_ON_HOLD_STATUSES = EnumSet.of(ON_HOLD_PLANNED, ON_HOLD, ON_HOLD_TRANSFER,
			ON_HOLD_SOURCE_SHORTAGE_PLANNED, ON_HOLD_SOURCE_SHORTAGE);
	public static final Set<JobStatusEnum> POWER_SHORTAGE_SOURCE_STATUSES = EnumSet.of(
			IN_PROGRESS_BACKUP_ENERGY, ON_HOLD_SOURCE_SHORTAGE);
	public static final Set<JobStatusEnum> GREEN_ENERGY_STATUSES = EnumSet.of(IN_PROGRESS, ACCEPTED);
	public static final Set<JobStatusEnum> BACK_UP_POWER_STATUSES = EnumSet.of(IN_PROGRESS_BACKUP_ENERGY_PLANNED,
			IN_PROGRESS_BACKUP_ENERGY);
}
