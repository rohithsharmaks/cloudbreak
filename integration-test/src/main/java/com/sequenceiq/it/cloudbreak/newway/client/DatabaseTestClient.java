package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.database.DatabaseCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.database.DatabaseCreateIfNotExistsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.database.DatabaseDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.database.DatabaseListAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.database.DatabaseTestConnectionAction;
import com.sequenceiq.it.cloudbreak.newway.dto.database.DatabaseTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.database.DatabaseTestTestDto;

@Service
public class DatabaseTestClient {

    public IntegrationTestAction<DatabaseTestDto> createV4() {
        return new DatabaseCreateAction();
    }

    public IntegrationTestAction<DatabaseTestDto> deleteV4() {
        return new DatabaseDeleteAction();
    }

    public IntegrationTestAction<DatabaseTestDto> listV4() {
        return new DatabaseListAction();
    }

    public IntegrationTestAction<DatabaseTestTestDto> testV4() {
        return new DatabaseTestConnectionAction();
    }

    public IntegrationTestAction<DatabaseTestDto> createIfNotExistV4() {
        return new DatabaseCreateIfNotExistsAction();
    }
}
