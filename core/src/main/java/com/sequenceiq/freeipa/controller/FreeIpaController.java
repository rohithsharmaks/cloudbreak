package com.sequenceiq.freeipa.controller;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackStatusV4Response;
import com.sequenceiq.cloudbreak.api.freeipa.FreeIpaEndpoint;
import com.sequenceiq.cloudbreak.api.freeipa.FreeIpaRequest;
import com.sequenceiq.cloudbreak.api.freeipa.FreeIpaResponse;
import com.sequenceiq.cloudbreak.common.model.user.CloudbreakUser;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.service.CloudbreakRestRequestThreadLocalService;
import com.sequenceiq.cloudbreak.service.user.UserService;
import com.sequenceiq.cloudbreak.service.workspace.WorkspaceService;
import com.sequenceiq.freeipa.service.FreeIpaCreationService;

@Controller
public class FreeIpaController implements FreeIpaEndpoint {

    @Inject
    private UserService userService;

    @Inject
    private CloudbreakRestRequestThreadLocalService restRequestThreadLocalService;

    @Inject
    private WorkspaceService workspaceService;

    @Inject
    private FreeIpaCreationService freeIpaCreationService;

    @Override
    public List<FreeIpaResponse> list(Long workspaceId, String environment, Boolean onlyDatalakes) {
        return null;
    }

    @Override
    public FreeIpaResponse post(Long workspaceId, @Valid FreeIpaRequest request) {
        CloudbreakUser cloudbreakUser = restRequestThreadLocalService.getCloudbreakUser();
        User user = userService.getOrCreate(cloudbreakUser);
        Workspace workspace = workspaceService.get(workspaceId, user);
        return freeIpaCreationService.createFreeIpa(request, cloudbreakUser, user, workspace);
    }

    @Override
    public FreeIpaResponse get(Long workspaceId, String name, Set<String> entries) {
        return null;
    }

    @Override
    public void delete(Long workspaceId, String name, Boolean forced, Boolean deleteDependencies) {

    }

    @Override
    public void putSync(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putRetry(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putStop(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putStart(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FreeIpaRequest getRequestfromName(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StackStatusV4Response getStatusByName(Long workspaceId, String name) {
        throw new UnsupportedOperationException();
    }
}
