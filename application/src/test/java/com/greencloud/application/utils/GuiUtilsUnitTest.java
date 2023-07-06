package com.greencloud.application.utils;

import static com.greencloud.application.utils.GuiUtils.announceBookedJob;
import static com.greencloud.application.utils.GuiUtils.announceFinishedJob;
import static com.greencloud.application.utils.GuiUtils.announceNewClient;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.gui.controller.GuiController;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class GuiUtilsUnitTest {

	@Mock
	private CloudNetworkAgent cloudNetworkAgent;
	@Mock
	private GuiController guiController;

	@BeforeEach
	void setUp() {
		cloudNetworkAgent = mock(CloudNetworkAgent.class);
		guiController = mock(GuiController.class);

		doReturn(guiController).when(cloudNetworkAgent).getGuiController();
	}

	@Test
	@DisplayName("Test announcing finished job")
	void testAnnounceFinishedJob() {
		announceFinishedJob(cloudNetworkAgent);

		verify(guiController).updateActiveJobsCountByValue(-1);
		verify(guiController).updateAllJobsCountByValue(-1);
	}

	@Test
	@DisplayName("Test announcing booked job")
	void testAnnounceBookedJob() {
		announceBookedJob(cloudNetworkAgent);

		verify(guiController).updateAllJobsCountByValue(1);
	}

	@Test
	@DisplayName("Test announcing new client")
	void testAnnounceNewClient() {
		announceNewClient(cloudNetworkAgent);

		verify(guiController).updateClientsCountByValue(1);
	}
}